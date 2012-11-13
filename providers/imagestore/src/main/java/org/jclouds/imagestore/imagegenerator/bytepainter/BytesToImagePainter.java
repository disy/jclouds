package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

public class BytesToImagePainter implements IBytesToImagePainter {

    /** The amount of pixels needed for one byte in one layer. */
    private final int pixelsPerByte;
    /** The numeral system. */
    private final int numeralSystem;

    /**
     * This Class offers a byte painter.
     * <p/>
     * Numeral System: Septenary <br/>
     * Layers: 1 <br/>
     * 1 Byte = 3 Pixel <br/>
     * 7 colors <br/>
     * <p/>
     * Working with
     * <ul>
     * <li>Flickr</li>
     * <li>Picasa</li>
     * </ul>
     * Not working with
     * <ul>
     * <li>Facebook</li>
     * </ul>
     * 
     * @author Wolfgang Miller, University of Konstanz
     */

    /** The image type to be used. */
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;

    /** The colors. Used recommended colors from "Novel Color Scheme for 2D Barcode" paper. */
    private final Color[] colors = new Color[] {
        new Color(0f, 0f, 0f), new Color(0.5f, 0f, 0f), new Color(1f, 0f, 0f), new Color(0f, 0.5f, 0.5f),
        new Color(0.5f, 0.5f, 0.5f), new Color(1f, 0.5f, 0.5f), new Color(0f, 1f, 1f),
        new Color(0.5f, 1f, 1f), new Color(1f, 1f, 0), new Color(1, 1, 1)
    };

    public enum PainterType {

        BINARY(2), TERNARY(3), QUARTERNARY(4), SEPTENARY(7), HEXADECIMAL(16);

        /** The numeral system. */
        final int numSys;

        /**
         * Constructor. Sets the numeral system of a predefined ByteToImagePainter.
         * 
         * @param pNumSys
         *            the numeral system
         */
        PainterType(final int pNumSys) {
            numSys = pNumSys;
        }

        public IBytesToImagePainter getPainter() {
            return new BytesToImagePainter(numSys);
        }

    }

    public BytesToImagePainter(final int numSys) {
        pixelsPerByte = HBytesToImagePainterHelper.calcPixelsPerBytePerLayer(numSys);
        numeralSystem = numSys;
    }

    public BytesToImagePainter(final PainterType pt) {
        this(pt.numSys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getImageType() {
        return BUFFERED_IMAGE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float pixelsPerByte() {
        return pixelsPerByte;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage storeBytesInImage(final BufferedImage bi, final byte[] bs, final int startP,
        final int endP) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();

        int len = bs.length;
        int bsPos = 0;
        int[] colorIdx = null;

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                // absolute amount of pixels visited
                final int pix = hpix + x;

                // the difference between start position and pixels visited
                final int psPix = pix - startP;

                if (psPix < 0)
                    continue;

                if (pix >= endP)
                    return bi;

                final int pos = psPix % (int)pixelsPerByte;

                if (pos == 0) {

                    /* if picture is too small for next bytes return */
                    if ((y == h - 1) && (x + 3 > w))
                        return bi;

                    if (bsPos >= len) {
                        break;
                    }

                    byte currB = bs[bsPos++];

                    colorIdx =
                        HBytesToImagePainterHelper
                            .getColorsFromByte(currB, numeralSystem, (int)pixelsPerByte);
                }

                g.setColor(colors[colorIdx[pos]]);
                g.drawLine(x, y, x, y);
            }
        }
        return bi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytesFromImage(final BufferedImage img, final int startP, final int endP) {

        final ArrayList<Byte> li = new ArrayList<Byte>();
        final int w = img.getWidth();
        final int h = img.getHeight();

        String numVal = "";

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                // absolute amount of pixels visited
                final int pix = hpix + x;

                // the difference between start position and pixels visited
                final int psPix = pix - startP;

                if (psPix < 0)
                    continue;

                if (pix >= endP)
                    return HBytesToImagePainterHelper.arrayListToByteArray(li);

                numVal +=
                    HBytesToImagePainterHelper.getNumeralValueFromPixelColor(colors, img.getRGB(x, y),
                        numeralSystem);

                if (psPix % pixelsPerByte == pixelsPerByte - 1) {
                    byte b = (byte)Integer.parseInt(numVal, numeralSystem);
                    li.add(b);
                    numVal = "";
                }
            }
        }
        return HBytesToImagePainterHelper.arrayListToByteArray(li);
    }

    public String toString() {
        return "Normal " + numeralSystem;
    }
    
}
