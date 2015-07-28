package com.soon.fm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.soon.fm.api.model.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoader {

    private final Image image;
    private final ImageView imageView;

    private Task task = new Task();

    public ImageLoader(Image i, ImageView iv) {
        image = i;
        imageView = iv;
    }

    public void execute() {
        task.execute();
    }

    private class Task extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... params) {
            try (InputStream in = new URL(image.getUrl()).openStream()) {
                return BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }
}
