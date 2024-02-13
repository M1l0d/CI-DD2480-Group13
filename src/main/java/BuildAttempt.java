import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class BuildAttempt {
    String buildLog;
    String buildDate;
    String commitIdentifier;
    String buildSuccess;

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public void setBuildLog(String buildLog) {
        this.buildLog = buildLog;
    }

    public void setCommitIdentifier(String commitIdentifier) {
        this.commitIdentifier = commitIdentifier;
    }

    public void setBuildSuccess(String buildSuccess) {
        this.buildSuccess = buildSuccess;
    }

    public void saveToJsonFile() {
        Path jsonFilePath = Paths.get("src/main/resources/buildHistory.JSON");

        // JSONArray jsonArray = new JSONArray();
        // JSONObject jsonBuildAttempt = new JSONObject();
        // jsonBuildAttempt.put("buildDate", buildDate);
        // jsonBuildAttempt.put("buildLog", buildLog);
        // jsonBuildAttempt.put("commitIdentifier", commitIdentifier);
        // jsonBuildAttempt.put("buildSuccess", buildSuccess);
        // jsonArray.put(jsonBuildAttempt);

        try {
            // Read existing JSON content
            String existingJsonContent = new String(Files.readAllBytes(jsonFilePath));
            List<BuildAttempt> existingBuildAttempts = new Gson().fromJson(existingJsonContent, new TypeToken<List<BuildAttempt>>() {}.getType());
    
            // Append new builds to the existing list
            existingBuildAttempts.add(this);

            // Write the updated list back to the file with pretty printing
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String updatedJson = gson.toJson(existingBuildAttempts);

            Files.write(jsonFilePath, updatedJson.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
