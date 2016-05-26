package com.kartoshkad.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.util.Map;

/**
 * Created by user on 2/13/16.
 */
public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, opts);

        float srcWidth = opts.outWidth;
        float srcHeight = opts.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight)
                inSampleSize = Math.round(srcHeight / destHeight);
            else
                inSampleSize = Math.round(srcWidth / destWidth);
        }

        opts = new BitmapFactory.Options();
        opts.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, opts);
    }

}
