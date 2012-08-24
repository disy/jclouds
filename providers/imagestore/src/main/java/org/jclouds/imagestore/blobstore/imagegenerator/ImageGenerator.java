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

    /** The image width. */
    private int IMAGE_WIDTH = 5;

    /** The image height. */
    private int IMAGE_HEIGHT = 2;

    /** The bp. */
    private final BytesToImagePainter bp;


    /**
     * Instantiates a new image generator.
     * 
     * @param bytePainter
     *            the byte painter to be used
     */
    public ImageGenerator(final BytesToImagePainter bytePainter) {
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
     * Creates the buffered image.
     * 
     * @return the buffered image
     */
    BufferedImage createBufferedImage() {
        BufferedImage img =
            new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        return img;
    }

    public int [] getImageWidthAndHeight(int byteArrayLength){
        int w = 2048;
        int h = (int) (byteArrayLength / (float) w) + 1;
        return new int[]{w, h};
    }
    
    public BufferedImage createImageFromBytes(byte [] bs){
        int [] dim = getImageWidthAndHeight(bs.length);
        this.IMAGE_WIDTH = dim[0];
        this.IMAGE_HEIGHT = dim[1];
        return bp.storeBytesInImage(createBufferedImage(), bs);
    }
    
    public byte[] getBytesFromImage(BufferedImage img){
        return bp.getBytesFromImage(img);
    }
    
}
