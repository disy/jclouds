package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.ImageStoreConstants;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * This class offers an abstraction to create different RGB-layered byte painters.
 * 
 * @author Wolfgang Miller, University of Konstanz.
 * 
 */
public class LayeredBytesToImagePainter implements IBytesToImagePainter {

    /** The amount of image layers. */
    private static final int LAYERS = 3;
    /** The numeral system. */
    private final int numeralSystem;
    /** The amount of pixels needed for one byte in one layer. */
    private final int pixelsPerBytePerLayer;
    /** Size of Pixels for holding one information. */
    private final int blockSize;

    public enum PainterType {
        /**
         * The BinaryLayeredBytesToPixelPainter.
         * Numeral System: Binary (2) <br/>
         * Layers: 3 <br/>
         * 1Byte = 8/3Pixel
         * 8Bit pro Pixel (2Bit power 3Layers)<br/>
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
        BINARY_LAYERED(2),

        /**
         * The TernaryLayeredBytesToImagePainter.
         * <p/>
         * Numeral System: Ternary (3) <br/>
         * Layers: 3 <br/>
         * 1Byte = 2Pixels <br/>
         * 27Bit pro Pixel (3Bit power 3Layers) <br/>
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
        TERNARY(3),

        /**
         * The QuatenaryLayeredBytesToImagePainter.
         * <p/>
         * Numeral System: Quaternary (4) <br/>
         * Layers: 3 <br/>
         * 1Byte = 4/3 Pixels <br/>
         * 64Bit pro Pixel (4Bit power 3Layers) <br/>
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
        QUATENARY_LAYERED(4),

        /**
         * The SeptenaryLayeredBytesToImagePainter.
         * <p/>
         * Numeral System: Septenary (7) <br/>
         * Layers: 3 <br/>
         * 1Byte = 1 Pixel<br/>
         * 343Bit pro Pixel (7Bit power 3Layers)<br/>
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
         * */
        SEPTENARY_LAYERED(7),

        /**
         * HexadecimalLayeredBytesToImagePainter.
         * <p/>
         * Numeral System: Hexadecimal <br/>
         * Layers: 3 <br/>
         * 1Byte = 2/3 Pixel <br/>
         * 4096Bit pro Pixel (16Bit power 3Layers) <br/>
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
        HEXADECIMAL_LAYERED(16);

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

        /**
         * Returns a IBytesToImagePainter object with the given enum params.
         * 
         * @return the IBytesToImagePainter object
         */
        public IBytesToImagePainter getPainter() {
            return new LayeredBytesToImagePainter(numSys);
        }

    }

    /** The type of the BufferedImage. */
    private final int bufferedImageType = BufferedImage.TYPE_INT_RGB;

    /** The different pixel colors. */
    private final Color[][] colors;

    /**
     * Constructor. Generates a layered byte painter with the given numeral system.
     * 
     * @param numSys
     *            the numeral system
     */
    @Inject
    public LayeredBytesToImagePainter(@Named(ImageStoreConstants.PROPERTY_LAYERS) final String numSys) {
        this(Integer.parseInt(numSys));
    }

    /**
     * Constructor. Generates a layered byte painter with the given numeral system.
     * 
     * @param numSys
     *            The numeral system
     */
    public LayeredBytesToImagePainter(final int numSys) {
        pixelsPerBytePerLayer = HBytesToImagePainterHelper.calcPixelsPerBytePerLayer(numSys);
        numeralSystem = numSys;
        colors = HBytesToImagePainterHelper.generate3LayeredUniformlyDistributedColors(numeralSystem);
        blockSize = 1;
    }

    /**
     * Constructor. Generates a layered byte painter of predefined type.
     * 
     * @param pt
     *            the predefined LayeredBytesToImagePainter
     */
    public LayeredBytesToImagePainter(final PainterType pt) {
        this(pt.numSys);
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
        return pixelsPerBytePerLayer / (float)LAYERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage storeBytesInImage(final BufferedImage image, final byte[] bs, final int startP,
        final int endP) {

        final int w = image.getWidth();
        final int h = image.getHeight();
        final Graphics g = image.getGraphics();

        int[] currByteColor = null;
        int len = bs.length;

        // postion in byte-array
        int bp = 0;
        for (int y = 0; y < h; y = y + blockSize) {

            final int hpix = w * y;

            for (int x = 0; x < w; x = x + blockSize) {

                // amount of used pixels
                final int pix = hpix + x;

                // the difference between start position and pixels visited
                final int psPix = pix - startP;

                if (psPix < 0)
                    continue;

                if (endP <= pix)
                    return image;

                // if pos == 0 the new byte bucket starts
                final int pos = psPix % pixelsPerBytePerLayer;

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

                        // the current byte-color values
                        int[] bc =
                            getLayeredColorsFromByte(colors, b, layer, numeralSystem, pixelsPerBytePerLayer);

                        // if byte-color == null, set bc as initial byte-color
                        if (currByteColor == null) {
                            currByteColor = bc;
                            continue;
                        }

                        // add all byte-color values of the layers together
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
    public byte[] getBytesFromImage(final BufferedImage image, final int startP, final int endP) {
        final ArrayList<Byte> al = new ArrayList<Byte>();

        final int w = image.getWidth();
        final int h = image.getHeight();

        String[] bytesInNumSys = new String[] {
            "", "", ""
        };

        for (int y = 0; y < h; y = y + blockSize) {

            final int hpix = w * y;

            for (int x = 0; x < w; x = x + blockSize) {

                // absolute amount of pixels visited
                final int pix = hpix + x;

                // the difference between start position and pixels visited
                final int psPix = pix - startP;

                if (psPix < 0)
                    continue;

                if (endP <= pix)
                    return HBytesToImagePainterHelper.arrayListToByteArray(al);

                final int rgb = image.getRGB(x, y);

                // get the values of all three layers of the current pixel
                for (int layer = 0; layer < LAYERS; layer++) {

                    final int colorVal = HBytesToImagePainterHelper.extractLayerColorFromRGB(rgb, layer);

                    bytesInNumSys[layer] +=
                        HBytesToImagePainterHelper.getLayeredNumeralValueFromPixelColor(layer, colors,
                            colorVal, numeralSystem);
                }

                // if a complete chunk is collected, extract the bytes
                if (psPix % pixelsPerBytePerLayer == pixelsPerBytePerLayer - 1) {

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
        // unsign byte
        final int it = b & 0xFF;
        String numVal = Integer.toString(it, numeralSystem);
        final int[] byteColors = new int[pixelInNumSys];

        // add 0 to numVal-string until it matches the expected pixelSize of the numeral system
        while (numVal.length() < pixelInNumSys) {
            numVal = "0" + numVal;
        }

        for (int i = 0; i < pixelInNumSys; i++) {

            // the next value in given string
            String val = numVal.substring(i, i + 1);

            // get the color index
            int dc = Integer.parseInt(val, numeralSystem);

            byteColors[i] = colors[layer][dc].getRGB();
        }
        return byteColors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Layered " + numeralSystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumSys() {
        return numeralSystem;
    }

}
