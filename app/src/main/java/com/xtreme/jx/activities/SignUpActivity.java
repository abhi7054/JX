package com.xtreme.jx.activities;

import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.xtreme.jx.R;
import com.xtreme.jx.model.User;
import com.xtreme.jx.utils.AppPref;
import com.xtreme.jx.utils.Constant;
import com.xtreme.jx.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends BaseActivity {

    private static final String TAG = "SignUpActivity";
    @BindView(R.id.et_user)
    EditText userEditText;

    @BindView(R.id.et_email)
    EditText emailEditText;

    @BindView(R.id.et_password)
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
    }

    private boolean isUserNameValid() {
        final boolean[] status = {false};
        mDatabase.collection(Constant.USER_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (user.getUsername().equals(userEditText.getText().toString().trim())) {
                            status[0] = true;
                        }
                    }
                } else {
                    Log.d("TAG - - - ", "Error getting documents: ", task.getException());
                }
            }
        });
        return status[0];
    }

    void signUpUser() {
        setProgressBar(true);
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Toast.makeText(SignUpActivity.this, getResources().getString(R.string.sent_verification_email), Toast.LENGTH_SHORT).show();


                            sendVerificationCodeToEmail(user);
                        } else {
                            setProgressBar(false);
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            if(task.getException().getMessage().equals("The username is already in use by another account.")){

                                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.username_already_in_use), Toast.LENGTH_SHORT).show();


                            }else if(task.getException().getMessage().equals("The email address is already in use by another account.")){

                                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.email_already_in_use), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                });
    }

    private void sendVerificationCodeToEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            userEditText.setText("");
                            emailEditText.setText("");
                            passwordEditText.setText("");
                            createUser();
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            builder.setMessage(getResources().getString(R.string.sent_verification_email));
                            builder.setNegativeButton(getResources().getString(R.string.ok), null);
                            builder.setPositiveButton(getResources().getString(R.string.login_now), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    startActivity(new Intent(SignUpActivity.this, LogInActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void createUser() {
        User user = new User(userEditText.getText().toString().trim(), emailEditText.getText().toString().trim(), "", "", new Date());
        mDatabase.collection(Constant.USER_COLLECTION)
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        setProgressBar(false);
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        //AppPref.setLoginStatus(SignUpActivity.this, true);
                        user.setDocId(documentReference.getId());
                        AppPref.setUserData(SignUpActivity.this, user);
                        mDatabase.collection(Constant.USER_COLLECTION).document(documentReference.getId()).set(user);
//                        startActivity(new Intent(SignUpActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setProgressBar(false);
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @OnClick(R.id.rl_sign_up)
    void onClickSignUp() {
        Util.hideKeyboard(this);
        if (userEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_user), Toast.LENGTH_SHORT).show();
            return;
        } else if (emailEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_email), Toast.LENGTH_SHORT).show();
            return;
        } else if (!Util.isValidEmail(emailEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
            return;
        } else if (passwordEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
            return;
        } else if (passwordEditText.getText().toString().length() < 6) {
            Toast.makeText(this, getString(R.string.enter_valid_password), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isUserNameValid()) {
            signUpUser();
        } else {
            Toast.makeText(this, "User name already exist", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.tv_sign_in)
    void onClickSignIn() {
        startActivity(new Intent(this, LogInActivity.class));
        finish();
    }

    @OnClick(R.id.iv_back)
    void onClickBack() {
        finish();
    }
}
