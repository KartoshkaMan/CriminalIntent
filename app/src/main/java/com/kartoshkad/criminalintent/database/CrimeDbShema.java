package com.kartoshkad.criminalintent.database;

/**
 * Created by user on 09/02/16.
 */
public class CrimeDbShema {

    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String TITLE = "title";
            public static final String UUID = "uuid";
        }
    }
}
