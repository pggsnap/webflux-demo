package me.pggsnap.demos.webflux.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author pggsnap
 * @date 2020/4/27
 */
public class IgnoreSentisiveContentSerializer extends JsonSerializer {

    public IgnoreSentisiveContentSerializer() {}

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNull();
    }
}