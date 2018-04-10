package com.accountservice.helper;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonHelper {
	public static class DoubleDesirializer extends JsonSerializer<Double> {

		@Override
		public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers)
				throws IOException, JsonProcessingException {
			BigDecimal d = new BigDecimal(value);
			gen.writeNumber(String.format("%.6f", d));
		}

    }
}
