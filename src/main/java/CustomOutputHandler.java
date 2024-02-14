import org.apache.maven.shared.invoker.InvocationOutputHandler;

public class CustomOutputHandler implements InvocationOutputHandler {
    private StringBuilder outputBuffer = new StringBuilder();

    @Override
    public void consumeLine(String line) {
        // Append each line to the StringBuilder
        outputBuffer.append(line).append(System.lineSeparator());
    }

    // Additional method to get the captured output
    public String getOutput() {
        return outputBuffer.toString();
    }
}
