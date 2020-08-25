package me.pggsnap.demos.webflux.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

/**
 * @author pggsnap
 * @date 2020/8/24
 */
@RestControllerAdvice
public class RuntimeHandler {
    @Autowired
    private ObjectMapper om;

//    @ExceptionHandler(value = RuntimeException.class)
    public Mono handleRuntimeException(RuntimeException e) {
//        throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        return Mono.just(om.createObjectNode().put("msg", e.getMessage()));
    }
}
