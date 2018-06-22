package net.insomniakitten.crunchy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Mod(modid = "crunchy", name = "Crunchy", version = "%VERSION%", clientSideOnly = true)
public final class Crunchy {
    @Getter(value = AccessLevel.PRIVATE, onMethod = @__(@InstanceFactory))
    private static final Crunchy INSTANCE = new Crunchy();

    @EventHandler
    void onLoadComplete(@NonNull final FMLLoadCompleteEvent event) {
        @NonNull val runtime = Runtime.getRuntime();
        @NonNull val minecraft = Minecraft.getMinecraft();
        val thread = new Thread(() -> tick(runtime, minecraft), "CrunchyThread");
        thread.setDaemon(true);
        thread.start();
    }

    private void tick(final Runtime runtime, final Minecraft minecraft) {
        long prevFree = 0;
        long freed = 0;

        while (true) {
            long free = runtime.freeMemory();

            if (free > prevFree) {
                freed += free - prevFree;
            }

            prevFree = free;

            if (freed > 200_000) {
                minecraft.addScheduledTask(() -> crunch(minecraft));
                freed = 0;
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    // TODO https://www.youtube.com/watch?v=SMWi7CLoZ2Q
    private void crunch(final Minecraft minecraft) {
        @NonNull val sound = SoundEvents.ENTITY_GENERIC_EAT;
        @NonNull val record = PositionedSoundRecord.getRecord(sound, 1.0F, 1.0F);
        @NonNull val handler = minecraft.getSoundHandler();
        handler.playSound(record);
    }
}
