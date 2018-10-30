package com.example.rlarl.fingerprintex;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private static final String KEY_NAME = "_keyname";
    String res = "AndroidKeyStore";
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    private ImageView fingerImg;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 메소드 순으로 처리
        // 지문인증에 필요한 key, cipher, cryptoObject 객체를 준비
        init();
        settingCheck();
        generatorKey();
        makeKey();
        setCryptoObject();

    }

    private void init(){
        // 아래의 두개 시스템 서비스를 이용함.
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        fingerImg = findViewById(R.id.iv1);
    }

    private void settingCheck(){
        // 기기에 보안설정이 되어있는지 확인
        if (!keyguardManager.isKeyguardSecure()){
            Toast.makeText(this,"핸드폰 보안 설정을 세팅해주세요",Toast.LENGTH_SHORT).show();
            return;
        }
        // 권한설정이 되었는지 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"권한이 없습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        // 핸드폰에 지문이 등록되어있는지 확인
        if (!fingerprintManager.hasEnrolledFingerprints()){
            Toast.makeText(this,"핸드폰에 지문이 등록되어 있지않습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void generatorKey(){
        try {
            // Keystore 컨테이너의 식별자를 매개값으로 전달
            keyStore = KeyStore.getInstance(res);
        }catch (Exception e){
            Log.d(TAG,"KeyStore 인스턴스 못함");
        }

        try {
            // 생성될 key의 타입과 key가 저장될 keystore 컨테이너의 이름을 인자로 전달
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, res);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d(TAG,"keyGenerator 전달못받음");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            Log.d(TAG,"keyGenerator 전달못받음");
        }
    }

    private void makeKey(){
        try {
            // KeyStore 컨테이너가 로드되고 Keygenerator가 초기화됨
            keyStore.load(null);
            // Key의 타입을 지정함 -> 인스턴스 생성하면서 key이름참조, 암호화 및 해독에 사용되는 key 구성정보를 인자로 전달
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // setuserAuthenticationrequired -> true 경우 지문인증으로 키를 사용할 때 마다 사용자의 인증이 필요함
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            // Key 생성
            keyGenerator.generateKey();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    private boolean chiperInit(){
        // FingerprintManager.CryptoObject 인스턴스를 생성하는데 사용할 Cipher 값을 초기화함.
        try {
            // cipher에 지문인증에 필요한 속성들을 매개값으로 보내고 인스턴스를 얻음
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            // 저장된 key를 사용하여 cipher를 초기화 시켜줌
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            // keystore 에러시 false를 리턴
            Log.d(TAG,"Cipher초기화 과정에서 Keystore 에러가 났음");
            return false;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setCryptoObject(){
        // 키를 생성하고 cryptoObject 인스턴스화
        generatorKey();

        if (chiperInit()){
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            // 지문인증 실행
            FingerprintHandler handler = new FingerprintHandler(this);
            handler.startAuth(fingerprintManager, cryptoObject);
        }
    }
}
