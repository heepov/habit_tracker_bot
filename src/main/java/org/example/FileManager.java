package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    // Сохранение данных в файл
    public static void saveDataToFile(String filename, Map<Long, ArrayList<Habit>> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
            System.out.println("Данные успешно сохранены в файл: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка данных из файла
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
            // Если файла нет, создадим новый
            System.out.println("Файл не найден или не доступен для чтения. Создан новый файл.");
            try {
                Files.createFile(path);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                System.out.println("Не удалось создать файл.");
            }
            return new HashMap<>();
        }
    }

}
