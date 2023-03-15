package com.paichinger.helmfile.models.build;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HelmfileBuild(
		@JsonProperty("filepath") String filePath,
		Map<String, Environment> environments,
		Map<String, Object> helmDefaults,
		List<Release> releases
) {
}
