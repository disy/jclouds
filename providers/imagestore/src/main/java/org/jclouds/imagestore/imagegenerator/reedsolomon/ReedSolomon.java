/**
 * 
 */
package org.jclouds.imagestore.imagegenerator.reedsolomon;

import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.reedsolomon.GenericGF.GenericGFs;

/**
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class ReedSolomon implements IEncoder {

    private final static GenericGF field = GenericGFs.QR_CODE_FIELD_256.mGf;

    private final static ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
    private final static ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);

    // must be something between 1 and 248 (256 -8)
    private final static int mEcSize = 30;

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encode(byte[] param) {
        byte[][] splitted = split(param, field.getSize() - mEcSize);
        byte[][] returned = new byte[splitted.length][];
        for (int i = 0; i < splitted.length; i++) {
            int[] decoded = new int[splitted[i].length + mEcSize];
            int[] convertedInt = castToInt(splitted[i]);
            System.arraycopy(convertedInt, 0, decoded, 0, convertedInt.length);
            encoder.encode(decoded, decoded.length - convertedInt.length);
            returned[i] = castToByte(decoded);
        }
        return combine(returned, field.getSize());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decode(byte[] param) {
        byte[][] splitted = split(param, field.getSize());
        byte[][] returned = new byte[splitted.length][];

        for (int i = 0; i < splitted.length; i++) {
            int[] convertedInt = castToInt(splitted[i]);
            try {
                decoder.decode(convertedInt, mEcSize);
            } catch (ReedSolomonException exc) {
                return new byte[0];
            }
            int[] dataOnly = new int[convertedInt.length - mEcSize];
            System.arraycopy(convertedInt, 0, dataOnly, 0, dataOnly.length);
            returned[i] = castToByte(dataOnly);
        }

        return combine(returned, field.getSize() - mEcSize);
    }

    private byte[][] split(byte[] param, int splitToSize) {
        byte[][] returnVal = new byte[(int)Math.ceil(new Double(param.length) / new Double(splitToSize))][];

        for (int i = 0; i < returnVal.length - 1; i++) {
            returnVal[i] = new byte[splitToSize];
            System.arraycopy(param, i * splitToSize, returnVal[i], 0, splitToSize);
        }
        if (returnVal.length > 0) {
            returnVal[returnVal.length - 1] =
                new byte[param.length % splitToSize == 0 ? param.length : param.length % splitToSize];
            System.arraycopy(param, (returnVal.length - 1) * splitToSize, returnVal[returnVal.length - 1], 0,
                returnVal[returnVal.length - 1].length);
        }
        return returnVal;
    }

    private byte[] combine(byte[][] splitted, int splitToSize) {
        if (splitted.length > 0) {
            byte[] returnVal =
                new byte[((splitted.length - 1) * splitToSize) + splitted[splitted.length - 1].length];
            for (int i = 0; i < splitted.length - 1; i++) {
                System.arraycopy(splitted[i], 0, returnVal, i * splitToSize, splitToSize);
            }

            System.arraycopy(splitted[splitted.length - 1], 0, returnVal,
                (splitted.length - 1) * splitToSize, splitted[splitted.length - 1].length);
            return returnVal;
        } else {
            return new byte[0];
        }

    }

    /**
     * Method for casting an byte-array (signed) to an int-array(unsigned)
     * 
     * @param pInt
     *            the int array
     * @return a suitable byte array
     */
    public static int[] castToInt(final byte[] pInt) {
        int[] returnVal = new int[pInt.length];
        for (int i = 0; i < pInt.length; i++) {
            returnVal[i] = pInt[i] & 0xFF;
        }
        return returnVal;
    }

    /**
     * Method for casting an int-array (unsigned) to a byte-array (signed)
     * 
     * @param pInt
     *            the byte-array
     * @return a suitable int-array
     */
    public static byte[] castToByte(final int[] pInt) {
        byte[] returnVal = new byte[pInt.length];
        for (int i = 0; i < pInt.length; i++) {
            returnVal[i] = (byte)(pInt[i] & 0xFF);
        }
        return returnVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumbersOfBytesWasted(int numberOfEntireBytes) {
        return (int)(mEcSize * Math.ceil(new Double(numberOfEntireBytes) / new Double(field.getSize())));
    }

    public String toString() {
        return "ReedSolomon";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDummy() {
        return false;
    }

}
