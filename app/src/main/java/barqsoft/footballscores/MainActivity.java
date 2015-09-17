package barqsoft.footballscores;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.data.DatabaseContract;

public class MainActivity extends ActionBarActivity
{
    public static int selected_match_id;
    public static int start_fragment = 2;
    public static String LOG_TAG = "MainActivity";
    private final String save_tag = "Save Test";
    public static boolean isRTL = false;

    private PagerFragment my_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            Configuration config = getResources().getConfiguration();
            isRTL = config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        }

        Uri contentUri = getIntent() != null ? getIntent().getData() : null;
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        if ( contentUri != null) {
            String dateStr = DatabaseContract.ScoreEntry.getDateStrFromUri(contentUri);
            // setup start_fragment
            for (int i = 0;i < PagerFragment.NUM_PAGES;i++) {
                Date fragmentDate;
                if (isRTL)
                    fragmentDate = new Date(System.currentTimeMillis() + ((PagerFragment.NUM_PAGES / 2 - i) * 86400000));
                else
                    fragmentDate = new Date(System.currentTimeMillis() + ((i - PagerFragment.NUM_PAGES / 2) * 86400000));

                if (dateStr.equals(mformat.format(fragmentDate))) {
                    start_fragment = i;
                    break;
                }
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "Reached MainActivity onCreate");
        if (savedInstanceState == null) {
            my_main = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, my_main)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.v(save_tag,"will save");
        Log.v(save_tag,"fragment: "+String.valueOf(my_main.mPagerHandler.getCurrentItem()));
        Log.v(save_tag,"selected id: "+selected_match_id);
        outState.putInt("Pager_Current",my_main.mPagerHandler.getCurrentItem());
        outState.putInt("Selected_match",selected_match_id);
        getSupportFragmentManager().putFragment(outState,"my_main",my_main);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.v(save_tag,"will retrive");
        Log.v(save_tag,"fragment: "+String.valueOf(savedInstanceState.getInt("Pager_Current")));
        Log.v(save_tag,"selected id: "+savedInstanceState.getInt("Selected_match"));
        start_fragment = savedInstanceState.getInt("Pager_Current");
        selected_match_id = savedInstanceState.getInt("Selected_match");
        my_main = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,"my_main");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
