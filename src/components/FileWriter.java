package components;

import tasks.*;

import java.io.*;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileWriter {

    public static void writeTasksToCSV(List<Task> tasks, File saveFile, List<Task> history) throws IOException {
        try (Writer fileWriter = new java.io.FileWriter(saveFile, UTF_8);
             BufferedWriter bfWriter = new BufferedWriter(fileWriter)) {
            bfWriter.write("id,type,name,status,description,epic");
            bfWriter.newLine();
            for (Task task : tasks) {
                bfWriter.write(task.toString());
                bfWriter.newLine();
            }
            bfWriter.newLine();
            for (int i = 0; i < history.size(); i++) {
                Task task = history.get(i);
                String taskId = String.valueOf(task.getId());
                if (i > 0) {
                    taskId = "," + taskId;
                }
                bfWriter.write(taskId);
            }
        }

    }
}
