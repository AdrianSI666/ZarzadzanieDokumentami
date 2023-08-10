package com.asledz.kancelaria_prawnicza.security.filter;

import com.asledz.kancelaria_prawnicza.dto.UserDTO;
import com.asledz.kancelaria_prawnicza.exception.LoginException;
import com.asledz.kancelaria_prawnicza.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith({SystemStubsExtension.class, SpringExtension.class})
class CustomAuthenticationFilterTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserService userService;
    @Mock
    private Clock clock;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private CustomAuthenticationFilter authenticationFilter;
    @SystemStub
    private EnvironmentVariables environmentVariables;

    /**
     * Method under test: {@link CustomAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    void testAttemptAuthentication() throws IOException, AuthenticationException {
        MockHttpServletRequest request = mock(MockHttpServletRequest.class);
        Map<String, String> map = new HashMap<>();
        map.put("email", "Test");
        map.put("password", "Test");

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(map);
        byte[] bytes = byteOut.toByteArray();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(byteIn);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        when(request.getReader()).thenReturn(bufferedReader);
        when(objectMapper.readValue(bufferedReader.readLine(), Map.class)).thenReturn(map);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        assertDoesNotThrow(() -> authenticationFilter.attemptAuthentication(request, new Response()));
        verify(request).getReader();
    }

    /**
     * Method under test: {@link CustomAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    void testAttemptAuthenticationWrongCredentials() throws IOException, AuthenticationException {
        MockHttpServletRequest request = mock(MockHttpServletRequest.class);
        Map<String, String> map = new HashMap<>();
        map.put("email", "Test");
        map.put("password", "Test");

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(map);
        byte[] bytes = byteOut.toByteArray();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(byteIn);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        when(request.getReader()).thenReturn(bufferedReader);
        when(objectMapper.readValue(bufferedReader.readLine(), Map.class)).thenReturn(map);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        given(authenticationManager.authenticate(any()))
                .willThrow(new LoginException("Wrong credentials", new RuntimeException()));
        assertThrows(AuthenticationException.class,
                () -> authenticationFilter.attemptAuthentication(request, new Response()));
        verify(request).getReader();
    }

    /**
     * Method under test: {@link CustomAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    void testAttemptAuthenticationWrongDataStructure() throws IOException, AuthenticationException {
        MockHttpServletRequest request = mock(MockHttpServletRequest.class);
        String map = "I want to be a map";

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(map);
        byte[] bytes = byteOut.toByteArray();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(byteIn);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        when(request.getReader()).thenReturn(bufferedReader);
        given(objectMapper.readValue(bufferedReader.readLine(), Map.class))
                .willAnswer(invocation -> {
                    throw new IOException();
                });
        assertThrows(LoginException.class,
                () -> authenticationFilter.attemptAuthentication(request, new Response()));
        verify(request).getReader();
    }

    /**
     * Method under test: {@link CustomAuthenticationFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)}
     */
    @Test
    void testAttemptAuthentication6() throws IOException, AuthenticationException {
        final var userAuthentication = mock(Authentication.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setServletPath((String) "/documents");
        environmentVariables.set("SECRETKEY", "SECRETKEY");
        Algorithm algorithm = Algorithm.HMAC256("SECRETKEY".getBytes());
        Instant time = Clock.systemUTC().instant();

        UserDetails user = User.builder().authorities("User").username("test").password("test").build();
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(time.toEpochMilli() + 10 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .sign(algorithm);
        request.addHeader(AUTHORIZATION, "Bearer %s".formatted(accessToken));
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(time.toEpochMilli() + 60 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        given(userAuthentication.getPrincipal()).willReturn(user);
        given(clock.instant()).willReturn(time);
        given(userService.getUserByEmail(user.getUsername())).willReturn(UserDTO.builder().id(1L).email(user.getUsername()).build());
        FilterChain filterChain = mock(FilterChain.class);
        environmentVariables.set("SECRETKEY", "SECRETKEY");

        assertThatNoException().isThrownBy(() -> authenticationFilter.successfulAuthentication(request, response, filterChain, userAuthentication));
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        tokens.put("user_id", String.valueOf(userService.getUserByEmail(user.getUsername()).id()));
        MockHttpServletResponse expected = new MockHttpServletResponse();
        new ObjectMapper().writeValue(expected.getOutputStream(), tokens);
        String expectedStr = expected.getContentAsString();
        String actualStr = response.getContentAsString();
        assertEquals(expectedStr, actualStr);
    }
}

