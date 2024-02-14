import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jetty.util.log.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;

import java.io.File;

/**
 * Skeleton of a ContinuousIntegrationServer which acts as webhook
 * See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler {

    /**
     * Method that handles the request from the webhook
     *
     * @param target      - The target of the request
     * @param baseRequest - The original unwrapped request object
     * @param request     - The request either as the Request object or a wrapper of
     *                    that request
     * @param response    - The response as the Response object or a wrapper of that
     *                    request
     * @throws IOException      - If an input or output exception occurs
     * @throws ServletException - If a servlet exception occurs
     */
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        String eventType = request.getHeader("X-Github-Event"); // Get the event type from the header
        String jsonRequest = IOUtils.toString(request.getReader());
        JSONObject jsonObject = new JSONObject(jsonRequest);

        // If it is not a push event, do not continue
        if (!"push".equals(eventType)) {
            response.getWriter().println("Not performing CI job - Event is not 'push'");
            return;
        } else {
            System.out.println("Event is 'push' - Proceeding with CI job");
            handlePushEvent(jsonObject);
        }
        response.getWriter().println("CI job done");
    }

    /**
     * Method that handles the push event by accepting the JSON object and running
     * methods to clone the repository, compile it and delete the cloned repository
     *
     * @param jsonObject - JSON object containing the push event
     */
    public void handlePushEvent(JSONObject jsonObject) {
        String cloneddirectoryPath = "src/main/resources/";
        File clonedRepoFile = new File(cloneddirectoryPath);

        // Create commit status object
        JSONObject head_commit = jsonObject.getJSONObject("head_commit");
        String sha = head_commit.getString("id");
        String url = "";
        CommitStatus status = new CommitStatus(sha, url);

        cloneRepository(jsonObject, clonedRepoFile);
        compileRepository(cloneddirectoryPath, clonedRepoFile, status);

        File gitFile = new File(cloneddirectoryPath + ".git");
        deleteDirectory(gitFile);
    }

    private boolean isRepositoryCloned(String clonedRepoFile) {
        File gitDirectory = new File(clonedRepoFile);
        return gitDirectory.exists() && gitDirectory.isDirectory();
    }

    /**
     * Method that clones the repository from the JSON object
     *
     * @param jsonObject     - JSON object containing the push event
     * @param clonedRepoFile - File object representing the cloned repository
     */
    public void cloneRepository(JSONObject jsonObject, File clonedRepoFile) {
        // Get clone url and branch name
        JSONObject repository = jsonObject.getJSONObject("repository");
        String cloneUrl = repository.getString("clone_url");
        String branchName = jsonObject.getString("ref").replace("refs/heads/", "");

        if (isRepositoryCloned(clonedRepoFile.toString())) {
            System.out.println("Repo is already cloned in a directory. Will delete now!");
            deleteDirectory(clonedRepoFile);
        }
        // Cloning the repository
        try {
            System.out.println("Cloning repository...");
            Git.cloneRepository()
                    .setURI(cloneUrl)
                    .setDirectory(clonedRepoFile)
                    .setBranch(branchName)
                    .call().close();
            System.out.println("Repository cloned successfully");
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that compiles the repository using maven commands, specifically "clean
     * install"
     *
     * @param cloneddirectoryPath - Path to the cloned repository
     *                            (src/main/resources/)
     * @param clonedRepoFile      - File object representing the cloned repository
     */
    public void compileRepository(String cloneddirectoryPath, File clonedRepoFile, CommitStatus status) {
        InvocationRequest invocationRequest = new DefaultInvocationRequest();
        invocationRequest.setPomFile(new File(cloneddirectoryPath, "pom.xml")); // pom.xml is the file that contains the
        // maven configuration
        invocationRequest.setBaseDirectory(clonedRepoFile);
        invocationRequest.setGoals(Collections.singletonList("clean install"));

        DefaultInvoker invoker = new DefaultInvoker(); // Invoker is used to execute the maven commands

        status.setCommitStatusToPending(); // pending status sent to Github
        // Excuting the maven commands
        try {
            InvocationResult result = invoker.execute(invocationRequest);

            if (result.getExitCode() == 0) {
                System.out.println("Build successful!");
                status.setCommitStatusToSuccess();
            } else {
                System.out.println("Build failed. Exit code: " + result.getExitCode());
                if (result.getExitCode() == 1) {
                    status.setCommitStatusToFailure(); // General failure such as test failure
                } else {
                    status.setCommitStatusToError(); // Every other failure classed as error
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that deletes the cloned repository after the CI job is done to avoid
     * clutter
     * The method works recursively by deleting all files and directories within the
     * cloned repository
     *
     * @param directory - File object representing the cloned repository
     */
    public void deleteDirectory(File directory) {
        Path directoryPath = directory.toPath();
        try {
            Files.walk(directoryPath)
                    .map(Path::toFile)
                    .sorted((o1, o2) -> -o1.compareTo(o2))
                    .forEach(File::delete);
            System.out.println("Local repository deleted successfully: " + directoryPath);
        } catch (IOException e) {
            System.err.println("Error deleting local repository: " + e.getMessage());
        }
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        Server server = new Server(8040);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}