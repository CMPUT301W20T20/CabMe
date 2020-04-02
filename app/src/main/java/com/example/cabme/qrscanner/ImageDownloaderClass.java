package com.example.cabme.qrscanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Downloads the Barcode Image based on the String Provided
 */

public class ImageDownloaderClass extends AsyncTask<String, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;

    /**
     * Downloads the Image
     * @param  imageView
     */

    public ImageDownloaderClass(ImageView imageView){
        imageViewReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return downloadBitmap(strings[0]);
    }

    /**
     * sets the image view as the bitmap image that was downloaded if it is now null
     * @param bitmap
     */
    @Override
    protected void onPostExecute(Bitmap bitmap){
        if (isCancelled()) {
            bitmap = null;
        }

        ImageView imageView = imageViewReference.get();
        if (imageView != null){
            if (bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * Downloading Image Process from the URL connection if there are no errors in the URL
     * @param url
     */

    private Bitmap downloadBitmap(String url){
        HttpURLConnection urlConnection = null;
        try{
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK){
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null){
                return BitmapFactory.decodeStream(inputStream);
            }

        }catch (Exception e){
            urlConnection.disconnect();
        }
        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
        }
        return null;
    }
}
