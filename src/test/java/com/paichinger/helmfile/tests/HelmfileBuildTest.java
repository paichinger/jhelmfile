package com.paichinger.helmfile.tests;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.paichinger.helmfile.commands.BuildCommand;
import com.paichinger.helmfile.models.build.HelmfileBuild;
import com.paichinger.helmfile.runtimes.CommandLineRuntime;

public class HelmfileBuildTest {
	
	@Test
	@DisplayName("Test build command.")
	void testInstalledHelmfile() {
		File helmfileYaml = new File(this.getClass().getResource("/helmfiles/helmfile.yaml").getFile());
		CommandLineRuntime runtime = CommandLineRuntime.builder().helmfileBinaryPath("helmfile").build();
		BuildCommand command = BuildCommand.builder().helmfileYaml(helmfileYaml).build();
		HelmfileBuild helmfileBuild = runtime.build(command);
		assertThat(helmfileBuild.releases().get(0).name(), equalTo("prom-norbac-ubuntu"));
	}
	
}
