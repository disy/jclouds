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
 * Numeral System: Quaternary
 * Layers: 1
 * 1 Byte = 4 Pixel
 * 
 * @author Wolfgang Miller
 */
public class QuaternaryBytesToImagePainter implements BytesToImagePainter {

    /** The used numeral system. */
    private final int NUMERAL_SYSTEM = 4;
    /** Pixels needed for one Byte. */
    private final float PIXELS_PER_BYTE = 4;

    /** The grey colors. */
    Color[] greyColors = new Color[] {
        Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.BLACK
    };

    @Override
    public float bytesPerPixel() {
        return PIXELS_PER_BYTE;
    }

    @Override
    public BufferedImage storeBytesInImage(final BufferedImage bi, final byte[] bs) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();

        int len = bs.length;
        int bsPos = 0;
        int[] greys = null;

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;
                final int pos = pix % 4;

                if (pix % 4 == 0) {

                    /* if picture is too small for next bytes return */
                    if ((y == h - 1) && (x + 3 > w))
                        return bi;

                    if (bsPos >= len) {
                        break;
                    }

                    byte currB = bs[bsPos++];

                    greys = getGreyFromByte(currB);
                }

                g.setColor(greyColors[greys[pos]]);

                g.drawLine(x, y, x, y);
            }
        }
        return bi;
    }

    /**
     * Gets the grey from byte.
     * 
     * @param b
     *            the b
     * @return the grey from byte
     */
    private int[] getGreyFromByte(final byte b) {
        final int it = b & 0xFF;
        String quaternary = Integer.toString(it, NUMERAL_SYSTEM);
        int[] byteColors = new int[NUMERAL_SYSTEM];
        final int l = 4;

        while (quaternary.length() < l) {
            quaternary = "0" + quaternary;
        }

        for (int i = 0; i < l; i++) {

            String val = quaternary.substring(i, i + 1);

            int dc = Integer.parseInt(val, NUMERAL_SYSTEM);

            byteColors[i] = dc;
        }
        return byteColors;
    }

    @Override
    public byte[] getBytesFromImage(final BufferedImage img) {

        final ArrayList<Byte> li = new ArrayList<Byte>();
        final int w = img.getWidth();
        final int h = img.getHeight();
        final int mod = (int)(PIXELS_PER_BYTE - 1);

        String quaternary = "";

        for (int y = 0; y < h; y++) {

            final int hpix = w * y;

            for (int x = 0; x < w; x++) {

                final int pix = hpix + x;

                quaternary += getNumericalValueFromPixelColor(img.getRGB(x, y));

                if (pix % PIXELS_PER_BYTE == mod) {
                    byte b = (byte)Integer.parseInt(quaternary, NUMERAL_SYSTEM);
                    li.add(b);
                    quaternary = "";
                }
            }
        }
        return BytesToImagePainterHelper.arrayListToByteArray(li);
    }

    /**
     * Extracts the hexadecimal value from current pixel's RGB-value.
     * 
     * @param rgb
     *            The RGB-value of the current pixel.
     * @return The hexadecimal value.
     */
    private String getNumericalValueFromPixelColor(final int rgb) {
        final Color c = new Color(rgb);
        final int red = c.getRed();
        final int green = c.getGreen();
        final int blue = c.getBlue();

        int dist = -1;
        int idx = -1;

        for (int i = 0; i < greyColors.length; i++) {
            final int cred = greyColors[i].getRed();
            final int cgreen = greyColors[i].getGreen();
            final int cblue = greyColors[i].getBlue();

            int currDist = Math.abs(cred - red) + Math.abs(cgreen - green) + Math.abs(cblue - blue);

            if (dist == -1 || currDist < dist) {
                dist = currDist;
                idx = i;
            }
        }

        return Integer.toString(idx, NUMERAL_SYSTEM);
    }
}
