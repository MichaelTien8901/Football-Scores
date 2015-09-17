package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.service.MyFetchService;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{
    public static final int NUM_PAGES = 5;
    public ViewPager mPagerHandler;
    private myPageAdapter mPagerAdapter;
    private MainScreenFragment[] viewFragments = new MainScreenFragment[NUM_PAGES];
    private void update_scores()
    {
        Intent service_start = new Intent(getActivity(), MyFetchService.class);
        getActivity().startService(service_start);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        update_scores();
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new myPageAdapter(getChildFragmentManager());
        for (int i = 0;i < NUM_PAGES;i++)
        {
            Date fragmentDate;
            if ( MainActivity.isRTL)
                fragmentDate = new Date(System.currentTimeMillis()+((NUM_PAGES/2-i)*86400000));
            else
                fragmentDate = new Date(System.currentTimeMillis()+((i-NUM_PAGES/2)*86400000));

            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            viewFragments[i] = new MainScreenFragment();
            viewFragments[i].setFragmentDate(mformat.format(fragmentDate));
        }
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.start_fragment);
        return rootView;
    }
    private class myPageAdapter extends FragmentStatePagerAdapter
    {
        @Override
        public Fragment getItem(int i)
        {
            return viewFragments[i];
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        public myPageAdapter(FragmentManager fm)
        {
            super(fm);
        }
        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position)
        {
            Long pageDate = System.currentTimeMillis() +
                    (MainActivity.isRTL ? ((NUM_PAGES/2- position)*86400000): ((position- NUM_PAGES/2)*86400000));
            return Utilites.getDayName(getActivity(), pageDate);
        }
    }
}
