package com.app.merabillstask.data;

import com.app.merabillstask.model.Payment;

import java.util.List;

public class PaymentRepository {
    private FileDataSource fileDataSource;

    public PaymentRepository(FileDataSource fileDataSource) {
        this.fileDataSource = fileDataSource;
    }

    public void savePayments(List<Payment> payments) {
        fileDataSource.savePayments(payments);
    }

    public List<Payment> loadPayments() {
        return fileDataSource.loadPayments();
    }
}
