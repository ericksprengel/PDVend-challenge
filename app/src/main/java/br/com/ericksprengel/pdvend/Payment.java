package br.com.ericksprengel.pdvend;

/**
 * Created by erick on 11/29/16.
 */

public class Payment {
    PaymentType type;
    double value;

    public Payment(PaymentType type, double value) {
        this.type = type;
        this.value = value;
    }

    enum PaymentType {
        MONEY("Dinheiro"), CREDIT("Cartão");

        String label;
        PaymentType(String label) {
            this.label = label;
        }
    }
}
