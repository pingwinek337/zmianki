package pl.autoreroll.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.autoreroll.AutoRerollClient;

/** Tryb "uczenia" slotow: klikniecie slotu w GUI serwera zapisuje jego ID do configu. */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
	@Inject(method = "onMouseClick", at = @At("HEAD"), cancellable = true)
	private void autoreroll$learnSlot(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
		if (AutoRerollClient.learn == AutoRerollClient.LearnMode.NONE || slot == null) return;

		MinecraftClient client = MinecraftClient.getInstance();
		ci.cancel();

		if (slot.inventory instanceof PlayerInventory) {
			AutoRerollClient.notify(client, "§cTo slot Twojego ekwipunku - kliknij slot w GUI serwera.");
			return;
		}

		if (AutoRerollClient.learn == AutoRerollClient.LearnMode.REROLL) {
			AutoRerollClient.CONFIG.rerollSlot = slot.id;
			AutoRerollClient.notify(client, "Slot rerolla ustawiony: " + slot.id);
		} else {
			AutoRerollClient.CONFIG.previewSlot = slot.id;
			AutoRerollClient.notify(client, "Slot miecza ustawiony: " + slot.id);
		}
		AutoRerollClient.CONFIG.save();
		AutoRerollClient.learn = AutoRerollClient.LearnMode.NONE;
	}
}
