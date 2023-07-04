package com.paichinger.helmfile.models.build;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Release(
		String name,
		String chart,
		String namespace,
		@JsonDeserialize(using = SetValuesDeserializer.class)
		Map<String, String> set
) {

}
