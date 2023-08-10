package com.asledz.kancelaria_prawnicza.security;

import com.asledz.kancelaria_prawnicza.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.accept.ContentNegotiationStrategy;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {SecurityConfig.class, BCryptPasswordEncoder.class, AuthenticationConfiguration.class,
        UserDetailsService.class})
@ExtendWith(SpringExtension.class)
class SecurityConfigTest {
    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private AuthenticationTrustResolver authenticationTrustResolver;

    @MockBean
    private Clock clock;

    @MockBean
    private ContentNegotiationStrategy contentNegotiationStrategy;

    @Autowired
    private ObjectPostProcessor<Object> objectPostProcessor;

    @Autowired
    private SecurityConfig securityConfig;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserService userService;

    /**
     * Method under test: {@link SecurityConfig#configure(AuthenticationManagerBuilder)}
     */
    @Test
    void testConfigure() throws Exception {
        AuthenticationManagerBuilder auth = new AuthenticationManagerBuilder(objectPostProcessor);
        securityConfig.configure(auth);
        assertTrue(((DaoAuthenticationProvider) ((ProviderManager) auth.getOrBuild()).getProviders().get(0))
                .getUserCache() instanceof NullUserCache);
        assertTrue(((DaoAuthenticationProvider) ((ProviderManager) auth.getOrBuild()).getProviders().get(0))
                .isHideUserNotFoundExceptions());
        assertFalse(((DaoAuthenticationProvider) ((ProviderManager) auth.getOrBuild()).getProviders().get(0))
                .isForcePrincipalAsString());
    }

    /**
     * Method under test: {@link SecurityConfig#configure(AuthenticationManagerBuilder)}
     */
    @Test
    void testConfigure3() throws Exception {
        AuthenticationManagerBuilder auth = new AuthenticationManagerBuilder(objectPostProcessor);
        auth.authenticationEventPublisher(new DefaultAuthenticationEventPublisher(mock(ApplicationEventPublisher.class)));
        securityConfig.configure(auth);
        assertTrue(((DaoAuthenticationProvider) ((ProviderManager) auth.getOrBuild()).getProviders().get(0))
                .getUserCache() instanceof NullUserCache);
        assertTrue(((DaoAuthenticationProvider) ((ProviderManager) auth.getOrBuild()).getProviders().get(0))
                .isHideUserNotFoundExceptions());
        assertFalse(((DaoAuthenticationProvider) ((ProviderManager) auth.getOrBuild()).getProviders().get(0))
                .isForcePrincipalAsString());
    }
}

