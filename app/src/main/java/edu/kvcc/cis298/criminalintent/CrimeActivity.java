package edu.kvcc.cis298.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID =
            "edu.kvcc.cis298.criminalintent.crime_id";

    public static Intent newIntent(
            Context packageContext,
            UUID crimeId
    ) {
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        // Get the crimeId out of the intent that was used to get this activity started.
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        // Ask CrimeFragment to give us a new instance of itself. We send the
        // UUID we just got out over as a param that will then be available in the
        // fragment that gets created.
        return CrimeFragment.newInstance(crimeId);
    }
}
