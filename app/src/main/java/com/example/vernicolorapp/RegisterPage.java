package com.example.vernicolorapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import java.sql.SQLException;

public class RegisterPage extends AppCompatActivity {

    TextInputEditText editTextUsername, editTextMobile, editTextEmail, editTextPassword, editTextConPassword;
    Button signUp;
    TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextUsername = findViewById(R.id.username);
        editTextMobile = findViewById(R.id.mobile);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConPassword = findViewById(R.id.conpassword);
        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString();
                String mobile = editTextMobile.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String conPassword = editTextConPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(RegisterPage.this, "Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterPage.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterPage.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(conPassword)) {
                    Toast.makeText(RegisterPage.this, "Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(conPassword)) {
                    Toast.makeText(RegisterPage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(RegisterPage.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidMobileNumber(mobile)) {
                    Toast.makeText(RegisterPage.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Replace Firebase authentication logic with SQL Server registration logic
                ConnectionDatabase connectionDatabase = new ConnectionDatabase();
                Connection conn = connectionDatabase.connect();

                try {
                    String query = "INSERT INTO Users (username, email, password, mobile) VALUES (?, ?, ?, ?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, email);
                    preparedStatement.setString(3, password);
                    preparedStatement.setString(4, mobile);
                    preparedStatement.executeUpdate();

                    Toast.makeText(RegisterPage.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterPage.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private boolean isValidMobileNumber(String mobile) {
        // Check if mobile number has 8 digits
        if (mobile.length() != 8) {
            return false;
        }

        // Check if the first two digits are in the allowed prefixes
        String prefix = mobile.substring(0, 2);
        String[] validPrefixes = {"20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
                "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
                "90", "91", "92", "93", "94", "95", "96", "97", "98", "99",
                "71"};

        for (String validPrefix : validPrefixes) {
            if (prefix.equals(validPrefix)) {
                return true;
            }
        }

        return false;
    }
}
