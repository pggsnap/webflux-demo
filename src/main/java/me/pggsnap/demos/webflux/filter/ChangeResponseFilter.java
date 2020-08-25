package me.pggsnap.demos.webflux.filter;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

/**
 * @author pggsnap
 * @date 2020/8/21
 */
//@Component
//@Order(0)
//public class ChangeResponseFilter implements WebFilter {
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        return chain.filter(exchange)
//                .then(chain.filter(exchange.mutate().response(new ServerHttpResponse).build()));
////                .doOnSuccess(aVoid -> {
////                    ServerHttpResponse resp = exchange.getResponse();
////                    resp.setRawStatusCode(401);
////                    System.out.println(exchange.getResponse().getRawStatusCode() + "");
////                });
//    }
//}
