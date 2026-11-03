package com.equipevii.triviamaster.data.local;
import android.net.Uri;
import android.provider.BaseColumns;

public final class TriviaContract {
    private TriviaContract(){}
    public static final String AUTHORITY = "com.equipevii.triviamaster.triviaprovider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class MatchEntry implements BaseColumns {
        public static final String TABLE_NAME = "match";
        public static final String COLUMN_MATCH_PLAYER = "match_player";
        public static final String COLUMN_MATCH_CATEGORY = "match_category";
        public static final String COLUMN_MATCH_DIFFICULTY = "match_difficulty";
        public static final String COLUMN_CORRECT_GUESSES = "correct_guesses";
        public static final String COLUMN_TOTAL_GUESSES = "total_guesses";
        public static final String COLUMN_MATCH_SCORE = "match_score";
        public static final String COLUMN_MATCH_TIME = "match_time";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                "matches");
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE: " + MatchEntry.TABLE_NAME + " (" + MatchEntry._ID +
            " INTEGER PRIMARY KEY," + MatchEntry.COLUMN_MATCH_PLAYER
            + " TEXT," + MatchEntry.COLUMN_MATCH_CATEGORY + " TEXT," +
            MatchEntry.COLUMN_MATCH_DIFFICULTY + " TEXT," + MatchEntry.COLUMN_CORRECT_GUESSES
            + " INTEGER," + MatchEntry.COLUMN_TOTAL_GUESSES + " INTEGER," +
            MatchEntry.COLUMN_MATCH_SCORE + " INTEGER," + MatchEntry.COLUMN_MATCH_TIME +
            " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MatchEntry.TABLE_NAME;
}
