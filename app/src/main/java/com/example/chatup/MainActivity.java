package com.example.chatup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatup.adapter.ChatListAdapter;
import com.example.chatup.model.Messaggio;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main_activity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    //UI
    private EditText mInputText;
    private Button mBtnSend;

    private ChatListAdapter chatListAdapter;
    private RecyclerView recyclerView;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Collego l'app a Firebas per il login
        mAuth = FirebaseAuth.getInstance();

        //Imposto il titolo
        if(mAuth.getCurrentUser() != null)
            setTitle(mAuth.getCurrentUser().getDisplayName());
        else
            setTitle("unknown");

        //Collego l'app al DB Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Inizializzo l'UI
        initUI();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        chatListAdapter = new ChatListAdapter(this, mDatabaseReference, mAuth.getCurrentUser().getDisplayName());
        recyclerView.setAdapter(chatListAdapter);

        //Estraggo gli extras
        Bundle intent_login = getIntent().getExtras();
        String email = intent_login.getString("login_data");

        //Carico le pubblicità
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Mostro i dati
        Toast.makeText(this, "Accesso effettuato come:\n" + email, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        updateUI();
    }

    private void initUI() {
        mInputText = findViewById(R.id.et_msg);
        mBtnSend = findViewById(R.id.btn_send);
        mAdView = findViewById(R.id.adView);
        recyclerView = findViewById(R.id.list_chat);

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

    //Impostazioni menù
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
        Log.i("chatup", "Invio messaggio");
        String inputMsg = mInputText.getText().toString();

        if (inputMsg.equals("")) {

            Toast.makeText(this, "Inserisci prima del testo", Toast.LENGTH_SHORT).show();
        }else if(!chekInputMsg(inputMsg)) {

            mInputText.setText("");
        } else {
            Messaggio chat = new Messaggio(inputMsg, mAuth.getCurrentUser().getDisplayName());

            //Scroll down della chat quando invio un messaggio
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(chatListAdapter.getItemCount());
                }
            });
            //Salvo il messaggio nel DB
            mDatabaseReference.child("messaggi").push().setValue(chat);
        }
    }

    private boolean chekInputMsg(String input) {

        for(int i = 0; i < input.length(); ++i) {
            if(input.trim().length() > 0)
                return true;
            else
                return false;
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        chatListAdapter.clean();
    }
}
