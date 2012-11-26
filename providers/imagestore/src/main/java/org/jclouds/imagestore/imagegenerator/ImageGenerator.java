/**
 * Copyright (c) 2012, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jclouds.imagestore.imagegenerator;

import java.awt.color.CMMException;
import java.awt.image.BufferedImage;

import org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter;
import static org.jclouds.imagestore.imagegenerator.HImageGenerationHelper.HEADER_OFFSET;
import static org.jclouds.imagestore.imagegenerator.HImageGenerationHelper.ROBUST_PAINTER;

import com.google.inject.Inject;

/**
 * This class is used to generate images from byte arrays with specified byte painter.
 * 
 * @author Wolfgang Miller
 */
public class ImageGenerator {

    /** The bytes to image painter. */
    private final IBytesToImagePainter bp;
    /** Encoder for applying operations on the bytes. */
    private final IEncoder enc;
    /** The image type for the given bytes to image painter. */
    private final int imageType;
    /** The maximum image width for the specific image host. */
    private final int maxImageHostWidth;
    /** The maximum image height for the specific image host. */
    private final int maxImageHostHeight;
    /** The maximum bytes that can be stored in one image. */
    private final int maxBytesPerImage;
    /** True if used painter uses layers. */
    private final boolean layeredBp;
    /** The used numeral system. */
    private final int numeralSystemBp;
    /** True if error correction is used. */
    private final boolean ecc;

    /**
     * Instantiates a new image generator.
     * 
     * @param bytePainter
     *            the byte painter to be used.
     * @param encoder
     *            encoder to apply any operation on the bytes
     * @param ihMaxWidth
     *            The maximum image width for the specific image host.
     * @param ihMaxHeight
     *            The maximum image height for the specific image host.
     */
    @Inject
    public ImageGenerator(final IBytesToImagePainter bytePainter, final IEncoder encoder,
        final int ihMaxWidth, final int ihMaxHeight) {
        bp = bytePainter;
        enc = encoder;
        ecc = !enc.isDummy();
        imageType = bp.getImageType();
        layeredBp = bp.isLayered();
        numeralSystemBp = bp.getNumeralSystem();
        maxImageHostHeight = ihMaxHeight;
        maxImageHostWidth = ihMaxWidth;
        final float ppb = bp.pixelsPerByte();
        int numberOfBytesInPicture = (int)((maxImageHostHeight - 1) * maxImageHostWidth / ppb);
        maxBytesPerImage = numberOfBytesInPicture - enc.getNumbersOfBytesWasted(numberOfBytesInPicture);
    }

    /**
     * Creates a new buffered image.
     * 
     * @param width
     *            The width.
     * @param height
     *            The height.
     * @return The created buffered image.
     */
    private BufferedImage createBufferedImage(final int width, final int height) {
        return new BufferedImage(width, height, imageType);
    }

    /**
     * Calculates the width and height for images.
     * 
     * @param byteArrayLength
     *            The length of the byte array to store in an image.
     * @return An Array with width and height.
     */
    private int[] getImageWidthAndHeight(final int byteArrayLength) {
        final int w = maxImageHostWidth;
        int computedHeight = (int)((byteArrayLength * bp.pixelsPerByte() + HEADER_OFFSET) / (float)w) + 1;
        final int h = computedHeight < 5 ? 5 : computedHeight;

        if (h > maxImageHostHeight) {
            try {
                throw new IllegalArgumentException(
                    "Byte array too large for image generation! Generated image would be out of image-host's maximum image size.");
            } catch (IllegalArgumentException e) {
                new RuntimeException(e);
            }
        }

        return new int[] {
            w, h
        };
    }

    /**
     * Creates an image from given bytes.
     * 
     * @param bs
     *            The bytes.
     * @return The image created from the given bytes.
     */

    public BufferedImage createImageFromBytes(final byte[] bs) {
        byte[] toStore = enc.encode(bs);
        int[] dim = getImageWidthAndHeight(toStore.length);
        final BufferedImage bi = createBufferedImage(dim[0], dim[1]);

        saveDecodeInformationInFirst4Bytes(bi, toStore.length, numeralSystemBp, layeredBp, ecc);
        return bp.storeBytesInImage(bi, toStore, HEADER_OFFSET, HImageGenerationHelper.getEndPixel(bi));
    }

    /**
     * Returns the maximum amount of bytes one image can hold with the given provider and byte painter.
     * 
     * @return the maximum amount of bytes
     */
    public int getMaximumBytesPerImage() {
        return maxBytesPerImage;
    }

    /**
     * Returns the current byte painter.
     * 
     * @return the byte painter
     */
    public IBytesToImagePainter getPainter() {
        return bp;
    }

    /**
     * Stores the length of the array as first for bytes in the image.
     * 
     * @param bi
     *            the BufferedImage.
     * @param length
     *            the length of the byte array to be stored in the image
     * @param numSys
     *            the numeral system
     * @param layered
     *            true if layered painter
     * @param ecc
     *            true if painter uses ecc
     */
    private void saveDecodeInformationInFirst4Bytes(final BufferedImage bi, final int length,
        final int numSys, final boolean layered, final boolean ecc) {
        final byte[] bss =
            new byte[] {
                (byte)length, (byte)(length >> 8), (byte)(length >> 16), (byte)(length >> 24),
                (byte)(numSys - 1),
                /* move bits to the leftmost of the byte */
                (byte)((layered ? (1 << 6) : 0) + (ecc ? (1 << 7) : 0))
            };
        ROBUST_PAINTER.storeBytesInImage(bi, bss, 0, HEADER_OFFSET);
    }

}
