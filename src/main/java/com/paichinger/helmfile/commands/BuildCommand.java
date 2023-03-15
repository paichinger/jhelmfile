package com.paichinger.helmfile.commands;

import java.io.File;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Singular;

@Builder
public class BuildCommand implements Command {
	private Map<String, String> stateValuesSet;
	@Singular private final List<File> stateValuesFiles;
	private final String environment;
	private static final CommandUtils utils = new CommandUtils();
	
	public BuildCommand(Map<String, String> stateValuesSet, List<File> stateValuesFiles, String environment) {
		this.stateValuesSet = stateValuesSet;
		this.stateValuesFiles = stateValuesFiles;
		this.environment = environment;
	}
	
	@Override
	public String generateCommandString(String helmfileBinaryPath) {
		return String.format("%s %s %s",
				helmfileBinaryPath,
				"build --log-level=ERROR",
				String.join(" ", utils.processBasicParameters(stateValuesSet, stateValuesFiles, environment)));
	}
	
}
