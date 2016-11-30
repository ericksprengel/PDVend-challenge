package br.com.ericksprengel.pdvend;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_PAYMENT = "br.com.ericksprengel.intent.action.PAYMENT";
    public static final String EXTRA_VALUE = "value";

    private static final String RESULT_ACTION_CHANGE = "br.com.ericksprengel.intent.result.PAYMENT";
    private static final String RESULT_EXTRA_CHANGE = "change";

    public RetainedFragment mDataFragment;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentView = findViewById(R.id.main_activity_root);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        mDataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        // create the fragment and data the first time
        if (mDataFragment == null) {
            // add the fragment
            mDataFragment = new RetainedFragment();
            fm.beginTransaction().add(mDataFragment, "data").commit();
        }
    }

    public void showMessage(String msg, String action, View.OnClickListener listener) {
        Snackbar.make(mContentView, msg, Snackbar.LENGTH_LONG).setAction(action, listener).show();
    }

    public void showMessage(String msg) {
        Snackbar.make(mContentView, msg, Snackbar.LENGTH_LONG).show();
    }

    public void finishPayment(double change) {
        Log.i(BuildConfig.LOG_TAG, "Payment finishing... Change: " + NumberFormat.getCurrencyInstance().format(change));
        Intent result = new Intent(RESULT_ACTION_CHANGE);
        result.putExtra(RESULT_EXTRA_CHANGE, change);
        setResult(AppCompatActivity.RESULT_OK, result);
        finish();
    }
}
