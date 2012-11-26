package org.jclouds.imagestore.imagegenerator;

import static org.jclouds.imagestore.imagegenerator.HImageGenerationHelper.HEADER_OFFSET;
import static org.jclouds.imagestore.imagegenerator.HImageGenerationHelper.ROBUST_PAINTER;

import java.awt.image.BufferedImage;

import org.jclouds.imagestore.imagegenerator.bytepainter.BytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.DihectpenthexagonBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.DihectpenthexagonLayeredBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.LayeredBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.reedsolomon.ReedSolomon;

/**
 * This class is used to extract bytes from images.
 * 
 * @author Wolfgang Miller, University of Konstanz
 * 
 */
public class ImageExtractor {

    /** The numeral system of the dihectpenthexagon-painters. */
    private static final int DIHECTPENTHEXAGON_NUMERAL_SYSTEM = 256;
    /** The ecc encoder. */
    private final IEncoder enc = new ReedSolomon();
    /** The used byte painter. */
    private IBytesToImagePainter bp;
    /** The numeral system of the used byte painter. */
    private int numeralSystemOfBytePainter = -1;
    /** Boolean, true if a layered byte painter is used. */
    private boolean isLayeredBytePainter;
    /** Boolean, true if ecc is used. */
    private boolean ecc;

    /**
     * Extracts bytes from given image.
     * 
     * @param image
     *            The image.
     * @return The bytes extracted from the given image.
     */
    public byte[] getBytesFromImage(final BufferedImage image) {
        final int oLength = setPainterAndgetOriginalArrayLength(image);

        byte[] bs = bp.getBytesFromImage(image, HEADER_OFFSET, HImageGenerationHelper.getEndPixel(image));

        if (oLength < 0 || oLength > bs.length) {
            return new byte[0];
        }

        byte[] bss = new byte[oLength];
        System.arraycopy(bs, 0, bss, 0, oLength);

        // if no ecc return array
        if (!ecc) {
            return bss;
        }

        // if ecc decode bytes and return
        return enc.decode(bss);
    }

    /**
     * Sets new byte painter to decode the image content.
     * 
     * @param ns
     *            the numeral system
     * @param layered
     *            true if layere painter
     */
    private void setBytePainter(final int ns, final boolean layered) {

        numeralSystemOfBytePainter = ns;
        isLayeredBytePainter = layered;

        if (layered) {

            if (ns == DIHECTPENTHEXAGON_NUMERAL_SYSTEM) {
                bp = new DihectpenthexagonLayeredBytesToImagePainter();
            } else {
                bp = new LayeredBytesToImagePainter(ns);
            }

        } else {

            if (ns == DIHECTPENTHEXAGON_NUMERAL_SYSTEM) {
                bp = new DihectpenthexagonBytesToImagePainter();
            } else {
                bp = new BytesToImagePainter(ns);
            }
        }
    }

    /**
     * Extracts the original array from the image.
     * 
     * @param bi
     *            the image with the stored byte array
     * @return the original byte array length.
     */
    private int setPainterAndgetOriginalArrayLength(final BufferedImage bi) {

        final byte[] bal = ROBUST_PAINTER.getBytesFromImage(bi, 0, HEADER_OFFSET);
        final int b1 = (int)bal[0] & 0xFF;
        final int b2 = (int)bal[1] & 0xFF;
        final int b3 = (int)bal[2] & 0xFF;
        final int b4 = (int)bal[3] & 0xFF;
        final int oLength = b1 + (b2 << 8) + (b3 << 16) + (b4 << 24);

        // The numeral system
        final int ns = (int)(bal[4] & 0xFF) + 1;
        final boolean layered = (bal[5] & 1) == 1;

        // set right byte painter to decode image information right
        if (numeralSystemOfBytePainter != ns || isLayeredBytePainter != layered) {
            setBytePainter(ns, layered);
        }

        // boolean if ecc is used
        ecc = (bal[5] & 2) == 2;

        return oLength;
    }
}
