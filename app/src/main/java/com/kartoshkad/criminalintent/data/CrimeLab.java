package com.kartoshkad.criminalintent.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.kartoshkad.criminalintent.database.CrimeBaseHelper;
import com.kartoshkad.criminalintent.database.CrimeCursorWrapper;
import com.kartoshkad.criminalintent.database.CrimeDbShema;
import com.kartoshkad.criminalintent.database.CrimeDbShema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by user on 2/1/16.
 */
public class CrimeLab {

    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new CrimeCursorWrapper(cursor);
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null)
            sCrimeLab = new CrimeLab(context);

        return sCrimeLab;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }
    public File getPhotoFile(Crime crime) {
        File externalFileDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFileDir == null)
            return null;

        return new File(externalFileDir, crime.getPhotoFilename());
    }
    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;
    }

    public void addCrime(Crime c) {
        ContentValues cv = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, cv);
    }
    public void deleteCrime(Crime c) {
        mDatabase.delete(
                CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { c.getId().toString() }
        );
    }
    public void updateCrime(Crime c) {
        String uuidString = c.getId().toString();
        ContentValues cv = getContentValues(c);

        mDatabase.update(
                CrimeTable.NAME,    // Table name to update
                cv,                 // Content values with new crime-data
                CrimeTable.Cols.UUID + " = ?",  // Field to search for row to update
                new String[] {uuidString}       // Field value
        );
    }
}
