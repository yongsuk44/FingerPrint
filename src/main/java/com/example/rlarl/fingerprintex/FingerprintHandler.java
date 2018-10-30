package com.example.rlarl.fingerprintex;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private CancellationSignal cancellationSignal;
    private Context context;

    public FingerprintHandler(Context context){
        // 인증 상태를 user에게 callback 시켜주기 위한 method
        this.context = context;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject){
        // 지문인증을 시작하기 위한 method
        cancellationSignal = new CancellationSignal();

        // 지문 인증 퍼미션이 승인되었는지 재확인
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            Log.d("handler","지문인증 권한이 없음");
            return;
        }

        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        Toast.makeText(context,"지문 인증 에러",Toast.LENGTH_SHORT).show();
        Log.d("authenticationError","error 문제");
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
        Toast.makeText(context,"인증 센서에 지문을 제대로 대주세요",Toast.LENGTH_SHORT).show();
        Log.d("authenticationHelp","일치 하지않음");
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        Toast.makeText(context,"지문 인증 성공",Toast.LENGTH_SHORT).show();
        Log.d("authenticationSucceeded","Succeeded");
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        Toast.makeText(context,"지문이 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
        Log.d("authenticationFailed","Failed 문제");
    }
}
