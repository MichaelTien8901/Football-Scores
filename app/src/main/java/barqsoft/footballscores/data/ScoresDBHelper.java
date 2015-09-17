package barqsoft.footballscores.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barqsoft.footballscores.data.DatabaseContract.ScoreEntry;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresDBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 3;
    public ScoresDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String CreateScoresTable = "CREATE TABLE " + DatabaseContract.SCORES_TABLE + " ("
                + ScoreEntry._ID + " INTEGER PRIMARY KEY,"
                + ScoreEntry.DATE_COL + " TEXT NOT NULL,"
                + ScoreEntry.TIME_COL + " INTEGER NOT NULL,"
                + ScoreEntry.HOME_COL + " TEXT NOT NULL,"
                + ScoreEntry.AWAY_COL + " TEXT NOT NULL,"
                + ScoreEntry.LEAGUE_COL + " INTEGER NOT NULL,"
                + ScoreEntry.HOME_GOALS_COL + " TEXT NOT NULL,"
                + ScoreEntry.AWAY_GOALS_COL + " TEXT NOT NULL,"
                + ScoreEntry.MATCH_ID + " INTEGER NOT NULL,"
                + ScoreEntry.MATCH_DAY + " INTEGER NOT NULL,"
                + ScoreEntry.INT_DATE_COL + " INTEGER NOT NULL,"
                + " UNIQUE ("+ ScoreEntry.MATCH_ID+") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(CreateScoresTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.SCORES_TABLE);
    }
}
