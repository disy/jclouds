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
package org.jclouds.imagestore.blobstore.imagegenerator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

/**
 * This class is used to generate images from byte arrays or vice versa with specified byte painter.
 * 
 * @author Wolfgang Miller
 */
public class ImageGenerator {

    /** The bp. */
    private final IBytesToImagePainter bp;

    /**
     * Instantiates a new image generator.
     * 
     * @param bytePainter
     *            the byte painter to be used
     */
    public ImageGenerator(final IBytesToImagePainter bytePainter) {
        bp = bytePainter;
    }

    /**
     * Compress data.
     * 
     * @param bs
     *            the bs
     * @return the byte[]
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public byte[] compressData(final byte[] bs) throws IOException {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(bs);
        deflater.finish();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(bs.length);

        byte[] buffer = new byte[1024];

        /*
         * Use
         * 
         * boolean finished() method of Deflater class to determine whether end
         * of compressed data output stream reached.
         */
        while (!deflater.finished()) {
            /*
             * use int deflate(byte[] buffer) method to fill the buffer with the
             * compressed data.
             * 
             * This method returns actual number of bytes compressed.
             */

            int bytesCompressed = deflater.deflate(buffer);
            bos.write(buffer, 0, bytesCompressed);
        }

        return bos.toByteArray();
    }

    /**
     * Creates a new buffered image.
     * 
     * @param width
     *            The width.
     * @param height
     *            The height.
     * @return The created buffered image
     */
    BufferedImage createBufferedImage(final int width, final int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        return img;
    }

    /**
     * Calculates the width and height for images.
     * 
     * @param byteArrayLength
     *            The length of the byte array to store in an image.
     * @return An Array with width and height.
     */
    private int[] getImageWidthAndHeight(final int byteArrayLength) {
        int w = 2048;
        int h = (int)(byteArrayLength * bp.bytesPerPixel() / (float)w) + 1;

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
        int[] dim = getImageWidthAndHeight(bs.length);
        return bp.storeBytesInImage(createBufferedImage(dim[0], dim[1]), saveArrayLengthInFirst4Bytes(bs));
    }

    /**
     * Extracts bytes from given image.
     * 
     * @param image
     *            The image.
     * @return The bytes extracted from the given image.
     */
    public byte[] getBytesFromImage(final BufferedImage image) {
        return getOriginalArray(bp.getBytesFromImage(image));
    }

    /**
     * Saves the array length in the first 4 bytes of the array.
     * 
     * @param bs
     *            the byte array.
     * @return the byte array with original array length stored in first 4 bytes.
     */
    private static byte[] saveArrayLengthInFirst4Bytes(final byte[] bs) {
        final int length = bs.length;
        final int newLength = length + 4;
        final byte[] bss = new byte[newLength];
        bss[0] = (byte)length;
        bss[1] = (byte)(length >> 8);
        bss[2] = (byte)(length >> 16);
        bss[3] = (byte)(length >> 24);
        System.arraycopy(bs, 0, bss, 4, length);
        return bss;
    }

    /**
     * Extracts the original array through the original array length stored in the first 4 bytes of the image.
     * 
     * @param bs
     *            the byte array.
     * @return the original byte array.
     */
    private static byte[] getOriginalArray(final byte[] bs) {
        final int b1 = (int)bs[0] & 0xFF;
        final int b2 = (int)bs[1] & 0xFF;
        final int b3 = (int)bs[2] & 0xFF;
        final int b4 = (int)bs[3] & 0xFF;
        final int oLength = b1 + (b2 << 8) + (b3 << 16) + (b4 << 24);
        byte[] bss = new byte[oLength];
        System.arraycopy(bs, 4, bss, 0, oLength);
        return bss;
    }
}
