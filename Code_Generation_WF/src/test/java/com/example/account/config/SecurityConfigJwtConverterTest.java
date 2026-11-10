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

class SecurityConfigJwtConverterTest {

    private JwtAuthenticationConverter buildConverter() throws Exception {
        Method converterMethod = SecurityConfig.class.getDeclaredMethod("jwtAuthenticationConverter");
        converterMethod.setAccessible(true);
        return (JwtAuthenticationConverter) converterMethod.invoke(new SecurityConfig());
    }

    @Test
    void should_prefixAuthoritiesWithScope() throws Exception {
        JwtAuthenticationConverter converter = buildConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "account.balance.read transactions.read")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        AbstractAuthenticationToken authentication = (AbstractAuthenticationToken) converter.convert(jwt);

        assertNotNull(authentication, "Authentication token must not be null");
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        assertTrue(authorities.contains("SCOPE_account.balance.read"));
        assertTrue(authorities.contains("SCOPE_transactions.read"));
    }

    @Test
    void should_returnEmptyAuthorities_whenScopeClaimMissing() throws Exception {
        JwtAuthenticationConverter converter = buildConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();

        AbstractAuthenticationToken authentication = (AbstractAuthenticationToken) converter.convert(jwt);

        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().isEmpty(),
                "Missing scope claim should result in zero granted authorities");
    }
}
