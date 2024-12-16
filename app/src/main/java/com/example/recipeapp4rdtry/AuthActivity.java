package com.example.recipeapp4rdtry;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private static final String LOGIN_URL = "http://192.168.31.228/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (!username.isEmpty() && !password.isEmpty()) {
                loginUser(username, password);
            } else {
                Toast.makeText(AuthActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String username, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("success") == 1) {
                            // Получаем данные пользователя
                            JSONObject userObject = jsonObject.getJSONObject("user");
                            String userId = userObject.getString("id");
                            String userRole = userObject.getString("role");

                            Toast.makeText(AuthActivity.this, "Добро пожаловать, " + username, Toast.LENGTH_SHORT).show();

                            // Переход на главный экран и передача данных
                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                            intent.putExtra("userId", userId);   // Передаем id
                            intent.putExtra("userRole", userRole); // Передаем роль
                            Log.d("AuthActivity", "User ID: " + userId);
                            Log.d("AuthActivity", "User ROLE: " + userRole);
                            startActivity(intent);
                            finish(); // Закрываем экран авторизации
                        } else {
                            Toast.makeText(AuthActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AuthActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AuthActivity.this, "Ошибка подключения: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

}