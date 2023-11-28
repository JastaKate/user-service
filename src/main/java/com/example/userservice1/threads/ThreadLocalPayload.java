package com.example.userservice1.threads;

public class ThreadLocalPayload {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setId(String tenantId) {
        threadLocal.set(tenantId);
    }

    public static String getId() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }

}
