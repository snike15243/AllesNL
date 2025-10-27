package nl.allesnl.template.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InternalAuthInterceptor implements HandlerInterceptor {
    private final String internalAuthToken;

    InternalAuthInterceptor(String internalAuthToken) {
        this.internalAuthToken = internalAuthToken;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("X-Internal-Auth");

        if (internalAuthToken != null && internalAuthToken.equals(token)) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
