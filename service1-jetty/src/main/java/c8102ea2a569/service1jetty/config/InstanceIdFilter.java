package c8102ea2a569.service1jetty.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Добавляет в ответ заголовок X-Instance-Id и пишет в лог идентификатор инстанса для каждого запроса.
 * По нему видно, какой контейнер обработал запрос (распределение нагрузки).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InstanceIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(InstanceIdFilter.class);

    @Value("${INSTANCE_TAG:${server.port:unknown}}")
    private String instanceId;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("X-Instance-Id", instanceId);
        log.info("[{}] {} {}", instanceId, request.getMethod(), request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
