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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.baking.R;
import com.example.android.baking.model.Recipe;
import com.example.android.baking.sync.RecipeServiceSyncUtil;
import com.example.android.baking.sync.RecipeSyncTask;
import com.example.android.baking.util.RecipeListLoader;
import com.example.android.baking.util.SimpleIdlingResource;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * This activity is responsible for displaying the list of recipes a user can select to view.
 */
public class RecipeListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Recipe>>, RecipeListAdapter.OnClickHandler {

    private static final String RECIPE_LIST_EXTRAS = "recipe_list";
    private static final int ID_RECIPE_LOADER = 1;

    @BindView(R.id.rv_recipe_list)
    RecyclerView recipeRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar loadingIndicator;

    @Nullable
    private SimpleIdlingResource idlingResource;

    private RecipeListAdapter recipeListAdapter;

    private BroadcastReceiver broadcastReceiver;

    private int recyclerViewPosition = RecyclerView.NO_POSITION;

    /**
     * Returns the instance of {@link SimpleIdlingResource}. This is only called from test code.
     */
    @VisibleForTesting
    @NonNull
    public SimpleIdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }

        return idlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());
        getIdlingResource();

        int columnSpanCount = 1;
        if (getResources().getBoolean(R.bool.isTablet)) {
            columnSpanCount = 3;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columnSpanCount = 2;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(this, columnSpanCount);
        recipeRecyclerView.setLayoutManager(layoutManager);
        recipeRecyclerView.setHasFixedSize(true);

        recipeListAdapter = new RecipeListAdapter(this, this);
        recipeRecyclerView.setAdapter(recipeListAdapter);

        showLoading();
        RecipeServiceSyncUtil.initialize(this);

        // If we're restoring state, no need to fetch from the database again
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECIPE_LIST_EXTRAS)) {
                List<Recipe> savedRecipes = savedInstanceState
                        .getParcelableArrayList(RECIPE_LIST_EXTRAS);
                recipeListAdapter.swapRecipes(savedRecipes);
                showData();
            }
        } else {
            getSupportLoaderManager().initLoader(ID_RECIPE_LOADER, null, this);
        }

        // Prepare a broadcast receiver to listen for database update completion.
        // This helps mostly on the initial load of the application, where the database has not
        // been fully updated when the activity fetches from it.
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(RecipeSyncTask.DB_UPDATE_COMPLETE_ACTION)) {
                    getSupportLoaderManager().restartLoader(ID_RECIPE_LOADER, null,
                            RecipeListActivity.this);
                }
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(
                RecipeSyncTask.DB_UPDATE_COMPLETE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * Saves the list of recipes from the adapter.
     *
     * @param outState The state bundle to persist
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recipeListAdapter != null) {
            List<Recipe> recipes = recipeListAdapter.getRecipes();
            if (recipes != null && !recipes.isEmpty()) {
                outState.putParcelableArrayList(RECIPE_LIST_EXTRAS, new ArrayList<>(recipes));
            }
        }
    }

    /**
     * Restores the list of recipes saves in the given {@link Bundle}.
     *
     * @param savedInstanceState The state bundle to restore
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECIPE_LIST_EXTRAS)) {
                List<Recipe> savedRecipes = savedInstanceState
                        .getParcelableArrayList(RECIPE_LIST_EXTRAS);
                recipeListAdapter.swapRecipes(savedRecipes);
                showData();
            }
        }
    }

    /**
     * Creates a new instance of the loader with the given ID and arguments.
     *
     * @param id   The ID of the loader
     * @param args The Bundle of arguments
     * @return The newly created loader
     */
    @NonNull
    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, @Nullable Bundle args) {
        return new RecipeListLoader(this, idlingResource);
    }

    /**
     * Updates the {@link RecipeListAdapter} with the new data retrieved from the loader and
     * displays it.
     *
     * @param loader  The loader object
     * @param recipes The list of recipes retrieves from the loader
     */
    @Override
    public void onLoadFinished(@NonNull Loader<List<Recipe>> loader, List<Recipe> recipes) {
        recipeListAdapter.swapRecipes(recipes);
        if (recyclerViewPosition == RecyclerView.NO_POSITION) recyclerViewPosition = 0;
        recipeRecyclerView.smoothScrollToPosition(recyclerViewPosition);
        if (idlingResource != null) idlingResource.setIdleState(true);
        showData();
    }

    /**
     * Clears the data stored in the {@link RecipeListAdapter}.
     *
     * @param loader The loader object
     */
    @Override
    public void onLoaderReset(@NonNull Loader<List<Recipe>> loader) {
        recipeListAdapter.swapRecipes(null);
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent recipeDetailIntent = new Intent(RecipeListActivity.this, RecipeDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable(RecipeDetailActivity.RECIPE_EXTRAS, recipe);

        recipeDetailIntent.putExtras(bundle);
        startActivity(recipeDetailIntent);
    }

    /**
     * Hides the loading indicator and shows the recipe list
     */
    private void showData() {
        recipeRecyclerView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.INVISIBLE);
    }

    /**
     * Hides the recipe list and displays the loading indicator.
     */
    private void showLoading() {
        recipeRecyclerView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

}
