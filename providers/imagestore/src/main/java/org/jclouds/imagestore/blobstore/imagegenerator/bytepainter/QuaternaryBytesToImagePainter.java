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
 * The Class TetraGreyByteToPixelPainter.
 */
public class QuaternaryBytesToImagePainter implements BytesToImagePainter {

    /** The grey colors. */
    Color[] greyColors = new Color[] {
        Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.BLACK
    };

    @Override
    public BufferedImage storeBytesInImage(BufferedImage bi, byte[] bs) {

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

                    greys = getGreyFromByte(currB);
                }

                g.setColor(greyColors[greys[pos]]);

                g.drawLine(x, y, x, y);
            }
        }
        return bi;
    }

    /**
     * Gets the grey from byte.
     * 
     * @param b
     *            the b
     * @return the grey from byte
     */
    private int[] getGreyFromByte(final byte b) {
        final int it = b & 0xFF;
        String tetra = Integer.toString(it, 4);
        int[] byteColors = new int[4];
        final int l = 4;

        while (tetra.length() < l) {
            tetra = "0" + tetra;
        }

        for (int i = 0; i < l; i++) {

            String val = tetra.substring(i, i + 1);

            int dc = Integer.parseInt(val, 4);

            byteColors[i] = dc;
        }
        return byteColors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.imagegenerator.bytepainter.ByteToPixelPainter#getBytesFromPixels(java.awt.image.BufferedImage,
     * java.util.ArrayList)
     */
    @Override
    public byte[] getBytesFromImage(BufferedImage img) {

        final ArrayList<Byte> li = new ArrayList<Byte>();
        final int w = img.getWidth();
        final int h = img.getHeight();

        String[] hepts = new String[] {
            "", "", ""
        };

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;

                getHeptsFromPixel(img.getRGB(x, y), hepts);

                if (pix % 3 == 2) {

                    for (int i = 0; i < 3; i++) {
                        byte b = (byte)Integer.parseInt(hepts[i], 7);
                        li.add(b);
                    }

                    hepts = new String[] {
                        "", "", ""
                    };
                }
            }
        }
        return BytesToImagePainterHelper.arrayListToByteArray(li);
    }

    /**
     * Gets the hepts from pixel.
     * 
     * @param pix
     *            the pix
     * @param hepts
     *            the hepts
     * @return the hepts from pixel
     */
    private void getHeptsFromPixel(final int pix, String[] hepts) {

        Color c = new Color(pix);
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();

    }
}
