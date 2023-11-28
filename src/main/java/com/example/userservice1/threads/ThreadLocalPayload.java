package com.example.userservice1.threads;

public class ThreadLocalPayload {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setId(String tenantId) {
        CONTEXT.set(tenantId);
    }

    public static String getId() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }

}
