package com.paichinger.helmfile.tests;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.paichinger.helmfile.commands.TemplateCommand;
import com.paichinger.helmfile.models.template.HelmfileTemplate;
import com.paichinger.helmfile.runtimes.BinaryRuntime;
import com.paichinger.helmfile.runtimes.DockerRuntime;

public class HelmfileTemplateTest {
	
	@Test
	@DisplayName("Test templating with locally installed helmfile.")
	void testInstalledHelmfile() {
		File helmfileYaml = new File(this.getClass().getResource("/helmfiles/helmfile.yaml").getFile());
		BinaryRuntime runtime = BinaryRuntime.builder().helmfileBinaryPath("helmfile").build();
		TemplateCommand command = TemplateCommand.builder()
				.helmfileYaml(helmfileYaml)
				.skipDeps(true)
				.build();
		HelmfileTemplate result = runtime.template(command);
		assertThat(result.deployments().get(0).getMetadata().getName(), equalTo("prom-norbac-ubuntu-kube-state-metrics"));
		assertThat(result.services(), hasSize(6));
	}
	
	@Test
	@DisplayName("Test templating with docker-helmfile.")
	void testDockerHelmfile() {
		File helmfileYaml = new File(this.getClass().getResource("/helmfiles/helmfile.yaml").getFile());
		DockerRuntime runtime = DockerRuntime
				.builder()
				.dockerHost("unix:///var/run/docker.sock")
				.imageRepository("ghcr.io/helmfile/helmfile")
				.imageTag("v0.156.0")
				.helmfileBinaryPath("helmfile")
				.build();
		TemplateCommand command = TemplateCommand.builder()
				.helmfileYaml(helmfileYaml)
				.build();
		HelmfileTemplate result = runtime.template(command);
		assertThat(result.deployments().get(0).getMetadata().getName(), equalTo("prom-norbac-ubuntu-kube-state-metrics"));
		assertThat(result.services(), hasSize(6));
	}
	
}
