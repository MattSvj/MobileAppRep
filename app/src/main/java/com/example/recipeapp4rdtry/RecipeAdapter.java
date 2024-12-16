package com.example.recipeapp4rdtry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Recipe> recipes;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }
    public void updateRecipeList(List<Recipe> newRecipeList) {
        this.recipes = new ArrayList<>(newRecipeList); // Обновляем список рецептов
        notifyDataSetChanged(); // Уведомляем адаптер о том, что данные изменились
    }
    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Object getItem(int position) {
        return recipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        Recipe recipe = recipes.get(position);
        TextView title = convertView.findViewById(android.R.id.text1);
        TextView description = convertView.findViewById(android.R.id.text2);

        title.setText(recipe.getTitle());
        description.setText(recipe.getDescription());

        return convertView;
    }
}