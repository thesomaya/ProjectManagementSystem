package com.example.myjavaapplication.activities;

import android.app.Dialog;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myjavaapplication.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private Dialog mProgressDialog;

    public void showProgressDialog(String text) {
        mProgressDialog = new Dialog(this);
        mProgressDialog.setContentView(R.layout.dialog_progress);
        ((TextView) mProgressDialog.findViewById(R.id.tv_progress_text)).setText(text);
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getCurrentUserID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    public void showErrorSnackBar(String message) {
        Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackBar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error_color));
        snackBar.show();
    }
}