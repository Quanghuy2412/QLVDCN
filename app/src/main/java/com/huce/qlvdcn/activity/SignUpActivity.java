package com.huce.qlvdcn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.huce.qlvdcn.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText edEmail, edPass, edConfirmPass;
    private AppCompatButton btnSignup;
    private TextView tvSignIn;
    private ImageView imgEyePass, imgEyeCfPass;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private boolean isPasswordHidden = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");

        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        edConfirmPass = findViewById(R.id.edConfirmPass);
        btnSignup = findViewById(R.id.btnSignup);
        tvSignIn = findViewById(R.id.tvSignIn);
        imgEyePass = findViewById(R.id.imgEyePass);
        imgEyeCfPass = findViewById(R.id.imgEyeCfPass);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        imgEyePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrHidePassword();
            }
        });

        imgEyeCfPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrHideConfirmPassword();
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

        edConfirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pass = edPass.getText().toString().trim();
                String confirmPass = edConfirmPass.getText().toString().trim();
                if (!confirmPass.equals(pass)) {
                    edConfirmPass.setError("Mật khẩu không khớp!");
                    edConfirmPass.requestFocus();
                }
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getText().toString().trim();
                String pass = edPass.getText().toString().trim();
                String confirmPass = edConfirmPass.getText().toString().trim();
                progressDialog.show();

                if (email.isEmpty()) {
                    progressDialog.dismiss();
                    edEmail.setError("Email is required!");
                    //Toast.makeText(getApplicationContext(), "Email không được để trống! ", Toast.LENGTH_SHORT).show();
                    edEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edEmail.setError("Email không hợp lệ!");
                    edEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    progressDialog.dismiss();
                    edPass.setError("Password is required!");
                    //Toast.makeText(getApplicationContext(), "Password không được để trống! ", Toast.LENGTH_SHORT).show();
                    edPass.requestFocus();
                } else if (confirmPass.isEmpty()) {
                    progressDialog.dismiss();
                    edPass.setError("Confirm Password is required!");
                    //Toast.makeText(getApplicationContext(), "Confirm Password không được để trống! ", Toast.LENGTH_SHORT).show();
                    edConfirmPass.requestFocus();
                } else if (pass.length() < 6) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Độ dài mật khẩu ít nhất 6 kí tự!", Toast.LENGTH_SHORT).show();
                    edPass.requestFocus();
                } else if (!pass.equals(confirmPass)) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                    edConfirmPass.requestFocus();
                } else {
                    //register user to firebase
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            } else {
                                Toast.makeText(getApplicationContext(), "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    //send email verification
    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Email xác thực đã được gửi. Vui lòng tiến hành xác thực và đăng nhập lại!", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Gửi email xác thực thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showOrHidePassword() {
        isPasswordHidden = !isPasswordHidden;
        if (!isPasswordHidden) {
            imgEyePass.setImageDrawable(getDrawable(R.drawable.ic_show_password));
            edPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            imgEyePass.setImageDrawable(getDrawable(R.drawable.ic_hide_password));
            edPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        String pass = edPass.getText().toString();
        edPass.setText("");
        edPass.append(pass);
    }

    private void showOrHideConfirmPassword() {
        isPasswordHidden = !isPasswordHidden;
        if (!isPasswordHidden) {
            imgEyeCfPass.setImageDrawable(getDrawable(R.drawable.ic_show_password));
            edConfirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            imgEyeCfPass.setImageDrawable(getDrawable(R.drawable.ic_hide_password));
            edConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        String cfpass = edConfirmPass.getText().toString();
        edConfirmPass.setText("");
        edConfirmPass.append(cfpass);
    }
}