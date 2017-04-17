package com.udacity.stockhawk.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.util.NetworkUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class AddStockDialog extends DialogFragment {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.dialog_stock)
    EditText stock;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View custom = inflater.inflate(R.layout.add_stock_dialog, null);

        ButterKnife.bind(this, custom);

        stock.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addStock();
                return true;
            }
        });
        builder.setView(custom);

        builder.setMessage(getString(R.string.dialog_title));
        builder.setPositiveButton(getString(R.string.dialog_add), null);
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);

        Dialog dialog = builder.create();
        final Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setContentDescription(getString(R.string.description_negative_button)); //FIXME: java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.Button.setContentDescription(java.lang.CharSequence)' on a null object reference
        final Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setContentDescription(getString(R.string.description_positive_button));
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean stockAdded = addStock();
                        if (stockAdded) {
                            dismiss();
                        }
                    }
                });
            }
        });
        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }


    private boolean addStock() {
        boolean isStockAvailable = false;
        Stock newStock = null;
        Activity parent = getActivity();
        if (NetworkUtils.networkUp((ConnectivityManager) parent.getSystemService(Context.CONNECTIVITY_SERVICE))) {
            try {
                newStock = new GetStockTask().execute(stock.getText().toString()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            isStockAvailable = newStock != null && newStock.getName() != null;
            if (isStockAvailable) {
                if (parent instanceof MainActivity) {
                    ((MainActivity) parent).addStock(stock.getText().toString());
                }
            } else {
                stock.setError(getString(R.string.error_stock_not_found));
            }
        }
        return isStockAvailable;
    }

    private class GetStockTask extends AsyncTask<String, Void, Stock> {
        @Override
        protected Stock doInBackground(String... params) {
            Stock stock = null;
            String symbol = params[0];
            try {
                stock = YahooFinance.get(symbol);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stock;
        }
    }



}
