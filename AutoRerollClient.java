package pl.autoreroll;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoRerollClient implements ClientModInitializer {
	public enum LearnMode { NONE, REROLL, PREVIEW }

	public static final RerollConfig CONFIG = RerollConfig.load();

	/** Ustawiane z GUI configu: nastepne klikniecie slotu w GUI serwera zapisuje jego ID. */
	public static LearnMode learn = LearnMode.NONE;

	private static boolean running = false;
	private static int rolls = 0;
	private static int cooldown = 0;
	private static int misses = 0;

	private static KeyBinding toggleKey;
	private static KeyBinding configKey;
	private static Pattern pattern;

	@Override
	public void onInitializeClient() {
		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.autoreroll.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.autoreroll"));
		configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.autoreroll.config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.autoreroll"));

		// Klawisz start/stop dziala wewnatrz GUI zaklinania (klawisze z KeyBinding nie dzialaja w ekranach).
		ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
			if (screen instanceof HandledScreen<?>) {
				ScreenKeyboardEvents.afterKeyPress(screen).register((s, key, scancode, modifiers) -> {
					if (toggleKey.matchesKey(key, scancode)) {
						if (running) stop(client, "Zatrzymano.");
						else start(client);
					}
				});
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(AutoRerollClient::tick);
	}

	private static void msg(MinecraftClient client, String text) {
		if (client.player != null) {
			client.player.sendMessage(Text.literal("§6[AutoReroll] §f" + text), false);
		}
	}

	public static void notify(MinecraftClient client, String text) {
		msg(client, text);
	}

	private static void start(MinecraftClient client) {
		if (CONFIG.rerollSlot < 0) {
			msg(client, "§cNajpierw ustaw slot rerolla (klawisz O -> 'Wybierz slot rerolla').");
			return;
		}
		try {
			pattern = Pattern.compile(CONFIG.regex);
		} catch (Exception e) {
			msg(client, "§cZly regex w configu: " + e.getMessage());
			return;
		}
		running = true;
		rolls = 0;
		misses = 0;
		cooldown = 0;
		msg(client, String.format("Start! Szukam %.1f%% - %.1f%%. Zatrzymanie: %s",
				CONFIG.min, CONFIG.max, toggleKey.getBoundKeyLocalizedText().getString()));
	}

	private static void stop(MinecraftClient client, String reason) {
		if (running) {
			running = false;
			msg(client, reason + " (rolli: " + rolls + ")");
		}
	}

	private static void tick(MinecraftClient client) {
		if (!running) return;

		if (!(client.currentScreen instanceof HandledScreen<?> screen)
				|| client.player == null || client.interactionManager == null) {
			stop(client, "GUI zamkniete, stop.");
			return;
		}

		if (cooldown > 0) {
			cooldown--;
			return;
		}

		Double percent = readPercent(screen);

		if (percent == null) {
			misses++;
			if (misses > 10) {
				stop(client, "§cNie znalazlem procentu w GUI. Sprawdz slot miecza / regex w configu.");
			}
			cooldown = Math.max(1, CONFIG.delayTicks);
			return;
		}
		misses = 0;

		if (percent >= CONFIG.min && percent <= CONFIG.max) {
			stop(client, String.format("§aTrafiono %.1f%%!", percent));
			return;
		}

		if (CONFIG.maxRolls > 0 && rolls >= CONFIG.maxRolls) {
			stop(client, "Osiagnieto limit rolli.");
			return;
		}

		client.interactionManager.clickSlot(
				screen.getScreenHandler().syncId, CONFIG.rerollSlot, 0, SlotActionType.PICKUP, client.player);
		rolls++;
		cooldown = Math.max(1, CONFIG.delayTicks);
		client.player.sendMessage(Text.literal(String.format("§7Roll #%d: %.1f%%", rolls, percent)), true);
	}

	private static Double readPercent(HandledScreen<?> screen) {
		var slots = screen.getScreenHandler().slots;

		if (CONFIG.previewSlot >= 0) {
			if (CONFIG.previewSlot >= slots.size()) return null;
			return parse(slots.get(CONFIG.previewSlot).getStack());
		}

		for (Slot slot : slots) {
			if (slot.inventory instanceof PlayerInventory) continue;
			if (slot.id == CONFIG.rerollSlot) continue;
			Double p = parse(slot.getStack());
			if (p != null) return p;
		}
		return null;
	}

	private static Double parse(ItemStack stack) {
		if (stack.isEmpty()) return null;

		StringBuilder sb = new StringBuilder(stack.getName().getString());
		LoreComponent lore = stack.get(DataComponentTypes.LORE);
		if (lore != null) {
			for (Text line : lore.lines()) {
				sb.append('\n').append(line.getString());
			}
		}

		Matcher m = pattern.matcher(sb);
		if (m.find()) {
			try {
				return Double.parseDouble(m.group(1).replace(',', '.'));
			} catch (NumberFormatException ignored) {
			}
		}
		return null;
	}

	public static KeyBinding getConfigKey() {
		return configKey;
	}
}
