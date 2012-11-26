package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jclouds.imagestore.ImageStoreConstants;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

import com.google.inject.Inject;
import com.google.inject.name.Named;

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
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;

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
        BINARY(2) {
            @Override
            public Color[] getColors() {
                return new Color[] {
                    Color.BLACK, Color.WHITE
                };
            }
        },

        /**
         * This Class offers a byte painter.
         * <p/>
         * Numeral System: Ternary <br/>
         * Layers: 1 <br/>
         * 1 Byte = 6 Pixel <br/>
         * 3 colors <br/>
         * <p/>
         * Working with
         * <ul>
         * <li>Flickr</li>
         * <li>Picasa</li>
         * <li>Facebook</li>
         * </ul>
         */
        TERNARY(3) {
            @Override
            public Color[] getColors() {
                return new Color[] {
                    Color.BLACK, new Color(0.5f, 0.5f, 0.5f), Color.WHITE
                };
            }
        },

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
        QUARTERNARY(4) {
            @Override
            public Color[] getColors() {
                return new Color[] {
                    Color.BLACK, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.WHITE
                };
            }
        },

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
        SEPTENARY(7) {
            @Override
            public Color[] getColors() {
                return new Color[] {
                    Color.BLACK, new Color(0.5f, 0f, 0f), new Color(1f, 0f, 0f), new Color(0f, 0.5f, 0.5f),
                    new Color(0.5f, 0.5f, 0.5f), new Color(1f, 0.5f, 0.5f), Color.WHITE
                };
            }
        },

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
        HEXADECIMAL(16) {
            @Override
            public Color[] getColors() {
                return new Color[] {
                    Color.BLACK, new Color(0.5f, 0f, 0f), new Color(1f, 0f, 0f), new Color(0f, 0.5f, 0.5f),
                    new Color(0.5f, 0.5f, 0.5f), new Color(1f, 0.5f, 0.5f), new Color(0f, 1f, 1f),
                    new Color(0.5f, 1f, 1f), new Color(1f, 1f, 0), new Color(0.25f, 1f, 0.25f),
                    new Color(0.25f, 0.5f, 0.25f), new Color(0.25f, 0, 0.25f), new Color(0.75f, 1f, 0.75f),
                    new Color(0.75f, 0.5f, 0.75f), new Color(0.75f, 0, 0.75f), Color.WHITE
                };
            }
        };

        /** The numeral system. */
        final int numSys;

        /** All painters. */
        static Map<Integer, PainterType> PAINTERS = new HashMap<Integer, PainterType>();
        static {
            for (PainterType type : PainterType.values()) {
                PAINTERS.put(type.numSys, type);
            }
        }

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
         * Returns a IBytesToImagePainter object with the given enum parameter.
         * 
         * @return the IBytesToImagePainter object
         */
        public IBytesToImagePainter getPainter() {
            return new BytesToImagePainter(numSys);
        }

        /**
         * Returns the used colors.
         * 
         * @return the colors
         */
        public abstract Color[] getColors();

        /**
         * Returns painter from given numeral system.
         * 
         * @param numSys
         *            the numeral system
         * @return the painter with given numeral system
         */
        public static PainterType getType(final int numSys) {
            if (PAINTERS.containsKey(numSys)) {
                return PAINTERS.get(numSys);
            } else {
                throw new IllegalArgumentException(new StringBuilder("Painter ").append(numSys).append(
                    " not implemented!").toString());
            }
        }

    }

    @Inject
    /**
     * Constructor. Creates a byte-painter with the given numeral system.
     * 
     * @param numSys the numeral system
     */
    public BytesToImagePainter(@Named(ImageStoreConstants.PROPERTY_LAYERS) final String numSys) {
        this(Integer.parseInt(numSys));
    }

    /**
     * Constructor. Creates a byte-painter with the given numeral system.
     * 
     * @param numSys
     *            the numeral system
     */
    public BytesToImagePainter(final int numSys) {
        PainterType type = PainterType.getType(numSys);
        pixelsPerByte = HBytesToImagePainterHelper.calcPixelsPerBytePerLayer(numSys);
        numeralSystem = numSys;
        colors = type.getColors();
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
        return "Normal" + numeralSystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumeralSystem() {
        return numeralSystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLayered() {
        return false;
    }

}
