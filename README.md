# jhelmfile
A Java runner for helmfile.
## What is it
jhelmfile is a java library which can be used to run the [helmfile](https://github.com/helmfile/helmfile) cli tool. 
It offers a convenient API to create and execute helmfile commands like `build`, `template`, or `sync`. 
The commands can be executed using a runtime which can be whether a locally installed helmfile binary or a docker image containing helmfile. 
The output of helmfile is mapped to java objects which can then be used for further processing. 
For example, you could run verifications against them, as you can see in the [example project](https://github.com/paichinger/jhelmfile/tree/main/examples) which uses jhelmfile to test a helmfile using junit.
## Usage
### Maven
```xml
<dependency>
    <groupId>io.github.paichinger</groupId>
    <artifactId>jhelmfile</artifactId>
    <version>1.12</version>
</dependency>
```
### Gradle
```groovy
implementation 'io.github.paichinger:jhelmfile:1.12'
```
## Commands
For each supported helmfile command (`build`, `template` and `sync`) you can find a builder. See here:
### build
The `build` command currently supports the following parameters:
- `--environment`
- `--state-values-set`
- `--state-values-file`

Here an example with the corresponding builder methods:
```java
BuildCommand buildCommand = BuildCommand
  .builder()
  .helmfileYaml(new File("./my-project/helmfile.yaml"))  // mandatory
  .environment("acceptance") // optional
  .stateValuesFiles(List.of(new File("common-values.yaml"), new File("common-values.yaml"))) // optional
  .stateValuesSet(Map.of("myService.version", "v1.2", "myService.logLevel", "DEBUG")) // optional
  .build();
```
### template
The `template` command currently supports the following parameters:
- `--environment`
- `--state-values-set`
- `--state-values-file`
- `--selector`
- `--skip-deps`
- `--skip-needs`
- `--include-transitive-needs`

Here an example with the corresponding builder methods:
```java
TemplateCommand templateCommand = TemplateCommand
  .builder()
  .helmfileYaml(new File("./my-project/helmfile.yaml")) // mandatory
  .environment("acceptance") // optional
  .stateValuesFiles(List.of(new File("common-values.yaml"), new File("common-values.yaml"))) // optional
  .stateValuesSet(Map.of("myService.version", "v1.2", "myService.logLevel", "DEBUG")) // optional
  .selectors(List.of(new Selector("group", true, "progressive"))) // optional
  .skipDeps(true) // optional - default=false
  .skipNeeds(true) // optional - default=false
  .includeTransitiveNeeds(true) // optional - default=false
  .build();
```
### sync
The `sync` command currently supports the following parameters:
- `--environment`
- `--state-values-set`
- `--state-values-file`
- `--selector`
- `--skip-deps`
- `--skip-needs`
- `--include-transitive-needs`

Here an example with the corresponding builder methods:
```java
SyncCommand templateCommand = SyncCommand
  .builder()
  .helmfileYaml(new File("./my-project/helmfile.yaml")) // mandatory
  .environment("acceptance") // optional
  .stateValuesFiles(List.of(new File("common-values.yaml"), new File("common-values.yaml"))) // optional
  .stateValuesSet(Map.of("myService.version", "v1.2", "myService.logLevel", "DEBUG")) // optional
  .selectors(List.of(new Selector("group", true, "progressive"))) // optional
  .skipDeps(true) // optional - default=false
  .skipNeeds(true) // optional - default=false
  .includeTransitiveNeeds(true) // optional - default=false
  .build();
```
## Runtimes
The above commands can be executed using one of the available runtimes:
### Binary Runtime
This runtime can be used if you have the helmfile binary available on your machine. Usage:
```java
BinaryRuntime runtime = BinaryRuntime
  .builder()
  .helmfileBinaryPath("/bin/helmfile")
  .build();
```
### Docker Runtime
You can use the docker runtime if you have docker installed on your machine. 
```java
DockerRuntime runtime = DockerRuntime
  .builder()
  .helmfileBinaryPath("/bin/helmfile") // mandatory - points to to the helmfile binary inside the docker image
  .dockerHost("unix:///var/run/docker.sock") // optional - default on linux: "unix:///var/run/docker.sock" on win and mac: "tcp://localhost:2376"
  .imageRepository("ghcr.io/helmfile/helmfile")  // optional - default "ghcr.io/helmfile/helmfile"
  .imageTag("v0.151.0") // optional - default "v0.151.0"
  .build();
```
### Execution
The runtimes offer methods to execute the supported operations (`build`,`template`,`sync`). 
Those methods return POJOs representing the corresponding helmfile output ([HelmfileBuild](https://github.com/paichinger/jhelmfile/blob/main/src/main/java/com/paichinger/helmfile/models/build/HelmfileBuild.java) and [HelmfileTemplate](https://github.com/paichinger/jhelmfile/blob/main/src/main/java/com/paichinger/helmfile/models/template/HelmfileTemplate.java)). Usage:
```java
HelmfileTemplate templateOutput = runtime.template(templateCmmand);
// or
String syncOutput = runtime.sync(syncCommand);
// or
HelmfileBuild buildOutput = runtime.build(buildCommand);
```
### Note
jhelfmile doesn't support all the features the helmifle cli offers yet. However, if you're missing a feature or a command don't hesitate to open an issue or create a PR. 
### Use cases
In the [examples](https://github.com/paichinger/jhelmfile/tree/main/examples) folder you can find a project which uses jhelmfile to run tests against a helmfile. There, the output of jhelmfile is used to run verifications using junit.