package com.kartoshkad.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by user on 07/02/16.
 */
public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    private static final String EXTRA_DATE = "com.kartoshkad.criminalintent.date";

    private DatePicker mDatePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date crimeDate = (Date) getArguments().getSerializable(ARG_DATE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(crimeDate);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        mDatePicker = (DatePicker) v.findViewById(R.id.date_picker_dialog_date);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.crime_date_title)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();

                        Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }

    public static Date getDate(Intent data) {
        return (Date) data.getSerializableExtra(EXTRA_DATE);
    }
    public static DatePickerFragment newInstance(Date crimeDate) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, crimeDate);

        DatePickerFragment dialog = new DatePickerFragment();
        dialog.setArguments(args);
        return dialog;
    }

    private void sendResult(int resultCode, Date date) {
        if(getTargetFragment() == null)
            return;

        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
