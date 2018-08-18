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

package com.example.android.baking.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.android.baking.R;
import com.example.android.baking.model.Ingredient;
import com.example.android.baking.model.Recipe;
import com.example.android.baking.ui.RecipeDetailActivity;
import com.example.android.baking.ui.RecipeListActivity;
import com.example.android.baking.util.AppPreferences;
import com.example.android.baking.util.DatabaseQueryUtil;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeIngredientListWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {
        RemoteViews views;

        if (recipe != null) {
            // TODO: Temp layout stuff for ingredient list
            StringBuilder stringBuilder = new StringBuilder();
            for (Ingredient ingredient : recipe.getIngredients()) {
                stringBuilder.append(ingredient.getQuantity()).append(" ")
                        .append(ingredient.getMeasure()).append(" ")
                        .append(ingredient.getIngredient()).append("\n");
            }

            // Construct the RemoteViews object
            views = new RemoteViews(context.getPackageName(),
                    R.layout.recipe_ingredient_list_widget);
            views.setTextViewText(R.id.widget_recipe_name, recipe.getName());
            views.setTextViewText(R.id.tv_widget_recipe_detail_ingredients_list,
                    stringBuilder.toString());

            // Launch the selected recipe's detail activity upon clicking widget
            Intent appIntent = new Intent(context, RecipeDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(RecipeDetailActivity.RECIPE_EXTRAS, recipe);
            appIntent.putExtras(bundle);

            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_layout_container, appPendingIntent);
        } else {
            views = new RemoteViews(context.getPackageName(),
                    R.layout.recipe_ingredient_list_no_data_widget);

            Intent appIntent = new Intent(context, RecipeListActivity.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_layout_container, appPendingIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidgets(context, appWidgetManager, appWidgetIds);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                        int[] appWidgetIds) {
        Recipe recipe = null;

        int pinnedRecipeId = AppPreferences.getPinnedRecipeId(context);
        if (pinnedRecipeId != -1) {
            recipe = DatabaseQueryUtil.retrieveRecipe(context.getContentResolver(),
                    pinnedRecipeId);
        }

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

