package me.lordierclaw.azfe.util;

import java.lang.reflect.Field;

public class MockUtil {
    public static <T> void setPrivateField(T obj, String fieldName, Object fieldValue) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, fieldValue);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Unable to set private field to " + fieldName, e);
        }
    }
}
