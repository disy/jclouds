import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.internal.BlobBuilderImpl;
import org.jclouds.encryption.internal.JCECrypto;
import org.jclouds.imagestore.blobstore.ImageBlobStore;


public class BlobTest {

    public static void main(String args[]) throws IOException{
       
        ImageBlobStore ib = new ImageBlobStore();
        BlobBuilder bb = null;
        try {
            bb = new BlobBuilderImpl(new JCECrypto());
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CertificateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String name = Long.toString(System.currentTimeMillis()) + "_image";
        bb.payload(new byte[]{23,2, 5, 2, 5});
        bb.name(name);
        ib.putBlob("Test", bb.build());
        Blob bbb = ib.getBlob("Test", name);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bbb.getPayload().writeTo(bos);
        byte [] bss = bos.toByteArray();
        
        for(byte b : bss){
            System.out.println(b);
        }
        
    }
}
