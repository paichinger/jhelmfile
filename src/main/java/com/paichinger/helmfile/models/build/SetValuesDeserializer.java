package com.paichinger.helmfile.models.build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class SetValuesDeserializer extends StdDeserializer<Map<String, String>> {
	
	protected SetValuesDeserializer() {
		this(null);
	}
	
	protected SetValuesDeserializer(Class<?> vc) {
		super(vc);
	}
	
	@Override
	public Map<String, String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode node = p.getCodec().readTree(p);
		List<String> valueFiles = new ArrayList<>();
		Map<String, String> setValues = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		for (Iterator<JsonNode> it = node.elements(); it.hasNext(); ) {
			JsonNode child = it.next();
			if (child.isTextual()) {
				valueFiles.add(child.asText());
			}
			if (child.isObject()) {
				Map<String, Object> value = mapper.convertValue(child, new TypeReference<>() {});
				setValues.put(child.get("name").asText(), child.get("value").asText());
			}
		}
		return setValues;
	}
}
