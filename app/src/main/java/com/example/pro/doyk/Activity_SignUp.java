package com.example.pro.doyk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pro.doyk.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class Activity_SignUp extends AppCompatActivity {
    EditText username;
    EditText uemail;
    EditText upassword;
    EditText urepassword;
    Button btn_Sign_Up;
    Button btn_Already_Member;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean isTaken=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.ab_align);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Реєстрація");

        username = (EditText) findViewById(R.id.edusername);
        uemail = (EditText) findViewById(R.id.edemail);
        upassword = (EditText) findViewById(R.id.edpass);
        urepassword = (EditText) findViewById(R.id.edrepass);
        btn_Sign_Up = (Button) findViewById(R.id.btnSignUp);
        btn_Already_Member = (Button) findViewById(R.id.btn_Already_Member);
        setAuthInstance();
        setDatabaseInstance();
        btn_Already_Member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Activity_Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        btn_Sign_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterUser();
            }
        });
    }

    private void setAuthInstance() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void setDatabaseInstance() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public boolean validate() {
        boolean valid = true;

        String name = username.getText().toString();
        String email = uemail.getText().toString();
        String password = upassword.getText().toString();
        String reEnterPassword = urepassword.getText().toString();
        if (name.isEmpty()) {
            username.setError("не введено нікнейм");
            valid = false;
        } else {
            username.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uemail.setError("некоректна електронна адреса");
            valid = false;
        } else {
            uemail.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 15) {
            upassword.setError("пароль повинен містити не менше 8 символів");
            valid = false;
        } else {
            upassword.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 8 || reEnterPassword.length() > 15 || !(reEnterPassword.equals(password))) {
            urepassword.setError("паролі не співпадають");
            valid = false;
        } else {
            urepassword.setError(null);
        }
        return valid;
    }

    private void onRegisterUser() {
        String name = username.getText().toString();
        Log.d("Name",""+name);
        boolean exist = retreiveUserNames(name);
        if (!validate()) {
        }
        else if (exist) {
            username.setError("цей нікнейм вже зайнято");
        }
        else {
            signUp(getUserEmail(), getUserPassword());
        }
    }

    public boolean retreiveUserNames(final String sUserName) {
        mDatabase = FirebaseDatabase.getInstance().getReference("userNames");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String existingUsername = userSnapshot.getKey();
                    Log.d("Ex",""+sUserName);
                    Log.d("Shot ", "" + existingUsername);
                    if(sUserName.equals(existingUsername)) {
                        isTaken = true;
                        Log.d("BooleanShot ", "" + isTaken);
                        break;
                    }
                    else if(!(sUserName.equals(existingUsername))) {
                        isTaken = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Відсутнє підключення до інтернету.", Toast.LENGTH_SHORT).show();
            }
        });
        return isTaken;
    }


    private String getUserDisplayName() {
        return username.getText().toString().trim();
    }

    private String getUserEmail() {
        return uemail.getText().toString().trim();
    }

    private String getUserPassword() {
        return upassword.getText().toString().trim();
    }

    private int getCatPhisMarks(){return 0;}
    private int getCatGeoMarks(){return 0;}
    private int getCatRelMarks(){return 0;}
    private int getCatHisMarks(){return 0;}
    private int getCatBioMarks(){return 0;}
    private int getCatCinMarks(){return 0;}
    private int getCatArtMarks(){return 0;}
    private int getCatLinMarks(){return 0;}
    private int getCatSpoMarks(){return 0;}
    private int getCatTecMarks(){return 0;}
    private int getCatLitMarks(){return 0;}
    private int getCatAllMarks(){return 0;}

    private int getTotalScoreMarks(){
        return 0;
    }


    private void signUp(String email, String password) {
        final ProgressDialog progressDialog = new ProgressDialog(Activity_SignUp.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if(task.isSuccessful()) {
                    onAuthSuccess(task.getResult().getUser());
                }
                else {
                    Toast.makeText(getApplicationContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void onAuthSuccess(FirebaseUser user) {
        createNewUser(user.getUid());
        createUserNames();
        goToMainActivity();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(Activity_SignUp.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void createNewUser(String userId){
        UserModel user = buildNewUser();
        FirebaseDatabase.getInstance().getReference("users").child(userId).setValue(user);
    }

    public void createUserNames() {
        //mDatabase.child("userNames").child(getUserDisplayName()).setValue(true);
        FirebaseDatabase.getInstance().getReference("userNames").child(getUserDisplayName()).setValue(true);
    }
    private UserModel buildNewUser() {
        return new UserModel(
                getUserDisplayName(),
                getUserEmail(),
                new Date().getTime(),
                getCatPhisMarks(),
                getCatGeoMarks(),
                getCatRelMarks(),
                getCatHisMarks(),
                getCatBioMarks(),
                getCatCinMarks(),
                getCatArtMarks(),
                getCatLinMarks(),
                getCatSpoMarks(),
                getCatTecMarks(),
                getCatLitMarks(),
                getCatAllMarks(),
                getTotalScoreMarks()
        );
    }
}