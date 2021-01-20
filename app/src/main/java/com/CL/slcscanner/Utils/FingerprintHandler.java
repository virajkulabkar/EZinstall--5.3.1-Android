package com.CL.slcscanner.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

/**
* Created by whit3hawks on 11/16/16.
*/
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

   public interface OnSuccessAPI {
            void onSuccessForLogin();
            void onSuccessForStatusCmd(int cmdType, Dialog dialog);
   };

   OnSuccessAPI objOnSuccessAPI;
   private Context context;
   private int cmdType;
   private Dialog dialog;

   // Constructor
   public FingerprintHandler(Context mContext, OnSuccessAPI objOnSuccessAPI) {
       context = mContext;
       this.objOnSuccessAPI=objOnSuccessAPI;
   }

    public FingerprintHandler(Context mContext, OnSuccessAPI objOnSuccessAPI, int cmdType, Dialog dialog) {
        context = mContext;
        this.objOnSuccessAPI=objOnSuccessAPI;
        this.cmdType=cmdType;
        this.dialog=dialog;
    }

   public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
       CancellationSignal cancellationSignal = new CancellationSignal();
       if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
           return;
       }
       manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
   }
 
   @Override
   public void onAuthenticationError(int errMsgId, CharSequence errString) {
       //this.update("Fingerprint Authentication error. " + errString, false);
   }

   @Override
   public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
       this.update("Fingerprint Authentication help\n" + helpString, false);
   }
 
   @Override
   public void onAuthenticationFailed() {
       this.update("Fingerprint Authentication failed.", false);
   }

   @Override
   public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
       //Toast.makeText(context,"Fingerprint Authentication succeeded.",Toast.LENGTH_SHORT).show();
       //this.update("Fingerprint Authentication succeeded.", true);
       objOnSuccessAPI.onSuccessForLogin();
       objOnSuccessAPI.onSuccessForStatusCmd(cmdType,dialog);
   }
 
   public void update(String e, Boolean success){
       Util.dialogForMessage((Activity) context,e);
       //TextView textView = (TextView) ((Activity)context).findViewById(R.id.tv_error_fingure);
       //textView.setText(e);
       //if(success){
       //    textView.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
       //}
   }
}