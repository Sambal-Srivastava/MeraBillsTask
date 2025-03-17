package com.app.merabillstask.ui.view;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.app.merabillstask.R;
import com.app.merabillstask.data.FileDataSource;
import com.app.merabillstask.data.PaymentRepository;
import com.app.merabillstask.databinding.ActivityMainBinding;
import com.app.merabillstask.databinding.LayoutDialogAddPaymentBinding;
import com.app.merabillstask.model.Payment;
import com.app.merabillstask.viewmodel.PaymentViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PaymentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new PaymentViewModel(new PaymentRepository(new FileDataSource(getApplicationContext())));
            }
        }).get(PaymentViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.getPayments().observe(this, payments -> {
            binding.chipGroup.removeAllViews();
            double totalAmount = 0.0;
            for (Payment payment : payments) {
                totalAmount += payment.getAmount();
                Chip chip = new Chip(this);
                chip.setText(payment.getType() + " = ₹ " + payment.getAmount());
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(v -> viewModel.removePayment(payment));
                binding.chipGroup.addView(chip);
            }
            binding.tvTotalAmount.setText(getString(R.string.total_amount_text) + totalAmount);
        });

        viewModel.getSavePaymentStatus().observe(this, status -> {
            if (status) {
                Toast.makeText(this, getString(R.string.save_payment_success_msg), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.save_payment_failure_msg), Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvAddPayment.setOnClickListener(v -> showAddPaymentDialog());
        binding.btnSave.setOnClickListener(v -> viewModel.savePayments());
    }

    private void showAddPaymentDialog() {
        Dialog dialog = new Dialog(this);
        LayoutDialogAddPaymentBinding binding = LayoutDialogAddPaymentBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        binding.btnAddPayment.setEnabled(false);
        viewModel.getErrorMessage().observe(this, error -> {
            binding.tvError.setVisibility(error != 0 ? View.VISIBLE : View.GONE);
            if (error == 1) {
                binding.tvError.setText(getText(R.string.error_message_empty_amount));
            } else if (error == 2) {
                binding.tvError.setText(getText(R.string.error_message_invalid_amount));
            } else {
                binding.tvError.setText(getText(R.string.error_message_provider_transref_required));
            }
        });

        viewModel.getAddPaymentStatus().observe(this, status -> {
            if (status) {
                dialog.dismiss();
            }
        });

        List<String> addedTypes = new ArrayList<>();
        for (Payment payment : Objects.requireNonNull(viewModel.getPayments().getValue())) {
            addedTypes.add(payment.getType());
        }

        List<String> availableTypes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.available_payment_types)));
        availableTypes.removeAll(addedTypes);

        if (availableTypes.isEmpty()) {
            binding.btnAddPayment.setEnabled(false);
            Toast.makeText(this, "no more payment type can be added.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().trim().isEmpty()) {
                    binding.tvError.setText(getString(R.string.error_message_empty_amount));
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.btnAddPayment.setEnabled(false);
                } else {
                    binding.tvError.setVisibility(View.GONE);
                    binding.btnAddPayment.setEnabled(true);
                }
            }
        });

        binding.etProvider.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.etProvider.getVisibility() == View.VISIBLE && s.toString().trim().isEmpty()) {
                    binding.tvError.setText(getString(R.string.error_message_provider_transref_required));
                    binding.tvError.setVisibility(View.VISIBLE);
                } else {
                    binding.tvError.setVisibility(View.GONE);
                }
            }
        });

        binding.etTransactionRef.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.etTransactionRef.getVisibility() == View.VISIBLE && s.toString().trim().isEmpty()) {
                    binding.tvError.setText(getString(R.string.error_message_provider_transref_required));
                    binding.tvError.setVisibility(View.VISIBLE);
                } else {
                    binding.tvError.setVisibility(View.GONE);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, availableTypes);
        binding.spPaymentType.setAdapter(adapter);

        binding.spPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = availableTypes.get(position);
                boolean needsDetails = selectedType.equals("Bank Transfer") || selectedType.equals("Credit Card");

                binding.etProvider.setVisibility(needsDetails ? View.VISIBLE : View.GONE);
                binding.etTransactionRef.setVisibility(needsDetails ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.btnAddPayment.setOnClickListener(v -> {
            String type = binding.spPaymentType.getSelectedItem().toString();
            double amount = Double.parseDouble(binding.etAmount.getText().toString());
            String provider = binding.etProvider.getVisibility() == View.VISIBLE ? binding.etProvider.getText().toString() : "";
            String transactionRef = binding.etTransactionRef.getVisibility() == View.VISIBLE ? binding.etTransactionRef.getText().toString() : "";
            viewModel.addPayment(type, String.valueOf(amount), provider, transactionRef);
        });
        dialog.show();

        binding.tvCancel.setOnClickListener(v -> dialog.dismiss());
    }
}