package br.com.ericksprengel.pdvend;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends ListFragment implements View.OnClickListener {

    final NumberFormat mCurrencyFormat = NumberFormat.getCurrencyInstance();
    final NumberFormat mNumberFormat = NumberFormat.getNumberInstance();

    private TextView mPaymentTotalValue;
    private EditText mPaymentCurrentValue;
    private TextView mPaymentPendingValue;
    private Button mMoneyButton;
    private Button mCreditButton;
    private Button mSendButton;


    private double mTotalValue;
    private PaymentsListAdapter mAdapter;
    private TextWatcher mCurrencyWatcher = new CurrencyWatcherFormatter();

    public MainActivityFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        getActivity().setTitle("PAGAMENTO");

        mPaymentTotalValue = (TextView) root.findViewById(R.id.payment_fragment_total_value);
        mPaymentCurrentValue = (EditText) root.findViewById(R.id.payment_fragment_current_value);
        mPaymentPendingValue = (TextView) root.findViewById(R.id.payment_fragment_pending_value);

        mMoneyButton = (Button) root.findViewById(R.id.payment_fragment_money_button);
        mCreditButton = (Button) root.findViewById(R.id.payment_fragment_credit_button);
        mSendButton = (Button) root.findViewById(R.id.payment_fragment_send_button);

        mMoneyButton.setOnClickListener(this);
        mCreditButton.setOnClickListener(this);
        mSendButton.setOnClickListener(this);
        root.findViewById(R.id.payment_fragment_cancel_button).setOnClickListener(this);

        mPaymentCurrentValue.addTextChangedListener(mCurrencyWatcher);
        mNumberFormat.setMinimumFractionDigits(2);

        List<Payment> payments = new ArrayList<>();
        // payments.add(new Payment(Payment.PaymentType.MONEY, 123.45));
        // payments.add(new Payment(Payment.PaymentType.CREDIT, 20.35));
        mAdapter = new PaymentsListAdapter(getLayoutInflater(savedInstanceState), payments);
        setListAdapter(mAdapter);

        mPaymentCurrentValue.setText(mCurrencyFormat.format(mTotalValue));

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Intent intent = getActivity().getIntent();

        if(intent.getAction() != MainActivity.ACTION_PAYMENT) {
            mTotalValue = 1100.00;
            getMainActivity().showMessage("I'm starting a payment test with fixed value: " + NumberFormat.getCurrencyInstance().format(mTotalValue));
        } else if(intent.hasExtra(MainActivity.EXTRA_VALUE)) {
            mTotalValue = intent.getDoubleExtra(MainActivity.EXTRA_VALUE, 0.00);
        } else {
            Toast.makeText(getContext(), "Error: invalid intet", Toast.LENGTH_LONG).show();
            getMainActivity().finish();
        }
        updateView();
    }

    private void updateView() {
        double totalValueAdded = mAdapter.getTotalValue();

        mPaymentTotalValue.setText(mCurrencyFormat.format(mTotalValue));
        mPaymentPendingValue.setText(mNumberFormat.format(mTotalValue - totalValueAdded));

        double totalValueByCreditAdded = mAdapter.getTotalValueByCredit();
        if(totalValueAdded >= mTotalValue) {
            mCreditButton.setEnabled(false);
            mMoneyButton.setEnabled(false);
            mSendButton.setEnabled(true);
        } else {
            mCreditButton.setEnabled(!(totalValueByCreditAdded + getCurrentValue() > mTotalValue));
            mMoneyButton.setEnabled(true);
            mSendButton.setEnabled(totalValueAdded >= mTotalValue);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payment_fragment_money_button:
                mAdapter.addPayment(new Payment(Payment.PaymentType.MONEY, getCurrentValue()));
                updateView();
                break;
            case R.id.payment_fragment_credit_button:
                mAdapter.addPayment(new Payment(Payment.PaymentType.CREDIT, getCurrentValue()));
                updateView();
                break;
            case R.id.payment_fragment_send_button:
                getMainActivity().finishPayment(getChange());
                break;
            case R.id.payment_fragment_cancel_button:
                getActivity().finish();
                break;
        }
    }

    private double getChange() {
        return mAdapter.getTotalValue() - mTotalValue;
    }

    public double getCurrentValue() {
        try {
            return mCurrencyFormat.parse(mPaymentCurrentValue.getText().toString()).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Desculpe-me, mas esta operação não pode ser concluída.", Toast.LENGTH_LONG).show();
            return 0.0;
        }
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    private class PaymentsListAdapter extends BaseAdapter {

        private final LayoutInflater mLayoutInflater;
        private List<Payment> mPayments;

        public PaymentsListAdapter(LayoutInflater layoutInflater, List<Payment> payments) {
            mLayoutInflater = layoutInflater;
            mPayments = payments;
        }
        @Override
        public int getCount() {
            return mPayments.size();
        }

        @Override
        public Payment getItem(int position) {
            return mPayments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if(convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.payment_list_item, parent, false);
                holder = new ViewHolder();
                holder.type = (TextView) convertView.findViewById(R.id.payment_list_item_type);
                holder.value = (TextView) convertView.findViewById(R.id.payment_list_item_value);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Payment payment = mPayments.get(position);
            holder.type.setText(payment.type.label);
            holder.value.setText(mCurrencyFormat.format(payment.value));

            return convertView;
        }

        public double getTotalValue() {
            double res = 0.0;
            for(Payment p : mPayments) {
                res+=p.value;
            }
            // mPayments.stream().mapToDouble(p -> p.value).reduce(0.0, Double::sum);
            return res;
        }

        public double getTotalValueByCredit() {
            double res = 0.0;
            for(Payment p : mPayments) {
                if(p.type.equals(Payment.PaymentType.CREDIT)) {
                    res += p.value;
                }
            }
            // mPayments.stream().mapToDouble(p -> p.value).reduce(0.0, Double::sum);
            return res;
        }

        public void addPayment(Payment payment) {
            mPayments.add(payment);
            notifyDataSetChanged();
        }


        class ViewHolder {
            TextView type;
            TextView value;
        }

    }

    private class CurrencyWatcherFormatter implements TextWatcher {
        private String current = "";

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                mPaymentCurrentValue.removeTextChangedListener(this);

                String cleanString = s.toString().replaceAll("[\\D]", "");

                double value = Double.parseDouble(cleanString) / 100;
                String formatted = mCurrencyFormat.format(value);

                current = formatted;
                mPaymentCurrentValue.setText(formatted);
                mPaymentCurrentValue.setSelection(formatted.length());

                mPaymentCurrentValue.addTextChangedListener(this);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            updateView();
        }
    }
}
