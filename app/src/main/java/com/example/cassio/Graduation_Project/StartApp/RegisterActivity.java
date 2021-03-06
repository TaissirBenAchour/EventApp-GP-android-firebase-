package com.example.cassio.Graduation_Project.StartApp;

/**
 * Created by cassio on 02/12/17.
 */


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.cassio.Graduation_Project.R;
import com.example.cassio.Graduation_Project.SponsoringActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference DefaultReference,insightusersRef;


    private EditText userName,ageuser;
    private EditText userEmail;
    private EditText userPassword;
    private Button registerBtn;
    private ProgressDialog progressing;
    private EditText userPhone;
    private RadioGroup userStatus;
    private RadioGroup userGender;
    private   String _gender;
    private String _status;
    private String _age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        userName = (EditText) findViewById(R.id.user_name);
        userEmail = (EditText) findViewById(R.id.user_email);
        userPhone = (EditText) findViewById(R.id.user_phone);
        userGender = (RadioGroup) findViewById(R.id.user_gender);
        userStatus = (RadioGroup) findViewById(R.id.user_status);
        userPassword = (EditText) findViewById(R.id.user_password);
        registerBtn = (Button) findViewById(R.id.btn_createAccount);
        ageuser = (EditText)findViewById(R.id.age_user);



        progressing = new ProgressDialog(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitFormGender();
                submitFormStatus();

                String name = userName.getText().toString();
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                String gender = _gender;
                String phone = userPhone.getText().toString();
                String status = _status;
                String age = ageuser.getText().toString();

                userAccount(name, email, password, gender,phone,status,age);

            }
        });


    }



    private void submitFormGender() {
        int selectedId = userGender.getCheckedRadioButtonId();

        if(selectedId == R.id.female_radio_btn)
            _gender = "Female";
        else
            _gender = "Male";
    }
    private void submitFormStatus() {
        int selectedId = userStatus.getCheckedRadioButtonId();

        if(selectedId == R.id.event_planner_radio_btn)
            _status = "Event planner";
        else
            _status = "Event Seeker";
    }



    private void userAccount(final String name, final String email, String password, final String gender , final String phone, final String status, final String age) {

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please check your name field ",
                    Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please check your email field ",
                    Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please check your password field ",
                    Toast.LENGTH_LONG).show();}
           else {
            progressing.setTitle("Create Account ");
            progressing.setMessage(" We are creating your account , thanks for joining us");
            progressing.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {

                                FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                Toast.makeText(RegisterActivity.this, "Failed Registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            } else {


                                insightusersRef = FirebaseDatabase.getInstance().getReference().child("insightUsers");
                                insightusersRef.child("status").child(status).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String x = dataSnapshot.getValue().toString();
                                        int i = Integer.valueOf(x);
                                        i++;
                                        insightusersRef.child(status).setValue(i);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                String dviceToken = FirebaseInstanceId.getInstance().getToken();

                                final String currentUserID = mAuth.getCurrentUser().getUid();
                                DefaultReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
                                DefaultReference.child("userName").setValue(name);
                                DefaultReference.child("userStatus").setValue(status);
                                DefaultReference.child("userImage").setValue("profile_pic");
                                DefaultReference.child("deviceToken").setValue(dviceToken);
                                DefaultReference.child("usergender").setValue(gender);
                                DefaultReference.child("userId").setValue(currentUserID);
                                DefaultReference.child("age").setValue(age);
                                DefaultReference.child("userBio").setValue("");
                                DefaultReference.child("userEmail").setValue(email);
                                DefaultReference.child("userPhone").setValue(phone)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {


                                                    Intent goToProfileIntent = new Intent(RegisterActivity.this, SponsoringActivity.class);
                                                    goToProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    goToProfileIntent.putExtra("id",currentUserID);
                                                    startActivity(goToProfileIntent);
                                                    finish();

                                                }
                                            }
                                        });


                            }
                            progressing.dismiss();
                        }


                    });
        }

    }

}
