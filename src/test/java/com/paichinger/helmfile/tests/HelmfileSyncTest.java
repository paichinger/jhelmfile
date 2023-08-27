package com.paichinger.helmfile.tests;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.paichinger.helmfile.commands.SyncCommand;
import com.paichinger.helmfile.runtimes.BinaryRuntime;

public class HelmfileSyncTest {
	
	@Test
	@DisplayName("Test templating with locally installed helmfile.")
	void testInstalledHelmfile() {
		File helmfileYaml = new File(this.getClass().getResource("/helmfiles/helmfile.yaml").getFile());
		BinaryRuntime runtime = BinaryRuntime.builder().helmfileBinaryPath("helmfile").build();
		SyncCommand command = SyncCommand.builder()
				.helmfileYaml(helmfileYaml)
				.skipDeps(true)
				.build();
		String result = runtime.sync(command);
		assertThat(result, startsWith("Release \"prom-norbac-ubuntu\" has been upgraded. Happy Helming!"));
	}
	
}
