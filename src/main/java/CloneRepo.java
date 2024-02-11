package src.main.java;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

public class CloneRepo {
    public static void main(String[] args) {
        String repoURL = "https://github.com/M1l0d/CI-DD2480-Group13";
        File localPath = new File("src/main/resources/");

        try {
            System.out.println("Cloning repository...");
            Git.cloneRepository()
                    .setURI(repoURL)
                    .setDirectory(localPath)
                    .call();
            System.out.println("Repository cloned successfully");
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}