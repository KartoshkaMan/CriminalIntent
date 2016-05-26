package com.kartoshkad.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.kartoshkad.criminalintent.data.Crime;
import com.kartoshkad.criminalintent.data.CrimeLab;

import java.util.List;
import java.util.UUID;

/**
 * Created by user on 2/6/16.
 */
public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks {

    private static final String EXTRA_CRIME_ID = "com.kartoshkad.criminalintent.crime_id";
    private static final String EXTRA_CRIME_NUM = "com.kartoshkad.criminalintent.crime_num";

    private List<Crime> mCrimes;

    private ViewPager mViewPager;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent i = new Intent(packageContext, CrimePagerActivity.class);
        i.putExtra(EXTRA_CRIME_ID, crimeId);
//        i.putExtra(EXTRA_CRIME_NUM, cNum);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {



            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
//        mViewPager.setCurrentItem(
//                getIntent().getIntExtra(EXTRA_CRIME_NUM, 0),
//                true
//        );
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        //
    }
}
