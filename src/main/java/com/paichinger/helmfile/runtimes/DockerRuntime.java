package com.paichinger.helmfile.runtimes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;

import com.paichinger.helmfile.commands.Command;
import com.paichinger.helmfile.utils.OperatingSystem;

@Slf4j
public class DockerRuntime extends Runtime {
	private static final String DEFAULT_IMAGE_REPOSITORY = "ghcr.io/helmfile/helmfile";
	private static final String DEFAULT_IMAGE_TAG = "v0.156.0";
	private final String imageRepository;
	private final String imageTag;
	private final String dockerHost;
	
	public DockerRuntime(String helmfileBinaryPath, String dockerHost, String imageRepository, String imageTag) {
		super(helmfileBinaryPath);
		this.dockerHost = dockerHost;
		this.imageRepository = imageRepository;
		this.imageTag = imageTag;
	}
	
	@Builder
	public static DockerRuntime create(String helmfileBinaryPath, String dockerHost, String imageRepository, String imageTag) {
		String nonNullDockerHost = isNotEmpty(dockerHost) ? dockerHost : getDefaultDockerHost();
		String nonNullImageRepository = isNotEmpty(imageRepository) ? imageRepository : DEFAULT_IMAGE_REPOSITORY;
		String nonNullImageTag = isNotEmpty(imageTag) ? imageTag : DEFAULT_IMAGE_TAG;
		return new DockerRuntime(helmfileBinaryPath, nonNullDockerHost, nonNullImageRepository, nonNullImageTag);
	}
	
	@Override
	String run(Command command) {
		DockerClient dockerClient = createDockerClient();
		pullDockerImage(dockerClient);
		CreateContainerResponse container = createDockerContainer(command, dockerClient);
		dockerClient.startContainerCmd(container.getId()).exec();
		ByteArrayOutputStream logStream = collectContainerLogs(dockerClient, container);
		dockerClient.removeContainerCmd(container.getId());
		return logStream.toString();
	}
	
	@NotNull
	private static ByteArrayOutputStream collectContainerLogs(DockerClient dockerClient, CreateContainerResponse container) {
		ByteArrayOutputStream logStream = new ByteArrayOutputStream();
		LogContainerCmd logContainerCmd = dockerClient.logContainerCmd(container.getId());
		logContainerCmd.withStdOut(true).withStdErr(true).withTail(100).withFollowStream(true);
		
		try {
			logContainerCmd.exec(new LogContainerResultCallback() {
				@Override
				public void onNext(Frame item) {
					try {
						logStream.write(item.getPayload());
					}
					catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}).awaitCompletion();
		}
		catch (InterruptedException e) {
			log.error("Interrupted Exception!" + e.getMessage());
		}
		return logStream;
	}
	
	private CreateContainerResponse createDockerContainer(Command command, DockerClient dockerClient) {
		return dockerClient.createContainerCmd(String.format("%s:%s", imageRepository, imageTag))
				.withHostConfig(HostConfig.newHostConfig()
						.withNetworkMode("host")
						.withBinds(List.of(
								Bind.parse(System.getenv("HOME") + "/.kube:/root/.kube"),
								Bind.parse(System.getenv("HOME") + "/.config/helm:/root/.config/helm"),
								Bind.parse(command.getHelmfileYaml().getParent() + ":/wd"))))
				.withWorkingDir("/wd")
				.withCmd(command.generateCommandString(helmfileBinaryPath).split(" "))
				.withTty(true)
				.exec();
	}
	
	private void pullDockerImage(DockerClient dockerClient) {
		try {
			dockerClient.pullImageCmd(imageRepository)
					.withTag(imageTag)
					.exec(new PullImageResultCallback())
					.awaitCompletion(120, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	private DockerClient createDockerClient() {
		DockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
		DockerHttpClient dockerHttpClient;
		try {
			dockerHttpClient = new ZerodepDockerHttpClient.Builder().dockerHost(new URI(dockerHost)).build();
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient);
	}
	
	private static boolean isNotEmpty(String s) {
		return s != null && !s.trim().isEmpty();
	}
	
	private static String getDefaultDockerHost() {
		return switch (OperatingSystem.getOS()) {
			case LINUX -> "unix:///var/run/docker.sock";
			case MAC, WINDOWS -> "tcp://localhost:2376";
			default -> throw new UnsupportedOperationException("No supported OS detected.");
		};
	}
}
