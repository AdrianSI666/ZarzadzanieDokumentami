package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.domain.Role;
import com.asledz.kancelaria_prawnicza.dto.UserAuthorities;
import com.asledz.kancelaria_prawnicza.exception.ForbiddenException;
import com.asledz.kancelaria_prawnicza.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith({SystemStubsExtension.class, SpringExtension.class})
class TokenControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private Clock clock;
    @InjectMocks
    private TokenController tokenController;
    @SystemStub
    private EnvironmentVariables environmentVariables;

    /**
     * Method under test: {@link TokenController#refreshToken(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    void testRefreshToken() {
        TokenController tokenController = new TokenController(userService,
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertThrows(ForbiddenException.class, () -> tokenController.refreshToken(request, new Response()));
    }

    /**
     * Method under test: {@link TokenController#refreshToken(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    void testRefreshToken2() {

        TokenController tokenController = new TokenController(userService,
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));
        DefaultMultipartHttpServletRequest request = mock(DefaultMultipartHttpServletRequest.class);
        when(request.getHeader(Mockito.<String>any())).thenReturn("https://example.org/example");
        assertThrows(ForbiddenException.class, () -> tokenController.refreshToken(request, new Response()));
        verify(request, atLeast(1)).getHeader(Mockito.<String>any());
    }

    /**
     * Method under test: {@link TokenController#refreshToken(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    void testRefreshToken3() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setServletPath((String) "/documents");
        environmentVariables.set("SECRETKEY", "SECRETKEY");
        Algorithm algorithm = Algorithm.HMAC256("SECRETKEY".getBytes());
        Instant time = Clock.systemUTC().instant();

        UserDetails user = User.builder().authorities("User").username("test").password("test").build();
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(time.toEpochMilli() - 60 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .sign(algorithm);
        request.addHeader(AUTHORIZATION, "Bearer %s".formatted(accessToken));
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(time.toEpochMilli() + 60 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        request.addHeader("Refresh", refreshToken);
        given(clock.millis()).willReturn(time.toEpochMilli());
        given(userService.getUserDetailsByEmail(user.getUsername())).willReturn(new UserAuthorities(user.getUsername(), user.getAuthorities().stream().map((authority) -> Role.builder().name(authority.getAuthority()).build()).toList()));
        assertThatNoException().isThrownBy(() -> tokenController.refreshToken(request, response));
        String newAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(time.toEpochMilli() + 10 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .sign(algorithm);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", newAccessToken);
        tokens.put("refresh_token", refreshToken);
        MockHttpServletResponse expected = new MockHttpServletResponse();
        new ObjectMapper().writeValue(expected.getOutputStream(), tokens);
        String expectedStr = expected.getContentAsString();
        String actualStr = response.getContentAsString();
        assertEquals(expectedStr, actualStr);
    }

    /**
     * Method under test: {@link TokenController#refreshToken(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    void testRefreshToken4() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setServletPath((String) "/documents");
        environmentVariables.set("SECRETKEY", "SECRETKEY");
        Algorithm algorithm = Algorithm.HMAC256("SECRETKEY".getBytes());
        Instant time = Clock.systemUTC().instant();

        UserDetails user = User.builder().authorities("User").username("test").password("test").build();
        request.addHeader(AUTHORIZATION, "Bearer ");
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(time.toEpochMilli() + 60 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        request.addHeader("Refresh", refreshToken);
        given(clock.millis()).willReturn(time.toEpochMilli());
        given(userService.getUserDetailsByEmail(user.getUsername())).willReturn(new UserAuthorities(user.getUsername(), user.getAuthorities().stream().map((authority) -> Role.builder().name(authority.getAuthority()).build()).toList()));
        assertThrows(ForbiddenException.class, () -> tokenController.refreshToken(request, response));
    }
}

