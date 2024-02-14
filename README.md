---
runme:
  id: 01HPKGYX39DVNYEW8WRZ6K93VY
  version: v3
---

# CI-DD2480-Group13

Repository containing lab 2 in the course DD2480 Software Engineering Fundamentals

## Assignment #2: Continous Integration

This is an assignment to master the core of continuous integration. A small continous integration (CI) server is implemented. This CI server will only contain the core features of continuous integration.

## The Files

### ContinousIntegrationServer.java

A simple web-hook based continuous integration (CI) server containing a set of methods to handle GitHub push events. Upon receiving a push event from GitHub webhook, this server clones the repository, compiles it using Maven commands ('clean install'), and then deletes the cloned repository after the CI job is completed to avoid clutter. The server is started on a specified port (8013) using Jetty server.

```sh {"id":"01HPKGYX33NJZR8CKJM28YSP2F"}
public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)

```

Listens for incoming requests and extracts the event type from the request header. The method only proceeds if the event type is 'push'. It then passes on the handling to the **handlePushEvent** method.

```sh {"id":"01HPKGYX35Y2E0WS6SBQDAAQ0P"}
public void handlePushEvent(JSONObject jsonObject)

```

Processes the push event JSON object by cloning the repository and compiling it using the methods **cloneRepository** and **compileRepository**, respectively. The cloned repository is then deleted by a method call to **deleteDirectory**.

```sh {"id":"01HPKGYX35Y2E0WS6SBVCKMKDV"}
public void cloneRepository(JSONObject jsonObject, File clonedRepoFile, BuildAttempt buildAttempt)

```

The method extracts the clone URL and branch name from the JSON object and proceeds by using JGit to clone the repository into the specified directory (src/main/resources).

```sh {"id":"01HPKGYX35Y2E0WS6SBY3NT7GN"}
public void compileRepository(String clonedRepoPath, File clonedRepoFile,CommitStatus status, BuildAttempt buildAttempt)

```

Sets up Maven invocation requests to compile the repository using 'clean install'. The Maven Invoker API is used to execute the requests.

```sh {"id":"01HPKGYX35Y2E0WS6SC0P7X4CD"}
public void deleteRepository(File directory)

```

The method is used to recursively delete the cloned repository directory after the CI job is done. This is done to avoid clutter and accumulation of unnecessary files.

```sh {"id":"01HPKGYX35Y2E0WS6SC3VQZMG7"}
private boolean isRepositoryCloned(String clonedRepoFile)

```

This method is used to check if a repository is cloned by looking for the directory and check that it is actually a directory.

### ConfigData.java

The code in this file is used to access configuration data stored in a properties file called **config.properties**. It contains the two methods **getAccessToken** and **getNgrokLink**. Since it contains private information, it is not pushed to the repository.

```sh {"id":"01HPKGYX35Y2E0WS6SC7B429D5"}
public static String getAccessToken()

```

This method retrieves the access token for GitHub from the properties file. It loads the properties file using a _FileInputStream_ and extracts the value associated with the key _'access.token'_. It handles errors regarding the file reading process.

### CommitStatus.java

This code is used to manage commit status on GitHub repositories using the GitHub API.

```sh {"id":"01HPKGYX36A4S95AGC2VQKXQEJ"}
public CommitStatus(String sha, String targetUrl)
public CommitStatus(String sha, String targetUrl, CloseableHttpClient httpClient)

```

This is a constructer method that initializes the _'CommitStatus'_ object with the commit _SHA_ and the target URL of the CI server. Another version of this constructor exists which alos allows passing a custom HTTP client.

```sh {"id":"01HPKGYX36A4S95AGC2ZQ2EJSX"}
public void sendCommitStatus(String state, String description)

```

This method sends a commit status to GitHub. It constructs a JSON payload containing the information about the commit status. The method sends an HTTP POST request to the GitHub API with the JSON payload to update the commit status. The HTTP response is also handled.

```sh {"id":"01HPKGYX36A4S95AGC303MVA95"}
public void setCommitStatusToPending()

```

```sh {"id":"01HPKGYX36A4S95AGC32PHTNFD"}
public void setCommitStatusToSuccess()

```

```sh {"id":"01HPKGYX36A4S95AGC34QJJXQX"}
public void setCommitStatusToFailure()

```

```sh {"id":"01HPKGYX36A4S95AGC357EJ3SG"}
public void setCommitStatusToError()

```

These four methods are used to set the commit status to either _"pending", "success", "failure" or "error"_ with a corresponding description.

### CommitStatusTest.java

This is a test file providing unit tests for the **CommitStatus** class to ensure that its functionality works as intended.

```sh {"id":"01HPKGYX36A4S95AGC37ACBXQC"}
@Mock CloseableHttpClient httpClient

```

```sh {"id":"01HPKGYX36A4S95AGC3905TAQ4"}
@Mock CloseableHttpResponse response

```

```sh {"id":"01HPKGYX36A4S95AGC3C3542BD"}
@Mock StatusLine statusLine

```

These are used to mock the HTTP client, HTTP response and the status line of the HTTP response.

```sh {"id":"01HPKGYX36A4S95AGC3FNQ67XB"}
void setUp()

```

Initializes the mock objects using _'MockitoAnnotations.initMocks(this)'_.

```sh {"id":"01HPKGYX36A4S95AGC3G3XN7DX"}
void testSetCommitStatusWhenRequestIsSuccessful()

```

This method tests the behaviour when the HTTP request is successful with status code _201_. The method mocks the behaviour of the response object to return as a status code of _201_. The output stream is captured to check if the expected status code is printed.

```sh {"id":"01HPKGYX36A4S95AGC3J252P0Z"}
void testSetCommitStatusWhenRequestIsNotSuccessful()

```

This method tests the behaviour when the HTTP request is not successful with status code _400_. Similarly to the previous method, this method mocks the behaviour of the response object to return a status code of _400_. The output stream is once more captured to verify that the expected status code is printed.

All tests in **CommitStatusTest.java** use **JUnit assertions** to verify that the expected status code is printed in the output stream.

### ContinousIntegrationTest.java

This is a test file providing unit tests for the **ContinousIntegrationServer** class to ensure that its functionality works as intended.

```sh {"id":"01HPKGYX36A4S95AGC3J3CR2A3"}
public void setUp()

```

Initializes the _ContinousIntegrationServer_ instance before each test method execution.

```sh {"id":"01HPKGYX36A4S95AGC3KWHF99V"}
public void testThatItClonesRepositorySuccessfully()

```

This method tests the __cloneRepository__ method to check that it clones the repository successfully. It creates a _JSONObject_ to simulate the payload received from the GitHub webhook. The __cloneRepository__ method is called with the _JSONObject_ and a file representing the cloned repository directory. The method asserts that the cloned repository directory exists.

```sh {"id":"01HPKGYX36A4S95AGC3QJSREDW"}
public void testSetCommitStatusWhenRequestIsSuccessful()

```

The method tests the __deleteDirectory__ method to ensure that it deletes a repository successfully. Similarly to the testing method before, a _JSONObject_ is set up together with the cloned repository file. A repository is cloned and 5 seconds is waited (assuming it takes less than 5 seconds to clone). The repository is then deleted and the method asserts that the repository does not exist.

All tests in __CommitStatusTest.java__ use __JUnit assertions__ (_assertTrue/assertFalse)_ to verify that the expected behaviour of the methods.

### BuildAttempt.java

```sh {"id":"01HPKGYX36A4S95AGC3VC3VB14"}
public void setCommitId(String commitId)

```

Sets objects commitId to current commitId.

```sh {"id":"01HPKGYX36A4S95AGC3YV2K0NM"}
public void setBuildDate(String buildDate)

```

Sets objects buildDate to current buildDate.

```sh {"id":"01HPKGYX36A4S95AGC428QND6B"}
public void setBuildLog(String buildLog)

```

Sets objects buildLog to current buildLog.

```sh {"id":"01HPKGYX36A4S95AGC42F4TZV9"}
public void setCommitMadeBy(String commitMadeBy)

```

Sets objects commitMadeBy to current commitMadeBy.

```sh {"id":"01HPKGYX37V021K2FCZPSYGNAS"}
public void setBuildSuccess(String buildSuccess)

```

Sets objects buildSuccess to current buildSuccess.

```sh {"id":"01HPKGYX37V021K2FCZRQTTX6A"}
public String getBuildDate()

```

Returns buildDate.

```sh {"id":"01HPKGYX37V021K2FCZVFYHZW9"}
public String getBuildLog()

```

Returns buildLog.

```sh {"id":"01HPKGYX37V021K2FCZWTCVABP"}
public String getBuildSuccess()

```

Returns buildSuccess.

```sh {"id":"01HPKGYX37V021K2FCZYD0APYZ"}
public String getCommitMadeBy()

```

Returns commitMadeBy.

```sh {"id":"01HPKGYX37V021K2FD021ZXP9Q"}
public String getCommitId()

```

Returns commitId

```sh {"id":"01HPKGYX37V021K2FD02CNX8R7"}
public void saveToJsonFile()

```

### CustomOutputHandler.java

```sh {"id":"01HPKGYX37V021K2FD03WX9KG2"}
public void consumeLine(String line)

```

```sh {"id":"01HPKGYX37V021K2FD065Y7AVJ"}
public String getOutput()

```

## Build history

Recent build history can be acessed through this [url](https://normal-full-glider.ngrok-free.app/)

## Way of working

We are currently in the state of “Working Well” as we feel that a little more time is needed to assure that we are continually tuning and adapting our way-of-working. Since the last assignment, stakeholders have agreed to our way-of-working and we have made sure to inspect the practices and tools we work with regularly. We have been able to adapt and tune this way-of-working in accordance to the current assignment, so those goals are expected to be reached with a little more time. By working with this some more, we will also be able to reach the goals of being able to apply all of these practices without thinking about them, and everything will be more natural for us.

## Team

We feel that we are currently in the state of “Performing”. We as a team are able to consistently meet our commitments and we can adapt to the changing context, which in this case are the different assignments. We are able to tackle problems together without outside help and we are well on our way to eliminate any wasted work and inefficiencies. Since there are still assignments left, we have not yet reached the stage of “Adjourned”. What is left for us to do is to become even more efficient as a team by eliminating wasted work and to just keep working towards completing all assignments.

## Statement of contributions

We have all as a team put in a lot of work on our assigned tasks. This time around, there has been a lot more teamwork where we have helped each other out by discussing and pair-programming. In this statement of contributions, some of that help is reflected where it was the most prevalent, but more help has been spread out across the tasks and the team.

### Alexander Widman - [AlexWidman](https://github.com/AlexWidman) :

- Co-author for ContinuousIntegrationServerTest.java
- Co-author for Documentation in README.md
- Helped with ContinuousIntegrationServer.java

### Alva Sundström - [alvasundstrom](https://github.com/alvasundstrom) :

- Co-author for ContinuousIntegrationServer.java
- Author of Buildattempt.java
- Author of CustomOutputHandler.java
- Co-author for ContinuousIntegrationServerTest.java

### Annie Kihlert - [kihlert](https://github.com/kihlert) :

- Co-author for ContinuousIntegrationServerTest.java
- Co-author for Documentation in README.md
- Helped with ContinuousIntegrationServer.java

### Milad Sarbandi Farhani - [M1l0d](https://github.com/M1l0d) :

- Co-author for ContinuousIntegrationServer.java
- Helped with ContinuousIntegrationServerTest.java
- Co-author for Documentation in README.md
- Configuration of Maven.

### Tomas Weldetinsae - [tywe00](https://github.com/tywe00) :

- Author of CommitStatus.java
- Author of ConfigData.java
- Author of CommitStatusTest.java
- Co-author for ContinuousIntegrationServerTest.java
- Helped with ContinuousIntegrationServer.java
