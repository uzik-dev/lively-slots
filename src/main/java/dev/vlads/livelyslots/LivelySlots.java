package dev.vlads.livelyslots;

import net.fabricmc.api.ClientModInitializer;

public class LivelySlots implements ClientModInitializer {
    public static final String MOD_ID = "lively_slots";
    public static LivelySlotsConfig config = new LivelySlotsConfig();

    @Override
    public void onInitializeClient() {
        config = LivelySlotsConfig.load();
    }
}
