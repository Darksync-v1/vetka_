package dev.mycheat.setting;

public final class TextSetting {
    public final String name;
    private String value;

    public TextSetting(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String get() { return value; }
    public void set(String value) { this.value = value == null ? "" : value; }
}
