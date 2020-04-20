package com.example.yummpies;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mCreateBtn, forgotTextLink;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    ImageButton loginButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users = db.collection("users");
    String userId;
    private  static FirebaseUser user;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    void requestLocation()
    {
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(this)
                    .setTitle("Location Services Required")
                    .setMessage("GPS not enabled")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel",null)
                    .show();
        }
    }



    private void checkPermissionLocation()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // Permission already Granted
            //Do your work here
            //Perform operations here only which requires permission
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Granted
                //Do your work here
                //Perform operations here only which requires permission
            }
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestLocation();
        checkPermissionLocation();

        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginBtn);
        mCreateBtn = findViewById(R.id.createText);
        forgotTextLink = findViewById(R.id.textView3);
        fAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.fb);


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password Must be > 6 Characters");
                    return;
                }
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DocumentReference docref = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    final boolean dboy = documentSnapshot.getBoolean("deliveryBoy");
                                    Toast.makeText(MainActivity.this, "user created", Toast.LENGTH_SHORT).show();
                                    if(dboy){
                                    user = fAuth.getCurrentUser();
                                    Intent a = new Intent(MainActivity.this,delActivity.class);
                                    a.putExtra("user", user);
                                    MainActivity.this.startActivity(a);}
                                    else {
                                        user = fAuth.getCurrentUser();
                                        Intent b = new Intent(MainActivity.this,MapsActivity.class);
                                        b.putExtra("user", user);
                                        MainActivity.this.startActivity(b);
                                    }
                                }
                            });
                        }
                        else { Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show(); }
                    }
                });
            }
        });

                mCreateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), registerActivity.class));
                    }
                });

                forgotTextLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText resetMail = new EditText(v.getContext());
                        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                        passwordResetDialog.setTitle("Reset Password");
                        passwordResetDialog.setMessage("Enter your email to receive reset link");
                        passwordResetDialog.setView(resetMail);

                        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String mail = resetMail.getText().toString();
                                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error! reset link not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        });
                        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        passwordResetDialog.create().show();
                    }
                });
            }
    }
