package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;

/** Отменяет пакет, которым сервер принудительно поворачивает камеру (/teleport ... facing и т.п.). Экспериментально. */
public class NoServerRotate extends Module {
    public NoServerRotate() {
        super("NoServerRotate", Category.MISC, "Не даёт серверу принудительно повернуть твою камеру. Экспериментально: если не сработает, игра всё равно не упадёт.");
    }
}
