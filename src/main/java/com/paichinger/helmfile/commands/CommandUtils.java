package com.paichinger.helmfile.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class CommandUtils {
	
	List<String> processBasicParameters(Map<String, String> stateValuesSet, List<File> stateValuesFiles, String environment, String helmfileYaml) {
		List<String> parameters = new ArrayList<>();
		if (stateValuesSet != null && !stateValuesSet.isEmpty()) {
			parameters.add(Optional
					.of(stateValuesSet)
					.get()
					.entrySet()
					.stream()
					.filter(e -> !e.getValue().isEmpty())
					.map(e -> String.format("--state-values-set %s=%s", e.getKey(), e.getValue()))
					.collect(Collectors.joining(" ")));
		}
		if (stateValuesFiles != null && !stateValuesFiles.isEmpty()) {
			parameters.add(Optional
					.of(stateValuesFiles)
					.get()
					.stream()
					.map(f -> String.format("--state-values-file %s", f.getAbsolutePath()))
					.collect(Collectors.joining(" ")));
		}
		parameters.add(environment != null && environment.length() > 0 ? "-e " + environment : "");
		parameters.add("-f " + helmfileYaml);
		return parameters;
	}
	
}
