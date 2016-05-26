package com.kartoshkad.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.kartoshkad.criminalintent.data.Crime;
import com.kartoshkad.criminalintent.data.CrimeLab;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final int REQUEST_CODE_CONTACT = 0;
    private static final int REQUEST_CODE_DATE = 1;
    private static final int REQUEST_CODE_NONE = 2;
    private static final int REQUEST_CODE_PHOTO = 3;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String TAG_CRIME_FRAGMENT = "CrimeFragment";
    private static final String TAG_DATE_DIALOG = "DateDialog";
    private static final String TAG_IMAGE_DIALOG = "ImageDialog";

    private static final String DATE_FORMAT = "EEEE, MMM dd";

    private Callbacks mCallbacks;
    private Crime mCrime;
    private File mCrimePhoto;

    private Button mDateButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private CheckBox mSolvedCheckBox;
    private EditText mTitleField;
    private ImageButton mCameraButton;
    private ImageView mImageView;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment cf = new CrimeFragment();
        cf.setArguments(args);
        return cf;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean resultIsOk = resultCode == Activity.RESULT_OK;

        if (resultIsOk && requestCode == REQUEST_CODE_DATE) {
            activityResultForDate(data);
        } else if (resultIsOk && requestCode == REQUEST_CODE_CONTACT) {
            activityResultForContact(data);
        } else if (resultIsOk && requestCode == REQUEST_CODE_PHOTO) {
            updateCrime();
            updateImageView();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mCrimePhoto = CrimeLab.get(getContext()).getPhotoFile(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        initCameraButton(v);
        initDateButton(v);
        initImageView(v);
        initReportButton(v);
        initSolvedCheckBox(v);
        initSuspectButton(v);
        initTitleField(v);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG_CRIME_FRAGMENT, "onPause");
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_delete_crime: {
                CrimeLab crimeLab = CrimeLab.get(getActivity());
                crimeLab.deleteCrime(mCrime);
                getActivity().finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String formatDate(Date date) {
        return DateFormat.format(DATE_FORMAT, date).toString();
    }
    private String getCrimeReport() {
        String solvedString;
        if (mCrime.isSolved()) solvedString = getString(R.string.crime_report_solved);
        else solvedString = getString(R.string.crime_report_unsolved);

        String dateString = formatDate(mCrime.getDate());

        String suspect = mCrime.getSuspect();
        if (suspect == null) suspect = getString(R.string.crime_report_no_suspect);
        else suspect = getString(R.string.crime_report_suspect, suspect);

        return getString(
                R.string.crime_report,
                mCrime.getTitle(),
                dateString,
                solvedString,
                suspect
        );
    }

    private void activityResultForContact(Intent data) {
        Uri contactUri = data.getData();
        String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME
        };
        Cursor cursor = getActivity()
                .getContentResolver()
                .query(contactUri, queryFields, null, null, null);

        try {
            if (cursor.getCount() == 0)
                return;

            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            mCrime.setSuspect(suspect);
            updateCrime();
            mSuspectButton.setText(suspect);
        } finally {
            cursor.close();
        }
    }
    private void activityResultForDate(Intent data) {
        Date date = DatePickerFragment.getDate(data);
        mCrime.setDate(date);
        mDateButton.setText(formatDate(date));
        updateCrime();
    }
    private void checkForContactApp(Intent intent) {
        PackageManager pm = getActivity().getPackageManager();
        if (pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null)
            mSuspectButton.setEnabled(false);
    }
    private void initCameraButton(View view) {
        PackageManager pm = getActivity().getPackageManager();
        final Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mCrimePhoto != null && photoIntent.resolveActivity(pm) != null;

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mCrimePhoto);
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mCameraButton = (ImageButton) view.findViewById(R.id.crime_camera_button);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(photoIntent, REQUEST_CODE_PHOTO);
            }
        });


    }
    private void initDateButton(View view) {
        mDateButton = (Button) view.findViewById(R.id.crime_date_button);
        mDateButton.setText(formatDate(mCrime.getDate()));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();

                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CODE_DATE);
                dialog.show(fm, TAG_DATE_DIALOG);
            }
        });
    }
    private void initImageView(View view) {
        mImageView = (ImageView) view.findViewById(R.id.crime_image_view);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();

                ImageViewFragment dialog = ImageViewFragment.newInstance(
                        mCrimePhoto,
                        mCrime.getTitle()
                );
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CODE_NONE);
                dialog.show(fm, TAG_IMAGE_DIALOG);
            }
        });
        this.updateImageView();
    }
    private void initReportButton(View view) {
        mReportButton = (Button) view.findViewById(R.id.crime_send_report_button);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = Intent.createChooser(
                        intent,
                        getString(R.string.crime_send_report_label)
                );
                startActivity(intent);
            }
        });
    }
    private void initSolvedCheckBox(View view) {
        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved_checkbox);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });
    }
    private void initSuspectButton(View view) {
        final Intent pickContactIntent = new Intent(
                Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI
        );

        mSuspectButton = (Button) view.findViewById(R.id.crime_choose_suspect_button);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContactIntent, REQUEST_CODE_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null)
            mSuspectButton.setText(mCrime.getSuspect());


        checkForContactApp(pickContactIntent);
    }
    private void initTitleField(View view) {
        mTitleField = (EditText) view.findViewById(R.id.crime_title_edit_text);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });
    }
    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }
    private void updateImageView() {
        if (mCrimePhoto == null || !mCrimePhoto.exists())
            mImageView.setImageDrawable(null);
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mCrimePhoto.getPath(), getActivity());
            mImageView .setImageBitmap(bitmap);
        }
    }

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }
}
