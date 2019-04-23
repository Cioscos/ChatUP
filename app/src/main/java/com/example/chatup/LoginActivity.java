package com.example.chatup;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText mail, psw;
    private FirebaseAuth mAuth;

    private void updateUI() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Name, email address, and profile photo Url
            String email = user.getEmail();

            Intent log_to_main = new Intent(this, MainActivity.class);
            log_to_main.putExtra("login_data", email);
            startActivity(log_to_main);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

    }

    public void btnLogin(View view) {

        mail = findViewById(R.id.user_id);
        psw = findViewById(R.id.psw_id);

        if(!validEmail(mail.getText().toString()))
            Toast.makeText(getApplicationContext(), "Email non valida",
                    Toast.LENGTH_SHORT).show();
        else
            if(!validPsw(psw.getText().toString()))
                Toast.makeText(getApplicationContext(), "Password non valida",
                        Toast.LENGTH_SHORT).show();
            else
                loginUser(mail.getText().toString().trim(), psw.getText().toString());
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("chatup login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("chatup login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.login_error,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void TextClickRegister(View view) {
        Log.d("LoginActivity", "Login text click");
        Intent log_to_reg = new Intent(this, RegisterActivity.class);
        startActivity(log_to_reg);
    }

    private boolean validEmail(String input) {
        return input.contains("@");
    }

    private boolean validPsw(String input) {
        return input.length() > 7;
    }
}
