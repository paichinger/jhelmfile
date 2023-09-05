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
		TemplateCommand templateCommand = TemplateCommand.builder()
				.helmfileYaml(helmfileYaml)
				.build();
		HelmfileTemplate templateOutput = runtime.template(templateCommand);
		verifyTemplate(templateOutput);
		DockerRuntime dockerRuntime = DockerRuntime.builder().dockerHost("unix:///var/run/docker.sock").helmfileBinaryPath("helmfile").build();
		templateOutput = dockerRuntime.template(templateCommand);
		verifyTemplate(templateOutput);
	}
	
	private static void verifyTemplate(HelmfileTemplate template) {
		assertThat(template.deployments().get(0).getMetadata().getName(), equalTo("prom-norbac-ubuntu-kube-state-metrics"));
		assertThat(template.services(), hasSize(6));
	}
}
