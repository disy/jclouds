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
        int[] decoded;
        decoded = new int[param.length + mEcSize];
        int[] convertedInt = castToInt(param);
        System.arraycopy(convertedInt, 0, decoded, 0, param.length);
        encoder.encode(decoded, decoded.length - param.length);
        return castToByte(decoded);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] decode(byte[] param) {
        int datasize;
        datasize = param.length - mEcSize;
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

    public static int[] castToInt(final byte[] pInt) {
        int[] returnVal = new int[pInt.length];
        for (int i = 0; i < pInt.length; i++) {
            returnVal[i] = pInt[i] & 0xFF;
        }
        return returnVal;
    }

    public static byte[] castToByte(final int[] pInt) {
        byte[] returnVal = new byte[pInt.length];
        for (int i = 0; i < pInt.length; i++) {
            returnVal[i] = (byte)(pInt[i] & 0xFF);
        }
        return returnVal;
    }

}
