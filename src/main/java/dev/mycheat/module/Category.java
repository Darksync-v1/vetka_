package dev.mycheat.module;

public enum Category {
    COMBAT("Бой"), MOVEMENT("Движение"), RENDER("Рендер"), MISC("Разное");
    public final String title;
    Category(String title) { this.title = title; }
}
