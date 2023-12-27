package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    // Save data file
    public static void saveDataToFile(String filename, Map<Long, ArrayList<Habit>> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
            // logging print
            System.out.println("Data successfully saved to file:" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load or create data file
    public static Map<Long, ArrayList<Habit>> loadDataFromFile(String filename) {
        Path path = Paths.get(filename);

        if (Files.exists(path) && Files.isRegularFile(path) && Files.isReadable(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
                return (Map<Long, ArrayList<Habit>>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return new HashMap<>(); // Возвращаем пустую карту в случае ошибки
            }
        } else {
            // If data file isnt exist than create data file
            System.out.println("The file was not found or cannot be read. A new file has been created.");
            try {
                Files.createFile(path);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.out.println("Failed to create file.");
            }
            return new HashMap<>();
        }
    }

}

