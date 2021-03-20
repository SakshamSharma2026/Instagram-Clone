package com.codewithshadow.instagram.CreateAccount;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codewithshadow.instagram.R;

public class ConfirmationCodeActivity extends AppCompatActivity {
    TextView codetext;
    String email,uniquekey;
    EditText item_input_code;
    FrameLayout btnNext;
    Runnable mrunnable;
    Handler mhandler;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_code);
        codetext = findViewById(R.id.confirmationText);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        uniquekey = intent.getStringExtra("uniquekey");
        codetext.setText("Enter the confirmation code we sent to " + email +" .");
        item_input_code = findViewById(R.id.item_input_code);
        progressBar = findViewById(R.id.button_progress);

        btnNext = findViewById(R.id.btn_next);
        btnNext.setEnabled(false);
        btnNext.setBackgroundColor(getResources().getColor(R.color.blue2));

        item_input_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().equals("")) {
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundColor(getResources().getColor(R.color.blue2));

                } else if (!s.toString().isEmpty() && s.toString().length()==5){
                    btnNext.setBackgroundColor(getResources().getColor(R.color.blue));
                    btnNext.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String codestring = item_input_code.getText().toString().trim();

                if (codestring.equals(uniquekey))
                {
                    mrunnable = new Runnable() {
                        @Override
                        public void run() {
                            btnNext.setEnabled(false);
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(ConfirmationCodeActivity.this, Name_UsernameActivity.class);
                            intent.putExtra("email",email);
                            startActivity(intent);
                            finish();
                        }
                    };
                    mhandler=new Handler();
                    mhandler.postDelayed(mrunnable,2000);

                }
                else
                {
                    Toast.makeText(ConfirmationCodeActivity.this,"Invalid Confirmation Code",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}