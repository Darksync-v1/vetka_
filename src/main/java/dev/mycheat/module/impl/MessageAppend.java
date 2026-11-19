package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.TextSetting;

public final class MessageAppend extends Module {
    public final TextSetting message = add(new TextSetting("Message", " | Vetka v1"));
    public MessageAppend() { super("MessageAppend", Category.MISC, "Добавляет заданный текст в конец отправляемого сообщения."); }
}
