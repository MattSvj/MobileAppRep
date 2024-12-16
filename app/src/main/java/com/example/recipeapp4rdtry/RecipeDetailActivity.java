package com.example.recipeapp4rdtry;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RecipeDetailActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText ingredientsEditText;
    private String recipeId;
    private String userRole, userId;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);


        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userRole = getIntent().getStringExtra("userRole");
        Log.d("user_role:", userRole);
        userId = getIntent().getStringExtra("userId");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Детали рецепта");
        }

        titleEditText = findViewById(R.id.etTitle);
        descriptionEditText = findViewById(R.id.etDescription);
        ingredientsEditText = findViewById(R.id.etIngredients);

        // Поля доступны только для чтения по умолчанию
        setEditMode(false);

        // Получение данных из Intent
        recipeId = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String ingredients = getIntent().getStringExtra("ingredients");
        // Установка данных в EditText
        titleEditText.setText(title);
        descriptionEditText.setText(description);
        ingredientsEditText.setText(ingredients);

        // Кнопка "Сохранить изменения"
        findViewById(R.id.btnSaveChanges).setOnClickListener(v -> {
            if (isEditing) {
                saveChanges();
            }
        });
        if (!"admin".equals(userRole)) {
            findViewById(R.id.btnSaveChanges).setVisibility(View.GONE); // Скрыть кнопку для редактирования
        }
    }

    private void setEditMode(boolean enabled) {
        isEditing = enabled;
        titleEditText.setEnabled(enabled);
        descriptionEditText.setEnabled(enabled);
        ingredientsEditText.setEnabled(enabled);

        // Показывать кнопку "Сохранить изменения" только в режиме редактирования
        findViewById(R.id.btnSaveChanges).setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void saveChanges() {
        // Базовый URL сервера
        String baseUrl = "http://192.168.31.228/update_recipe.php";

        // Получение данных из полей
        String newTitle = titleEditText.getText().toString().trim();
        String newDescription = descriptionEditText.getText().toString().trim();
        String newIngredients = ingredientsEditText.getText().toString().trim();

        // Формирование полного URL с параметрами
        String url = baseUrl + "?id=" + recipeId
                + "&title=" + newTitle
                + "&description=" + newDescription
                + "&ingredients=" + newIngredients;

        // Лог для отладки
        Log.d("RecipeDetailActivity", "Request URL: " + url);

        // Создание запроса
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Toast.makeText(RecipeDetailActivity.this, "Данные обновлены", Toast.LENGTH_SHORT).show();

                    // Передача обновлённых данных обратно в MainActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("id", recipeId);
                    resultIntent.putExtra("title", newTitle);
                    resultIntent.putExtra("description", newDescription);
                    resultIntent.putExtra("ingredients", newIngredients);
                    setResult(RESULT_OK, resultIntent);

                    finish(); // Закрыть активность
                },
                error -> {
                    Toast.makeText(RecipeDetailActivity.this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
                    Log.e("RecipeDetailActivity", "Ошибка: " + error.toString());
                });

        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ("admin".equals(userRole)) {
            getMenuInflater().inflate(R.menu.recipe_detail_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_edit) {
            // Включение режима редактирования
            setEditMode(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}