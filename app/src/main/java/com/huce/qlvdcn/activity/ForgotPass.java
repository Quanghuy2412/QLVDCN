package com.huce.qlvdcn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.huce.qlvdcn.R;

public class ForgotPass extends AppCompatActivity {

    private EditText edEmail;
    private AppCompatButton btnRecoverPass;
    private TextView tvSignin;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");

        edEmail = findViewById(R.id.edEmail);
        btnRecoverPass = findViewById(R.id.btnRecoverPass);
        tvSignin = findViewById(R.id.tvSignin);

        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPass.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        edEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = edEmail.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edEmail.setError("Email không hợp lệ!");
                    edEmail.requestFocus();
                    btnRecoverPass.setEnabled(false);
                }else {
                    btnRecoverPass.setEnabled(true);
                }
            }
        });


        btnRecoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString().trim();
                progressDialog.show();
                if (email.isEmpty()) {
                    progressDialog.dismiss();
                    edEmail.setError("Email is required!");
                    //Toast.makeText(getApplicationContext(), "Email không được để trống!", Toast.LENGTH_SHORT).show();
                    edEmail.requestFocus();
                } else {
                    //we have to send password recover email
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Truy cập Email để tiến hành đặt lại Password!", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgotPass.this, SignInActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Email sai hoặc tài khoản không tồn tại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}