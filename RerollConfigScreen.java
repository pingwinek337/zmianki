package pl.autoreroll;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class RerollConfigScreen extends Screen {
	private final Screen parent;
	private TextFieldWidget minField, maxField, delayField, limitField, rerollField, previewField;

	public RerollConfigScreen(Screen parent) {
		super(Text.literal("AutoReroll"));
		this.parent = parent;
	}

	private static String fmt(double d) {
		return d == Math.floor(d) ? String.valueOf((long) d) : String.valueOf(d);
	}

	private TextFieldWidget field(int x, int y, String value) {
		TextFieldWidget f = new TextFieldWidget(textRenderer, x, y, 150, 18, Text.empty());
		f.setMaxLength(12);
		f.setText(value);
		addDrawableChild(f);
		return f;
	}

	@Override
	protected void init() {
		RerollConfig c = AutoRerollClient.CONFIG;
		int left = width / 2 - 155;
		int right = width / 2 + 5;
		int top = height / 4 - 10;

		minField = field(left, top + 12, fmt(c.min));
		maxField = field(left, top + 52, fmt(c.max));
		delayField = field(left, top + 92, String.valueOf(c.delayTicks));

		rerollField = field(right, top + 12, String.valueOf(c.rerollSlot));
		previewField = field(right, top + 52, String.valueOf(c.previewSlot));
		limitField = field(right, top + 92, String.valueOf(c.maxRolls));

		addDrawableChild(ButtonWidget.builder(Text.literal("Wybierz slot rerolla"), b -> learn(AutoRerollClient.LearnMode.REROLL))
				.dimensions(left, top + 120, 150, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Wybierz slot miecza"), b -> learn(AutoRerollClient.LearnMode.PREVIEW))
				.dimensions(right, top + 120, 150, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Zapisz i zamknij"), b -> close())
				.dimensions(width / 2 - 100, top + 148, 200, 20).build());
	}

	private void learn(AutoRerollClient.LearnMode mode) {
		save();
		AutoRerollClient.learn = mode;
		client.setScreen(null);
		AutoRerollClient.notify(client, "Otworz GUI zaklinania i kliknij odpowiedni slot (klikniecie nic nie zrobi na serwerze).");
	}

	private void save() {
		RerollConfig c = AutoRerollClient.CONFIG;
		try {
			double min = Double.parseDouble(minField.getText().replace(',', '.'));
			double max = Double.parseDouble(maxField.getText().replace(',', '.'));
			c.min = Math.min(min, max);
			c.max = Math.max(min, max);
		} catch (NumberFormatException ignored) {
		}
		c.delayTicks = parseInt(delayField, c.delayTicks, 1);
		c.maxRolls = parseInt(limitField, c.maxRolls, 0);
		c.rerollSlot = parseInt(rerollField, c.rerollSlot, -1);
		c.previewSlot = parseInt(previewField, c.previewSlot, -1);
		c.save();
	}

	private static int parseInt(TextFieldWidget f, int fallback, int minValue) {
		try {
			return Math.max(minValue, Integer.parseInt(f.getText().trim()));
		} catch (NumberFormatException e) {
			return fallback;
		}
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);
		int left = width / 2 - 155;
		int right = width / 2 + 5;
		int top = height / 4 - 10;

		ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("AutoReroll - zakres procentu"), width / 2, top - 22, 0xFFFFFF);

		ctx.drawTextWithShadow(textRenderer, "Zatrzymaj od (%)", left, top, 0xAAAAAA);
		ctx.drawTextWithShadow(textRenderer, "Zatrzymaj do (%)", left, top + 40, 0xAAAAAA);
		ctx.drawTextWithShadow(textRenderer, "Opoznienie (ticki)", left, top + 80, 0xAAAAAA);
		ctx.drawTextWithShadow(textRenderer, "Slot rerolla (ID)", right, top, 0xAAAAAA);
		ctx.drawTextWithShadow(textRenderer, "Slot miecza (-1 = auto)", right, top + 40, 0xAAAAAA);
		ctx.drawTextWithShadow(textRenderer, "Limit rolli (0 = brak)", right, top + 80, 0xAAAAAA);
	}

	@Override
	public void close() {
		save();
		client.setScreen(parent);
	}
}
