package com.example.todolist.utils;

import java.util.Random;

public class TaskIDGenerator {
    private static Random random = new Random();

    public static int generateUniqueID() {
        long currentTimeMillis = System.currentTimeMillis();
        random.setSeed(currentTimeMillis);
        return random.nextInt(10000);
    }
}
