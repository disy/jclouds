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
 * The Class HeptalLayeredByteToPixelPainter.
 */
public class HeptalLayeredBytesToImagePainter implements BytesToImagePainter {

    /** The colors. */
    private final Color[][] colors = ColorGenerator.generateUniformlyDistributedColors(7);

    /*
     * (non-Javadoc)
     * 
     * @see org.imagegenerator.bytepainter.ByteToPixelPainter#saveBytesToPixels(java.awt.image.BufferedImage,
     * byte[])
     */
    @Override
    public BufferedImage storeBytesInImage(BufferedImage img, byte[] bs) {

        final int w = img.getWidth();
        final int h = img.getHeight();
        final Graphics g = img.getGraphics();

        int[] currByteColor = null;
        int len = bs.length;

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;
                final int pos = pix % 3;

                if (pix % 3 == 0) {

                    currByteColor = null;

                    for (int layer = 0; layer < 3; layer++) {

                        final int bsPos = pix + layer;

                        /* if picture is too small for next bytes return */
                        if ((y == h - 1) && (x + 3 > w))
                            return img;

                        if (bsPos >= len) {
                            if (layer == 0)
                                return img;
                            else
                                break;
                        }

                        byte currB = bs[bsPos];

                        int[] bc = getColorFromByte(currB, layer);

                        if (currByteColor == null) {
                            currByteColor = bc;
                            continue;
                        }

                        for (int c = 0; c < 3; c++) {
                            currByteColor[c] = currByteColor[c] + bc[c];
                        }
                    }
                }

                Color nc = new Color(currByteColor[pos]);
                g.setColor(nc);

                g.drawLine(x, y, x, y);

            }
        }
        return img;
    }

    /**
     * Gets the color from byte.
     * 
     * @param b
     *            the b
     * @param layer
     *            the layer
     * @return the color from byte
     */
    private int[] getColorFromByte(final byte b, final int layer) {
        final int it = b & 0xFF;
        String hept = Integer.toString(it, 7);
        int[] byteColors = new int[3];

        while (hept.length() < 3) {
            hept = "0" + hept;
        }

        final int l = hept.length();

        for (int i = 0; i < l; i++) {

            String val = hept.substring(i, i + 1);

            int dc = Integer.parseInt(val, 7);

            byteColors[i] = colors[layer][dc].getRGB();

        }
        return byteColors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.imagegenerator.bytepainter.ByteToPixelPainter#getBytesFromPixels(java.awt.image.BufferedImage,
     * java.util.ArrayList)
     */
    public byte[] getBytesFromImage(BufferedImage img) {
        final ArrayList<Byte> al = new ArrayList<Byte>();

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
                        al.add(b);
                    }

                    hepts = new String[] {
                        "", "", ""
                    };
                }
            }
        }
        
       return ColorGenerator.arrayListToByteArray(al);
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

        for (int l = 0; l < 3; l++) {
            int dist = -1;
            int idx = -1;

            if (l == 0) {

                for (int i = 0; i < colors[l].length; i++) {
                    int cred = colors[l][i].getRed();

                    int currDist = Math.abs(cred - red);

                    if (dist == -1 || currDist < dist) {
                        dist = currDist;
                        idx = i;
                    }
                }

            } else if (l == 1) {

                for (int i = 0; i < colors[l].length; i++) {
                    int cgreen = colors[l][i].getGreen();

                    int currDist = Math.abs(cgreen - green);

                    if (dist == -1 || currDist < dist) {
                        dist = currDist;
                        idx = i;
                    }
                }
            } else {

                for (int i = 0; i < colors[l].length; i++) {
                    int cblue = colors[l][i].getBlue();

                    int currDist = Math.abs(cblue - blue);

                    if (dist == -1 || currDist < dist) {
                        dist = currDist;
                        idx = i;
                    }
                }
            }
            hepts[l] += Integer.toString(idx, 7);
        }
    }
    
    public int [] getImageWidthAndHeight(int byteArrayLength){
        int w = 2048;
        int h = (int) (byteArrayLength / (float) w) + 1;
        return new int[]{w, h};
    }

}
