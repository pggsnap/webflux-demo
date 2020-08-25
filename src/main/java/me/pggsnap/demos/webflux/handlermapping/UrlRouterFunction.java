package me.pggsnap.demos.webflux.handlermapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author pggsnap
 * @date 2020/8/22
 */
@RestController
@RequestMapping("/testrouter")
public class UrlRouterFunction implements RouterFunction {
    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("RequestMappingHandlerMapping");
    }

    @Override
    public Mono<HandlerFunction> route(ServerRequest request) {
        HandlerFunction urlHandler = req -> ServerResponse.ok().bodyValue("RouterFunctionMapping");

        if (request.uri().getPath().equals("/testrouter/test")) {
            return Mono.defer(() -> Mono.just(urlHandler));
        } else {
            return Mono.empty();
        }
    }
}