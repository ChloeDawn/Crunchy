package io.github.insomniakitten.crunchy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
abstract class Crunchy {
  private Crunchy() {
    throw new UnsupportedOperationException();
  }

  @Inject(method = "init", at = @At("TAIL"))
  private void crunchy$buildThread(final CallbackInfo ci) {
    final Runtime runtime = Runtime.getRuntime();
    final MinecraftClient $this = (MinecraftClient) (Object) this;
    final Thread thread = new Thread(() -> {
      long prevFree = 0;
      long freed = 0;

      while (true) {
        final long free = runtime.freeMemory();

        if (free > prevFree) {
          freed += free - prevFree;
        }

        prevFree = free;

        if (freed > 200_000) {
          $this.execute(() -> $this.getSoundManager().play(
            PositionedSoundInstance.master(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F)
          ));
          freed = 0;
        }

        try {
          Thread.sleep(250);
        } catch (final InterruptedException e) {
          break;
        }
      }
    });
    thread.setName("CrunchyThread");
    thread.setDaemon(true);
    thread.start();
  }
}
