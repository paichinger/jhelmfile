package com.paichinger.helmfile.models.template;

import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.Accessors;

import io.kubernetes.client.openapi.models.V1ClusterRole;
import io.kubernetes.client.openapi.models.V1ClusterRoleBinding;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Ingress;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceAccount;
import io.kubernetes.client.openapi.models.V1StatefulSet;

@Builder
@Getter
@Accessors(fluent = true)
public final class HelmfileTemplate {
	@Default private final List<V1Service> services = Collections.emptyList();
	@Default private final List<V1ServiceAccount> serviceAccounts = Collections.emptyList();
	@Default private final List<V1ClusterRole> clusterRoles = Collections.emptyList();
	@Default private final List<V1ClusterRoleBinding> clusterRoleBindings = Collections.emptyList();
	@Default private final List<V1Secret> secrets = Collections.emptyList();
	@Default private final List<V1Deployment> deployments = Collections.emptyList();
	@Default private final List<V1ConfigMap> configMaps = Collections.emptyList();
	@Default private final List<V1Ingress> ingresses = Collections.emptyList();
	@Default private final List<V1StatefulSet> statefulSets = Collections.emptyList();
}
