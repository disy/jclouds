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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

import org.testng.annotations.Test;

/**
 * @author Sean Owen
 * @author sanfordsquires
 */
public final class ReedSolomonDecoderDataMatrixTestCase extends AbstractReedSolomonTestCase {

    private static final int[] test = {
        142, 164, 186
    };
    private static final int[] testWithECC = {
        142, 164, 186, 114, 25, 5, 88, 102
    };
    private static final int numberOfECCBytes = testWithECC.length - test.length;
    private static final int maxCorrectable = numberOfECCBytes / 2;

    private final ReedSolomonDecoder dmRSDecoder = new ReedSolomonDecoder(
        GenericGF.GenericGFs.AZTEC_DATA_8.mGf);

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
        dmRSDecoder.decode(received, numberOfECCBytes);
        for (int i = 0; i < test.length; i++) {
            assertEquals(received[i], test[i]);
        }
    }

}
