package com.paichinger.helmfile.runtimes;

import static com.paichinger.helmfile.utils.OperatingSystem.OS.WINDOWS;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import com.paichinger.helmfile.commands.Command;
import com.paichinger.helmfile.exceptions.CommandLineException;

import com.paichinger.helmfile.utils.OperatingSystem;

@Slf4j
public class BinaryRuntime extends Runtime {
	public BinaryRuntime(String helmfileBinaryPath, String workDir) {
		super(helmfileBinaryPath);
	}
	
	@Builder
	public static BinaryRuntime create(String helmfileBinaryPath, String workDir) {
		return new BinaryRuntime(helmfileBinaryPath, workDir);
	}
	
	@Override
	String run(Command command) {
		ProcessBuilder builder = new ProcessBuilder();
		if (isWindows()) {
			builder.command("cmd.exe", "/c", command.generateCommandString(helmfileBinaryPath));
		}
		else {
			builder.command("sh", "-c", command.generateCommandString(helmfileBinaryPath));
		}
		builder.directory(new File(command.getHelmfileYaml().getParent()));
		try {
			Process process = builder.start();
			StringBuilder output = new StringBuilder();
			StreamGobbler streamGobbler =
					new StreamGobbler(process.getInputStream(), s -> output.append(s).append("\n"));
			StreamGobbler errorStreamGobbler =
					new StreamGobbler(process.getErrorStream(), s -> output.append(s).append("\n"));
			Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
			Future<?> errorFuture = Executors.newSingleThreadExecutor().submit(errorStreamGobbler);
			int exitCode = process.waitFor();
			if (exitCode == 0) {
				future.get(10, TimeUnit.SECONDS);
				return output.toString();
			}
			else {
				errorFuture.get(10, TimeUnit.SECONDS);
				log.error(output.toString());
				throw new CommandLineException(exitCode, output.toString());
			}
		}
		catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static boolean isWindows() {
		return OperatingSystem.getOS().equals(WINDOWS);
	}
	
	private record StreamGobbler(InputStream inputStream, Consumer<String> consumer) implements Runnable {
		@Override
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines()
					.forEach(consumer);
		}
	}
}
