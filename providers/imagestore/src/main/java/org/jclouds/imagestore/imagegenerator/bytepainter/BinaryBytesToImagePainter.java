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
package org.jclouds.imagestore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;

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
 * 
 * @author Wolfgang Miller, University of Konstanz
 */
public class BinaryBytesToImagePainter implements IBytesToImagePainter {

    /** The image type to be used. */
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_BYTE_BINARY;
    /** The used numeral system. */
    private static final int NUMERAL_SYSTEM = 2;
    /** Pixels needed for one Byte. */
    private static final int PIXELS_PER_BYTE = 8;

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
    public BufferedImage storeBytesInImage(final BufferedImage bi, final byte[] bs, final int startP,
        final int endP) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();
        g.setColor(Color.WHITE);

        int len = bs.length;
        // position in byte array
        int bsPos = 0;
        // boolean array standing for black and white color
        boolean[] bw = null;

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                // absolute amount of pixels visited
                final int pix = hpix + x;

                // the difference between start position and pixels visited
                final int psPix = pix - startP;
                
                if (psPix < 0)
                    continue;

                if (pix > endP)
                    return bi;

                final int pos = psPix % PIXELS_PER_BYTE;

                // if pos == 0 a new Byte starts
                if (pos == 0) {

                    if (bsPos >= len)
                        return bi;

                    byte currB = bs[bsPos++];

                    bw = getBinaryFromByte(currB);
                }

                if (!bw[pos])
                    continue;

                // draw the pixel
                g.drawLine(x, y, x, y);
            }
        }
        return bi;
    }

    /**
     * Returns a boolean array which stands for the binary representation of the given byte.
     * 
     * @param b
     *            The byte to be represented in the boolean array.
     * @return The boolean array which represents the given byte.
     */
    private boolean[] getBinaryFromByte(final byte b) {
        final int it = b & 0xFF;
        String bin = Integer.toString(it, NUMERAL_SYSTEM);

        // fill length of binary representation to match byte-length
        while (bin.length() < PIXELS_PER_BYTE) {
            bin = "0" + bin;
        }

        boolean[] bw = new boolean[PIXELS_PER_BYTE];

        for (int i = 0; i < PIXELS_PER_BYTE; i++) {
            if (bin.charAt(i) == '1') {
                bw[i] = true;
            }
        }
        return bw;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytesFromImage(final BufferedImage img, final int startP, final int endP) {

        final ArrayList<Byte> li = new ArrayList<Byte>();
        final int w = img.getWidth();
        final int h = img.getHeight();

        String binary = "";

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                // absolute amount of pixels visited
                final int pix = hpix + x;

                // the difference between start position and pixels visited
                final int psPix = pix - startP;
                
                if (psPix < 0)
                    continue;

                if (pix > endP)
                    return HBytesToImagePainterHelper.arrayListToByteArray(li);

                binary += getNumeralValueFromPixelColor(img.getRGB(x, y));

                if (psPix % PIXELS_PER_BYTE == PIXELS_PER_BYTE - 1) {
                    byte b = (byte)Integer.parseInt(binary, NUMERAL_SYSTEM);
                    li.add(b);
                    binary = "";
                }
            }
        }
        return HBytesToImagePainterHelper.arrayListToByteArray(li);
    }

    /**
     * Extracts the numeral value from current pixel's RGB-value.
     * 
     * @param rgb
     *            The RGB-value of the current pixel.
     * @return The binary-value.
     */
    private char getNumeralValueFromPixelColor(final int rgb) {
        final Color c = new Color(rgb);
        final int bl = c.getBlue() > 128 ? 1 : 0;
        final int gr = c.getGreen() > 128 ? 1 : 0;
        final int re = c.getRed() > 128 ? 1 : 0;

        if (bl + gr + re >= 2) {
            return '1';
        }
        return '0';
    }
}
