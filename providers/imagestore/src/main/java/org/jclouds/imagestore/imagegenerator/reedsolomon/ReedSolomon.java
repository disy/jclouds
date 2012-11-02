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

    private final int mEcSize;

    /**
     * 
     * Constructor.
     * 
     * @param pEcSize
     *            must be something between 1 and 248 (256 -8)
     */
    public ReedSolomon(int pEcSize) {
        assert pEcSize > 0 && pEcSize <= 248;
        mEcSize = pEcSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encode(byte[] param) {
        byte[][] splitted = split(param);
        byte[][] returned = new byte[splitted.length][];
        for (int i = 0; i < splitted.length; i++) {
            int[] decoded = new int[splitted[i].length + mEcSize];
            int[] convertedInt = castToInt(splitted[i]);
            System.arraycopy(convertedInt, 0, decoded, 0, convertedInt.length);
            encoder.encode(decoded, decoded.length - convertedInt.length);
            returned[i] = castToByte(decoded);
        }
        return combine(returned);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decode(byte[] param) {
        int datasize = param.length - mEcSize;
        int[] convertedInt = castToInt(param);
        try {
            decoder.decode(convertedInt, param.length - datasize);
        } catch (ReedSolomonException exc) {
            throw new RuntimeException(exc);
        }
        int[] data = new int[datasize];
        System.arraycopy(convertedInt, 0, data, 0, data.length);
        return castToByte(data);
    }

    private byte[][] split(byte[] param) {
        final int dataSize = field.getSize() - mEcSize;
        byte[][] returnVal = new byte[(int)Math.ceil(new Double(param.length) / new Double(dataSize))][];

        for (int i = 0; i < returnVal.length - 1; i++) {
            returnVal[i] = new byte[dataSize];
            System.arraycopy(param, i * dataSize, returnVal[i], 0, dataSize);
        }
        returnVal[returnVal.length - 1] =
            new byte[param.length % dataSize == 0 ? param.length : param.length % dataSize];
        System.arraycopy(param, (returnVal.length - 1) * dataSize, returnVal[returnVal.length - 1], 0,
            returnVal[returnVal.length - 1].length);
        return returnVal;
    }

    private byte[] combine(byte[][] splitted) {
        byte[] returnVal =
            new byte[((splitted.length - 1) * field.getSize()) + splitted[splitted.length - 1].length];
        for (int i = 0; i < splitted.length - 1; i++) {
            System.arraycopy(splitted[i], 0, returnVal, i * field.getSize(), field.getSize());
        }

        System.arraycopy(splitted[splitted.length - 1], 0, returnVal,
            (splitted.length - 1) * field.getSize(), splitted[splitted.length - 1].length);
        return returnVal;

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

}
