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
package org.jclouds.imagestore.blobstore.imagegenerator.bytepainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.jclouds.imagestore.blobstore.imagegenerator.BytesToImagePainter;

/**
 * This Class offers a byte painter.
 * 
 * Numeral System: Binary
 * Layers: 1
 * 1 Byte = 8 Pixel
 * 
 * @author Wolfgang Miller
 */
public class BinaryBytesToImagePainter implements BytesToImagePainter {

    /** Bytes needed per pixel. */
    public final float BYTES_PER_PIXEL = 8;

    @Override
    public float bytesPerPixel() {
        return BYTES_PER_PIXEL;
    }

    @Override
    public BufferedImage storeBytesInImage(final BufferedImage bi, final byte[] bs) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();
        g.setColor(Color.WHITE);

        int len = bs.length;
        int bsPos = 0;
        boolean[] bw = null;

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;
                final int pos = pix % 8;

                if (pix % 8 == 0) {

                    /* if picture is too small for next bytes return */
                    if ((y == h - 1) && (x + 8 > w))
                        return bi;

                    if (bsPos >= len) {
                        break;
                    }

                    byte currB = bs[bsPos++];

                    bw = getBinaryFromByte(currB);
                }

                if (!bw[pos])
                    continue;

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
        String bin = Integer.toBinaryString(it);

        while (bin.length() < 8) {
            bin = "0" + bin;
        }

        final int l = bin.length();
        boolean[] bw = new boolean[l];

        for (int i = 0; i < l; i++) {
            if (bin.charAt(i) == '1') {
                bw[i] = true;
            }
        }
        return bw;
    }

    @Override
    public byte[] getBytesFromImage(final BufferedImage img) {

        final ArrayList<Byte> li = new ArrayList<Byte>();
        final int w = img.getWidth();
        final int h = img.getHeight();

        String binary = "";

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;

                binary += getNumericalValueFromPixelColor(img.getRGB(x, y));

                if (pix % 8 == 7) {
                    byte b = (byte)Integer.parseInt(binary, 2);
                    li.add(b);
                    binary = "";
                }
            }
        }
        return BytesToImagePainterHelper.arrayListToByteArray(li);
    }

    /**
     * Extracts the numerical value from current pixel's RGB-value.
     * 
     * @param rgb
     *            The RGB-value of the current pixel.
     * @return The binary-value.
     */
    private char getNumericalValueFromPixelColor(final int rgb) {
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
