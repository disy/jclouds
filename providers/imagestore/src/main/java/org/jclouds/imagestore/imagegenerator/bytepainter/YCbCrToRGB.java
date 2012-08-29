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

// TODO: Auto-generated Javadoc
/**
 * The Class YCbCrToRGB.
 */
public final class YCbCrToRGB {

    /** The red color constant. */
    static final double COLOR_RED = 0.299;
    /** The green color constant. */
    static final double COLOR_GREEN = 0.587;
    /** The blue color constant. */
    static final double COLOR_BLUE = 0.114;

    /**
     * Protected constructor. Protects helper class from being instantiated.
     */
    private YCbCrToRGB() {
    };

    /**
     * Gets the rGB color from y cb cr.
     * 
     * @param y
     *            the y
     * @param cb
     *            the cb
     * @param cr
     *            the cr
     * @return the rGB color from y cb cr
     */
    static Color getRGBColorFromYCbCr(final float y, final float cb, final float cr) {
        return getRGBColorFromYCbCr((int)(y * 255 + 0.5), (int)(cb * 255 + 0.5), (int)(cr * 255 + 0.5));
    }

    /**
     * Gets the rGB color from y cb cr.
     * 
     * @param Y
     *            the y
     * @param cB
     *            the c b
     * @param cR
     *            the c r
     * @return the rGB color from y cb cr
     */
    static Color getRGBColorFromYCbCr(final int Y, final int cB, final int cR) {

        // range of each input (R,G,B) is [-128...+127]
        // http://www.impulseadventure.com/photo/jpeg-color-space.html

        int R = (int)(Y + 1.402 * (cR - 128)) & 0xFF;
        int G = (int)(Y - 0.34414 * (cB - 128) - 0.71414 * (cR - 128)) & 0xFF;
        int B = (int)(Y + 1.772 * (cB - 128)) & 0xFF;

        System.out.println(R + " " + Integer.toBinaryString(R));
        System.out.println(G + " " + Integer.toBinaryString(G));
        System.out.println(B + " " + Integer.toBinaryString(B));

        return new Color((int)R, (int)G, (int)B);
    }
}
