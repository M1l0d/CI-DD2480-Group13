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

### ConfigData.java
The code in this file is used to access configuration data stored in a properties file called **config.properties**. It contains the two methods **getAccessToken** and **getNgrokLink**.

```sh
public static String getAccessToken()
```
This method retrieves the access token for GitHub from the properties file. It loads the properties file using a _FileInputStream_ and extracts the value associated with the key _'access.token'_. It handles errors regarding the file reading process.

```sh
public static String getNgrokLink()
```
This method works similarly to the **getAccessToken** method as it retrieves the current _Ngrok link_ from the properties file. It loads the properties file and extracts the value associated with the key _'current.ngroklink'_. This method also handles errors regarding the file reading process.

### CommitStatus.java
This code is used to manage commit status on GitHub repositories using the GitHub API.

```sh
public CommitStatus()
```
This is a constructer method that initializes the _'CommitStatus'_ object with the commit _SHA_ and the target URL of the CI server. Another version of this constructor exists which alos allows passing a custom HTTP client.

```sh
public void sendCommitStatus()
```
This method sends a commit status to GitHub. It constructs a JSON payload containing the information about the commit status. The method sends an HTTP POST request to the GitHub API with the JSON payload to update the commit status. The HTTP response is also handled.

```sh
public void setCommitStatusToPending()
```
```sh
public void setCommitStatusToSuccess()
```
```sh
public void setCommitStatusToFailure()
```
```sh
public void setCommitStatusToError()
```
These four methods are used to set the commit status to either _"pending", "success", "failure" or "error"_ with a corresponding description.

```sh
private String getDateandTime()
```
Retrieves the current date and time in the format _"yyyy/MM/dd HH:mm:ss"_

### CommitStatusTest.java
This is a test file providing unit tests for the **CommitStatus** class to ensure that its functionality works as intended.

```sh
@Mock CloseableHttpClient httpClient
```
```sh
@Mock CloseableHttpResponse response
```
```sh
@Mock StatusLine statusLine
```
These are used to mock the HTTP client, HTTP response and the status line of the HTTP response.

```sh
void setUp()
```
Initializes the mock objects using _'MockitoAnnotations.initMocks(this)'_.

```sh
void testSetCommitStatusWhenRequestIsSuccessful()
```
This method tests the behaviour when the HTTP request is successful with status code _201_. The method mocks the behaviour of the response object to return as a status code of _201_. The output stream is captured to check if the expected status code is printed.

```sh
void testSetCommitStatusWhenRequestIsNotSuccessful()
```
This method tests the behaviour when the HTTP request is not successful with status code _400_. Similarly to the previous method, this method mocks the behaviour of the response object to return a status code of _400_. The output stream is once more captured to verify that the expected status code is printed.

All tests in **CommitStatusTest.java** use **JUnit assertions** to verify that the expected status code is printed in the output stream.

### ContinousIntegrationTest.java
This is a test file providing unit tests for the **ContinousIntegrationServer** class to ensure that its functionality works as intended.

```sh
public void setUp()
```
Initializes the _ContinousIntegrationServer_ instance before each test method execution.

```sh
public void testThatItClonesRepositorySuccessfully()
```
This method tests the **cloneRepository** method to check that it clones the repository successfully. It creates a _JSONObject_ to simulate the payload received from the GitHub webhook. The **cloneRepository** method is called with the _JSONObject_ and a file representing the cloned repository directory. The method asserts that the cloned repository directory exists.

```sh
public void testSetCommitStatusWhenRequestIsSuccessful()
```
The method tests the **deleteDirectory** method to ensure that it deletes a repository successfully. Similarly to the testing method before, a _JSONObject_ is set up together with the cloned repository file. A repository is cloned and 5 seconds is waited (assuming it takes less than 5 seconds to clone). The repository is then deleted and the method asserts that the repository does not exist.

All tests in **CommitStatusTest.java** use **JUnit assertions** (_assertTrue/assertFalse)_ to verify that the expected behaviour of the methods.
