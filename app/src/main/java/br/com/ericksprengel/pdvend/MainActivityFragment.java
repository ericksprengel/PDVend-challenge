package br.com.ericksprengel.pdvend;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

    private TextView mPaymentTotalValue;
    private EditText mPaymentCurrentValue;
    private TextView mPaymentPendingValue;

    private double mTotalValue;
    private PaymentsListAdapter mAdapter;

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

        root.findViewById(R.id.payment_fragment_money_button).setOnClickListener(this);
        root.findViewById(R.id.payment_fragment_credit_button).setOnClickListener(this);

        mTotalValue = 500.00;

        List<Payment> payments = new ArrayList<>();
        payments.add(new Payment(Payment.PaymentType.MONEY, 123.45));
        payments.add(new Payment(Payment.PaymentType.CREDIT, 20.35));
        mAdapter = new PaymentsListAdapter(getLayoutInflater(savedInstanceState), payments);
        setListAdapter(mAdapter);

        updateView();
        return root;
    }

    private void updateView() {
        double totalValueAdded = mAdapter.getTotalValue();

        mPaymentTotalValue.setText(mCurrencyFormat.format(mTotalValue));
        mPaymentPendingValue.setText(mCurrencyFormat.format(mTotalValue - totalValueAdded));
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
        }
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

        public void addPayment(Payment payment) {
            mPayments.add(payment);
            notifyDataSetChanged();
        }


        class ViewHolder {
            TextView type;
            TextView value;
        }

    }
}
