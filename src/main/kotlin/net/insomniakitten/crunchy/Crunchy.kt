package net.insomniakitten.crunchy

import net.minecraft.client.Minecraft
import net.minecraft.init.SoundEvents
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraft.client.audio.PositionedSoundRecord as Records

@SideOnly(Side.CLIENT)
@Mod(
    modid = "crunchy",
    name = "Crunchy",
    version = "%VERSION%",
    clientSideOnly = true,
    dependencies = "required-after:forgelin",
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter"
)
object Crunchy {
    @EventHandler fun onLoadComplete(e: FMLLoadCompleteEvent) = Thread {
        val runtime = Runtime.getRuntime()
        val minecraft = Minecraft.getMinecraft()
        var prevFree = 0L
        var freed = 0L

        while (true) {
            val free = runtime.freeMemory()

            if (free > prevFree) {
                freed += free - prevFree
            }

            prevFree = free

            if (freed > 200000) {
                minecraft.addScheduledTask {
                    val sound = SoundEvents.ENTITY_GENERIC_EAT
                    val record = Records.getRecord(sound, 1.0f, 1.0f)
                    val handler = minecraft.soundHandler
                    handler.playSound(record)
                }
                freed = 0
            }

            try {
                Thread.sleep(250)
            } catch (e: InterruptedException) {
                break
            }
        }
    }.run {
        name = "CrunchyThread"
        isDaemon = true
        start()
    }
}
