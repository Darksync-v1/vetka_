package dev.mycheat.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public final class Tp {
    /** Отправляет серверу цепочку пакетов позиции от from до to шагами не длиннее step блоков. */
    public static void send(ClientPlayerEntity p, Vec3d from, Vec3d to, double step, boolean lastOnGround) {
        int n = Math.max(1, (int) Math.ceil(from.distanceTo(to) / step));
        for (int i = 1; i <= n; i++) {
            Vec3d v = from.lerp(to, (double) i / n);
            p.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(v.x, v.y, v.z, i == n && lastOnGround));
        }
    }
}
