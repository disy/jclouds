package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

/**
 * This abstract class offers an abstraction to create different layered byte painters.
 * 
 * @author Wolfgang Miller, University of Konstanz.
 * 
 */
public abstract class AAbstractLayeredBytesToImagePainter implements IBytesToImagePainter {

    /** The amount of image layers. */
    private static final int LAYERS = 3;
    /** The numeral system. */
    private final int numeralSystem;
    /** The amount of pixels needed for one byte in one layer. */
    private final int pixelsPerBytePerLayer;

    /** The different pixel colors. */
    private final Color[][] colors;

    /**
     * Constructor. Generates a layered byte painter with the values given by the subclass.
     * 
     * @param numSys
     *            The numeral system
     * @param ppBpL
     *            The pixels amount of pixels needed for one byte per layer
     */
    public AAbstractLayeredBytesToImagePainter(final int numSys, final int ppBpL) {
        numeralSystem = numSys;
        colors = HBytesToImagePainterHelper.generateLayeredUniformlyDistributedColors(numeralSystem);
        pixelsPerBytePerLayer = ppBpL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int getImageType();

    /**
     * {@inheritDoc}
     */
    @Override
    public float pixelsPerByte() {
        return pixelsPerBytePerLayer / (float)LAYERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage storeBytesInImage(final BufferedImage image, final byte[] bs) {

        final int w = image.getWidth();
        final int h = image.getHeight();
        final Graphics g = image.getGraphics();

        int[] currByteColor = null;
        int len = bs.length;

        // postion in byte-array
        int bp = 0;
        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                // amount of used pixels
                final int pix = hpix + x;
                // if pos == 0 the new byte buckets start
                final int pos = pix % pixelsPerBytePerLayer;

                if (pos == 0) {

                    currByteColor = null;

                    for (int layer = 0; layer < LAYERS; layer++) {

                        if (bp >= len) {
                            // if layer == 0 no bytes to be drawn, else break the loop and draw the bytes of
                            // the layers above
                            if (layer == 0)
                                return image;
                            else
                                break;
                        }

                        byte b = bs[bp++];

                        int[] bc =
                            getLayeredColorsFromByte(colors, b, layer, numeralSystem, pixelsPerBytePerLayer);

                        if (currByteColor == null) {
                            currByteColor = bc;
                            continue;
                        }

                        for (int c = 0; c < pixelsPerBytePerLayer; c++) {
                            currByteColor[c] = currByteColor[c] + bc[c];
                        }
                    }
                }

                Color nc = new Color(currByteColor[pos]);
                g.setColor(nc);
                g.drawLine(x, y, x, y);
            }
        }
        return image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytesFromImage(final BufferedImage img) {
        final ArrayList<Byte> al = new ArrayList<Byte>();

        final int w = img.getWidth();
        final int h = img.getHeight();

        String[] bytesInNumSys = new String[] {
            "", "", ""
        };

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;

                getLayeredNumericalValueFromPixelColor(colors, img.getRGB(x, y), numeralSystem, bytesInNumSys);

                if (pix % pixelsPerBytePerLayer == pixelsPerBytePerLayer - 1) {

                    for (int layer = 0; layer < LAYERS; layer++) {
                        byte b = (byte)Integer.parseInt(bytesInNumSys[layer], numeralSystem);
                        al.add(b);
                    }

                    bytesInNumSys = new String[] {
                        "", "", ""
                    };
                }
            }
        }

        return HBytesToImagePainterHelper.arrayListToByteArray(al);
    }

    /**
     * Calculates the colors for the current byte.
     * 
     * @param colors
     *            The colors used
     * @param b
     *            The current byte
     * @param layer
     *            The current layer
     * @param numeralSystem
     *            The numeral system used
     * @param pixelInNumSys
     *            The amount of pixels needed for one byte in one layer
     * @return The byte's colors.
     */
    private int[] getLayeredColorsFromByte(final Color[][] colors, final byte b, final int layer,
        final int numeralSystem, final int pixelInNumSys) {
        final int it = b & 0xFF;
        String numVal = Integer.toString(it, numeralSystem);
        final int[] byteColors = new int[pixelInNumSys];

        while (numVal.length() < pixelInNumSys) {
            numVal = "0" + numVal;
        }

        for (int i = 0; i < pixelInNumSys; i++) {

            String val = numVal.substring(i, i + 1);

            int dc = Integer.parseInt(val, numeralSystem);

            byteColors[i] = colors[layer][dc].getRGB();
        }
        return byteColors;
    }

    /**
     * Extracts the numerical value from current pixel's RGB-value.
     * 
     * @param colors
     *            The colors used
     * @param rgb
     *            The RGB-value of the current pixel
     * @param numeralSystem
     *            The numeral system used
     * @param numericalValues
     *            Array to be filled with the numerical values of the current pixel
     */
    private void getLayeredNumericalValueFromPixelColor(final Color[][] colors, final int rgb,
        final int numeralSystem, final String[] numericalValues) {

        final Color c = new Color(rgb);
        final int red = c.getRed();
        final int green = c.getGreen();
        final int blue = c.getBlue();

        for (int l = 0; l < LAYERS; l++) {
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
            numericalValues[l] += Integer.toString(idx, numeralSystem);
        }
    }
}
