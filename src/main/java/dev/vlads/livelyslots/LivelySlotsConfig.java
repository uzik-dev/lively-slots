package dev.vlads.livelyslots;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Settings in config/lively_slots.properties; missing keys are written back with their defaults. */
public final class LivelySlotsConfig {
    private static final Logger LOG = LoggerFactory.getLogger(LivelySlots.MOD_ID);

    public boolean enabled = true;
    /** Item appears in a slot: grows from popStartScale to full size with a slight overshoot. */
    public boolean popIn = true;
    public int popDurationMs = 180;
    public float popStartScale = 0.5f;
    /** A stack gets bigger: short pulse. */
    public boolean pulseOnStackGrow = true;
    public int pulseDurationMs = 140;
    public float pulseScale = 1.18f;
    /** Shift-click: the item flies from the slot it left to the slot it landed in. */
    public boolean flyOnQuickMove = true;
    public int flyDurationMs = 200;
    /** The creative item list changes on every scroll, so it is left alone unless asked. */
    public boolean animateCreativeInventory = false;
    /** Slot contents arriving right after a screen opens are the initial sync, not changes. */
    public int openGraceMs = 200;

    public static LivelySlotsConfig load() {
        LivelySlotsConfig config = new LivelySlotsConfig();
        Path file = FabricLoader.getInstance().getConfigDir().resolve(LivelySlots.MOD_ID + ".properties");
        Properties props = new Properties();
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                props.load(reader);
            } catch (IOException e) {
                LOG.warn("Could not read {}, using defaults", file, e);
            }
        }
        config.enabled = bool(props, "enabled", config.enabled);
        config.popIn = bool(props, "popIn", config.popIn);
        config.popDurationMs = integer(props, "popDurationMs", config.popDurationMs);
        config.popStartScale = decimal(props, "popStartScale", config.popStartScale);
        config.pulseOnStackGrow = bool(props, "pulseOnStackGrow", config.pulseOnStackGrow);
        config.pulseDurationMs = integer(props, "pulseDurationMs", config.pulseDurationMs);
        config.pulseScale = decimal(props, "pulseScale", config.pulseScale);
        config.flyOnQuickMove = bool(props, "flyOnQuickMove", config.flyOnQuickMove);
        config.flyDurationMs = integer(props, "flyDurationMs", config.flyDurationMs);
        config.animateCreativeInventory = bool(props, "animateCreativeInventory", config.animateCreativeInventory);
        config.openGraceMs = integer(props, "openGraceMs", config.openGraceMs);
        try (Writer writer = Files.newBufferedWriter(file)) {
            props.store(writer, "Lively Slots. Durations are in milliseconds, scales are relative to the normal size.");
        } catch (IOException e) {
            LOG.warn("Could not write {}", file, e);
        }
        return config;
    }

    private static boolean bool(Properties props, String key, boolean fallback) {
        String value = props.getProperty(key);
        if (value == null) {
            props.setProperty(key, Boolean.toString(fallback));
            return fallback;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private static int integer(Properties props, String key, int fallback) {
        String value = props.getProperty(key);
        try {
            if (value != null) return Math.max(0, Integer.parseInt(value.trim()));
        } catch (NumberFormatException ignored) {
        }
        props.setProperty(key, Integer.toString(fallback));
        return fallback;
    }

    private static float decimal(Properties props, String key, float fallback) {
        String value = props.getProperty(key);
        try {
            if (value != null) return Float.parseFloat(value.trim());
        } catch (NumberFormatException ignored) {
        }
        props.setProperty(key, Float.toString(fallback));
        return fallback;
    }
}
