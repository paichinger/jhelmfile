package com.paichinger.helmfile.tests;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.paichinger.helmfile.commands.TemplateCommand;
import com.paichinger.helmfile.models.template.HelmfileTemplate;
import com.paichinger.helmfile.runtimes.CommandLineRuntime;
import com.paichinger.helmfile.runtimes.DockerRuntime;

public class HelmfileTemplateTest {
	
	@Test
	@DisplayName("Test templating with locally installed helmfile.")
	void testInstalledHelmfile() {
		String helmfileDirectory = this.getClass().getResource("/helmfiles").getPath();
		CommandLineRuntime runtime = CommandLineRuntime.builder().helmfileBinaryPath("helmfile").workDir(helmfileDirectory).build();
		TemplateCommand command = TemplateCommand.builder()
				.skipDeps(true)
				.build();
		HelmfileTemplate result = runtime.template(command);
		assertThat(result.deployments().get(0).getMetadata().getName(), equalTo("prom-norbac-ubuntu-kube-state-metrics"));
		assertThat(result.services(), hasSize(6));
	}
	
	@Test
	@DisplayName("Test templating with docker-helmfile.")
	void testDockerHelmfile() {
		String helmfileDirectory = this.getClass().getResource("/helmfiles").getPath();
		DockerRuntime runtime = DockerRuntime.builder().helmfileBinaryPath("helmfile").workDir(helmfileDirectory).build();
		TemplateCommand command = TemplateCommand.builder()
				.build();
		HelmfileTemplate result = runtime.template(command);
		assertThat(result.deployments().get(0).getMetadata().getName(), equalTo("prom-norbac-ubuntu-kube-state-metrics"));
		assertThat(result.services(), hasSize(6));
	}
	
}
