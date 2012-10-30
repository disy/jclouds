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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

import java.util.Random;

import org.testng.annotations.Test;

/**
 * @author Sean Owen
 */
public final class ReedSolomonDecoderQRCodeTestCase extends AbstractReedSolomonTestCase {

    /** See ISO 18004, Appendix I, from which this example is taken. */
    private static final int[] test = {
        16, 32, 12, 86, 97, 128, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17
    };
    private static final int[] testWithECC = {
        16, 32, 12, 86, 97, 128, 236, 17, 236, 17, 236, 17, 236, 17, 236, 17, 165, 36, 212, 193, 237, 54,
        199, 135, 44, 85
    };
    private static final int numberOfECCBytes = testWithECC.length - test.length;
    private static final int maxCorrectable = numberOfECCBytes / 2;

    private final ReedSolomonDecoder qrRSDecoder = new ReedSolomonDecoder(
        GenericGF.GenericGFs.QR_CODE_FIELD_256.mGf);

    @Test
    public void testNoError() throws ReedSolomonException {
        int[] received = new int[testWithECC.length];
        System.arraycopy(testWithECC, 0, received, 0, received.length);
        // no errors
        checkQRRSDecode(received);
    }

    @Test
    public void testOneError() throws ReedSolomonException {
        int[] received = new int[testWithECC.length];
        Random random = getRandom();
        for (int i = 0; i < received.length; i++) {
            System.arraycopy(testWithECC, 0, received, 0, received.length);
            received[i] = random.nextInt(256);
            checkQRRSDecode(received);
        }
    }

    @Test
    public void testMaxErrors() throws ReedSolomonException {
        int[] received = new int[testWithECC.length];
        Random random = getRandom();
        for (int i = 0; i < test.length; i++) { // # iterations is kind of arbitrary
            System.arraycopy(testWithECC, 0, received, 0, received.length);
            corrupt(received, maxCorrectable, random);
            checkQRRSDecode(received);
        }
    }

    @Test
    public void testTooManyErrors() {
        int[] received = new int[testWithECC.length];
        System.arraycopy(testWithECC, 0, received, 0, received.length);
        Random random = getRandom();
        corrupt(received, maxCorrectable + 1, random);
        try {
            checkQRRSDecode(received);
            fail("Should not have decoded");
        } catch (ReedSolomonException rse) {
            // good
        }
    }

    private void checkQRRSDecode(int[] received) throws ReedSolomonException {
        qrRSDecoder.decode(received, numberOfECCBytes);
        for (int i = 0; i < test.length; i++) {
            assertEquals(received[i], test[i]);
        }
    }

}
