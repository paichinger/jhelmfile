package com.paichinger.helmfile.commands;


import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
public class SyncCommand implements Command {
	private static final CommandUtils UTILS = new CommandUtils();
	@Builder.Default private final Map<String, String> stateValuesSet = Collections.emptyMap();
	@Singular private final List<File> stateValuesFiles;
	private final String environment;
	@Singular private final List<Selector> selectors;
	private final boolean skipDeps;
	private final boolean skipNeeds;
	private final boolean includeTransitiveNeeds;
	@Getter private final File helmfileYaml;
	@Builder.Default private final String logLevel = "ERROR";
	
	@Override
	public String generateCommandString(String helmfileBinaryPath) {
		return String.format("%s %s%s %s",
				helmfileBinaryPath,
				"sync --log-level=",
				logLevel,
				String.join(" ", generateHelmfileCommandLineParameters()));
	}
	
	private List<String> generateHelmfileCommandLineParameters() {
		List<String> parameters = UTILS.processBasicParameters(stateValuesSet, stateValuesFiles, environment, helmfileYaml.getName());
		if (selectors != null && !selectors.isEmpty()) {
			String selectorParameters = Optional
					.of(selectors)
					.get()
					.stream()
					.map(Selector::generateParameterString)
					.collect(Collectors.joining(" "));
			parameters.add(selectorParameters);
		}
		parameters.add(skipDeps ? "--skip-deps" : "");
		parameters.add(skipNeeds ? "--skip-needs" : "");
		parameters.add(includeTransitiveNeeds ? "--include-transitive-needs" : "");
		return parameters;
	}
	
}
