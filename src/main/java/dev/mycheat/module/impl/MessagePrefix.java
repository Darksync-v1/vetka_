package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.TextSetting;

public final class MessagePrefix extends Module {
    public final TextSetting message = add(new TextSetting("Message", "[Vetka] "));
    public MessagePrefix() { super("MessagePrefix", Category.MISC, "Добавляет заданный текст в начало отправляемого сообщения."); }
}
