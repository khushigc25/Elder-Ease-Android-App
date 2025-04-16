package com.example.elderease.medicinesreminder;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.elderease.R;

class MealTimeTextWatcher implements TextWatcher {
    private final AlertDialog dialog;
    private final EditText breakfast, lunch, dinner;

    MealTimeTextWatcher(AlertDialog dialog, EditText breakfast, EditText lunch, EditText dinner) {
        this.dialog = dialog;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean allFilled = !breakfast.getText().toString().trim().isEmpty() &&
                !lunch.getText().toString().trim().isEmpty() &&
                !dinner.getText().toString().trim().isEmpty();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(allFilled);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void afterTextChanged(Editable s) {}
}
