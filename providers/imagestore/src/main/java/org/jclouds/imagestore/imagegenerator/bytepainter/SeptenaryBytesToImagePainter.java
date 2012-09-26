package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

/**
 * This Class offers a byte painter.
 * 
 * Numeral System: Septenary
 * Layers: 1
 * 1 Byte = 3 Pixel
 * 
 * @author Wolfgang Miller
 */
public class SeptenaryBytesToImagePainter implements IBytesToImagePainter {

    /** The used numeral system. */
    private static final int NUMERAL_SYSTEM = 7;
    /** Pixels needed for one Byte. */
    private static final float PIXELS_PER_BYTE = 3;

    /** The colors. */
    private final Color[] colors = new Color[] {
        new Color(0f, 0f, 0f), new Color(1f, 0f, 0f), new Color(0f, 1f, 0f), new Color(0f, 0f, 1f),
        new Color(1f, 0f, 1f), new Color(1f, 1f, 0f), new Color(0f, 1f, 1f)
    };

    @Override
    public float pixelsPerByte() {
        return PIXELS_PER_BYTE;
    }

    @Override
    public BufferedImage storeBytesInImage(final BufferedImage bi, final byte[] bs) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();

        int len = bs.length;
        int bsPos = 0;
        int[] greys = null;

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;
                final int pos = pix % 4;

                if (pix % 4 == 0) {

                    /* if picture is too small for next bytes return */
                    if ((y == h - 1) && (x + 3 > w))
                        return bi;

                    if (bsPos >= len) {
                        break;
                    }

                    byte currB = bs[bsPos++];

                    greys = getColorFromByte(currB);
                }

                g.setColor(colors[greys[pos]]);

                g.drawLine(x, y, x, y);
            }
        }
        return bi;
    }

    /**
     * Gets color from byte.
     * 
     * @param b
     *            the byte
     * @return the color from byte
     */
    private int[] getColorFromByte(final byte b) {
        final int it = b & 0xFF;
        String septenary = Integer.toString(it, NUMERAL_SYSTEM);
        int[] byteColors = new int[NUMERAL_SYSTEM];
        final int l = 4;

        while (septenary.length() < l) {
            septenary = "0" + septenary;
        }

        for (int i = 0; i < l; i++) {

            String val = septenary.substring(i, i + 1);

            int dc = Integer.parseInt(val, NUMERAL_SYSTEM);

            byteColors[i] = dc;
        }
        return byteColors;
    }

    @Override
    public byte[] getBytesFromImage(final BufferedImage img) {

        final ArrayList<Byte> li = new ArrayList<Byte>();
        final int w = img.getWidth();
        final int h = img.getHeight();
        final int mod = (int)(PIXELS_PER_BYTE - 1);

        String septenary = "";

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;

                septenary +=
                    HBytesToImagePainterHelper.getNumericalValueFromPixelColor(colors, img.getRGB(x, y),
                        NUMERAL_SYSTEM);

                if (pix % PIXELS_PER_BYTE == mod) {
                    byte b = (byte)Integer.parseInt(septenary, NUMERAL_SYSTEM);
                    li.add(b);
                    septenary = "";
                }
            }
        }
        return HBytesToImagePainterHelper.arrayListToByteArray(li);
    }
}
