package top.gosleep.blog.context;

import top.gosleep.blog.dto.UserDto;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class UserContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpSession session = req.getSession(true);
        try {
            AtomicReference<UserDto> ref = (AtomicReference<UserDto>) session.getAttribute(SessionKey.USER.getKey());
            if (ref == null) {
                ref = new AtomicReference<>();
                session.setAttribute(SessionKey.USER.getKey(), ref);
            }
            UserContext.init(ref);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.clear();
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
