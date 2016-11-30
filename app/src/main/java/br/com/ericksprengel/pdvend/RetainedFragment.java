package br.com.ericksprengel.pdvend;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by erick on 11/30/16.
 */

public class RetainedFragment extends Fragment {
    private List<Payment> mPayments = new ArrayList<>();


    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public List<Payment> getPayments() {
        return mPayments;
    }

    public void setPayments(List<Payment> payments) {
        mPayments = payments;
    }
}
