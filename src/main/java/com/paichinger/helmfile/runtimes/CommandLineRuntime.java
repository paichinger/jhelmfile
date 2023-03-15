package com.paichinger.helmfile.runtimes;

import static utils.OperatingSystem.OS.WINDOWS;

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

import com.paichinger.helmfile.exceptions.CommandLineException;

import utils.OperatingSystem;

@Slf4j
public class CommandLineRuntime extends Runtime {
	public CommandLineRuntime(String helmfileBinaryPath, String workDir) {
		super(helmfileBinaryPath, workDir);
	}
	
	@Builder
	public static CommandLineRuntime create(String helmfileBinaryPath, String workDir) {
		return new CommandLineRuntime(helmfileBinaryPath, workDir);
	}
	
	@Override
	String run(String command, String workdir) {
		ProcessBuilder builder = new ProcessBuilder();
		if (isWindows()) {
			builder.command("cmd.exe", "/c", command);
		}
		else {
			builder.command("sh", "-c", command);
		}
		builder.directory(new File(workdir));
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
