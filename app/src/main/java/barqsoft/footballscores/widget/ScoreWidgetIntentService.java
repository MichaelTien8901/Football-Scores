package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.Date;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.PagerFragment;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilites;
import barqsoft.footballscores.data.DatabaseContract;

/**
 * Created by Michael Tien on 2015/9/17.
 */
public class ScoreWidgetIntentService extends IntentService {
    private static final String[] SCORES_COLUMNS = {
            DatabaseContract.SCORES_TABLE + "." + DatabaseContract.ScoreEntry._ID,
            DatabaseContract.ScoreEntry.DATE_COL,
            DatabaseContract.ScoreEntry.TIME_COL,
            DatabaseContract.ScoreEntry.HOME_COL,
            DatabaseContract.ScoreEntry.AWAY_COL,
            DatabaseContract.ScoreEntry.LEAGUE_COL,
            DatabaseContract.ScoreEntry.HOME_GOALS_COL,
            DatabaseContract.ScoreEntry.AWAY_GOALS_COL,
            DatabaseContract.ScoreEntry.INT_DATE_COL
    };
    // these indices must match the projection
    static final int INDEX_SCORE_ID = 0;
    static final int INDEX_DATE = 1;
    static final int INDEX_TIME = 2;
    static final int INDEX_HOME = 3;
    static final int INDEX_AWAY = 4;
    static final int INDEX_LEAGUE = 5;
    static final int INDEX_HOME_GOALS = 6;
    static final int INDEX_AWAY_GOALS = 7;
    static final int INDEX_INT_DATE = 8;

    public ScoreWidgetIntentService() {
        super("ScoreWidgetIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ScoreWidgetProvider.class));

        Date currentDate = new Date(System.currentTimeMillis());
        long startDateInt = Utilites.removeTime(currentDate).getTime() - PagerFragment.NUM_PAGES/2*86400000;
        String[] currentStrs = new String[] {
                String.valueOf(startDateInt)};
        Uri dataUri = DatabaseContract.ScoreEntry.buildScoreWithStartDate();
        String sortArgs = DatabaseContract.ScoreEntry.INT_DATE_COL + " DESC LIMIT 1";
        String selections = DatabaseContract.ScoreEntry.INT_DATE_COL
                + " >= ? AND " + DatabaseContract.ScoreEntry.HOME_GOALS_COL + " >= 0";
        Cursor data = getContentResolver().query(dataUri,
                SCORES_COLUMNS,
                selections,
                currentStrs,
                sortArgs );
        if (data == null)
            return;
        if ( !data.moveToFirst()) {
            data.close();
            return;
        }
        String homeNameText = data.getString(INDEX_HOME);
        String awayNameText = data.getString(INDEX_AWAY);
        String timeText = data.getString(INDEX_TIME);
        String dateText = data.getString(INDEX_DATE);
        String scoreText = Utilites.getScores(data.getInt(INDEX_HOME_GOALS), data.getInt(INDEX_AWAY_GOALS));
        int resHomeCrest = Utilites.getTeamCrestByTeamName(homeNameText);
        int resAwayCrest = Utilites.getTeamCrestByTeamName(awayNameText);

        for (int appWidgetId : appWidgetIds) {
             int layoutId = R.layout.widget_score;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setTextViewText(R.id.widget_home_name, homeNameText);
            views.setTextViewText(R.id.widget_away_name, awayNameText);
            views.setTextViewText(R.id.widget_score_textview, scoreText);
            views.setTextViewText(R.id.widget_time_textview, timeText);
            views.setTextViewText(R.id.widget_date_textview, dateText);
            views.setImageViewResource(R.id.widget_home_crest, resHomeCrest);
            views.setImageViewResource(R.id.widget_away_crest, resAwayCrest);

            // Create an Intent to launch MainActivity, pass date
            Intent launchIntent = new Intent(this, MainActivity.class);
            Uri uri = DatabaseContract.ScoreEntry.buildScoreWithDateString( data.getString(INDEX_DATE));
            launchIntent.setData(uri);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.score_widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}