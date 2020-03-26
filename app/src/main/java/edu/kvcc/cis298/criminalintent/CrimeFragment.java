package edu.kvcc.cis298.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    // Key no for activity, so not operating at OS level, so no need
    // to include the package name in the string.
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate"; // For Dialog
    private static final int REQUEST_DATE = 0;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckbox;
    private Button mReportButton;

    // Setup a static method to create a new fragment.
    // This is similar to an activity asking another activity
    // how to get an intent to get it started. Only, now we
    // are setting up a method to ask the Fragment how to get
    // a new fragment of itself created.
    public static CrimeFragment newInstance(UUID crimeId) {
        // Bundle used to store fragment arguments
        Bundle args = new Bundle();
        // Again using serializable since UUID is a class instance
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the fragment arguments for this fragment.
        // Then retrieve the UUID from the fragment arguments.
        // UUID is not a primitive type, so it can only be stored if
        // it is serializable. Luckily UUID implements Serializable, so
        // we can send it in Extras. As a result we use getSerializableExtra
        // to retrieve it here.
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        // Query the Singleton to get the specific Crime out.
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        // We have to do the manual work of inflating the layout that we want
        // to use with this code file. We pass false as the third parameter
        // because we intend to add the fragment to the activity manually.
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        // Do all event listeners for widgets here before the view is returned.

        // FindViewById is a method on the view, so we need to access it from
        // the view v. In an activity, the method was right on the activity, so
        // it was shorter.
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing. The method must be overridden though, so here it is.
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing. The method must be overridden though, so here it is.
            }
        });

        // Put the date in the button text. Also disable the button.
        // functionality for the button will be added much later.
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getDate().toString());

        //****************************************************
        // This is Dialog code here. Not needed for assignment
        //****************************************************
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                //Ask the DialogFragment for fragment with the newInstance method
                DatePickerFragment dialog = DatePickerFragment.newIntance(mCrime.getDate());
                // Set the target fragment to this fragment for when the dialog finishes.
                // Also send over the REQUEST Code declared at the top.
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                // Tell the fragment to show. Send over the string DIALOG_DATE as a key that
                // the fragment manager will use for keeping track of the fragment.
                dialog.show(manager, DIALOG_DATE);
            }
        });
        //***************************************************

        mSolvedCheckbox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(
                    CompoundButton buttonView,
                    boolean isChecked
            ) {
                mCrime.setSolved(isChecked);
            }
        });

        // Setup the report button
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make an implicit intent that can send data
                Intent i = new Intent(Intent.ACTION_SEND);
                // Set the MIME type to text/plain
                i.setType("text/plain");
                // Set the data on the intent
                // Use putExtra just like an explicit intent.
                // There are keys built into the Intent class for common types of data
                // that would need to be sent to the implicit intents.
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(
                        Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject)
                );
                // Create a chooser so that the user ALWAYS has to choose how to send it.
                // removing this line will give them the option to set a default and never
                // be prompted again.
                i = Intent.createChooser(i, getString(R.string.send_report));
                // Start the activity.
                startActivity(i);
            }
        });

        return v;
    }

    //****************************************************
    // This is Dialog code here. Not needed for assignment
    //****************************************************

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the result code is not okay, then there is nothing to do. Just return
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        // If we are returning from the date picker fragment
        if (requestCode == REQUEST_DATE) {
            // Fetch the date from the data
            Date date = (Date) data.getSerializableExtra(
                    DatePickerFragment.EXTRA_DATE
            );
            // Set the date on the Crime and update the text.
            mCrime.setDate(date);
            mDateButton.setText(mCrime.getDate().toString());
        }

    }

    // Method to generate the crime report
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(
                R.string.crime_report,
                mCrime.getTitle(),
                dateString,
                solvedString,
                suspect
        );

        return report;
    }
}
