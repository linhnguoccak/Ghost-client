package com.ghost.client.module.config;

public class Setting<T> {

    private final String name;
    private final String description;
    private T value;
    private final T min;
    private final T max;

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
        this.min = null;
        this.max = null;
    }

    public Setting(String name, String description, T defaultValue, T min, T max) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public T getValue()            { return value; }
    public T getMin()              { return min; }
    public T getMax()              { return max; }

    public void setValue(T v) {
        if (min instanceof Number && max instanceof Number) {
            double d = ((Number) v).doubleValue();
            double lo = ((Number) min).doubleValue();
            double hi = ((Number) max).doubleValue();
            if (d < lo || d > hi) return;
        }
        this.value = v;
    }

    @SuppressWarnings("unchecked")
    public void setValueRaw(Object v) {
        try {
            if (value instanceof Boolean)      setValue((T)(Boolean) v);
            else if (value instanceof Integer) setValue((T)(Integer)((Number)v).intValue());
            else if (value instanceof Double)  setValue((T)(Double)((Number)v).doubleValue());
            else if (value instanceof Float)   setValue((T)(Float)((Number)v).floatValue());
            else if (value instanceof String)  setValue((T)(String) v);
        } catch (ClassCastException ignored) {}
    }
              }
