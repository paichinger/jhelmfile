import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.paichinger.helmfile.commands.BuildCommand;
import com.paichinger.helmfile.models.build.HelmfileBuild;
import com.paichinger.helmfile.runtimes.BinaryRuntime;
import com.paichinger.helmfile.runtimes.DockerRuntime;

public class HelmfileBuildTest {
	@Test
	@DisplayName("Simple test for the helmfile-build using binary and docker.")
	void testSimpleBuild() {
		File helmfileYaml = new File(this.getClass().getResource("/helmfiles/helmfile.yaml").getFile());
		BuildCommand buildCommand = BuildCommand
				.builder()
				.helmfileYaml(helmfileYaml)
				.build();
		BinaryRuntime binaryRuntime = BinaryRuntime.builder().helmfileBinaryPath("helmfile").build();
		HelmfileBuild buildResult = binaryRuntime.build(buildCommand);
		verifyBuild(buildResult);
		DockerRuntime dockerRuntime = DockerRuntime.builder().helmfileBinaryPath("helmfile").build();
		buildResult = dockerRuntime.build(buildCommand);
		verifyBuild(buildResult);
	}
	
	private static void verifyBuild(HelmfileBuild build) {
		assertThat(build.releases().get(0).name(), equalTo("prom-norbac-ubuntu"));
	}
}
