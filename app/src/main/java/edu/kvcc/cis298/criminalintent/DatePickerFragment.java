package edu.kvcc.cis298.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE =
            "edu.kvcc.cis298.criminalintent.date";

    private static final String ARG_DATE = "date";

    private DatePicker mDatePicker;

    public static DatePickerFragment newIntance(Date date) {
        // Make a new Bundle to put fragment arguments in
        Bundle args = new Bundle();
        // Put the args in the bundle
        args.putSerializable(ARG_DATE, date);
        // Make a new fragment and attach the args.
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        // Return that new fragment.
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Get the serialized date out of the fragment arguments that were
        // set in the newInstance method and cast it to a date object.
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        // Pull out the various parts of date to send to the widget
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Inflate the view
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        // Get a reference to the DatePicker widget
        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        // Initialize the date on the widget
        mDatePicker.init(year, month, day, null);

        // Build and return the AlertDialog
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Convert the date picker widget to a java Date
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();

                                Date date = new GregorianCalendar(
                                        year,
                                        month,
                                        day
                                ).getTime();
                                // Call the sendResult method declared below.
                                // Use the Constant Activity.RESULT_OK to signify
                                // that everything was good. Even though it is an
                                // Activity constant, we can use it here since it
                                // is just a value accessible from everywhere.
                                sendResult(Activity.RESULT_OK, date);
                            }
                        })
                .create();
    }

    // Method to send the result of the dialog to the calling fragment
    private void sendResult(int resultCode, Date date) {
        // If the targetFragment was not set by the calling fragment, just return
        if (getTargetFragment() == null) {
            return;
        }
        // Make a new Intent to hold the return data
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        // Call onActivityResult of the targetFragment to force the calling fragment
        // to update. Send it the request code, resultCode, and the intent that has
        // the return data.
        getTargetFragment().onActivityResult(
                getTargetRequestCode(),
                resultCode,
                intent
        );
    }
}
