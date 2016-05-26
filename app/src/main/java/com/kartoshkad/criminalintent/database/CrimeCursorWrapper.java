package com.kartoshkad.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.kartoshkad.criminalintent.data.Crime;
import com.kartoshkad.criminalintent.database.CrimeDbShema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by user on 09/02/16.
 */
public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        crime.setTitle(title);
        return crime;
    }
}
