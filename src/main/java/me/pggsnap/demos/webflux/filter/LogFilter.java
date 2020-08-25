package me.pggsnap.demos.webflux.filter;

import me.pggsnap.demos.webflux.filter.exchange.LogServerWebExchangeDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author pggsnap
 * @date 2020/8/20
 */
@Component
@Order(100)
public class LogFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        LogServerWebExchangeDecorator logServerWebExchangeDecorator = new LogServerWebExchangeDecorator(exchange);
        return logServerWebExchangeDecorator.logRequest()
                .then(chain.filter(logServerWebExchangeDecorator))
                .doOnSuccess(aVoid -> logServerWebExchangeDecorator.logResponse())
                .doOnError(RuntimeException.class, e -> logger.error("error: {}", e.getMessage()));
    }
}
