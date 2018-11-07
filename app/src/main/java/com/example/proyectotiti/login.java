package com.example.proyectotiti;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.proyectotiti.models.User;


public class login extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "login";


    private EditText userInput;
    private EditText passInput;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    /* This function runs upon the opening of the app.
    * Sets up the input text boxes and buttons, sets onclick listeners for each,
    * and sets up the database.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        userInput = (EditText)findViewById(R.id.usernameInput);
        passInput = (EditText)findViewById(R.id.passwordInput);
        Button signInButton = (Button) findViewById(R.id.startButton);
        Button signUpButton = (Button) findViewById(R.id.emailCreateAccountButton);
        Button forgotPasswordButton = (Button) findViewById(R.id.forgotPasswordButton);

        // Buttons
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        forgotPasswordButton.setOnClickListener(this);

        // Sets up Firebase auth and realtime database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Allows for data to be accessed while offline
        mDatabase.keepSynced(true);

    }

    /* This function runs upon the starting of the app.
    * Checks if a user is already signed in. */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    /* This function runs upon pressing the sign in button.  */
    private void signIn(String email, String password) {
        Log.d(TAG, "In signIn function");
        // Ensure password and username has been filled out
        if (!validateForm()) {
            return;
        }

        // Shows the "Loading" icon while processing
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                hideProgressDialog();

                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    onAuthSuccess(task.getResult().getUser());
                    Toast.makeText(getApplicationContext(), "Autenticación exitosa.", Toast.LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("DEBUG", "signInWithEmail:failure", task.getException());
                    Toast.makeText(login.this, "Autenticación fallida.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /* This function runs upon pressing the button to create a new account.
    * Checks if a user is already signed in. */
    private void createAccount(String email, String password) {
        Log.d(TAG, "In createAccount function");
        // Ensure password and username has been filled out
        if (!validateForm()) {
            return;
        }

        // Shows the "Loading" icon while processing
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        // Automatically sign in
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            onAuthSuccess(task.getResult().getUser());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("DEBUG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(login.this, "Autenticación fallida.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    /* This function runs upon pressing the button to send a password reset email. */
    private void sendPasswordReset() {
        // Disable button
        findViewById(R.id.forgotPasswordButton).setEnabled(false);

        // Send reset password email
        final FirebaseUser user = mAuth.getCurrentUser();
        mAuth.sendPasswordResetEmail(user.getEmail())
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Re-enable button
                        findViewById(R.id.forgotPasswordButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("DEBUG", "sendEmailVerification", task.getException());
                            Toast.makeText(getApplicationContext(),
                                    "Failed to send email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /* This function runs upon successful authenication.
    * Writes a new user to the database and goes to the home screen. */
    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to home
        startActivity(new Intent(login.this, home.class));
        finish();
    }

    /* This function runs upon signing in.
    * Parses the username from the user email. */
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    /* This function runs to check that the appropriate requirements are fulfilled when pressing
    a button.   */
    private boolean validateForm() {
        boolean valid = true;

        String email = userInput.getText().toString();
        if (TextUtils.isEmpty(email)) {
            userInput.setError("Necesario.");
            valid = false;
        } else {
            userInput.setError(null);
        }

        String password = passInput.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passInput.setError("Necesario.");
            valid = false;
        } else {
            passInput.setError(null);
        }

        return valid;
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email, userId);

        mDatabase.child("users").child(userId).setValue(user);
    }

    /* This function runs when one of the buttons is pressed.  */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.startButton) {
            signIn(userInput.getText().toString(), passInput.getText().toString());
        }
        else if(i == R.id.emailCreateAccountButton) {
            createAccount(userInput.getText().toString(), passInput.getText().toString());
        }
        else if(i == R.id.forgotPasswordButton) {
            sendPasswordReset();
        }
    }
}
