/*
 * PROJECT LICENSE
 *
 * This project was submitted by Brandon Ingram as part of the Android Developer
 * Nanodegree Program at Udacity.
 *
 * As part of Udacity Honor code, your submissions must be your own work, hence
 * submitting this project as yours will cause you to break the Udacity Honor Code
 * and the suspension of your account.
 *
 * Me, the author of the project, allow you to check the code as a reference, but if
 * you submit it, it's your own responsibility if you get expelled.
 *
 * Copyright (c) 2018 Brandon Ingram
 *
 * Besides the above notice, the following license applies and this license notice
 * must be included in all works derived from this project.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.example.android.baking.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.baking.R;
import com.example.android.baking.model.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This adapter is used to plug in a list of recipes to the {@link RecyclerView} that will display
 * it.
 */
public class RecipeListAdapter
        extends RecyclerView.Adapter<RecipeListAdapter.RecipeListAdapterViewHolder> {

    private final OnClickHandler clickHandler;

    private Context context;
    private List<Recipe> recipes;

    public RecipeListAdapter(@NonNull Context context, @NonNull OnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    public interface OnClickHandler {
        void onClick(Recipe recipe);
    }

    /**
     * Creates a new instance of a {@link RecipeListAdapterViewHolder} and returns it.
     *
     * @param parent   The parent {@link ViewGroup}
     * @param viewType The type of view to use
     * @return A new instance of a {@link RecipeListAdapterViewHolder}
     */
    @NonNull
    @Override
    public RecipeListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeListAdapterViewHolder(view);
    }

    /**
     * Populates the given {@link RecipeListAdapterViewHolder} with the data of the {@link Recipe}
     * found at the given position.
     *
     * @param holder   The {@link RecipeListAdapterViewHolder} to populate
     * @param position The position of the given {@link RecipeListAdapterViewHolder}
     */
    @Override
    public void onBindViewHolder(@NonNull RecipeListAdapterViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.recipeName.setText(recipe.getName());
    }

    /**
     * Retrieves the number of recipes stored in this adapter.
     *
     * @return The number of recipes stored in this adapter
     */
    @Override
    public int getItemCount() {
        if (recipes == null) return 0;
        return recipes.size();
    }

    /**
     * Replaces the list of recipes stored in this adapter with the given list of recipes.
     *
     * @param recipes The new list of recipes
     */
    public void swapRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    /**
     * Retrieves the list of recipes stored in this adapter.
     *
     * @return The list of recipes stored in this adapter
     */
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * This class is the implementation of the {@link RecyclerView.ViewHolder} that will display
     * a single {@link Recipe} from the list.
     */
    class RecipeListAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.tv_recipe_list_item_name)
        TextView recipeName;

        RecipeListAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Recipe recipe = recipes.get(getAdapterPosition());
            clickHandler.onClick(recipe);
        }
    }

}
