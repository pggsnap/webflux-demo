package me.pggsnap.demos.webflux.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author pggsnap
 * @date 2020/8/20
 */
@RestController
@RequestMapping("/testfilter")
public class TestFilterController {
    private static final Logger logger = LoggerFactory.getLogger(TestFilterController.class);

    @GetMapping("/test-ok")
    public Mono<String> testOk() {
        logger.info("invoke method: testOk");
        return Mono.just("ok");
    }

    @GetMapping("/test-error")
    public Mono<String> testError() {
        logger.info("invoke method: testError");
        throw new RuntimeException("error");
    }
}
