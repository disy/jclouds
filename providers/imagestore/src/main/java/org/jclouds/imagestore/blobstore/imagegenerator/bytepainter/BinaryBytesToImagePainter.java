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
 * The Class BlackAndWhiteByteToPixelPainter.
 */
public class BinaryBytesToImagePainter implements BytesToImagePainter {

    /*
     * (non-Javadoc)
     * 
     * @see org.imagegenerator.bytepainter.ByteToPixelPainter#saveBytesToPixels(java.awt.image.BufferedImage,
     * byte[])
     */
    public BufferedImage storeBytesInImage(BufferedImage bi, byte[] bs) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();

        int len = bs.length;
        int bsPos = 0;
        boolean[] bw = null;

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;
                final int pos = pix % 8;

                if (pix % 8 == 0) {

                    /* if picture is too small for next bytes return */
                    if ((y == h - 1) && (x + 3 > w))
                        return bi;

                    if (bsPos >= len) {
                        break;
                    }

                    byte currB = bs[bsPos++];

                    bw = getBinaryFromByte(currB);
                }

                if (bw[pos]) {
                    g.setColor(Color.BLACK);
                } else {
                    continue;
                }

                g.drawLine(x, y, x, y);
            }
        }
        return bi;
    }

    /**
     * Gets the binary from byte.
     * 
     * @param b
     *            the b
     * @return the binary from byte
     */
    private boolean[] getBinaryFromByte(final byte b) {
        final int it = b & 0xFF;
        String bin = Integer.toString(it, 2);

        while (bin.length() < 8) {
            bin = "0" + bin;
        }

        final int l = bin.length();
        boolean[] bw = new boolean[l];

        for (int i = 0; i < l; i++) {
            if (bin.charAt(i) == '1') {
                bw[i] = true;
            }
        }
        return bw;
    }

    @Override
    public byte[] getBytesFromImage(BufferedImage img) {

        final ArrayList<Byte> li = new ArrayList<Byte>();
        final int w = img.getWidth();
        final int h = img.getHeight();

        String[] binary = new String[] {
            "", "", ""
        };

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;

                getHeptsFromPixel(img.getRGB(x, y), binary);

                if (pix % 3 == 2) {

                    for (int i = 0; i < 3; i++) {
                        byte b = (byte)Integer.parseInt(binary[i], 2);
                        li.add(b);
                    }

                    binary = new String[] {
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
