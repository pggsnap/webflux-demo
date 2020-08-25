package me.pggsnap.demos.webflux.filter.exchange;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.commons.io.IOUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author pggsnap
 * @date 2020/8/25
 */
public class LogServerWebExchangeDecorator extends ServerWebExchangeDecorator {
    private static final Logger logger = LoggerFactory.getLogger(LogServerWebExchangeDecorator.class);

    private LogServerHttpRequestDecorator requestDecorator;

    private LogServerHttpResponseDecorator responseDecorator;

    public LogServerWebExchangeDecorator(ServerWebExchange delegate) {
        super(delegate);
        this.requestDecorator = new LogServerHttpRequestDecorator(delegate.getRequest());
        this.responseDecorator = new LogServerHttpResponseDecorator(delegate.getResponse());
    }

    @Override
    public ServerHttpRequest getRequest() {
        return requestDecorator;
    }

    @Override
    public ServerHttpResponse getResponse() {
        return responseDecorator;
    }

    static class LogServerHttpRequestDecorator extends ServerHttpRequestDecorator {
        private ByteBuf byteBuf;
        private boolean copy = false;
        private boolean first = true;

        public LogServerHttpRequestDecorator(ServerHttpRequest delegate) {
            super(delegate);
            this.byteBuf = Unpooled.buffer();
        }

        @Override
        public Flux<DataBuffer> getBody() {
            if (copy) {
                if (first) {
                    first = false;
                    Flux<DataBuffer> flux = super.getBody();
                    return flux.map(this::cache);
                } else {
                    return Flux.just(getBodyMore());
                }
            } else {
                return super.getBody();
            }
        }

        public void setCopy(boolean copy) {
            this.copy = copy;
        }

        private DataBuffer getBodyMore() {
            NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
            DataBuffer bodyDataBuffer = nettyDataBufferFactory.wrap(byteBuf);
            return bodyDataBuffer;
        }

        private DataBuffer cache(DataBuffer buffer) {
            try {
                byte[] bytes = IOUtils.toByteArray(buffer.asInputStream());
                byteBuf.writeBytes(bytes);
                DataBufferUtils.release(buffer);
                NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
                return nettyDataBufferFactory.wrap(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    static class LogServerHttpResponseDecorator extends ServerHttpResponseDecorator {
        private ByteBuf byteBuf;

        public LogServerHttpResponseDecorator(ServerHttpResponse delegate) {
            super(delegate);
            this.byteBuf = Unpooled.buffer();
        }

        public String getBody() {
            return byteBuf.toString(Charset.forName("UTF-8"));
        }

        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            if (body instanceof Mono) {
                return getDelegate().writeWith(
                        ((Mono<? extends DataBuffer>) body)
                                .map(b -> cache(b))
                );
            } else if (body instanceof Flux) {
                return getDelegate().writeWith(
                        ((Flux<? extends DataBuffer>) body)
                                .map(b -> cache(b))
                );
            }
            return getDelegate().writeWith(body);
        }

        @Override
        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
            return getDelegate().writeAndFlushWith(body);
        }

        private DataBuffer cache(DataBuffer buffer) {
            try {
                byte[] bytes = IOUtils.toByteArray(buffer.asInputStream());
                byteBuf.writeBytes(bytes);
                DataBufferUtils.release(buffer);
                NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
                return nettyDataBufferFactory.wrap(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static final List<MediaType> LogMediaTypes = new ArrayList<>(Arrays.asList(
            MediaType.TEXT_PLAIN,
            MediaType.TEXT_XML,
            MediaType.APPLICATION_JSON)
    );

    public Mono<String> logRequest() {
        String ip = requestDecorator.getRemoteAddress().getAddress().getHostAddress();
        String uri = requestDecorator.getURI().getPath();
        MediaType contentType = requestDecorator.getHeaders().getContentType();
        this.getAttributes().put("receive-time", Instant.now());
        this.getAttributes().put("ip", ip);
        this.getAttributes().put("uri", uri);
        StringBuffer sb = new StringBuffer();
        sb.append(">>> ").append(this.getLogPrefix())
                .append("ip: " + this.getAttribute("ip") + ", ")
                .append("uri: " + this.getAttribute("uri") + ", ");
        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            requestDecorator.setCopy(true);
            return requestDecorator.getBody()
                    .collectList()
                    .map(dataBuffers -> {
                        ByteBuf byteBuf = Unpooled.buffer();
                        dataBuffers.forEach(buffer -> {
                            try {
                                byteBuf.writeBytes(IOUtils.toByteArray(buffer.asInputStream()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        return byteBuf.toString(Charset.forName("UTF-8"));
                    })
                    .doOnSuccess(body -> logger.info(sb.append("body: " + body).toString()));

        } else if (contentType == null || LogMediaTypes.contains(contentType)) {
            MultiValueMap<String, String> queryParams = requestDecorator.getQueryParams();
            if (!queryParams.isEmpty()) {
                sb.append("queryParams: " + queryParams);
            }
            logger.info(sb.toString());
        }

        return Mono.empty();
    }

    public void logResponse() {
        StringBuffer sb = new StringBuffer();
        sb.append("<<< ").append(this.getLogPrefix())
                .append("ip: " + this.getAttribute("ip") + ", ")
                .append("uri: " + this.getAttribute("uri") + ", ");
        logger.info(sb.append("result: " + responseDecorator.getBody()).toString());
    }
}
