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
 * 
 * Numeral System: Hexadecimal
 * Layers: 1
 * 1 Byte = 2 Pixel
 * 
 * @author Wolfgang Miller
 */
public class HexadecimalBytesToImagePainter implements IBytesToImagePainter {

    /** The image type to be used. */
    private static final int BUFFERED_IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;
    /** The used numeral system. */
    private static final int NUMERAL_SYSTEM = 16;
    /** Pixels needed for one Byte. */
    private static final float PIXELS_PER_BYTE = 2;

    /** The colors. */
    private final Color[] colors = new Color[] {
        new Color(1f, 1f, 1f), new Color(0f, 0f, 0.5f), new Color(0f, 0.5f, 0.5f), new Color(0.5f, 0f, 0.5f),
        new Color(0.5f, 0.5f, 0f), new Color(0.5f, 0.5f, 0.5f), new Color(0f, 0f, 1f),
        new Color(0f, 0.5f, 1f), new Color(0.5f, 0.5f, 1f), new Color(0f, 1f, 0f), new Color(0f, 1f, 0.5f),
        new Color(0.5f, 1f, 0f), new Color(1f, 1f, 0.5f), new Color(0.5f, 1f, 0.5f), new Color(1f, 0f, 0f),
        new Color(0f, 0f, 0f)
    };
    
    @Override
    public int getImageType() {
        return BUFFERED_IMAGE_TYPE;
    }

    @Override
    public float pixelsPerByte() {
        return PIXELS_PER_BYTE;
    }

    @Override
    public BufferedImage storeBytesInImage(final BufferedImage bi, final byte[] bs) {

        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final Graphics g = bi.getGraphics();

        final int len = bs.length;

        int bp = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x += 2) {
                if (bp >= len)
                    break;
                byte b = bs[bp++];
                drawByteToColoredImage(g, x, y, b);
            }
        }
        return bi;
    }

    @Override
    public byte[] getBytesFromImage(final BufferedImage img) {

        final ArrayList<Byte> li = new ArrayList<Byte>();

        final int w = img.getWidth();
        final int h = img.getHeight();

        String hex = "";

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                hex +=
                    HBytesToImagePainterHelper.getNumericalValueFromPixelColor(colors, img.getRGB(x, y),
                        NUMERAL_SYSTEM);

                if (x % 2 == 1) {

                    byte b = (byte)Integer.parseInt(hex, NUMERAL_SYSTEM);
                    li.add(b);
                    hex = "";
                }

            }
        }
        return HBytesToImagePainterHelper.arrayListToByteArray(li);
    }

    /**
     * Draws the given byte to the colored image.
     * 
     * @param g
     *            The Graphics reference of the image.
     * @param x
     *            The x coordinate.
     * @param y
     *            The y coordinate.
     * @param b
     *            The byte to be drawn.
     */
    private void drawByteToColoredImage(final Graphics g, final int x, final int y, final byte b) {
        final int it = b & 0xFF;
        final String hex = Integer.toString(it, NUMERAL_SYSTEM);
        final int l = hex.length();

        if (l == 1) {

            int dc = Integer.parseInt(hex, NUMERAL_SYSTEM);

            g.setColor(colors[0]);
            g.drawLine(x, y, x, y);

            g.setColor(colors[dc]);
            g.drawLine(x + 1, y, x + 1, y);

        } else {

            for (int i = 0; i < l; i++) {

                String val = hex.substring(i, i + 1);

                int dc = Integer.parseInt(val, NUMERAL_SYSTEM);

                g.setColor(colors[dc]);

                int xx = x + i;
                g.drawLine(xx, y, xx, y);
            }
        }
    }
}
