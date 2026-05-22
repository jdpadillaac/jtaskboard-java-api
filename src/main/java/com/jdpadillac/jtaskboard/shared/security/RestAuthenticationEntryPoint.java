package com.jdpadillac.jtaskboard.shared.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Returns a JSON 401 response (matching the global error shape) when an
 * unauthenticated request hits a protected endpoint.
 *
 * <p>The body is rendered without an {@code ObjectMapper} dependency so the bean can be
 * built early during the security configuration phase.
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String body = """
                {"timestamp":"%s","status":401,"error":"Unauthorized",\
                "message":"Authentication required: provide a valid Bearer token"}\
                """.formatted(Instant.now());

        response.getWriter().write(body);
    }
}
