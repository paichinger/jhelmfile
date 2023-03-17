package com.paichinger.helmfile.commands;

import java.io.File;

public interface Command {
	File getHelmfileYaml();
	String generateCommandString(String helmfileBinaryPath);
}
