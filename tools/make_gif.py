"""Builds the demo GIF from the frames LivelySlotsGifRecorder took (port-tests).

Frames were taken once per 50 ms tick with the animations slowed down SLOWDOWN times, so each frame is
shown for 50 / SLOWDOWN ms: the GIF plays at real speed. The inventory panel is found by its light grey
background and cropped out of the full window.

Usage: python tools/make_gif.py <screenshots dir> <output.gif>   (needs Pillow)
"""
import sys
from pathlib import Path

from PIL import Image

SLOWDOWN = 2.5
TICK_MS = 50
PANEL = (198, 198, 198)  # vanilla container background
MARGIN = 4


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
    paths = sorted(frames_dir.glob("*gif_*.png"))
    if not paths:
        sys.exit(f"no gif_*.png frames in {frames_dir}")
    box = panel_box(Image.open(paths[-1]))
    frames = [Image.open(p).convert("RGB").crop(box) for p in paths]
    # One shared palette keeps colours stable from frame to frame.
    palette = frames[0].quantize(colors=255, method=Image.Quantize.MEDIANCUT)
    gif = [f.quantize(palette=palette, dither=Image.Dither.NONE) for f in frames]
    delay = round(TICK_MS / SLOWDOWN)
    gif[0].save(out, save_all=True, append_images=gif[1:], duration=delay, loop=0, optimize=True, disposal=1)
    print(f"{out}: {len(gif)} frames, {delay} ms each, {box[2] - box[0]}x{box[3] - box[1]}")


if __name__ == "__main__":
    main()
