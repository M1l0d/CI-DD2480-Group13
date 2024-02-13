
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jetty.util.log.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.PrintStreamHandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;

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

        System.out.println(target);


        if (target.equals("/builds")) {

            Path jsonFilePath = Paths.get("src/main/resources/buildHistory.JSON");

            String existingJsonContent = new String(Files.readAllBytes(jsonFilePath));
            List<BuildAttempt> existingBuildAttempts = new Gson().fromJson(existingJsonContent, new TypeToken<List<BuildAttempt>>() {}.getType());

            response.getWriter().println("<html><body>");
            for (BuildAttempt buildAttempt : existingBuildAttempts) {
                response.getWriter().println("<a href='" + buildAttempt.getBuildDate() + "'>" + buildAttempt.getBuildSuccess() + "</a><br>");
            }
            response.getWriter().println("</body></html>");
        }
        

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
    private void handlePushEvent(JSONObject jsonObject) {
        String clonedRepoPath = "src/main/resources/clonedRepo";
        File clonedRepoFile = new File(clonedRepoPath);
        BuildAttempt buildAttempt = new BuildAttempt();

        cloneRepository(jsonObject, clonedRepoFile, buildAttempt);
        compileRepository(clonedRepoPath, clonedRepoFile, buildAttempt);
        deleteDirectory(clonedRepoFile);
    }

    /**
     * Method that clones the repository from the JSON object
     * 
     * @param jsonObject     - JSON object containing the push event
     * @param clonedRepoFile - File object representing the cloned repository
     */
    private void cloneRepository(JSONObject jsonObject, File clonedRepoFile, BuildAttempt buildAttempt) {
        // Get clone url and branch name
        JSONObject repository = jsonObject.getJSONObject("repository");
        String cloneUrl = repository.getString("clone_url");
        String branchName = jsonObject.getString("ref").replace("refs/heads/", "");

        JSONObject head_commit = jsonObject.getJSONObject("head_commit");
        buildAttempt.setBuildDate(head_commit.getString("timestamp"));
        JSONObject pusher = jsonObject.getJSONObject("pusher");
        buildAttempt.setCommitIdentifier(pusher.getString("name"));

        // Cloning the repository
        try {
            System.out.println("Cloning repository...");
            Git.cloneRepository()
                    .setURI(cloneUrl)
                    .setDirectory(clonedRepoFile)
                    .setBranch(branchName)
                    .call();
            System.out.println("Repository cloned successfully");
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that compiles the repository using maven commands, specifically "clean
     * install"
     * 
     * @param clonedRepoPath - Path to the cloned repository (src/main/resources/)
     * @param clonedRepoFile - File object representing the cloned repository
     */
    public void compileRepository(String clonedRepoPath, File clonedRepoFile, BuildAttempt buildAttempt) {
        InvocationRequest invocationRequest = new DefaultInvocationRequest();
        invocationRequest.setPomFile(new File(clonedRepoPath, "pom.xml")); // pom.xml is the file that contains the
                                                                           // maven configuration
        invocationRequest.setBaseDirectory(clonedRepoFile);
        invocationRequest.setGoals(Collections.singletonList("clean install"));

        DefaultInvoker invoker = new DefaultInvoker(); // Invoker is used to execute the maven commands
        
        // Create a CustomOutputHandler
        CustomOutputHandler outputHandler = new CustomOutputHandler();
        invoker.setOutputHandler(outputHandler);
        // Excuting the maven commands
        try {
            InvocationResult result = invoker.execute(invocationRequest);

            if (result.getExitCode() == 0) {
                System.out.println("Build successful!");
                buildAttempt.setBuildSuccess("Build successful!");
            } else {
                System.out.println("Build failed. Exit code: " + result.getExitCode());
                buildAttempt.setBuildSuccess("Build failed");
            }

            // Retrieve the captured output
            String capturedOutput = outputHandler.getOutput();
            //System.out.println("Captured Output:\n" + capturedOutput);
            buildAttempt.setBuildLog(capturedOutput);
            buildAttempt.saveToJsonFile();

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
    private void deleteDirectory(File directory) {
        try {
            if (directory.exists()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            deleteDirectory(file);
                        } else {
                            file.delete();
                        }
                    }
                }
                directory.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        //SpringApplication.run(ContinuousIntegrationServer.class, args);
        Server server = new Server(8040);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}