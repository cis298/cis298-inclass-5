package edu.kvcc.cis298.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.List;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // Inflate the layout file for this fragment
        View view = inflater.inflate(
                R.layout.fragment_crime_list,
                container,
                false
        );

        // Get a handle to the recycler view
        mCrimeRecyclerView = (RecyclerView) view
            .findViewById(R.id.crime_recycler_view);
        // Set the LayoutManager. Without this, the
        // app would just crash.
        mCrimeRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        getActivity()
                )
        );

        updateUI();

        // Return the view now that things are wired up.
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        // Get the singleton of crimes
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        // If the crimeLab is empty, load the crimes to populate the list
        if (crimeLab.isEmpty() && !crimeLab.isDataLoadedOnce()) {
            InputStream csvStream = getResources().openRawResource(R.raw.crimes);
            crimeLab.loadCrimeList(csvStream);
            //crimeLab.addDefaultCrimes();
        }

        // Pull out the list of crimes from the singleton
        List<Crime> crimes = crimeLab.getCrimes();

        // If coming back from detail view, the adapter
        // may already exist and we just need to notify
        // it that the data has changed.
        if (mAdapter == null) {
            // Create a new adapter sending over the list of crimes
            mAdapter = new CrimeAdapter(crimes);
            // Set the adapter for the RecyclerView as the adapter we
            // just created.
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        // If you don't want anyone to be able to read your code
        // do this! It is the same as the above 4 lines.
        //mCrimeRecyclerView.setAdapter(new CrimeAdapter(crimeLab.get(getActivity()).getCrimes()));
    }

    private class CrimeHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
        private TextView mTitleTextView;
        private TextView mDateTextView;

        private Crime mCrime;

        public CrimeHolder(
                LayoutInflater inflater,
                ViewGroup parent
        ) {
            super(
                    inflater.inflate(R.layout.list_item_crime,
                            parent,
                            false)
            );
            // itemView is defined in RecyclerView. We did not define this,
            // but we are using it.
            // When the super constructor above inflates the layout file
            // it stores it in itemView
            //
            // We are setting the onClickListenter for that layout to this
            // CrimeHolder class.
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(
                    R.id.crime_title
            );
            mDateTextView = (TextView) itemView.findViewById(
                    R.id.crime_date
            );
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimePagerActivity.newIntent(
                    getActivity(),
                    mCrime.getId()
            );
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>
    {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }


        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType
        ) {
            // Get the activities layout inflator so that we
            // can use it send to the CrimeHolder, who will
            // then in turn use it to inflate the CrimeHolder's
            // view.
            LayoutInflater layoutInflater = LayoutInflater.from(
                    getActivity()
            );

            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(
                @NonNull CrimeHolder holder,
                int position
        ) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
