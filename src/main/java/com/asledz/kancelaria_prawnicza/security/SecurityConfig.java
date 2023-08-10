package com.asledz.kancelaria_prawnicza.security;

import com.asledz.kancelaria_prawnicza.enums.Path;
import com.asledz.kancelaria_prawnicza.security.filter.CustomAuthenticationFilter;
import com.asledz.kancelaria_prawnicza.security.filter.CustomAuthorizationFilter;
import com.asledz.kancelaria_prawnicza.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.Clock;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Clock clock;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers(POST, "/login/**").permitAll();
        http.authorizeRequests().antMatchers(GET, "/token/refresh/**").permitAll();
        http.authorizeRequests().antMatchers(Path.DOCUMENT_VALUE + "s/by/**").hasAnyAuthority("User", "Manager");
        http.authorizeRequests().antMatchers(Path.DOCUMENT_VALUE + "s/**").hasAnyAuthority("User", "Manager");
        http.authorizeRequests().antMatchers(Path.FILE_VALUE + "s/**").hasAnyAuthority("User", "Manager");
        http.authorizeRequests().antMatchers(Path.USER_VALUE + "s/**").hasAnyAuthority("Manager");
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean(), userService, clock, new ObjectMapper()));
        http.addFilterBefore(new CustomAuthorizationFilter(clock), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:8092/");
        config.addAllowedHeader("*"); // this allows all headers
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
