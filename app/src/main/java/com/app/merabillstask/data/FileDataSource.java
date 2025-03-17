package com.app.merabillstask.data;

import android.content.Context;

import com.app.merabillstask.model.Payment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FileDataSource {
    private Context context;
    private final String fileName = "LastPayment.txt";
    private Gson gson = new Gson();

    public FileDataSource(Context context) {
        this.context = context;
    }

    public void savePayments(List<Payment> payments) {
        File file = new File(context.getFilesDir(), fileName);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(payments, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Payment> loadPayments() {
        File file = new File(context.getFilesDir(), fileName);
        if (!file.exists()) return new ArrayList<>();
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Payment>>() {
            }.getType();
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
