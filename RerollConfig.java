package pl.autoreroll;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RerollConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("autoreroll.json");

	/** Dolna granica zakresu (w %). */
	public double min = 47.0;
	/** Gorna granica zakresu (w %). */
	public double max = 50.0;
	/** Odstep miedzy rollami w tickach (20 ticków = 1 sekunda). */
	public int delayTicks = 6;
	/** Maksymalna liczba rolli, 0 = bez limitu. */
	public int maxRolls = 0;
	/** ID slotu z przyciskiem rolowania w GUI, -1 = nie ustawiono. */
	public int rerollSlot = -1;
	/** ID slotu z mieczem/podgladem procentu, -1 = skanuj wszystkie sloty GUI serwera. */
	public int previewSlot = -1;
	/** Regex wyciagajacy procent z nazwy i opisu (lore) przedmiotu. Grupa 1 = liczba. */
	public String regex = "(\\d+(?:[.,]\\d+)?)\\s*%";

	public static RerollConfig load() {
		try {
			if (Files.exists(PATH)) {
				RerollConfig c = GSON.fromJson(Files.readString(PATH), RerollConfig.class);
				if (c != null) return c;
			}
		} catch (Exception e) {
			System.err.println("[AutoReroll] Nie udalo sie wczytac configu: " + e);
		}
		RerollConfig c = new RerollConfig();
		c.save();
		return c;
	}

	public void save() {
		try {
			Files.writeString(PATH, GSON.toJson(this));
		} catch (IOException e) {
			System.err.println("[AutoReroll] Nie udalo sie zapisac configu: " + e);
		}
	}
}
