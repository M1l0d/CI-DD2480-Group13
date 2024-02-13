# CI-DD2480-Group13
Repository containing lab 2 in the course DD2480 Software Engineering Fundamentals

## Assignment #2: Continous Integration
This is an assignment to master the core of continuous integration. A small continous integration (CI) server is implemented. This CI server will only contain the core features of continuous integration.

## The Files
### ContinousIntegrationServer.java
A simple web-hook based continuous integration (CI) server containing a set of methods to handle GitHub push events. Upon receiving a push event from GitHub webhook, this server clones the repository, compiles it using Maven commands ('clean install'), and then deletes the cloned repository after the CI job is completed to avoid clutter. The server is started on a specified port (8013) using Jetty server.

```sh
public void handle()
```
Listens for incoming requests and extracts the event type from the request header. The method only proceeds if the event type is 'push'. It then passes on the handling to the **handlePushEvent** method.

```sh
private void handlePushEvent()
```
Processes the push event JSON object by cloning the repository and compiling it using the methods **cloneRepository** and **compileRepository**, respectively. The cloned repository is then deleted by a method call to **deleteDirectory**.

```sh
private void cloneRepository()
```
The method extracts the clone URL and branch name from the JSON object and proceeds by using JGit to clone the repository into the specified directory (src/main/resources).

```sh
public void compileRepository()
```
Sets up Maven invocation requests to compile the repository using 'clean install'. The Maven Invoker API is used to execute the requests.

```sh
private void deleteRepository()
```
The method is used to recursively delete the cloned repository directory after the CI job is done. This is done to avoid clutter and accumulation of unnecessary files.
