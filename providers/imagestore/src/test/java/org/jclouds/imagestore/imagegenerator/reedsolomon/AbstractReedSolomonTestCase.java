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

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author Sean Owen
 */
abstract class AbstractReedSolomonTestCase {

    static void corrupt(int[] received, int howMany, Random random) {
        Set<Integer> modified = new HashSet<Integer>();
        // StringBuilder locations = new StringBuilder("{");
        // StringBuilder data = new StringBuilder("{");
        for (int j = 0; j < howMany; j++) {
            int location = random.nextInt(received.length);
            if (!modified.contains(location)) {
                received[location] = random.nextInt(256);
                // locations.append(location).append(",");
                // data.append(received[location]).append(",");
                modified.add(location);
            } else {
                j--;
            }
        }
        // locations.deleteCharAt(locations.length() - 1);
        // data.deleteCharAt(data.length() - 1);
        // locations.append("}");
        // data.append("}");
        // System.out.println(locations.toString());
        // System.out.println(data.toString());
    }

    static int[] corrupt(int[] received, int[][] corruptedBytes) {
        for (int i = 0; i < corruptedBytes.length; i++) {
            received[corruptedBytes[0][i]] = corruptedBytes[1][i];
        }
        return received;
    }

    static byte[] corrupt(byte[] received, int[][] corruptedBytes) {
        int[] input = ReedSolomon.castToInt(received);
        input = corrupt(input, corruptedBytes);
        return ReedSolomon.castToByte(input);
    }

    static byte[] corrupt(byte[] received, int howMany, Random random) {
        int[] input = ReedSolomon.castToInt(received);
        corrupt(input, howMany, random);
        return ReedSolomon.castToByte(input);
    }

    static void doTestQRCodeEncoding(int[] dataBytes, int[] expectedECBytes) {
        int[] toEncode = new int[dataBytes.length + expectedECBytes.length];
        System.arraycopy(dataBytes, 0, toEncode, 0, dataBytes.length);
        new ReedSolomonEncoder(GenericGF.GenericGFs.QR_CODE_FIELD_256.mGf).encode(toEncode,
            expectedECBytes.length);
        assertArraysEqual(dataBytes, 0, toEncode, 0, dataBytes.length);
        assertArraysEqual(expectedECBytes, 0, toEncode, dataBytes.length, expectedECBytes.length);
    }

    static Random getRandom() {
        return new SecureRandom(new byte[] {
            (byte)0xDE, (byte)0xAD, (byte)0xBE, (byte)0xEF
        });
    }

    static void assertArraysEqual(int[] expected, int expectedOffset, int[] actual, int actualOffset,
        int length) {
        for (int i = 0; i < length; i++) {
            // if (expected[expectedOffset + i] == actual[actualOffset + i]) {
            assertEquals("Difference at offset " + i, expected[expectedOffset + i], actual[actualOffset + i]);
            // } else {
            // System.out.println("Difference at offset " + i + ":" + expected[expectedOffset + i] + "!="
            // + actual[actualOffset + i]);
            // }
        }
    }

    static void assertArraysEqual(byte[] expected, int expectedOffset, byte[] actual, int actualOffset,
        int length) {
        for (int i = 0; i < length; i++) {
            // if (expected[expectedOffset + i] == actual[actualOffset + i]) {
            assertEquals("Difference at offset " + i, expected[expectedOffset + i], actual[actualOffset + i]);
            // } else {
            // System.out.println("Difference at offset " + i + ":" + expected[expectedOffset + i] + "!="
            // + actual[actualOffset + i]);
            // }

        }
    }

}
