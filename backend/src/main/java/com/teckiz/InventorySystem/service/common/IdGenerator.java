package com.teckiz.InventorySystem.service.common;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private static final AtomicInteger counter = new AtomicInteger(0);

    // Static method to generate a unique ID
    public static String generateUniqueId() {
        // Get the current time in microseconds
        long microTime = System.currentTimeMillis() * 1000;

        // Increment the counter to ensure uniqueness
        int count = counter.incrementAndGet();

        // Combine the time and counter to create a unique ID
        return Long.toHexString(microTime) + Integer.toHexString(count);
    }

}
