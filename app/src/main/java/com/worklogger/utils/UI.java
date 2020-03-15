package com.worklogger.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.internal.$Gson$Preconditions;

public class UI {

    private ProgressDialog progressDialog;
    private Context context;

    public UI(Context context){
        this.context=context;
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showSnackBar(View view, String message) {
        Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
    }

    public void showProgressDialog(){
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void hideProgressDialog(){
        if(progressDialog!=null&&progressDialog.isShowing()) progressDialog.dismiss();
    }
}
