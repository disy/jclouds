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
 * The Class TetralLayeredByteToPixelPainter.
 */
public class QuaternaryLayeredBytesToImagePainter implements BytesToImagePainter {

    /** The colors. */
    private final Color[][] colors = BytesToImagePainterHelper.generateUniformlyDistributedColors(4);

    @Override
    public BufferedImage storeBytesInImage(BufferedImage bi, byte[] bs) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();

        final int len = bs.length;
        int[] currByteColor = null;

        int bp = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x += 4) {
                for (int layer = 0; layer < 3; layer++) {

                    if (bp >= len)
                        break;

                    byte b = bs[bp++];

                    int[] bc = getColorFromByte(b, layer);

                    if (currByteColor == null) {
                        currByteColor = bc;
                        continue;
                    }

                    for (int c = 0; c < 4; c++) {
                        currByteColor[c] = currByteColor[c] + bc[c];
                    }

                    if (layer == 2) {
                        for (int c = 0; c < 4; c++) {
                            Color nc = new Color(currByteColor[c]);

                            g.setColor(nc);

                            g.drawLine(x + c, y, x + c, y);

                        }
                        currByteColor = null;
                    }
                }
            }
        }
        return bi;

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
        String tetra = Integer.toString(it, 4);
        int[] byteColors = new int[4];
        final int l = 4;

        while (tetra.length() < l) {
            tetra = "0" + tetra;
        }

        for (int i = 0; i < l; i++) {

            String val = tetra.substring(i, i + 1);

            int dc = Integer.parseInt(val, 4);

            byteColors[i] = colors[layer][dc].getRGB();
        }
        return byteColors;
    }

    public byte[] getBytesFromImage(BufferedImage img) {

        final ArrayList<Byte> li = new ArrayList<Byte>();
        final int w = img.getWidth();
        final int h = img.getHeight();

        String[] tetras = new String[] {
            "", "", ""
        };

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                getTetrasFromPixel(img.getRGB(x, y), tetras);

                if (x % 4 == 3) {

                    for (int i = 0; i < 3; i++) {
                        byte b = (byte)Integer.parseInt(tetras[i], 4);
                        li.add(b);
                    }

                    tetras = new String[] {
                        "", "", ""
                    };
                }
            }
        }

        return BytesToImagePainterHelper.arrayListToByteArray(li);
    }

    /**
     * Gets the tetras from pixel.
     * 
     * @param pix
     *            the pix
     * @param tetras
     *            the tetras
     * @return the tetras from pixel
     */
    private void getTetrasFromPixel(final int pix, String[] tetras) {

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

            tetras[l] += Integer.toString(idx, 4);
        }
    }
}
