"""Builds the demo GIF from the frames LivelySlotsGifRecorder took (port-tests).

Frames were taken once per 50 ms tick with the animations slowed down SLOWDOWN times, so each frame is
shown for 50 / SLOWDOWN ms: the GIF plays at real speed. The inventory panel is found by its light grey
background and cropped out of the full window.

Usage: python tools/make_gif.py <screenshots dir> <output.gif> [scene prefix, default "gif"]   (needs Pillow)
"""
import sys
from pathlib import Path

from PIL import Image

SLOWDOWN = 2.5
TICK_MS = 50
PANEL = (198, 198, 198)  # vanilla container background
MARGIN = 4
UPSCALE = 2  # crisp pixels on the Modrinth page


def panel_box(image):
    """Bounding box of the inventory panel: pixels of the vanilla container grey."""
    rgb = image.convert("RGB")
    w, h = rgb.size
    px = rgb.load()
    xs, ys = [], []
    for y in range(0, h, 2):
        for x in range(0, w, 2):
            if px[x, y] == PANEL:
                xs.append(x)
                ys.append(y)
    if not xs:
        return 0, 0, w, h
    return (max(0, min(xs) - MARGIN), max(0, min(ys) - MARGIN), min(w, max(xs) + MARGIN), min(h, max(ys) + MARGIN))


def main():
    frames_dir, out = Path(sys.argv[1]), sys.argv[2]
    prefix = sys.argv[3] if len(sys.argv) > 3 else "gif"
    paths = sorted(frames_dir.glob(f"*_{prefix}_*.png"))
    if not paths:
        sys.exit(f"no {prefix}_*.png frames in {frames_dir}")
    box = panel_box(Image.open(paths[-1]))
    frames = [Image.open(p).convert("RGB").crop(box) for p in paths]
    frames = [f.resize((f.width * UPSCALE, f.height * UPSCALE), Image.Resampling.NEAREST) for f in frames]
    # One shared palette keeps colours stable from frame to frame; it is built from frames spread over the
    # whole clip, so items that only show up later keep their own colours.
    samples = frames[:: max(1, len(frames) // 8)] + [frames[-1]]
    strip = Image.new("RGB", (frames[0].width, frames[0].height * len(samples)))
    for i, sample in enumerate(samples):
        strip.paste(sample, (0, i * sample.height))
    palette = strip.quantize(colors=255, method=Image.Quantize.MEDIANCUT)
    gif = [f.quantize(palette=palette, dither=Image.Dither.NONE) for f in frames]
    delay = round(TICK_MS / SLOWDOWN)
    gif[0].save(out, save_all=True, append_images=gif[1:], duration=delay, loop=0, optimize=True, disposal=1)
    print(f"{out}: {len(gif)} frames, {delay} ms each, {gif[0].width}x{gif[0].height}")


if __name__ == "__main__":
    main()
