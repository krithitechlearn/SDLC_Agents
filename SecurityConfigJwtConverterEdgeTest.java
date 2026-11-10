package com.example.account.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigJwtConverterEdgeTest {

    private JwtAuthenticationConverter buildConverter() throws Exception {
        Method converterMethod = SecurityConfig.class.getDeclaredMethod("jwtAuthenticationConverter");
        converterMethod.setAccessible(true);
        return (JwtAuthenticationConverter) converterMethod.invoke(new SecurityConfig());
    }

    @Test
    void should_createAuthoritiesForEachScopeEntry() throws Exception {
        // Arrange
        JwtAuthenticationConverter converter = buildConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "account.balance.read transactions.read profile.basic")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(90))
                .build();

        // Act
        AbstractAuthenticationToken authentication = (AbstractAuthenticationToken) converter.convert(jwt);

        // Assert
        assertNotNull(authentication, "Authentication token must not be null");
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        assertTrue(authorities.contains("SCOPE_account.balance.read"));
        assertTrue(authorities.contains("SCOPE_transactions.read"));
        assertTrue(authorities.contains("SCOPE_profile.basic"));
    }

    @Test
    void should_handleScpClaimProvidedAsCollection() throws Exception {
        // Arrange
        JwtAuthenticationConverter converter = buildConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scp", List.of("account.balance.read"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(120))
                .build();

        // Act
        AbstractAuthenticationToken authentication = (AbstractAuthenticationToken) converter.convert(jwt);

        // Assert
        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> "SCOPE_account.balance.read".equals(authority.getAuthority())),
                "scp claim should also be converted into scope-prefixed authorities");
    }

    @Test
    void should_returnEmptyAuthorities_whenScopeClaimsMissing() throws Exception {
        // Arrange
        JwtAuthenticationConverter converter = buildConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        // Act
        AbstractAuthenticationToken authentication = (AbstractAuthenticationToken) converter.convert(jwt);

        // Assert
        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().isEmpty(),
                "Missing scope/scope claims must produce zero granted authorities to prevent over-permission");
    }
}
