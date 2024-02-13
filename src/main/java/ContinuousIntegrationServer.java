import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
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
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        response.getWriter().println("CI job done");
    }


    /**
     * Method that handles the push event by accepting the JSON object and running
     * methods to clone the repository, compile it and delete the cloned repository
     * 
     * @param jsonObject - JSON object containing the push event
     */
    public void handlePushEvent(JSONObject jsonObject) {
        String clonedRepoPath = "src/main/resources/";
        File clonedRepoFile = new File(clonedRepoPath);

        cloneRepository(jsonObject, clonedRepoFile);
        compileRepository(clonedRepoPath, clonedRepoFile);
        deleteDirectory(clonedRepoFile);
    }

    /**
     * Method that clones the repository from the JSON object
     * 
     * @param jsonObject     - JSON object containing the push event
     * @param clonedRepoFile - File object representing the cloned repository
     */
    private void cloneRepository(JSONObject jsonObject, File clonedRepoFile) {
        // Get clone url and branch name
        JSONObject repository = jsonObject.getJSONObject("repository");
        String cloneUrl = repository.getString("clone_url");
        String branchName = jsonObject.getString("ref").replace("refs/heads/", "");

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
    public void compileRepository(String clonedRepoPath, File clonedRepoFile) {
        InvocationRequest invocationRequest = new DefaultInvocationRequest();
        invocationRequest.setPomFile(new File(clonedRepoPath, "pom.xml")); // pom.xml is the file that contains the
                                                                           // maven configuration
        invocationRequest.setBaseDirectory(clonedRepoFile);
        invocationRequest.setGoals(Collections.singletonList("clean install"));

        DefaultInvoker invoker = new DefaultInvoker(); // Invoker is used to execute the maven commands

        // Excuting the maven commands
        try {
            InvocationResult result = invoker.execute(invocationRequest);

            if (result.getExitCode() == 0) {
                System.out.println("Build successful!");
            } else {
                System.out.println("Build failed. Exit code: " + result.getExitCode());
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
        Server server = new Server(8013);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
