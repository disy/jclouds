/*
 * 
 */
package org.jclouds.imagestore.blobstore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.blobstore.imagegenerator.IBytesToImagePainter;

// TODO: Auto-generated Javadoc
/**
 * The Class HexalLayeredByteToPixelPainter.
 */
public class HexadecimalLayeredBytesToImagePainter implements IBytesToImagePainter {

    /** The red colors. */
    private final Color[] redColors = new Color[] {
        new Color(0f, 0f, 0f), new Color(0.125f, 0f, 0f), new Color(0.250f, 0f, 0f),
        new Color(0.375f, 0f, 0f), new Color(0.5f, 0f, 0f), new Color(0.625f, 0f, 0f),
        new Color(0.750f, 0f, 0f), new Color(0.875f, 0f, 0f), new Color(1f, 0f, 0f),
        new Color(0.14f, 0, 0.5f), new Color(0.285f, 0, 0.5f), new Color(0.428f, 0, 0.5f),
        new Color(0.571f, 0, 0.5f), new Color(0.714f, 0, 0.5f), new Color(0.857f, 0, 0.5f),
        new Color(1f, 0, 0.5f)
    };

    /** The blue colors. */
    private final Color[] blueColors = new Color[] {
        new Color(0f, 0f, 0f), new Color(0f, 0f, 0.125f), new Color(0f, 0f, 0.250f),
        new Color(0f, 0f, 0.375f), new Color(0f, 0f, 0.5f), new Color(0f, 0f, 0.625f),
        new Color(0f, 0f, 0.750f), new Color(0f, 0f, 0.875f), new Color(0f, 0f, 1f),
        new Color(0f, 0.25f, 0.14f), new Color(0f, 0.25f, 0.285f), new Color(0f, 0.25f, 0.428f),
        new Color(0f, 0.25f, 0.571f), new Color(0f, 0.25f, 0.714f), new Color(0f, 0.25f, 0.857f),
        new Color(0f, 0.25f, 1f)
    };

    /** The threshold. */
    private final int threshold = 15;
    
    /** Pixels needed for one Byte. */
    private final float PIXELS_PER_BYTE = 0.5f;

    @Override
    public float bytesPerPixel() {
        return PIXELS_PER_BYTE;
    }

    @Override
    public BufferedImage storeBytesInImage(BufferedImage bi, byte[] bs) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();

        final int len = bs.length;

        int bp = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x += 2) {
                if (bp >= len)
                    break;
                byte b = bs[bp++];
                drawByteToColoredImage(g, x, y, b);
            }
        }

        return bi;
    }

    /**
     * Draw byte to colored image.
     * 
     * @param g
     *            the g
     * @param x
     *            the x
     * @param y
     *            the y
     * @param b
     *            the b
     */
    private void drawByteToColoredImage(Graphics g, int x, int y, byte b) {
        final int it = b & 0xFF;
        final String hex = Integer.toString(it, 16);
        final int l = hex.length();

        if (l == 1) {

            int dc = Integer.parseInt(hex, 16);

            g.setColor(redColors[0]);
            g.drawLine(x, y, x, y);

            g.setColor(redColors[dc]);
            g.drawLine(x + 1, y, x + 1, y);

        } else {

            for (int i = 0; i < l; i++) {

                String val = hex.substring(i, i + 1);

                int dc = Integer.parseInt(val, 16);

                g.setColor(redColors[dc]);

                int xx = x + i;
                g.drawLine(xx, y, xx, y);
            }
        }
    }

    @Override
    public byte[] getBytesFromImage(final BufferedImage img) {

        final ArrayList<Byte> li = new ArrayList<Byte>();

        final int w = img.getWidth();
        final int h = img.getHeight();

        String hex = "";

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                hex += getHexFromPixel(img.getRGB(x, y));

                if (x % 2 == 1) {

                    byte b = (byte)Integer.parseInt(hex, 16);
                    li.add(b);
                    hex = "";
                }

            }
        }
        return BytesToImagePainterHelper.arrayListToByteArray(li);

    }

    /**
     * Gets the hex from pixel.
     * 
     * @param pix
     *            the pix
     * @return the hex from pixel
     */
    private String getHexFromPixel(final int pix) {
        int th = threshold;

        Color c = new Color(pix);

        int red = c.getRed();
        int green = c.getBlue();

        for (int i = 0; i < redColors.length; i++) {
            int cred = redColors[i].getRed();
            int cgreen = redColors[i].getBlue();

            if ((red - th <= cred && cred <= red + th) && (green - th <= cgreen && cgreen <= green + th)) {
                return Integer.toHexString(i);
            }
        }

        return "0";
    }

}
