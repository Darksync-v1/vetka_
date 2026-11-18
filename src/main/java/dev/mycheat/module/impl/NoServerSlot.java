package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;

/** Отменяет пакет, которым сервер принудительно меняет выбранный слот хотбара. Экспериментально. */
public class NoServerSlot extends Module {
    public NoServerSlot() {
        super("NoServerSlot", Category.MISC, "Не даёт серверу принудительно сменить выбранный слот хотбара. Экспериментально: если не сработает, игра всё равно не упадёт.");
    }
}
