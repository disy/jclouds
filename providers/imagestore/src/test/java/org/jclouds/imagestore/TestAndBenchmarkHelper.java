package org.jclouds.imagestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.imagestore.imagegenerator.IBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.IEncoder;
import org.jclouds.imagestore.imagegenerator.bytepainter.BinaryBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.DihectpenthexagonLayeredBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.HexadecimalBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.LayeredBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.LayeredBytesToImagePainter.PainterType;
import org.jclouds.imagestore.imagegenerator.bytepainter.QuaternaryBytesToImagePainter;
import org.jclouds.imagestore.imagegenerator.bytepainter.SeptenaryBytesToImagePainter;
import org.jclouds.imagestore.imagehoster.IImageHost;

import com.google.common.io.Files;

public class TestAndBenchmarkHelper {

    public static BlobStoreContext createContext(Class<? extends IImageHost> host,
        final Class<? extends IBytesToImagePainter> painter, final Class<? extends IEncoder> encoder) {
        return createContext(host, painter, encoder, 2);
    }

    public static BlobStoreContext createContext(Class<? extends IImageHost> host,
        final Class<? extends IBytesToImagePainter> painter, final Class<? extends IEncoder> encoder,
        int layers) {

        String identity = "user";
        String credential = "pass";

        Properties properties = new Properties();
        properties.setProperty(FilesystemConstants.PROPERTY_BASEDIR, Files.createTempDir().getAbsolutePath());
        properties.setProperty(ImageStoreConstants.PROPERTY_BYTEPAINTER, painter.getName());
        properties.setProperty(ImageStoreConstants.PROPERTY_ENCODER, encoder.getName());
        properties.setProperty(ImageStoreConstants.PROPERTY_IMAGEHOSTER, host.getName());
        properties.setProperty(ImageStoreConstants.PROPERTY_LAYERS, Integer.toString(layers));

        return ContextBuilder.newBuilder("imagestore").credentials(identity, credential)
            .overrides(properties).buildView(BlobStoreContext.class);
    }

    public static List<IBytesToImagePainter> getAllPainters() {
        List<IBytesToImagePainter> returnVal = new ArrayList<IBytesToImagePainter>();
        for (PainterType type : LayeredBytesToImagePainter.PainterType.values()) {
            returnVal.add(type.getPainter());
        }

        returnVal.add(new BinaryBytesToImagePainter());
        returnVal.add(new DihectpenthexagonLayeredBytesToImagePainter());
        returnVal.add(new HexadecimalBytesToImagePainter());
        returnVal.add(new QuaternaryBytesToImagePainter());
        returnVal.add(new SeptenaryBytesToImagePainter());

        return returnVal;
    }

    public static List<IBytesToImagePainter> getPaintersForFacebook() {
        List<IBytesToImagePainter> returnVal = new ArrayList<IBytesToImagePainter>();
        returnVal.add(new BinaryBytesToImagePainter());
        returnVal.add(new QuaternaryBytesToImagePainter());
        return returnVal;
    }

    public static List<IBytesToImagePainter> getPaintersForFlickr() {
        List<IBytesToImagePainter> returnVal = new ArrayList<IBytesToImagePainter>();

        returnVal.add(PainterType.BINARY_LAYERED.getPainter());
        returnVal.add(PainterType.TERNARY.getPainter());
        returnVal.add(PainterType.QUATENARY_LAYERED.getPainter());
        returnVal.add(PainterType.SEPTENARY_LAYERED.getPainter());

        returnVal.add(new BinaryBytesToImagePainter());
        returnVal.add(new HexadecimalBytesToImagePainter());
        returnVal.add(new QuaternaryBytesToImagePainter());
        returnVal.add(new SeptenaryBytesToImagePainter());
        return returnVal;
    }
}
