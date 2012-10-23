package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;


/**
 * 
 * 
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
 * </ul>
 * 
 * @author Wolfgang Miller, University of Konstanz
 */

public class SeptenaryBytesToImagePainter implements IBytesToImagePainter {

    /** The image type to be used. */
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
    /** The used numeral system. */
    private static final int NUMERAL_SYSTEM = 7;
    /** Pixels needed for one Byte. */
    private static final float PIXELS_PER_BYTE = 3;

    /** The colors. */
    private final Color[] colors = new Color[] {
        new Color(0f, 0f, 0f), new Color(1f, 0f, 0f), new Color(0f, 1f, 0f), new Color(0f, 0f, 1f),
        new Color(1f, 0f, 1f), new Color(1f, 1f, 0f), new Color(0f, 1f, 1f)
    };

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
        return PIXELS_PER_BYTE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage storeBytesInImage(final BufferedImage bi, final byte[] bs) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();

        int len = bs.length;
        int bsPos = 0;
        int[] colorIdx = null;

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;
                final int pos = pix % (int)PIXELS_PER_BYTE;

                if (pos == 0) {

                    /* if picture is too small for next bytes return */
                    if ((y == h - 1) && (x + 3 > w))
                        return bi;

                    if (bsPos >= len) {
                        break;
                    }

                    byte currB = bs[bsPos++];

                    colorIdx =
                        HBytesToImagePainterHelper.getColorsFromByte(currB, NUMERAL_SYSTEM,
                            (int)PIXELS_PER_BYTE);
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
    public byte[] getBytesFromImage(final BufferedImage img) {

        final ArrayList<Byte> li = new ArrayList<Byte>();
        final int w = img.getWidth();
        final int h = img.getHeight();
        final int mod = (int)(PIXELS_PER_BYTE - 1);

        String septenary = "";

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;

                septenary +=
                    HBytesToImagePainterHelper.getNumericalValueFromPixelColor(colors, img.getRGB(x, y),
                        NUMERAL_SYSTEM);

                if (pix % PIXELS_PER_BYTE == mod) {
                    byte b = (byte)Integer.parseInt(septenary, NUMERAL_SYSTEM);
                    li.add(b);
                    septenary = "";
                }
            }
        }
        return HBytesToImagePainterHelper.arrayListToByteArray(li);
    }
}
