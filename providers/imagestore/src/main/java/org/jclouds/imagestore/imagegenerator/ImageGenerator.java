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

import java.awt.image.BufferedImage;

import org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter;

import com.google.inject.Inject;

/**
 * This class is used to generate images from byte arrays or vice versa with specified byte painter.
 * 
 * @author Wolfgang Miller
 */
public class ImageGenerator {

    private final IBytesToImagePainter robustPainter = new BinaryBytesToImagePainter();
    /** The bytes to image painter. */
    private final IBytesToImagePainter bp;
    /** Encoder for applying operations on the bytes. */
    private final IEncoder enc;
    /** The image type for the given bytes to image painter. */
    private final int imageType;
    /** The byte array header offset. */
    private static final int HEADER_OFFSET = 4;
    /** The maximum image width for the specific image host. */
    private final int maxImageHostWidth;
    /** The maximum image height for the specific image host. */
    private final int maxImageHostHeight;
    /** The maximum bytes that can be stored in one image. */
    private final int maxBytesPerImage;

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
        imageType = bp.getImageType();
        maxImageHostHeight = ihMaxHeight;
        maxImageHostWidth = ihMaxWidth;
        final float ppb = bp.pixelsPerByte();
        maxBytesPerImage = (int)((maxImageHostHeight - 1) * maxImageHostWidth / ppb);
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
        final int h = (int)((byteArrayLength * bp.pixelsPerByte() + getStartPixel()) / (float)w) + 1;

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
        saveArrayLengthInFirst4Bytes(bi, toStore.length);
        return bp.storeBytesInImage(bi, toStore, getStartPixel(), getEndPixel(bi));
    }

    /**
     * Extracts bytes from given image.
     * 
     * @param image
     *            The image.
     * @return The bytes extracted from the given image.
     */
    public byte[] getBytesFromImage(final BufferedImage image) {
        byte[] getFromImage = getOriginalArray(image);
        return enc.decode(getFromImage);
    }

    /**
     * Returns the maximum amount of bytes one image can hold with the given provider and byte painter.
     * 
     * @return the maximum amount of bytes
     */
    public int getMaximumBytesPerImage() {
        return maxBytesPerImage;
    }

    public IBytesToImagePainter getPainter() {
        return bp;
    }

    /**
     * Saves the array length in the first 4 bytes of the array.
     * 
     * @param bs
     *            the byte array.
     * @return the byte array with original array length stored in first 4 bytes.
     */
    private void saveArrayLengthInFirst4Bytes(BufferedImage bi, int length) {
        final byte[] bss = new byte[] {
            (byte)length, (byte)(length >> 8), (byte)(length >> 16), (byte)(length >> 24)
        };
        robustPainter.storeBytesInImage(bi, bss, 0, getStartPixel());
    }

    /**
     * Extracts the original array through the original array length stored in the first 4 bytes of the image.
     * 
     * @param bs
     *            the byte array.
     * @return the original byte array.
     */
    private byte[] getOriginalArray(final BufferedImage image) {

        final byte[] bal = robustPainter.getBytesFromImage(image, 0, getStartPixel());
        final int b1 = (int)bal[0] & 0xFF;
        final int b2 = (int)bal[1] & 0xFF;
        final int b3 = (int)bal[2] & 0xFF;
        final int b4 = (int)bal[3] & 0xFF;
        final int oLength = b1 + (b2 << 8) + (b3 << 16) + (b4 << 24);

        byte[] bs = bp.getBytesFromImage(image, getStartPixel(), getEndPixel(image));

        byte[] bss;
        if (oLength < 0 || oLength > bs.length - HEADER_OFFSET) {
            bss = new byte[0];
        } else {
            bss = new byte[oLength];
            System.arraycopy(bs, 0, bss, 0, oLength);
        }
        return bss;
    }

    private int getStartPixel() {
        return (int)(robustPainter.pixelsPerByte() * HEADER_OFFSET);
    }

    private int getEndPixel(final BufferedImage bi) {
        return bi.getHeight() * bi.getWidth();
    }

}
