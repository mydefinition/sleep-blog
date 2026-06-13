package top.gosleep.blog.common.context;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import top.gosleep.blog.bean.dto.UserDTO;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SessionContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpSession session = req.getSession(true);
        try {
            SessionContext.init(session);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            SessionContext.clear();
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
