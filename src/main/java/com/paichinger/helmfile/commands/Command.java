package com.paichinger.helmfile.commands;

public interface Command {
	String generateCommandString(String helmfileBinaryPath);
}
