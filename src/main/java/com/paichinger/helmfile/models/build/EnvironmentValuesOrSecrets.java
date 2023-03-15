package com.paichinger.helmfile.models.build;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EnvironmentValuesDeserializer.class)
public record EnvironmentValuesOrSecrets(
		List<String> valueFiles,
		Map<String, Object> values
) {
}
