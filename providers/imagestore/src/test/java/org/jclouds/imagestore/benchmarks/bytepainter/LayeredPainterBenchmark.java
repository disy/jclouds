/**
 * 
 */
package org.jclouds.imagestore.benchmarks.bytepainter;

import java.util.Random;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.imagestore.TestAndBenchmarkHelper;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.bytepainter.LayeredBytesToImagePainter;
import org.jclouds.imagestore.imagehoster.IImageHost;
import org.jclouds.imagestore.imagehoster.file.ImageHostFile;

/**
 * Benchmark for different Image Generators
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class LayeredPainterBenchmark {

    private static final Random RAN = new Random(12l);

    // //SETTINGS TO SET MANUALLY
    private static final Class<? extends IImageHost> HOST = ImageHostFile.class;
    private static final Class<? extends IBytesToImagePainter> PAINTER = LayeredBytesToImagePainter.class;
    private static final Class<? extends IEncoder> ENCODER = IEncoder.DummyEncoder.class;

    private byte[] data;

    
    
    
    private BlobStoreContext setUpContext(int layers) {
        return TestAndBenchmarkHelper.createContext(HOST, PAINTER, ENCODER, layers);
    }

    private void setUpData(int size) {
        data = new byte[1 << size];
    }

    public static void main(String[] args) {

    }

}
