/*
 * 
 */
package org.jclouds.imagestore.blobstore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.blobstore.imagegenerator.BytesToImagePainter;

// TODO: Auto-generated Javadoc
/**
 * The Class HexalByteToPixelPainter.
 */
public class HexadecimalBytesToImagePainter implements BytesToImagePainter {

    /** The colors. */
    private final Color[] colors = new Color[] {
        new Color(1f, 1f, 1f), new Color(0f, 0f, 0.5f), new Color(0f, 0.5f, 0.5f), new Color(0.5f, 0f, 0.5f),
        new Color(0.5f, 0.5f, 0f), new Color(0.5f, 0.5f, 0.5f), new Color(0f, 0f, 1f),
        new Color(0f, 0.5f, 1f), new Color(0.5f, 0.5f, 1f), new Color(0f, 1f, 0f), new Color(0f, 1f, 0.5f),
        new Color(0.5f, 1f, 0f), new Color(1f, 1f, 0.5f), new Color(0.5f, 1f, 0.5f), new Color(1f, 0f, 0f),
        new Color(0f, 0f, 0f)
    };

    /** Bytes needed per pixel. */
    public final float BYTES_PER_PIXEL = 2;

    @Override
    public float bytesPerPixel() {
        return BYTES_PER_PIXEL;
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

    @Override
    public byte[] getBytesFromImage(BufferedImage img) {

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
    private String getHexFromPixel(int pix) {
        int th = 22;

        Color c = new Color(pix);

        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();

        for (int i = 0; i < colors.length; i++) {
            int cred = colors[i].getRed();
            int cgreen = colors[i].getGreen();
            int cblue = colors[i].getBlue();

            if ((red - th <= cred && cred <= red + th) && (green - th <= cgreen && cgreen <= green + th)
                && (blue - th <= cblue && cblue <= blue + th)) {
                return Integer.toHexString(i);
            }
        }

        return "0";
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
    private void drawByteToColoredImage(final Graphics g, final int x, final int y, final byte b) {
        final int it = b & 0xFF;
        final String hex = Integer.toString(it, 16);
        final int l = hex.length();

        if (l == 1) {

            int dc = Integer.parseInt(hex, 16);

            g.setColor(colors[0]);
            g.drawLine(x, y, x, y);

            g.setColor(colors[dc]);
            g.drawLine(x + 1, y, x + 1, y);

        } else {

            for (int i = 0; i < l; i++) {

                String val = hex.substring(i, i + 1);

                int dc = Integer.parseInt(val, 16);

                g.setColor(colors[dc]);

                int xx = x + i;
                g.drawLine(xx, y, xx, y);
            }
        }
    }
}
