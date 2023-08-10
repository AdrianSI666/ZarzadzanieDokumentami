package com.asledz.kancelaria_prawnicza.security.filter;

import com.asledz.kancelaria_prawnicza.exception.ForbiddenException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(SystemStubsExtension.class)
class CustomAuthorizationFilterTest {

    @SystemStub
    private EnvironmentVariables environmentVariables;

    /**
     * Method under test: {@link CustomAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     */
    @Test
    void testDoFilterInternalWithBadToken() throws IOException, ServletException {
        CustomAuthorizationFilter customAuthorizationFilter = new CustomAuthorizationFilter(
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath((String) "/documents");
        String authHeader = "Bearer awds";
        request.addHeader(AUTHORIZATION, authHeader);
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doNothing().when(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());
        environmentVariables.set("SECRETKEY", "SECRETKEY");
        assertThatThrownBy(() -> customAuthorizationFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(ForbiddenException.class);
    }

    /**
     * Method under test: {@link CustomAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     */
    @Test
    void testDoFilterInternalWithToken() throws IOException, ServletException {
        CustomAuthorizationFilter customAuthorizationFilter = new CustomAuthorizationFilter(
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));

        MockHttpServletRequest request = new MockHttpServletRequest();
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
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doNothing().when(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());

        environmentVariables.set("SECRETKEY", "SECRETKEY");
        customAuthorizationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());
    }


    /**
     * Method under test: {@link CustomAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     */
    @Test
    void testDoFilterInternalWithoutToken() throws IOException, ServletException {
        CustomAuthorizationFilter customAuthorizationFilter = new CustomAuthorizationFilter(
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));
        MockHttpServletRequest request = new MockHttpServletRequest();
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doNothing().when(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());
        customAuthorizationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());
    }

    /**
     * Method under test: {@link CustomAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     */
    @Test
    void testDoFilterInternalToLoginPage() throws IOException, ServletException {
        CustomAuthorizationFilter customAuthorizationFilter = new CustomAuthorizationFilter(
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath((String) "/login");
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doNothing().when(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());
        customAuthorizationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());
        assertFalse(request.isRequestedSessionIdFromURL());
        assertTrue(request.isRequestedSessionIdFromCookie());
        assertFalse(request.isAsyncSupported());
        assertFalse(request.isAsyncStarted());
        assertTrue(request.isActive());
        assertTrue(request.getSession() instanceof MockHttpSession);
        assertEquals("/login", request.getServletPath());
        assertEquals(80, request.getServerPort());
        assertEquals("localhost", request.getServerName());
        assertEquals("http", request.getScheme());
        assertEquals("", request.getRequestURI());
        assertEquals(80, request.getRemotePort());
        assertEquals("localhost", request.getRemoteHost());
        assertEquals("HTTP/1.1", request.getProtocol());
        assertEquals("", request.getMethod());
        assertEquals(80, request.getLocalPort());
        assertEquals("localhost", request.getLocalName());
        assertTrue(request.getInputStream() instanceof DelegatingServletInputStream);
        assertEquals(DispatcherType.REQUEST, request.getDispatcherType());
        assertEquals("", request.getContextPath());
        assertEquals(-1L, request.getContentLengthLong());
    }

    /**
     * Method under test: {@link CustomAuthorizationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)}
     */
    @Test
    void testDoFilterInternalToRefreshToken() throws IOException, ServletException {
        CustomAuthorizationFilter customAuthorizationFilter = new CustomAuthorizationFilter(
                Clock.fixed(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath((String) "/token/refresh");
        Response response = new Response();
        FilterChain filterChain = mock(FilterChain.class);
        doNothing().when(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());
        customAuthorizationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(Mockito.<ServletRequest>any(), Mockito.<ServletResponse>any());
        assertFalse(request.isRequestedSessionIdFromURL());
        assertTrue(request.isRequestedSessionIdFromCookie());
        assertFalse(request.isAsyncSupported());
        assertFalse(request.isAsyncStarted());
        assertTrue(request.isActive());
        assertTrue(request.getSession() instanceof MockHttpSession);
        assertEquals("/token/refresh", request.getServletPath());
        assertEquals(80, request.getServerPort());
        assertEquals("localhost", request.getServerName());
        assertEquals("http", request.getScheme());
        assertEquals("", request.getRequestURI());
        assertEquals(80, request.getRemotePort());
        assertEquals("localhost", request.getRemoteHost());
        assertEquals("HTTP/1.1", request.getProtocol());
        assertEquals("", request.getMethod());
        assertEquals(80, request.getLocalPort());
        assertEquals("localhost", request.getLocalName());
        assertTrue(request.getInputStream() instanceof DelegatingServletInputStream);
        assertEquals(DispatcherType.REQUEST, request.getDispatcherType());
        assertEquals("", request.getContextPath());
        assertEquals(-1L, request.getContentLengthLong());
    }
}

