package com.example.todolist.utils;

import java.util.Random;

public class IDGenerator {
    private static Random random = new Random();

    public static int generateTaskID() {
        long currentTimeMillis = System.currentTimeMillis();
        random.setSeed(currentTimeMillis);
        return (int)(currentTimeMillis % Integer.MAX_VALUE) + random.nextInt(10000);
    }

    public static int generateSubTaskID() {
        long currentTimeMillis = System.currentTimeMillis();
        random.setSeed(currentTimeMillis);
        return (int)(currentTimeMillis % Integer.MAX_VALUE) + random.nextInt(10000);
    }
}
