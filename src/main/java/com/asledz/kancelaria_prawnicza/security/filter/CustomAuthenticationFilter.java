package com.asledz.kancelaria_prawnicza.security.filter;

import com.asledz.kancelaria_prawnicza.exception.LoginException;
import com.asledz.kancelaria_prawnicza.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final Clock clock;
    private final ObjectMapper mapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email;
        String password;
        try (BufferedReader body = request.getReader()) {
            Map<String, String> map = mapper.readValue(body.readLine(), Map.class);
            email = map.get("email");
            password = map.get("password");
            log.info("Somebody tried to log in: {} with pass {}", email, password);
        } catch (IOException e) {
            log.info("Somebody tried to log in with wrong data structure: {}", e.getMessage());
            throw new LoginException(e.getMessage(), e);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        try {
            return authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException authenticationException) {
            log.error("Authentication error occurred: {}", authenticationException.getMessage());
            throw new LoginException(authenticationException.getMessage(), authenticationException);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256(System.getenv("SECRETKEY").getBytes());
        Instant time = clock.instant();
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(time.toEpochMilli() + 10 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(time.toEpochMilli() + 60 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        tokens.put("user_id", String.valueOf(userService.getUserByEmail(user.getUsername()).id()));
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
