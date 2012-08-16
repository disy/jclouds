package org.jclouds.imagestore;

import java.net.URI;
import java.util.Properties;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

public class ImageHostProviderMetadata extends BaseProviderMetadata {

    /**
     * 
     */
    private static final long serialVersionUID = 6050547247272100264L;
    
    
    public static Builder builder() {
        return new Builder();
     }

     @Override
     public Builder toBuilder() {
        return builder().fromProviderMetadata(this);
     }
     
     public ImageHostProviderMetadata() {
        super(builder());
     }

     public  ImageHostProviderMetadata(Builder builder) {
        super(builder);
     }

     public static Properties defaultProperties() {
        Properties properties = new Properties();
        return properties;
     }
     
     public static class Builder extends BaseProviderMetadata.Builder {

        protected Builder(){
           id("aws-s3")
           .name("Amazon Simple Storage Service (S3)")
           .homepage(URI.create("http://aws.amazon.com/s3"))
           .console(URI.create("https://console.aws.amazon.com/s3/home"))
           .linkedServices("aws-ec2","aws-elb", "aws-cloudwatch", "aws-s3", "aws-simpledb")
           .iso3166Codes("US", "US-CA", "US-OR", "BR-SP", "IE", "SG", "JP-13")
           .defaultProperties(ImageHostProviderMetadata.defaultProperties());
        }

        @Override
        public ImageHostProviderMetadata build() {
           return new ImageHostProviderMetadata(this);
        }
        
        @Override
        public Builder fromProviderMetadata(
              ProviderMetadata in) {
           super.fromProviderMetadata(in);
           return this;
        }
     }

}
