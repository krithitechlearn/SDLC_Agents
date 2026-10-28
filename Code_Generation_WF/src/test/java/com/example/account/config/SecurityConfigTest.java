package com.example.account.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.lang.reflect.Method;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    @Test
    void should_applyScopePrefixToJwtAuthorities() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig();
        Method converterMethod = SecurityConfig.class.getDeclaredMethod("jwtAuthenticationConverter");
        converterMethod.setAccessible(true);
        JwtAuthenticationConverter converter = (JwtAuthenticationConverter) converterMethod.invoke(securityConfig);

        Instant now = Instant.now();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "account.balance.read")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .build();

        AbstractAuthenticationToken authentication = (AbstractAuthenticationToken) converter.convert(jwt);

        assertNotNull(authentication, "Authentication token should not be null");
        assertTrue(
                authentication.getAuthorities().stream()
                        .anyMatch(authority -> "SCOPE_account.balance.read".equals(authority.getAuthority())),
                "Expected SCOPE-prefixed authority to be present");
    }
}
