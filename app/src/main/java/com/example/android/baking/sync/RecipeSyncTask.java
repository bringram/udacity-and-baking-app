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

package com.example.android.baking.sync;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.android.baking.model.Recipe;
import com.example.android.baking.service.RecipeService;
import com.example.android.baking.service.RecipeServiceGenerator;
import com.example.android.baking.util.DatabaseQueryUtil;
import com.example.android.baking.widget.RecipeIngredientListWidgetProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class RecipeSyncTask {

    public static final String DB_UPDATE_COMPLETE_ACTION =
            "com.example.android.baking.DB_UPDATE_COMPLETE";

    synchronized public static void syncRecipes(final Context context) {
        RecipeService recipeService = RecipeServiceGenerator.createService(RecipeService.class);
        Call<List<Recipe>> asyncRecipeCall = recipeService.getRecipes();

        asyncRecipeCall.enqueue(new Callback<List<Recipe>>() {

            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call,
                                   @NonNull Response<List<Recipe>> response) {
                List<Recipe> recipes = response.body();
                ContentResolver contentResolver = context.getContentResolver();

                if (recipes != null) {
                    // TODO: Temporary - see about only updating the existing ones if needed
                    DatabaseQueryUtil.deleteRecipes(contentResolver);
                    DatabaseQueryUtil.deleteIngredients(contentResolver);
                    DatabaseQueryUtil.deleteSteps(contentResolver);

                    DatabaseQueryUtil.insertRecipes(contentResolver, recipes);

                    // Update the widgets to reflect the new data
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                            RecipeIngredientListWidgetProvider.class));
                    RecipeIngredientListWidgetProvider.updateAppWidgets(context, appWidgetManager,
                            appWidgetIds);

                    Intent intent = new Intent(DB_UPDATE_COMPLETE_ACTION);
                    context.sendBroadcast(intent);
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, Throwable t) {
                Timber.e(t);
            }

        });
    }

}
