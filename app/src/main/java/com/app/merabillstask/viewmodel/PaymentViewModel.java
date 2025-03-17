package com.app.merabillstask.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.merabillstask.data.PaymentRepository;
import com.app.merabillstask.model.Payment;

import java.util.ArrayList;
import java.util.List;

public class PaymentViewModel extends ViewModel {
    private MutableLiveData<List<Payment>> payments = new MutableLiveData<>();
    private MutableLiveData<Integer> errorMessage = new MutableLiveData<>(0);
    private MutableLiveData<Boolean> addPaymentStatus = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> savePaymentStatus = new MutableLiveData<>();
    private PaymentRepository repository;

    public PaymentViewModel(PaymentRepository repository) {
        this.repository = repository;
        payments.setValue(repository.loadPayments());
    }

    public LiveData<List<Payment>> getPayments() {
        return payments;
    }

    public LiveData<Integer> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getAddPaymentStatus() {
        return addPaymentStatus;
    }

    public LiveData<Boolean> getSavePaymentStatus() {
        return savePaymentStatus;
    }

    public void addPayment(String type, String amountStr, String provider, String transactionRef) {
        if (amountStr.isEmpty()) {
            errorMessage.setValue(1);
            addPaymentStatus.setValue(false);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            errorMessage.setValue(2);
            addPaymentStatus.setValue(false);
            return;
        }

        if ((type.equals("Bank Transfer") || type.equals("Credit Card")) && (provider.isEmpty() || transactionRef.isEmpty())) {
            errorMessage.setValue(3);
            addPaymentStatus.setValue(false);
            return;
        }

        List<Payment> updatedPayments = new ArrayList<>(payments.getValue());
        updatedPayments.add(new Payment(type, amount, provider, transactionRef));
        payments.setValue(updatedPayments);
        errorMessage.setValue(0);
        addPaymentStatus.setValue(true);
    }

    public void removePayment(Payment payment) {
        List<Payment> updatedPayments = new ArrayList<>(payments.getValue());
        updatedPayments.remove(payment);
        payments.setValue(updatedPayments);
    }

    public void savePayments() {
        repository.savePayments(payments.getValue());
        savePaymentStatus.setValue(true);
    }
}
