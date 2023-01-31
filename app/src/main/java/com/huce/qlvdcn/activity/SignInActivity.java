package com.huce.qlvdcn.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.huce.qlvdcn.R;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private EditText edEmail, edPass;
    private TextView tvForgotPass, tvSignup;
    private ImageView imgEye;
    private AppCompatButton btnSignin;
    public boolean isPasswordHidden = true;
    private boolean checkEmail = false;
    private boolean checkPass = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
        processEvents();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setMessage("Processing...");
        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvSignup = findViewById(R.id.tvSignup);
        btnSignin = findViewById(R.id.btnSignin);
        imgEye = findViewById(R.id.imgEye);

        if (firebaseUser != null) {
            finish();
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
        }
    }

    private void processEvents() {
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, ForgotPass.class));
                finish();
            }
        });

        imgEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrHidePassword();
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
                }
            }
        });
        edPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pass = edPass.getText().toString().trim();
                if (pass.length() < 6) {
                    edPass.setError("Độ dài ít nhất 6 kí tự!");
                    edPass.requestFocus();
                }
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString().trim();
                String pass = edPass.getText().toString().trim();
                progressDialog.show();
                if (email.isEmpty()) {
                    progressDialog.dismiss();
                    edEmail.setError("Email is required!");
                    //Toast.makeText(SignInActivity.this, "Email không được để trống!", Toast.LENGTH_SHORT).show();
                    edEmail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    progressDialog.dismiss();
                    edEmail.setError("Email không hợp lệ!");
                    edEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    progressDialog.dismiss();
                    edPass.setError("Password is required!");
                    //Toast.makeText(SignInActivity.this, "Password không được để trống!", Toast.LENGTH_SHORT).show();
                    edPass.requestFocus();
                }else if (pass.length() < 6) {
                    progressDialog.dismiss();
                    edPass.setError("Độ dài ít nhất 6 kí tự!");
                    edPass.requestFocus();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            checkEmailVerification();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            if (firebaseUser.isEmailVerified()) {
                Toast.makeText(getApplicationContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), "Xác thực Email của bạn trước!", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }
        }
    }

    private void showOrHidePassword() {
        isPasswordHidden = !isPasswordHidden;
        if (!isPasswordHidden) {
            imgEye.setImageDrawable(getDrawable(R.drawable.ic_show_password));
            edPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            imgEye.setImageDrawable(getDrawable(R.drawable.ic_hide_password));
            edPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        String pass = edPass.getText().toString();
        edPass.setText("");
        edPass.append(pass);
    }
}