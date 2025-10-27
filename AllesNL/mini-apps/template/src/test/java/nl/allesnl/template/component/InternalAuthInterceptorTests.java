package nl.allesnl.template.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternalAuthInterceptorTests {

    @Nested
    @ExtendWith(MockitoExtension.class)
    class UnitTests {

        private static final String EXPECTED_TOKEN = "test-secret";

        private InternalAuthInterceptor interceptor;

        @Mock
        private HttpServletRequest request;

        @Mock
        private HttpServletResponse response;

        @BeforeEach
        void setUp() {
            interceptor = new InternalAuthInterceptor(EXPECTED_TOKEN);
        }

        @Test
        void preHandle_withValidToken_shouldReturnTrue() throws Exception {
            // Given
            when(request.getHeader("X-Internal-Auth")).thenReturn(EXPECTED_TOKEN);

            // When
            boolean result = interceptor.preHandle(request, response, new Object());

            // Then
            assertTrue(result, "Interceptor should allow request with valid token");
            verify(response, never()).setStatus(anyInt());
        }

        @Test
        void preHandle_withInvalidToken_shouldReturnFalseAndSetUnauthorized() throws Exception {
            // Given
            when(request.getHeader("X-Internal-Auth")).thenReturn("wrong-token");

            // When
            boolean result = interceptor.preHandle(request, response, new Object());

            // Then
            assertFalse(result, "Interceptor should block request with invalid token");
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        @Test
        void preHandle_withMissingToken_shouldReturnFalseAndSetUnauthorized() throws Exception {
            // Given
            when(request.getHeader("X-Internal-Auth")).thenReturn(null);

            // When
            boolean result = interceptor.preHandle(request, response, new Object());

            // Then
            assertFalse(result, "Interceptor should block request when token is missing");
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        @Test
        void preHandle_withNullInternalToken_shouldReturnFalseAndSetUnauthorized() throws Exception {
            // Given
            interceptor = new InternalAuthInterceptor(null);
            when(request.getHeader("X-Internal-Auth")).thenReturn(EXPECTED_TOKEN);

            // When
            boolean result = interceptor.preHandle(request, response, new Object());

            // Then
            assertFalse(result, "Interceptor should block request when internal token is null");
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
