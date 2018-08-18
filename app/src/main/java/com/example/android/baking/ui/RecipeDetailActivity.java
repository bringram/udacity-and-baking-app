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

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.baking.R;
import com.example.android.baking.model.Ingredient;
import com.example.android.baking.model.Recipe;
import com.example.android.baking.util.AppPreferences;
import com.example.android.baking.widget.RecipeIngredientListWidgetProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * This activity is responsible for displaying the various details for a given recipe, including
 * it's name, ingredients list, steps to make, etc.
 */
public class RecipeDetailActivity extends AppCompatActivity
        implements RecipeStepAdapter.OnClickHandler {

    public static final String RECIPE_EXTRAS = "recipe";

    @BindView(R.id.tv_recipe_detail_ingredients_list)
    TextView ingredientsList;

    @BindView(R.id.rv_recipe_detail_steps_list)
    RecyclerView recipeStepRecyclerView;

    @Nullable
    @BindView(R.id.recipe_step_fragment_container)
    FrameLayout recipeStepFragmentContainer;

    private RecipeStepAdapter recipeStepAdapter;
    private Recipe recipe;
    private boolean useTwoPaneLayout;
    private boolean isPinned = false;
    private int currentStepPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey(RECIPE_EXTRAS)) {
                recipe = bundle.getParcelable(RECIPE_EXTRAS);
            }
        } else {
            if (savedInstanceState.containsKey(RECIPE_EXTRAS)) {
                recipe = savedInstanceState.getParcelable(RECIPE_EXTRAS);
            }

            if (savedInstanceState.containsKey(RecipeDetailStepActivity.STEP_POSITION_EXTRAS)) {
                currentStepPosition = savedInstanceState
                        .getInt(RecipeDetailStepActivity.STEP_POSITION_EXTRAS);
            }
        }

        int pinnedRecipeId = AppPreferences.getPinnedRecipeId(this);
        if (pinnedRecipeId == recipe.getId()) {
            isPinned = true;
        }

        setTitle((recipe != null && recipe.getName() != null) ? recipe.getName() :
                getResources().getString(R.string.app_name));

        // TODO: Temp layout stuff for ingredient list
        StringBuilder stringBuilder = new StringBuilder();
        for (Ingredient ingredient : recipe.getIngredients()) {
            stringBuilder.append(ingredient.getQuantity()).append(" ")
                    .append(ingredient.getMeasure()).append(" ")
                    .append(ingredient.getIngredient()).append("\n");
        }

        ingredientsList.setText(stringBuilder.toString());

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recipeStepRecyclerView.setLayoutManager(layoutManager);
        recipeStepRecyclerView.setHasFixedSize(true);

        recipeStepAdapter = new RecipeStepAdapter(this, this, recipe.getSteps());
        recipeStepRecyclerView.setAdapter(recipeStepAdapter);

        if (recipeStepFragmentContainer != null) {
            useTwoPaneLayout = true;
            recipeStepAdapter.setCurrentStepPosition(currentStepPosition);

            if (savedInstanceState == null) {
                RecipeStepFragment fragment = new RecipeStepFragment();
                fragment.setCurrentStep(recipe.getSteps().get(currentStepPosition));

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.recipe_step_fragment_container, fragment)
                        .commit();
            }

        } else {
            useTwoPaneLayout = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECIPE_EXTRAS, recipe);
        outState.putInt(RecipeDetailStepActivity.STEP_POSITION_EXTRAS, currentStepPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(RECIPE_EXTRAS)) {
            recipe = savedInstanceState.getParcelable(RECIPE_EXTRAS);
        }
    }

    @Override
    public void onClick(int position) {
        currentStepPosition = position;

        if (useTwoPaneLayout) {
            recipeStepAdapter.setCurrentStepPosition(position);
            RecipeStepFragment fragment = new RecipeStepFragment();
            fragment.setCurrentStep(recipe.getSteps().get(position));

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_step_fragment_container, fragment)
                    .commit();
        } else {
            Intent recipeStepIntent = new Intent(RecipeDetailActivity.this,
                    RecipeDetailStepActivity.class);

            Bundle bundle = new Bundle();
            bundle.putParcelable(RecipeDetailActivity.RECIPE_EXTRAS, recipe);
            bundle.putInt(RecipeDetailStepActivity.STEP_POSITION_EXTRAS, position);

            recipeStepIntent.putExtras(bundle);
            startActivity(recipeStepIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_detail_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem pinRecipe = menu.findItem(R.id.toggle_menu_item);
        updateMenuIcon(pinRecipe, false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toggle_menu_item) {
            isPinned = !item.isChecked();
            updateMenuIcon(item, true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMenuIcon(MenuItem menuItem, boolean updatePreferences) {
        menuItem.setChecked(isPinned);

        if (isPinned) {
            menuItem.setIcon(R.drawable.ic_pin_selected);
            if (updatePreferences) {
                AppPreferences.pinRecipe(this, recipe.getId());
                updateAppWidgets();
            }
        } else {
            menuItem.setIcon(R.drawable.ic_pin_unselected);
            if (updatePreferences) {
                AppPreferences.unpinRecipe(this);
                updateAppWidgets();
            }
        }
    }

    private void updateAppWidgets() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                RecipeIngredientListWidgetProvider.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_layout_container);
        RecipeIngredientListWidgetProvider.updateAppWidgets(this, appWidgetManager,
                appWidgetIds);
    }

}
