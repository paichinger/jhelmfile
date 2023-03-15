package com.paichinger.helmfile.models.build;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Environment(
		EnvironmentValuesOrSecrets values,
		EnvironmentValuesOrSecrets secrets
) {
}
