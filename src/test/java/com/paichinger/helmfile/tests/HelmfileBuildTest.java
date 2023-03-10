package com.paichinger.helmfile.tests;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.paichinger.helmfile.commands.BuildCommand;
import com.paichinger.helmfile.commands.TemplateCommand;
import com.paichinger.helmfile.models.build.HelmfileBuild;
import com.paichinger.helmfile.models.template.HelmfileTemplate;
import com.paichinger.helmfile.runtimes.CommandLineRuntime;
import com.paichinger.helmfile.runtimes.DockerRuntime;

public class HelmfileBuildTest {
	
	@Test
	@DisplayName("Test build command.")
	void testInstalledHelmfile() {
		String helmfileDirectory = this.getClass().getResource("/helmfiles").getPath();
		CommandLineRuntime runtime = CommandLineRuntime.builder().helmfileBinaryPath("helmfile").workDir(helmfileDirectory).build();
		BuildCommand command = BuildCommand.builder().build();
		HelmfileBuild helmfileBuild = runtime.build(command);
		assertThat(helmfileBuild.releases().get(0).name(), equalTo("prom-norbac-ubuntu"));
	}
	
}
