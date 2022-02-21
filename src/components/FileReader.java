package components;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.ArrayList;
import java.util.List;

public class FileReader {
    public static List<String> readeFromFile(File file) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        try (java.io.FileReader fileReader = new java.io.FileReader(file, UTF_8);
             BufferedReader bfReader = new BufferedReader(fileReader)) {
            while (bfReader.ready()) {
                String line = bfReader.readLine();
                lines.add(line);
            }
        }
        return lines;
    }
}
