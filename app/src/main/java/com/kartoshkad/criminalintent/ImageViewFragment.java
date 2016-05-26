package com.kartoshkad.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by user on 2/13/16.
 */
public class ImageViewFragment extends DialogFragment {
    private static final String ARG_CRIME_TITLE = "crime_title";
    private static final String ARG_IMAGE_FILE = "image_file";

    private File mCrimePhoto;

    private ImageView mImageView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCrimePhoto = (File) getArguments().getSerializable(ARG_IMAGE_FILE);

        String crimeTitle = (String) getArguments().getSerializable(ARG_CRIME_TITLE);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_image_view, null);

        mImageView = (ImageView) v.findViewById(R.id.image_veiw);
        if (mCrimePhoto == null || !mCrimePhoto.exists())
            mImageView.setImageDrawable(null);
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mCrimePhoto.getPath(), getActivity());
            mImageView .setImageBitmap(bitmap);
        }

        return new AlertDialog.Builder(getActivity())
                .setNegativeButton(R.string.close, null)
                .setTitle(crimeTitle)
                .setView(v)
                .create();
    }

    public static ImageViewFragment newInstance(File file, String crimeTitle) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_TITLE, crimeTitle);
        args.putSerializable(ARG_IMAGE_FILE, file);

        ImageViewFragment dialog = new ImageViewFragment();
        dialog.setArguments(args);
        return dialog;
    }

}
