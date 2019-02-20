package edu.kvcc.cis298.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    // This is a static variable so there can be only one.
    // It will hold the instance of this class.
    private static CrimeLab sCrimeLab;

    // List for the collection of crimes.
    private List<Crime> mCrimes;

    // Static get method which will allow us to call
    // CrimeLab.get(context) from anywhere in our program
    // to always get the same instance of our crimeLab.
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    // Private constructor. Which means it is not possible
    // to create an instance from outside this class.
    // If you want an instance, you MUST use the static
    // get method above to get the instance.
    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 ==0);
            mCrimes.add(crime);
        }
    }

    // Getter for Crime List
    public List<Crime> getCrimes() {
        return mCrimes;
    }

    // Getter for single crime.
    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        return null;
    }
}
