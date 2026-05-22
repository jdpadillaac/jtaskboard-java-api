package com.jdpadillac.jtaskboard.auth.domain.model;

/**
 * Result of a successful registration or login: the issued JWT plus the authenticated user.
 */
public record AuthSession(String token, User user) {
}
