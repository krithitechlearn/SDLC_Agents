package com.example.account.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigConverterTest {

    private JwtAuthenticationConverter createConverter() throws Exception {
        Method method = SecurityConfig.class.getDeclaredMethod("jwtAuthenticationConverter");
        method.setAccessible(true);
        return (JwtAuthenticationConverter) method.invoke(new SecurityConfig());
    }

    @Test
    void should_generateScopeAuthoritiesFromSpaceSeparatedScopeClaim() throws Exception {
        // Arrange
        JwtAuthenticationConverter converter = createConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "account.balance.read transactions.read")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(120))
                .build();

        // Act
        AbstractAuthenticationToken authentication = (AbstractAuthenticationToken) converter.convert(jwt);

        // Assert
        assertNotNull(authentication);
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        assertTrue(authorities.contains("SCOPE_account.balance.read"));
        assertTrue(authorities.contains("SCOPE_transactions.read"));
    }

    @Test
    void should_notGrantAuthorities_whenScopeClaimMissing() throws Exception {
        // Arrange
        JwtAuthenticationConverter converter = createConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(120))
                .build();

        // Act
        AbstractAuthenticationToken authentication = (AbstractAuthenticationToken) converter.convert(jwt);

        // Assert
        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().isEmpty(),
                "Missing scope claim should produce zero granted authorities");
    }
}
