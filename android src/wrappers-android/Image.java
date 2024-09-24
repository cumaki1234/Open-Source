package org.sourceforge.kga.wrappers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by tidu8815 on 1/22/2016.
 */
public class Image
{
    Bitmap image;

    public Image(String image)
    {
        byte[] data = Base64.decode(image, Base64.DEFAULT);
        this.image = BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public Bitmap get()
    {
        return image;
    }
}
