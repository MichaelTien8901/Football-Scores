package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Date;

import barqsoft.footballscores.PagerFragment;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilites;
import barqsoft.footballscores.data.DatabaseContract;

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();

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

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // latest score

                Date currentDate = new Date(System.currentTimeMillis());
                long startDateInt = Utilites.removeTime(currentDate).getTime() - PagerFragment.NUM_PAGES/2*86400000;
                String[] currentStrs = new String[] {
                        String.valueOf(startDateInt)};

                final long identityToken = Binder.clearCallingIdentity();
                Uri dataUri = DatabaseContract.ScoreEntry.buildScoreWithStartDate();
                // change to all scores query
                String sortArgs = DatabaseContract.ScoreEntry.INT_DATE_COL + " ASC ";
                String selections = DatabaseContract.ScoreEntry.INT_DATE_COL
                        + " >= ? ";
//                       +"AND " + DatabaseContract.ScoreEntry.HOME_GOALS_COL + " >= 0";
                 data = getContentResolver().query(dataUri,
                        SCORES_COLUMNS,
                         selections,
                        currentStrs,
                        sortArgs );
//                String sortArgs = DatabaseContract.ScoreEntry.INT_DATE_COL + " DESC LIMIT 10 ";
//                String selections = DatabaseContract.ScoreEntry.INT_DATE_COL
//                        + " >= ? AND " + DatabaseContract.ScoreEntry.HOME_GOALS_COL + " >= 0";
//                 data = getContentResolver().query(dataUri,
//                        SCORES_COLUMNS,
//                         selections,
//                        currentStrs,
//                        sortArgs );
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);

                String home_name = data.getString(INDEX_HOME);
                views.setTextViewText(R.id.widget_list_home_name, home_name);
                String away_name = data.getString(INDEX_AWAY);
                views.setTextViewText(R.id.widget_list_away_name, away_name);
                views.setTextViewText(R.id.widget_list_time_textview, data.getString(INDEX_TIME));
                views.setTextViewText(R.id.widget_list_date_textview, data.getString(INDEX_DATE));
                int homeGoals = data.getInt(INDEX_HOME_GOALS);
                int awayGoals = data.getInt(INDEX_AWAY_GOALS);
                views.setTextViewText(R.id.widget_list_score_textview, Utilites.getScores(homeGoals, awayGoals));
                views.setImageViewResource(R.id.widget_listl_home_crest, Utilites.getTeamCrestByTeamName(home_name));
                views.setImageViewResource(R.id.widget_list_away_crest, Utilites.getTeamCrestByTeamName(away_name));

                String resultStr = homeGoals == awayGoals? getString(R.string.desc_tie):
                        (homeGoals > awayGoals?
                                getString(R.string.desc_win):
                                getString(R.string.desc_lose));
                String description =
                        home_name + resultStr + away_name + " " +
                                homeGoals + getString(R.string.desc_score_compare) + awayGoals;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, description);
                }
                // no need for fillInIntent
                final Intent fillInIntent = new Intent();
                Uri uri = DatabaseContract.ScoreEntry.buildScoreWithDateString( data.getString(INDEX_DATE));
                fillInIntent.setData(uri); // setup date time
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_list_item, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_SCORE_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
