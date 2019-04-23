package com.example.chatup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    //costanti
    static final String CHAT_PREFS = "ChatPrefs";
    static  final String NOME_KEY = "Username";

    EditText mConfermaPassword, mEmail, mPassword, mNome;
    private FirebaseAuth mAuth;
    private String username;

    private void updateUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            Intent reg_to_main = new Intent(RegisterActivity.this, MainActivity.class);
            reg_to_main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            reg_to_main.putExtra("login_data", mEmail.getText().toString());
            finish();
            startActivity(reg_to_main);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Inizializzo gli edit text
        initUI();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void initUI() {
        mConfermaPassword = findViewById(R.id.psw_id2);
        mEmail = findViewById(R.id.email_id);
        mPassword = findViewById(R.id.psw_id);
        mNome = findViewById(R.id.user_id);
    }

    private void createFirebaseUser(String email, String password, final String nome) {

        Log.d("Dati", email + password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.i("ChatUP Registration", "createUserWithEmail:success");
                            saveName();
                            setNome(nome);
                            showDialog("Registrazione effettuata con successo!", "successo", android.R.drawable.ic_dialog_info);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("ChatUP Registration", "createUserWithEmail:failure", task.getException());
                            showDialog("Errore nella registrazione!", "errore", android.R.drawable.ic_dialog_alert);
                        }

                        //Altre robe
                    }
                });
    }

    private void saveName() {

        //Inizializzo le shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(CHAT_PREFS, 0);

        sharedPreferences.edit().putString(NOME_KEY, mNome.getText().toString()).apply();
        username = sharedPreferences.getString("username", "");

    }

    private void showDialog(String message, String title, int icon) {

         new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateUI();
                    }
                })
                .setIcon(icon)
                .show();
    }

    public void btnRegister(View view) {
        Log.d("RegisterActivity", "Register button click");

        //validazione dati
        if(!validName(mNome.getText().toString()))
            Toast.makeText(getApplicationContext(), "Nome non valido", Toast.LENGTH_SHORT).show();
        else
            if(!validEmail(mEmail.getText().toString()))
                Toast.makeText(getApplicationContext(), "Email non valida",
                        Toast.LENGTH_SHORT).show();
            else
                if(!validPsw(mPassword.getText().toString()))
                    Toast.makeText(getApplicationContext(), "Password non valida",
                            Toast.LENGTH_SHORT).show();
                else
                    createFirebaseUser(mEmail.getText().toString().trim(), mPassword.getText().toString(), mNome.getText().toString());
    }

    public void TextClickLogin(View view) {
        Log.d("RegisterActivity", "Login button click");
        Intent reg_to_log = new Intent(this, LoginActivity.class);
        reg_to_log.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(reg_to_log);
    }

    private boolean validName(String input) {
        if(input.length() > 3)
            return true;
        else
            return false;
    }

    private boolean validEmail(String input) {
        return input.contains("@");
    }

    private boolean validPsw(String input) {
        return mConfermaPassword.getText().toString().equals(input) && input.length() > 7;
    }

    private void setNome(String nome) {
        FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(nome)
                .build();

        user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Log.i("setnome", "nome caricato con successo");
                else
                    Log.i("setnome", "errore nel caricamento del nome");
            }
        });
    }
}
