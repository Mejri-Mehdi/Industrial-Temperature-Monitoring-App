package com.example.vernicolorapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private Button signIn;
    private TextView signUp;
    private CheckBox keepMeLoggedIn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);
        keepMeLoggedIn = findViewById(R.id.keep_me_logged_in);

        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Check if the user is already logged in
        if (sharedPreferences.getBoolean("logged_in", false)) {
            startActivity(new Intent(MainActivity.this, HomePage.class));
            finish();
        }

        signUp.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterPage.class);
            startActivity(intent);
            finish();
        });

        signIn.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            ConnectionDatabase connectionDatabase = new ConnectionDatabase();
            Connection conn = connectionDatabase.connect();

            try {
                String query = "SELECT * FROM Users WHERE email=? AND password=?";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // User exists, proceed to login
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (keepMeLoggedIn.isChecked()) {
                        editor.putBoolean("logged_in", true);
                    } else {
                        editor.putBoolean("logged_in", false);
                    }
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User doesn't exist, show error
                    Toast.makeText(MainActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
