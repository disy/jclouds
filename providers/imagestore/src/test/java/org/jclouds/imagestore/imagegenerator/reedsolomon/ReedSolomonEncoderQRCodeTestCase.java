/*
 * Copyright 2008 ZXing authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jclouds.imagestore.imagegenerator.reedsolomon;

import java.util.Random;

import org.testng.annotations.Test;

/**
 * @author Sean Owen
 */
public final class ReedSolomonEncoderQRCodeTestCase extends AbstractReedSolomonTestCase {

    /**
     * Tests example given in ISO 18004, Annex I
     */
    @Test
    public void testISO18004Example() {
        int[] test = {
            16, 32, 12, 86, 97, 128, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17
        };
        int[] testWithECC = {
            165, 36, 212, 193, 237, 54, 199, 135, 44, 85
        };
        doTestQRCodeEncoding(test, testWithECC);
    }

    @Test
    public void testQRCodeVersusDecoder() throws Exception {
        Random random = getRandom();
        GenericGF field = GenericGF.GenericGFs.QR_CODE_FIELD_256.mGf;
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
        for (int i = 0; i < 100; i++) {
            int size = 2 + random.nextInt(254);
            int[] toEncode = new int[size];
            int ecBytes = 1 + random.nextInt(2 * (1 + size / field.getFieldSize()));
            ecBytes = Math.min(ecBytes, size - 1);
            int dataBytes = size - ecBytes;
            for (int j = 0; j < dataBytes; j++) {
                toEncode[j] = random.nextInt(256);
            }
            int[] original = new int[dataBytes];
            System.arraycopy(toEncode, 0, original, 0, dataBytes);
            encoder.encode(toEncode, ecBytes);
            corrupt(toEncode, ecBytes / 2, random);
            decoder.decode(toEncode, ecBytes);
            assertArraysEqual(original, 0, toEncode, 0, dataBytes);
        }
    }

    // Need more tests I am sure

}
