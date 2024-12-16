package com.example.recipeapp4rdtry;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView recipeListView;
    private RecipeAdapter adapter;
    private ArrayList<Recipe> recipeList;
    private static final String URL = "http://192.168.31.228/get_all_recipes.php"; // Замените на ваш URL
    private ArrayList<String> selectedIds = new ArrayList<>();
    private String userId, userRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userId = getIntent().getStringExtra("userId");
        userRole = getIntent().getStringExtra("userRole");
        recipeListView = findViewById(R.id.recipeListView);
        recipeList = new ArrayList<>();
        adapter = new RecipeAdapter(this, recipeList);
        recipeListView.setAdapter(adapter);

        loadRecipes();
        recipeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        recipeListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Recipe selectedRecipe = recipeList.get(position);
            String recipeId = selectedRecipe.getId();

            if (selectedIds.contains(recipeId)) {
                selectedIds.remove(recipeId);
                view.setBackgroundColor(Color.TRANSPARENT); // Снять выделение
            } else {
                selectedIds.add(recipeId);
                view.setBackgroundColor(Color.LTGRAY); // Выделить
            }

            return true;
        });
        recipeListView.setOnItemClickListener((parent, view, position, id) -> {
            Recipe selectedRecipe = recipeList.get(position);
            if (selectedRecipe.getId() == null) {
                Log.e("MainActivity", "ID для рецепта отсутствует!");
            }
            Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
            intent.putExtra("id", selectedRecipe.getId());
            intent.putExtra("userRole", userRole);
            intent.putExtra("title", selectedRecipe.getTitle());
            intent.putExtra("description", selectedRecipe.getDescription());
            intent.putExtra("ingredients", selectedRecipe.getIngredients());

            startActivityForResult(intent, 1);; // Добавлен код запроса (1)
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (!userRole.equals("admin")) {
            menu.findItem(R.id.action_delete).setVisible(false);  // Убираем пункт меню удаления
            menu.findItem(R.id.action_add).setVisible(false);  // Убираем пункт меню удаления
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) { // Здесь должен быть R.id.action_delete
            if (!selectedIds.isEmpty()) {
                deleteSelectedRecipes();
            } else {
                Toast.makeText(this, "Выберите записи для удаления", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == R.id.action_add) {
            showAddRecipeDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddRecipeDialog() {
        // Создаём View из пользовательского макета
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_recipe, null);

        EditText editTitle = dialogView.findViewById(R.id.editTitle);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        EditText editIngredients = dialogView.findViewById(R.id.editIngredients);

        // Создаём AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Добавить рецепт")
                .setView(dialogView)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    // Получаем введённые данные
                    String title = editTitle.getText().toString().trim();
                    String description = editDescription.getText().toString().trim();
                    String ingredients = editIngredients.getText().toString().trim();

                    if (!title.isEmpty() && !description.isEmpty() && !ingredients.isEmpty()) {
                        addRecipeToServer(title, description, ingredients);
                    } else {
                        Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void addRecipeToServer(String title, String description, String ingredients) {
        String url = "http://192.168.31.228/create_recipe.php?title=" + title
                + "&description=" + description + "&ingredients=" + ingredients;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        int success = response.getInt("success");
                        if (success == 1) {
                            Toast.makeText(this, "Рецепт добавлен", Toast.LENGTH_SHORT).show();
                            loadRecipes(); // Обновляем список
                        } else {
                            Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка ответа сервера", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Ошибка подключения", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    private void deleteSelectedRecipes() {
        String url = "http://192.168.31.228/delete_recipes.php"; // Ваш URL

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("ids", new JSONArray(selectedIds));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        int success = response.getInt("success");
                        if (success == 1) {
                            selectedIds.clear();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Записи удалены", Toast.LENGTH_SHORT).show();
                            loadRecipes(); // Обновляем список
                        } else {
                            Toast.makeText(MainActivity.this, "Ошибка при удалении", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(MainActivity.this, "Ошибка подключения", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            String updatedId = data.getStringExtra("id");
            String updatedTitle = data.getStringExtra("title");
            String updatedDescription = data.getStringExtra("description");
            String updatedIngredients = data.getStringExtra("ingredients");
            Log.d("id:", updatedId);
            Log.d("Title:", updatedTitle);
            Log.d("ingredients:", updatedIngredients);
            Log.d("Description:", updatedDescription);
            // Обновление рецепта в списке
            for (Recipe recipe : recipeList) {
                if (recipe.getId().equals(updatedId)) {
                    recipe.setTitle(updatedTitle);
                    recipe.setDescription(updatedDescription);
                    recipe.setIngredients(updatedIngredients);
                    break;
                }
            }
            // Уведомить адаптер об изменениях
            adapter.notifyDataSetChanged();
        }
    }
    public void loadRecipes() {
        recipeList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int success = response.getInt("success");
                    if (success == 1) {
                        JSONArray recipes = response.getJSONArray("recipes");
                        for (int i = 0; i < recipes.length(); i++) {
                            JSONObject recipeObject = recipes.getJSONObject(i);
                            String id = recipeObject.getString("id");
                            String title = recipeObject.getString("title");
                            String description = recipeObject.getString("description");
                            String ingredients = recipeObject.getString("ingredients");

                            Recipe recipe = new Recipe(id, title, description, ingredients);
                            recipeList.add(recipe);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, "No recipes found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error loading recipes", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}