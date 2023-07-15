package com.asledz.kancelaria_prawnicza.controller;

import com.asledz.kancelaria_prawnicza.domain.Role;
import com.asledz.kancelaria_prawnicza.dto.UserAuthorities;
import com.asledz.kancelaria_prawnicza.exception.ForbiddenException;
import com.asledz.kancelaria_prawnicza.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Clock;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController {
    private final UserService userService;
    private final Clock clock;

    @GetMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader(AUTHORIZATION);
        String refreshHeader = request.getHeader("Refresh");
        if (authHeader != null && refreshHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String accessToken = authHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(System.getenv("SECRETKEY").getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                try {
                    verifier.verify(accessToken);
                } catch (TokenExpiredException e) {
                    //ignore, because this function is here because it expired.
                }
                DecodedJWT decodedJWT = verifier.verify(refreshHeader);
                String email = decodedJWT.getSubject();
                UserAuthorities user = userService.getUserDetailsByEmail(email);
                String newAccessToken = JWT.create()
                        .withSubject(user.email())
                        .withExpiresAt(new Date(clock.millis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.roles().stream().map(Role::getName).toList())
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", newAccessToken);
                tokens.put("refresh_token", refreshHeader);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                throw new ForbiddenException("JWT verification didn't pass, with message: %s".formatted(e.getMessage()));
            }
        } else {
            throw new ForbiddenException("Refresh token is missing");
        }
    }
}
