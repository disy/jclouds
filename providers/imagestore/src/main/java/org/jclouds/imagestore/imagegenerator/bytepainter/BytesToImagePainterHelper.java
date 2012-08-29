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
import java.util.ArrayList;

/**
 * This Class offers helper methods for the byte painters.
 * 
 * @author Wolfgang Miller, University of Konstanz
 */
public final class BytesToImagePainterHelper {
    
    /**
     * Protected constructor. Protects helper class from being instantiated.
     */
    private BytesToImagePainterHelper() { };

    /**
     * Returns a 2-dimensional array. The first dimension stands .
     * 
     * @param numColors
     *            the number of different colors
     * @return the calculated colors
     */

    public static Color[][] generateUniformlyDistributedColors(final int numColors) {
        Color[][] caa = new Color[3][numColors];

        for (int i = 0; i < caa.length; i++) {
            Color[] ca = caa[i];
            int sum = 0;
            final int len = ca.length;
            final float ratio = 255f / (len - 1);

            for (int y = 0; y < len; y++) {

                if (i == 0) {
                    ca[y] = new Color(sum, 0, 0);
                } else if (i == 1) {
                    ca[y] = new Color(0, sum, 0);
                } else {
                    ca[y] = new Color(0, 0, sum);
                }

                sum += ratio;
            }
        }
        return caa;
    }

    /**
     * Converts ArrayList<Byte> to byte[].
     * 
     * @param li
     *            the array list
     * @return the byte array
     */

    public static byte[] arrayListToByteArray(final ArrayList<Byte> li) {
        byte[] bs = new byte[li.size()];
        int i = 0;
        for (Byte b : li) {
            bs[i++] = b;
        }
        return bs;
    }
}
