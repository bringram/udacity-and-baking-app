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

package com.example.android.baking.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

    private static final String RECIPE_PREFERENCES = "recipe_preferences";
    private static final String SELECTED_RECIPE_ID = "selected_recipe_id";

    public static void pinRecipe(Context context, int recipeId) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(RECIPE_PREFERENCES,
                Context.MODE_PRIVATE).edit();

        preferencesEditor.putInt(SELECTED_RECIPE_ID, recipeId);
        preferencesEditor.apply();
    }

    public static void unpinRecipe(Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(RECIPE_PREFERENCES,
                Context.MODE_PRIVATE).edit();

        preferencesEditor.remove(SELECTED_RECIPE_ID);
        preferencesEditor.apply();
    }

    public static int getPinnedRecipeId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(RECIPE_PREFERENCES,
                Context.MODE_PRIVATE);

        return sharedPreferences.getInt(SELECTED_RECIPE_ID, -1);
    }

}
