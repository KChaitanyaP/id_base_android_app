package com.example.id_chat_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    EditText username, email, password;
    Button btn_register;
    FirebaseAuth auth;
    DatabaseReference reference;
    String LOGTAG = "REGISTER_ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);
        auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_register.setOnClickListener(view -> {
            String txt_username = username.getText().toString();
            String txt_email = email.getText().toString().trim();
            String txt_password = password.getText().toString();

            if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else if (txt_password.length() < 6){
                Toast.makeText(RegisterActivity.this,
                        "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else{
                String ID_username = "ID_" + txt_username;
                String ID_email = "ID_" + txt_email;
                String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
                String ID_password = RandomStringUtils.random( 15, characters );
                registerID(ID_username, ID_email, ID_password);
                Log.i(LOGTAG, "txt_username, txt_email, txt_password: " + ID_username + ID_email + ID_password);
                Log.i(LOGTAG, "Registered ID user!");

                Log.i(LOGTAG, "txt_username, txt_email, txt_password: " + txt_username + txt_email + txt_password);
                register(txt_username, txt_email, txt_password);
                Log.i(LOGTAG, "Registered actual user!");
            }
        });
    }

    private void registerID (String username, String email, String password){
        FirebaseAuth authID;
        authID = FirebaseAuth.getInstance();
        authID.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = authID.getCurrentUser();
                        assert firebaseUser != null;

                        String userid = firebaseUser.getUid();
                        Log.i(LOGTAG, "userid: " + userid);
                        reference = FirebaseDatabase.getInstance()
                                .getReference("Users").child(userid);
                        Log.i(LOGTAG, "FirebaseDatabase reference: " + reference);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", username);
                        hashMap.put("imageURL", "default");
                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                Log.i(LOGTAG, "Registered successfully with userid: " + userid);
                                finish();
                            } else{
                                String exception = Objects.requireNonNull(task1.getException()).getMessage();
                                Log.i(LOGTAG, "Hashmap setting failed with reason: " + exception);
                            }
                        });
                    } else{
                        String exception = Objects.requireNonNull(task.getException()).getMessage();
                        Log.i(LOGTAG, "Register failed with reason: " + exception);
                        Toast.makeText(RegisterActivity.this,
                                exception,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void register (String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;

                        /*
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("message");

                        myRef.setValue("Hello, World!");
                        */
                        String userid = firebaseUser.getUid();
                        Log.i(LOGTAG, "userid: " + userid);
                        reference = FirebaseDatabase.getInstance()
                                .getReference("Users").child(userid);
                        Log.i(LOGTAG, "FirebaseDatabase reference: " + reference);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", username);
                        hashMap.put("imageURL", "default");
                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                Log.i(LOGTAG, "Registered successfully with userid: " + userid);
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else{
                                String exception = Objects.requireNonNull(task1.getException()).getMessage();
                                Log.i(LOGTAG, "Hashmap setting failed with reason: " + exception);
                            }
                        });
                    } else{
                        String exception = Objects.requireNonNull(task.getException()).getMessage();
                        Log.i(LOGTAG, "Register failed with reason: " + exception);
                        Toast.makeText(RegisterActivity.this,
                                exception,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}