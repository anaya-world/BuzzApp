package com.example.myapplication.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.myapplication.FingerPrintHandler.FingerprintHandler;
import com.example.myapplication.R;
import com.example.myapplication.Utils.SharedPrefManager;

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

@RequiresApi(api = Build.VERSION_CODES.M)
public class SignInWithFingerPrint extends AppCompatActivity {
    private static final String KEY_NAME = "buzz_key";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    AlertDialog alertDialog;
    private Button button_SignUp;
    private Cipher cipher;
    String deviceID;
    private KeyStore keyStore;
    ProgressDialog progressDialog;
    /* access modifiers changed from: private */
    public TextView textView;
    private Cipher cipherNotInvalidated;
    private KeyGenerator keyGenerator;
    private Cipher defaultCipher;
    String myuserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_with_finger_print);
        this.deviceID = getIntent().getStringExtra("DEVICE_ID");
      //  this.myuserid=getIntent().getStringExtra("USER_ID");
        Log.d("figerprint","value"+"-"+ SharedPrefManager.getInstance(this).isFingerPrintOn());
        if (Build.VERSION.SDK_INT >= 23) {
            final FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager==null) {
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setMessage((CharSequence) "Your device does not have a fingerprint sensor");
                ab.setTitle((CharSequence) "Error");
                ab.setCancelable(false);
                ab.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        SignInWithFingerPrint.this.finish();
                    }
                });
                ab.create();
                ab.show();
            } else if (!fingerprintManager.isHardwareDetected()) {
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setMessage((CharSequence) "Your device does not have a fingerprint sensor");
                ab.setTitle((CharSequence) "Error");
                ab.setCancelable(false);
                ab.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        SignInWithFingerPrint.this.finish();
                    }
                });
                ab.create();
                ab.show();
            } else if (ActivityCompat.checkSelfPermission(this, "android.permission.USE_FINGERPRINT") != 0) {
                // this.textView.setText("Fingerprint authentication permission not enabled");
                AlertDialog.Builder ab2 = new AlertDialog.Builder(this);
                ab2.setMessage((CharSequence) "Fingerprint authentication permission not enabled");
                ab2.setTitle((CharSequence) "Error");
                ab2.setCancelable(false);
                ab2.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        SignInWithFingerPrint.this.finish();
                    }
                });
                ab2.create();
                ab2.show();
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                // this.textView.setText("Register at least one fingerprint in Settings");
                AlertDialog.Builder ab3 = new AlertDialog.Builder(this);
                ab3.setMessage((CharSequence) "Register at least one fingerprint in Settings");
                ab3.setTitle((CharSequence) "Error");
                ab3.setCancelable(false);
                ab3.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        SignInWithFingerPrint.this.startActivityForResult(new Intent("android.settings.SETTINGS"), 0);
                        SignInWithFingerPrint.this.finish();
                    }
                });
                ab3.create();
                ab3.show();
            }
            else {
                this.progressDialog = new ProgressDialog(this);
                this.progressDialog.setMessage("Loading...");
                this.progressDialog.setCancelable(false);
                this.progressDialog.setIndeterminate(true);
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                this.textView = (TextView) findViewById(R.id.errorText );
                this.button_SignUp = (Button) findViewById(R.id.button_SignUp);
                this.button_SignUp.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (!fingerprintManager.isHardwareDetected()) {
                            SignInWithFingerPrint.this.textView.setText("Your device does not have a fingerprint sensor");
                        } else {
                            SignInWithFingerPrint.this.startActivityForResult(new Intent("android.settings.SETTINGS"), 0);
                        }
                    }
                });

                if (fingerprintManager==null) {
                    this.textView.setText("Your device does not have a fingerprint sensor");
                } else if (!fingerprintManager.isHardwareDetected()) {
                    this.textView.setText("Your device does not have a fingerprint sensor");
                } else if (ActivityCompat.checkSelfPermission(this, "android.permission.USE_FINGERPRINT") != 0) {
                    this.textView.setText("Fingerprint authentication permission not enabled");
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    this.textView.setText("Register at least one fingerprint in Settings");
                } else if (!keyguardManager.isKeyguardSecure()) {
                    this.textView.setText("Lock screen security not enabled in Settings");
                }
                else {
                    generateKey();
                    if (initCipher(defaultCipher, KEY_NAME)) {
                        new FingerprintHandler(this, this.deviceID).startAuth(fingerprintManager, new FingerprintManager.CryptoObject(this.cipher));

                    }
                }
            }
        } else {
            AlertDialog.Builder ab4 = new AlertDialog.Builder(this);
            ab4.setMessage((CharSequence) "Your device does not have a fingerprint sensor");
            ab4.setTitle((CharSequence) "Error");
            ab4.setCancelable(false);
            ab4.setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    SignInWithFingerPrint.this.finish();
                }
            });
            ab4.create();
            ab4.show();
        }
    }

    /* access modifiers changed from: protected */
    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
    /* access modifiers changed from: protected */
    @TargetApi(23)
    public void generateKey() {
        try {
            this.keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            try {
                this.keyStore.load(null);
                keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_VERIFY).setBlockModes(new String[]{"CBC"}).setUserAuthenticationRequired(true).setEncryptionPaddings(new String[]{"PKCS7Padding"}).build());
                keyGenerator.generateKey();
            } catch (IOException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | CertificateException e2) {
                throw new RuntimeException(e2);
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException e3) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e3);
        }
        // defaultCipher;
        //  cipherNotInvalidated;
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipherNotInvalidated = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
        FingerprintManager fingerprintManager = getSystemService(FingerprintManager.class);
        KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);
        createKey(KEY_NAME, true);
        createKey(KEY_NAME_NOT_INVALIDATED, false);
    }
    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            keyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    @TargetApi(23)
    public boolean cipherInit() {
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            try {
                this.keyStore.load(null);
                this.cipher.init(1, (SecretKey) this.keyStore.getKey(KEY_NAME, null));
                return true;
            } catch (KeyPermanentlyInvalidatedException e) {
                return false;
            } catch (IOException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException e2) {
                throw new RuntimeException("Failed to init Cipher", e2);
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e3) {
            throw new RuntimeException("Failed to get Cipher", e3);
        }


        }
        public String getMyuserid(){
        return myuserid;
        }
    }
