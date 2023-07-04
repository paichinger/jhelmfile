package com.paichinger.helmfile.runtimes;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paichinger.helmfile.commands.BuildCommand;
import com.paichinger.helmfile.commands.Command;
import com.paichinger.helmfile.commands.TemplateCommand;
import com.paichinger.helmfile.models.build.HelmfileBuild;
import com.paichinger.helmfile.models.template.HelmfileTemplate;

import io.kubernetes.client.openapi.models.V1ClusterRole;
import io.kubernetes.client.openapi.models.V1ClusterRoleBinding;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Ingress;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceAccount;
import io.kubernetes.client.openapi.models.V1StatefulSet;

abstract class Runtime {
	
	protected final String helmfileBinaryPath;
	
	public Runtime(String helmfileBinaryPath) {
		if (helmfileBinaryPath == null || helmfileBinaryPath.isBlank()) {
			this.helmfileBinaryPath = "helmfile";
		} else {
			this.helmfileBinaryPath = helmfileBinaryPath;
		}
		
	}
	public HelmfileBuild build(BuildCommand command) {
		String helmfileCommand = command.generateCommandString(helmfileBinaryPath);
		String helmfileBuildOutput = run(command);
		return unmarshallHelmfileBuildOutput(helmfileBuildOutput);
	}
	
	public HelmfileTemplate template(TemplateCommand command) {
		String helmfileCommand = command.generateCommandString(helmfileBinaryPath);
		String yamlManifests = run(command);
		return unmarshallHelmfileTemplateOutput(yamlManifests);
	}
	
	abstract String run(Command command);
	
	HelmfileBuild unmarshallHelmfileBuildOutput(String commandLineOutput) {
		InputStream in = new ByteArrayInputStream(commandLineOutput.getBytes());
		Yaml yaml = new Yaml();
		for (Object map : yaml.loadAll(in)) {
			ObjectMapper mapper = new ObjectMapper();
			//noinspection rawtypes
			Map build = (LinkedHashMap) map;
			if (build.get("filepath").equals("helmfile.yaml")) {
				return mapper.convertValue(build, HelmfileBuild.class);
			}
		}
		return null;
	}
	
	HelmfileTemplate unmarshallHelmfileTemplateOutput(String yamlManifests) {
		InputStream in = new ByteArrayInputStream(yamlManifests.getBytes());
		Yaml yaml = new Yaml();
		List<V1Service> services = new ArrayList<>();
		List<V1ServiceAccount> serviceAccounts = new ArrayList<>();
		List<V1ClusterRole> clusterRoles = new ArrayList<>();
		List<V1ClusterRoleBinding> clusterRoleBindings = new ArrayList<>();
		List<V1Secret> secrets = new ArrayList<>();
		List<V1Deployment> deployments = new ArrayList<>();
		List<V1ConfigMap> configMaps = new ArrayList<>();
		List<V1Ingress> ingresses = new ArrayList<>();
		List<V1StatefulSet> statefulSets = new ArrayList<>();
		for (Object map : yaml.loadAll(in)) {
			ObjectMapper mapper = new ObjectMapper();
			//noinspection rawtypes
			Map manifest = (LinkedHashMap) map;
			if (manifest.get("kind").equals("Service")) {
				services.add(mapper.convertValue(manifest, V1Service.class));
			}
			if (manifest.get("kind").equals("ServiceAccount")) {
				serviceAccounts.add(mapper.convertValue(manifest, V1ServiceAccount.class));
			}
			if (manifest.get("kind").equals("ClusterRole")) {
				clusterRoles.add(mapper.convertValue(manifest, V1ClusterRole.class));
			}
			if (manifest.get("kind").equals("ClusterRoleBinding")) {
				clusterRoleBindings.add(mapper.convertValue(manifest, V1ClusterRoleBinding.class));
			}
			if (manifest.get("kind").equals("Deployment")) {
				deployments.add(mapper.convertValue(manifest, V1Deployment.class));
			}
			if (manifest.get("kind").equals("Secret")) {
				secrets.add(mapper.convertValue(manifest, V1Secret.class));
			}
			if (manifest.get("kind").equals("ConfigMap")) {
				configMaps.add(mapper.convertValue(manifest, V1ConfigMap.class));
			}
			if (manifest.get("kind").equals("Ingress")) {
				ingresses.add(mapper.convertValue(manifest, V1Ingress.class));
			}
			if (manifest.get("kind").equals("StatefulSet")) {
				statefulSets.add(mapper.convertValue(manifest, V1StatefulSet.class));
			}
		}
		return HelmfileTemplate.builder()
				.services(services)
				.serviceAccounts(serviceAccounts)
				.clusterRoles(clusterRoles)
				.clusterRoleBindings(clusterRoleBindings)
				.secrets(secrets)
				.deployments(deployments)
				.configMaps(configMaps)
				.ingresses(ingresses)
				.statefulSets(statefulSets)
				.build();
	}
}
