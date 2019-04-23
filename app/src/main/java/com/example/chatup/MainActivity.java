package com.example.chatup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatup.model.Messaggio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    //UI
    private EditText mInputText;
    private Button mBtnSend;

    @Override
    protected void onStart() {
        super.onStart();

        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Collego l'app a Firebas per il login
        mAuth = FirebaseAuth.getInstance();

        //Imposto il titolo
        setTitle(mAuth.getCurrentUser().getDisplayName());

        //Collego l'app al DB Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Inizializzo l'UI
        initUI();

        //Estraggo gli extras
        Bundle intent_login = getIntent().getExtras();
        String email = intent_login.getString("login_data");

        //Mostro i dati
        Toast.makeText(this, "Accesso effettuato come:\n" + email, Toast.LENGTH_SHORT).show();
    }

    private void initUI() {
        mInputText = findViewById(R.id.et_msg);
        mBtnSend = findViewById(R.id.btn_send);

        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                mInputText.setText("");
                return true;
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                mInputText.setText("");
            }
        });
    }

    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            Intent main_to_log = new Intent(this, LoginActivity.class);
            startActivity(main_to_log);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.layout_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.logoutItem) {
            Log.i("logout", "Logout selezionato");
            mAuth.signOut();
            updateUI();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMessage() {
        Log.i("chatup","Invio messaggio");
        String inputMsg = mInputText.getText().toString();

        if(!inputMsg.equals("")) {

            Messaggio chat = new Messaggio(inputMsg, mAuth.getCurrentUser().getDisplayName());

            //Salvo il messaggio nel DB
            mDatabaseReference.child("messaggi").push().setValue(chat);
        }

    }
}
