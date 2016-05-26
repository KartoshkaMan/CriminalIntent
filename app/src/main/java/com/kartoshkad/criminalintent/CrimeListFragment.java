package com.kartoshkad.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kartoshkad.criminalintent.data.Crime;
import com.kartoshkad.criminalintent.data.CrimeLab;

import java.util.List;

/**
 * Created by user on 2/1/16.
 */
public class CrimeListFragment extends Fragment {

    private static final int REQUEST_CRIME_ACTIVITY = 1;
    private static final String BUNDLE_SUBTITLE_IS_VISIBLE = "subtitleIsVisible";
    private static final String TAG = "CrimeListFragment";

    private boolean mSubtitleIsVisible;
    private Callbacks mCallbacks;

    private Button mAddCrimeButton;
    private CrimeAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private View mEmptyView;

    public CrimeListFragment() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        if (savedInstanceState !=  null)
            mSubtitleIsVisible = savedInstanceState.getBoolean(BUNDLE_SUBTITLE_IS_VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        initView(view);
        updateUI();

        Log.d(TAG, "onCreateView");

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_SUBTITLE_IS_VISIBLE, mSubtitleIsVisible);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CrimeLab.get(getActivity()).getCrimes().isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            updateUI();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleIsVisible) subtitleItem.setTitle(R.string.hide_subtitle);
        else subtitleItem.setTitle(R.string.show_subtitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime: {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);

                return true;
            }
            case R.id.menu_item_show_subtitle: {
                mSubtitleIsVisible = !mSubtitleIsVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void initAddCrimeButton() {
        mAddCrimeButton = (Button) mEmptyView.findViewById(R.id.crime_list_add_crime_button);
        mAddCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(
                        getActivity(),
                        crime.getId()
                );
                startActivityForResult(intent, REQUEST_CRIME_ACTIVITY);
            }
        });
    }
    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mEmptyView = view.findViewById(R.id.crime_empty_placeholder);
        initAddCrimeButton();
    }
    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        String subtitle = getResources().getQuantityString(
                R.plurals.subtitle_format,
                crimeLab.getCrimes().size(),
                crimeLab.getCrimes().size()
        );

        if (!mSubtitleIsVisible) subtitle = null;

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar bar = activity.getSupportActionBar();
        if (bar != null)
            bar.setSubtitle(subtitle);
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }




    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(getActivity());
            View view = li.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

    }
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;

        private CheckBox mSolvedCheckBox;
        private TextView mDateTextView;
        private TextView mTitleTextView;

        public CrimeHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_checkbox);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onCrimeSelected(mCrime);
        }

        private void bindCrime(Crime crime) {
            mCrime = crime;
            mSolvedCheckBox.setChecked(crime.isSolved());
            mDateTextView.setText(crime.getDate().toString());
            mTitleTextView.setText(crime.getTitle());
        }
    }

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }
}
