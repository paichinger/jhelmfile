import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.paichinger.helmfile.commands.BuildCommand;
import com.paichinger.helmfile.models.build.HelmfileBuild;
import com.paichinger.helmfile.runtimes.BinaryRuntime;

public class HelmfileDocumentationShowCase {
	@Test
	@DisplayName("Test rbac-create values for testing and production.")
	void testRbacCreate() {
		// Load helmfile.yaml
		File helmfileYaml = new File(this.getClass().getResource("/helmfiles/helmfile.yaml").getFile());
		// Create build commands for different environments
		BuildCommand testingBuild = BuildCommand.builder()
				.helmfileYaml(helmfileYaml)
				.environment("testing")
				.build();
		BuildCommand productionBuild = BuildCommand.builder()
				.helmfileYaml(helmfileYaml)
				.environment("production")
				.build();
		// Create a runtime for helmfile using a binary
		BinaryRuntime runtime = BinaryRuntime.builder().helmfileBinaryPath("helmfile").build();
		// Exectute both build commands and capture the helmfile output
		HelmfileBuild testingResult = runtime.build(testingBuild);
		HelmfileBuild productionResult = runtime.build(productionBuild);
		// Verify if the results are as expected
		assertThat(testingResult.releases().get(0).set().get("rbac.create"), equalTo("true"));
		assertThat(productionResult.releases().get(0).set().get("rbac.create"), equalTo("false"));
	}
	
	private static void verifyBuild(HelmfileBuild build) {
		assertThat(build.releases().get(0).name(), equalTo("prom-norbac-ubuntu"));
	}
}
