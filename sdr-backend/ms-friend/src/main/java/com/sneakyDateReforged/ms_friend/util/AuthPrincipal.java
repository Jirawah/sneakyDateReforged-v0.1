package com.sneakyDateReforged.ms_friend.util;

import org.springframework.security.core.Authentication;

public final class AuthPrincipal {
    private AuthPrincipal(){}

    public static Long currentUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null)
            throw new IllegalStateException("Utilisateur non authentifié");
        Object p = auth.getPrincipal();
        if (p instanceof Long l) return l;
        if (p instanceof String s) return Long.parseLong(s);
        throw new IllegalStateException("Principal non supporté: " + p);
    }
}
