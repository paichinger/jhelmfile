package com.paichinger.helmfile.commands;

public record Selector(
		String key,
		boolean equals,
		String value
) {
	public String generateParameterString() {
		return String.format("-l %s%s%s", key, equals ? "=" : "!=", value);
	}
}
