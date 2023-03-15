package com.paichinger.helmfile.models.build;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Release(
		String name,
		String chart,
		String namespace,
		List<SetValues> set
) {

}
