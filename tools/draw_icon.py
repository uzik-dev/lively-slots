"""Draws the mod icon: an inventory slot with a gem popping into it. 32x32 pixel art scaled to 128x128.

Usage: python tools/draw_icon.py <output.png>
"""
import struct
import sys
import zlib

SIZE, SCALE = 32, 4
CLEAR = (0, 0, 0, 0)
px = [[CLEAR] * SIZE for _ in range(SIZE)]


def rgb(h):
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), 255)


def rect(x0, y0, x1, y1, color):
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            px[y][x] = color


# Slot: inset look, dark top-left edge, light bottom-right edge.
rect(3, 9, 28, 28, rgb("8b8b8b"))
rect(3, 9, 28, 9, rgb("373737"))
rect(3, 9, 3, 28, rgb("373737"))
rect(3, 28, 28, 28, rgb("ffffff"))
rect(28, 9, 28, 28, rgb("ffffff"))
rect(2, 8, 29, 8, rgb("1f1f1f"))
rect(2, 8, 2, 29, rgb("1f1f1f"))
rect(2, 29, 29, 29, rgb("1f1f1f"))
rect(29, 8, 29, 29, rgb("1f1f1f"))

# Shadow of the gem on the slot floor, smaller than the gem: it is above the slot.
rect(11, 25, 20, 26, rgb("6f6f6f"))

# Gem: a rhombus, lit from the top-left.
gem = {"outline": rgb("0b4f3a"), "dark": rgb("13805c"), "mid": rgb("1fb57f"), "light": rgb("63e6b0"), "shine": rgb("e9fff6")}
cx, top, half = 15.5, 4, 10
for y in range(top, top + 2 * half + 1):
    span = half - abs(y - (top + half))
    x0, x1 = int(cx - span), int(cx + span + 1)
    for x in range(x0, x1 + 1):
        edge = x in (x0, x1) or y in (top, top + 2 * half)
        if edge:
            px[y][x] = gem["outline"]
        elif y < top + half and x < cx:
            px[y][x] = gem["light"]
        elif y < top + half:
            px[y][x] = gem["mid"]
        elif x < cx:
            px[y][x] = gem["mid"]
        else:
            px[y][x] = gem["dark"]
for x, y in ((12, 9), (13, 8), (13, 9), (14, 7)):
    px[y][x] = gem["shine"]

# Motion lines on the right and a sparkle on the left: it just popped in.
for y, x0, x1 in ((7, 25, 28), (11, 27, 30), (15, 25, 29)):
    rect(x0, y, x1, y, rgb("ffffff"))
spark = rgb("ffd84a")
for x, y in ((4, 2), (4, 3), (4, 4), (3, 3), (5, 3)):
    px[y][x] = spark
px[3][4] = rgb("fff6c8")


def png(path):
    rows = b""
    for y in range(SIZE * SCALE):
        rows += b"\x00" + b"".join(bytes(px[y // SCALE][x // SCALE]) for x in range(SIZE * SCALE))

    def chunk(tag, data):
        return struct.pack(">I", len(data)) + tag + data + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)

    w = h = SIZE * SCALE
    data = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
    data += chunk(b"IDAT", zlib.compress(rows, 9)) + chunk(b"IEND", b"")
    open(path, "wb").write(data)


png(sys.argv[1])
