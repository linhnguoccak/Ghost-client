package com.ghost.client.module.config;

public class Setting<T> {

    private final String name;
    private T value;
    private final T min;
    private final T max;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
        this.min = null;
        this.max = null;
    }

    public Setting(String name, T defaultValue, T min, T max) {
        this.name = name;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
    }

    public String getName() { return name; }
    public T getValue() { return value; }
    public T getMin() { return min; }
    public T getMax() { return max; }

    @SuppressWarnings("unchecked")
    public void setValueRaw(Object v) {
        if (value instanceof Double && v instanceof Number)
            value = (T)(Double)((Number)v).doubleValue();
        else if (value instanceof Integer && v instanceof Number)
            value = (T)(Integer)((Number)v).intValue();
        else if (value instanceof Boolean && v instanceof Boolean)
            value = (T) v;
        else
            value = (T) v;
    }
}
