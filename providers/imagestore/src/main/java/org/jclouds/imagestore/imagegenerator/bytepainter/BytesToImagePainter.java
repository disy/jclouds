package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

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
public class BytesToImagePainter implements IBytesToImagePainter {

    /** The amount of pixels needed for one byte in one layer. */
    private final int pixelsPerByte;
    /** The numeral system. */
    private final int numeralSystem;
    /** The colors. Used recommended colors from "Novel Color Scheme for 2D Barcode" paper. */
    private final Color[] colors;

    /** The image type to be used. */
    private final int bufferedImageType;

    /** The binary colors. */
    private static final Color[] BINARY_COLORS = new Color[] {
        Color.BLACK, Color.WHITE
    };

    /** The ternary colors. */
    private static final Color[] TERNARY_COLORS = new Color[] {
        Color.BLACK, new Color(0.5f, 0.5f, 0.5f), Color.WHITE
    };

    /** The quaternary colors. */
    private static final Color[] QUATERNARY_COLORS = new Color[] {
        Color.BLACK, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.WHITE
    };

    /** The septenary colors. */
    private static final Color[] SEPTENARY_COLORS = new Color[] {
        Color.BLACK, new Color(0.5f, 0f, 0f), new Color(1f, 0f, 0f), new Color(0f, 0.5f, 0.5f),
        new Color(0.5f, 0.5f, 0.5f), new Color(1f, 0.5f, 0.5f), Color.WHITE
    };

    /** The hexadecimal colors. */
    private static final Color[] HEXADECIMAL_COLORS = new Color[] {
        Color.BLACK, new Color(0.5f, 0f, 0f), new Color(1f, 0f, 0f), new Color(0f, 0.5f, 0.5f),
        new Color(0.5f, 0.5f, 0.5f), new Color(1f, 0.5f, 0.5f), new Color(0f, 1f, 1f),
        new Color(0.5f, 1f, 1f), new Color(1f, 1f, 0), new Color(0.25f, 1f, 0.25f),
        new Color(0.25f, 0.5f, 0.25f), new Color(0.25f, 0, 0.25f), new Color(0.75f, 1f, 0.75f),
        new Color(0.75f, 0.5f, 0.75f), new Color(0.75f, 0, 0.75f), Color.WHITE
    };

    public enum PainterType {

        /**
         * This Class offers a byte painter.
         * <p/>
         * Numeral System: Binary <br/>
         * Layers: 1 <br/>
         * 1 Byte = 8 Pixel <br/>
         * 2 colors <br/>
         * <p/>
         * Working with
         * <ul>
         * <li>Facebook</li>
         * <li>Flickr</li>
         * <li>Picasa</li>
         * </ul>
         */
        BINARY_BLACK_WHITE(2, BINARY_COLORS, BufferedImage.TYPE_BYTE_BINARY),

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
         */
        TERNARY(3, TERNARY_COLORS, BufferedImage.TYPE_INT_RGB),

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
         */
        TERNARY_GREY(3, TERNARY_COLORS, BufferedImage.TYPE_BYTE_GRAY),

        /**
         * This Class offers a byte painter.
         * <p/>
         * Numeral System: Quaternary <br/>
         * Layers: 1 <br/>
         * 1 Byte = 4 Pixel<br/>
         * 4 colors <br/>
         * <p/>
         * Working with
         * <ul>
         * <li>Flickr</li>
         * <li>Facebook (only if ImmageType is RGB, GREY not working on Facebook)</li>
         * <li>Picasa</li>
         * </ul>
         */
        QUARTERNARY(4, QUATERNARY_COLORS, BufferedImage.TYPE_INT_RGB),

        // /**
        // * This Class offers a byte painter.
        // * <p/>
        // * Numeral System: Quaternary <br/>
        // * Layers: 1 <br/>
        // * 1 Byte = 4 Pixel<br/>
        // * 4 colors <br/>
        // * <p/>
        // * Working with
        // * <ul>
        // * <li>Flickr</li>
        // * <li>Facebook (only if ImmageType is RGB, GREY not working on Facebook)</li>
        // * <li>Picasa</li>
        // * </ul>
        // */
        // QUARTERNARY_GREY(4, QUATERNARY_COLORS, BufferedImage.TYPE_BYTE_GRAY),

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
             */
            SEPTENARY(7, SEPTENARY_COLORS, BufferedImage.TYPE_INT_RGB),

            /**
             * This Class offers a byte painter.
             * <p/>
             * Numeral System: Hexadecimal <br/>
             * Layers: 1 <br/>
             * 1 Byte = 2 Pixel <br/>
             * 16 colors <br/>
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
             */
            HEXADECIMAL(16, HEXADECIMAL_COLORS, BufferedImage.TYPE_INT_RGB);

        /** The numeral system. */
        final int numSys;

        /** The used colors. */
        final Color[] colors;

        /** The type of the buffered image. */
        final int bufferedImageType;

        /**
         * Constructor. Sets the numeral system of a predefined ByteToImagePainter.
         * 
         * @param pNumSys
         *            the numeral system
         * @param pcs
         *            the colors to be used
         * @param biType
         *            the buffered image type
         */
        PainterType(final int pNumSys, final Color[] pcs, final int biType) {
            numSys = pNumSys;
            colors = pcs;
            bufferedImageType = biType;
        }

        /**
         * Returns a IBytesToImagePainter object with the given enum parameter.
         * 
         * @return the IBytesToImagePainter object
         */
        public IBytesToImagePainter getPainter() {
            return new BytesToImagePainter(numSys, colors, bufferedImageType);
        }

    }

    public BytesToImagePainter(final int numSys, final Color[] pcs, final int biType) {
        pixelsPerByte = HBytesToImagePainterHelper.calcPixelsPerBytePerLayer(numSys);
        numeralSystem = numSys;
        colors = pcs;
        bufferedImageType = biType;
    }

    public BytesToImagePainter(final PainterType pt) {
        this(pt.numSys, pt.colors, pt.bufferedImageType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getImageType() {
        return bufferedImageType;
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

                    if (bsPos >= len)
                        break;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Normal " + numeralSystem + ": image-type: " + bufferedImageType;
    }

}
