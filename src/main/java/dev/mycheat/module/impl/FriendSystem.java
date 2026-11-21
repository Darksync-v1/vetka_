package dev.mycheat.module.impl;

import dev.mycheat.module.Category;
import dev.mycheat.module.Module;
import dev.mycheat.setting.NumberSetting;
import dev.mycheat.setting.TextSetting;
import dev.mycheat.util.Friends;

/** Adds/removes a player name from the persistent friend list when enabled. */
public class FriendSystem extends Module {
    public final TextSetting playerName = add(new TextSetting("PlayerName", ""));
    public final NumberSetting action = add(new NumberSetting("Action (0 Add, 1 Remove, 2 Toggle)", 0, 0, 2, 1));
    public FriendSystem() { super("FriendSystem", Category.COMBAT, "Управление друзьями. Aura и ElytraAura автоматически игнорируют друзей."); }
    @Override protected void onEnable() {
        String name = playerName.get().trim();
        if (name.isEmpty()) return;
        switch (action.getInt()) {
            case 1 -> Friends.remove(name);
            case 2 -> Friends.toggle(name);
            default -> Friends.add(name);
        }
        dev.mycheat.util.Config.save();
    }
}
