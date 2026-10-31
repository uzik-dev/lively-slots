package dev.vlads.livelyslots;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Per-screen memory of what each slot showed last frame. A slot whose contents changed since then gets a
 * short animation; the screen asks for the current offset and scale every time it draws the slot.
 */
public final class SlotAnimator {
    /** How long a shift-click source stays valid while its item lands in the destination slots. */
    private static final long QUICK_MOVE_WINDOW_MS = 300;

    public record Transform(float dx, float dy, float scale) {}

    private enum Kind { POP, PULSE, FLY }

    private static final class Track {
        ItemStack last;
        @Nullable Kind kind;
        long start;
        float fromDx, fromDy;

        Track(ItemStack last) {
            this.last = last;
        }
    }

    private record QuickMove(ItemStack stack, int x, int y, long at) {}

    /** How many animations have started since launch; read by tests. */
    public static int animationsStarted;

    private final Map<Slot, Track> tracks = new IdentityHashMap<>();
    private long openedAt;
    private @Nullable QuickMove quickMove;

    /** The screen (re)initialised: forget everything and treat the next contents as the starting point. */
    public void onOpen(long now) {
        tracks.clear();
        openedAt = now;
        quickMove = null;
    }

    public void onQuickMove(Slot source, long now) {
        if (source.hasItem()) quickMove = new QuickMove(source.getItem().copy(), source.x, source.y, now);
    }

    public @Nullable Transform update(Slot slot, long now) {
        LivelySlotsConfig config = LivelySlots.config;
        ItemStack stack = slot.getItem();
        Track track = tracks.get(slot);
        if (track == null) {
            tracks.put(slot, new Track(stack.copy()));
            return null;
        }
        if (!ItemStack.matches(track.last, stack)) {
            Kind kind = classify(track.last, stack, config);
            track.last = stack.copy();
            if (kind != null && now - openedAt >= config.openGraceMs) {
                track.kind = kind;
                track.start = now;
                track.fromDx = track.fromDy = 0;
                QuickMove move = quickMove;
                if (config.flyOnQuickMove && move != null && now - move.at <= QUICK_MOVE_WINDOW_MS
                        && (move.x != slot.x || move.y != slot.y)
                        && ItemStack.isSameItemSameComponents(move.stack, stack)) {
                    track.kind = Kind.FLY;
                    track.fromDx = move.x - slot.x;
                    track.fromDy = move.y - slot.y;
                }
                animationsStarted++;
            }
        }
        return track.kind == null ? null : transform(track, now, config);
    }

    private static @Nullable Kind classify(ItemStack before, ItemStack after, LivelySlotsConfig config) {
        if (after.isEmpty()) return null;
        if (before.isEmpty() || !ItemStack.isSameItemSameComponents(before, after)) return config.popIn ? Kind.POP : null;
        if (after.getCount() > before.getCount()) return config.pulseOnStackGrow ? Kind.PULSE : null;
        return null;
    }

    private static @Nullable Transform transform(Track track, long now, LivelySlotsConfig config) {
        int duration = switch (track.kind) {
            case POP -> config.popDurationMs;
            case PULSE -> config.pulseDurationMs;
            case FLY -> config.flyDurationMs;
        };
        float progress = duration <= 0 ? 1 : (now - track.start) / (float) duration;
        if (progress >= 1) {
            track.kind = null;
            return null;
        }
        return switch (track.kind) {
            case POP -> new Transform(0, 0, config.popStartScale + (1 - config.popStartScale) * easeOutBack(progress));
            case PULSE -> new Transform(0, 0, 1 + (config.pulseScale - 1) * (float) Math.sin(Math.PI * progress));
            case FLY -> {
                float left = 1 - easeOutCubic(progress);
                yield new Transform(track.fromDx * left, track.fromDy * left, 1);
            }
        };
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        float u = t - 1;
        return 1 + c3 * u * u * u + c1 * u * u;
    }

    private static float easeOutCubic(float t) {
        float u = 1 - t;
        return 1 - u * u * u;
    }
}
