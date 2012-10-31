/**
 * 
 */
package org.jclouds.imagestore.imagegenerator;

/**
 * Interface for hanging multiple encoders in the byte-stream before and after serialization e.g. Reed
 * Solomon.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public interface IEncoder {

    byte[] encode(byte[] param);

    byte[] decode(byte[] param);

    static class DummyEncoder implements IEncoder {

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] encode(byte[] param) {
            return param;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] decode(byte[] param) {
            return param;
        }

    }

}
