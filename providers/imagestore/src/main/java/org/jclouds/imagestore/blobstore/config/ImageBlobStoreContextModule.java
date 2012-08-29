
package org.jclouds.imagestore.blobstore.config;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.LocalAsyncBlobStore;
import org.jclouds.blobstore.LocalStorageStrategy;
import org.jclouds.blobstore.TransientBlobRequestSigner;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.config.LocalBlobStore;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.rest.config.BinderUtils;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;

/**
 * 
 * @author Sebastian Graf, University of Konstanz
 */
public class ImageBlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(AsyncBlobStore.class).to(LocalAsyncBlobStore.class).asEagerSingleton();
      // forward all requests from TransientBlobStore to TransientAsyncBlobStore.  needs above binding as cannot proxy a class
      BinderUtils.bindClient(binder(), LocalBlobStore.class, AsyncBlobStore.class, ImmutableMap.<Class<?>, Class<?>>of());
      bind(BlobStore.class).to(LocalBlobStore.class);

      install(new BlobStoreObjectModule());
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.STRICT);
//      bind(LocalStorageStrategy.class).to(FilesystemStorageStrategyImpl.class);
//      bind(BlobUtils.class).to(FileSystemBlobUtilsImpl.class);
//      bind(FilesystemBlobKeyValidator.class).to(FilesystemBlobKeyValidatorImpl.class);
//      bind(FilesystemContainerNameValidator.class).to(FilesystemContainerNameValidatorImpl.class);
      bind(BlobRequestSigner.class).to(TransientBlobRequestSigner.class);
   }

}
