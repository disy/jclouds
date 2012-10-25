/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jclouds.imagestore.imagehoster.picasa;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.jclouds.imagestore.imagehoster.picasa.model.AlbumEntry;
import org.jclouds.imagestore.imagehoster.picasa.model.AlbumFeed;
import org.jclouds.imagestore.imagehoster.picasa.model.PhotoEntry;
import org.jclouds.imagestore.imagehoster.picasa.model.UserFeed;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * @author Yaniv Inbar
 */
public class PicasaSample {

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, PicasaSample.class
                .getResourceAsStream("/client_secrets.json"));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
            || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
                + "into picasa-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }
        // set up file credential store
        FileCredentialStore credentialStore =
            new FileCredentialStore(new File(System.getProperty("user.home"), ".credentials/picasa.json"),
                JSON_FACTORY);
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow =
            new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections
                .singleton(PicasaUrl.ROOT_URL)).setCredentialStore(credentialStore).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String[] args) {
        try {
            Credential credential = authorize();
            PicasaGoogleApiClient client = new PicasaGoogleApiClient(HTTP_TRANSPORT.createRequestFactory(credential));
            client.setApplicationName("Google-PicasaSample/1.0");
            try {
                run(client);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw e;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                HTTP_TRANSPORT.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    public static void run(PicasaGoogleApiClient client) throws IOException, InterruptedException {
        UserFeed feed = showAlbums(client);
        AlbumEntry album = postAlbum(client, feed);
        // postPhoto(client, album);

        // The server will update the e-tag of the album multiple times
        // Wait for the latest version ...
        Thread.sleep(1000);
        album = getUpdatedAlbum(client, album);

        deleteAlbum(client, album);
    }

    private static UserFeed showAlbums(PicasaGoogleApiClient client) throws IOException {
        // build URL for the default user feed of albums
        PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
        // execute GData request for the feed
        UserFeed feed = client.executeGetUserFeed(url);
        System.out.println("User: " + feed.author.name);
        System.out.println("Total number of albums: " + feed.totalResults);
        // show albums
        if (feed.albums != null) {
            for (AlbumEntry album : feed.albums) {
                showAlbum(client, album);
            }
        }
        return feed;
    }

    private static void showAlbum(PicasaGoogleApiClient client, AlbumEntry album) throws IOException {
        System.out.println();
        System.out.println("-----------------------------------------------");
        System.out.println("Album title: " + album.title);
        System.out.println("Updated: " + album.updated);
        System.out.println("Album ETag: " + album.etag);
        if (album.summary != null) {
            System.out.println("Description: " + album.summary);
        }
        if (album.numPhotos != 0) {
            System.out.println("Total number of photos: " + album.numPhotos);
            PicasaUrl url = new PicasaUrl(album.getFeedLink());
            AlbumFeed feed = client.executeGetAlbumFeed(url);
            for (PhotoEntry photo : feed.photos) {
                System.out.println();
                System.out.println("Photo title: " + photo.title);
                if (photo.summary != null) {
                    System.out.println("Photo description: " + photo.summary);
                }
                System.out.println("Image MIME type: " + photo.mediaGroup.content.type);
                System.out.println("Image URL: " + photo.mediaGroup.content.url);
            }
        }
    }

    private static AlbumEntry postAlbum(PicasaGoogleApiClient client, UserFeed feed) throws IOException {
        System.out.println();
        AlbumEntry newAlbum = new AlbumEntry();
        newAlbum.access = "public";
        newAlbum.title = "blubb";
        AlbumEntry album = client.executeInsert(new PicasaUrl(feed.getPostLink()), newAlbum);
        showAlbum(client, album);
        return album;
    }

//    private static PhotoEntry postPhoto(PicasaGoogleApiClient client, AlbumEntry album) throws IOException {
//        String fileName = "picasaweblogo-en_US.gif";
//        String photoUrlString = "http://www.google.com/accounts/lh2/" + fileName;
//        InputStreamContent content =
//            new InputStreamContent("image/jpeg", new URL(photoUrlString).openStream());
//        PhotoEntry photo =
//            client.executeInsertPhotoEntry(new PicasaUrl(album.getFeedLink()), content, fileName);
//        System.out.println("Posted photo: " + photo.title);
//        return photo;
//    }

    private static AlbumEntry getUpdatedAlbum(PicasaGoogleApiClient client, AlbumEntry album) throws IOException {
        album = client.executeGetAlbum(new PicasaUrl(album.getSelfLink()));
        showAlbum(client, album);
        return album;
    }

    private static void deleteAlbum(PicasaGoogleApiClient client, AlbumEntry album) throws IOException {
        client.executeDelete(album);
        System.out.println();
        System.out.println("Album deleted.");
    }
}
