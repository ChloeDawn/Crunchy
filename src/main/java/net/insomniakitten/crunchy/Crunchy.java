package net.insomniakitten.crunchy;

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
@Mod(modid = "crunchy", name = "Crunchy", version = "%VERSION%", clientSideOnly = true)
public final class Crunchy {
    private static final Crunchy INSTANCE = new Crunchy();

    private Crunchy() {}

    @InstanceFactory
    public static Crunchy getInstance() {
        return INSTANCE;
    }

    @EventHandler
    void onLoadComplete(final FMLLoadCompleteEvent event) {
        final Runtime runtime = Runtime.getRuntime();
        final Minecraft minecraft = Minecraft.getMinecraft();
        final Thread thread = new Thread(() -> {
            long prevFree = 0;
            long freed = 0;

            while (true) {
                long free = runtime.freeMemory();

                if (free > prevFree) {
                    freed += free - prevFree;
                }

                prevFree = free;

                if (freed > 200_000) {
                    minecraft.addScheduledTask(() -> minecraft.getSoundHandler().playSound(
                        PositionedSoundRecord.getRecord(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F)
                    ));
                    freed = 0;
                }

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.setName("CrunchyThread");
        thread.setDaemon(true);
        thread.start();
    }
}
