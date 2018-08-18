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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.baking.model.Recipe;

import java.util.List;

/**
 * This loader assists in the task of fetching the list of recipes from the content provider.
 */
public class RecipeListLoader extends AsyncTaskLoader<List<Recipe>> {

    private final SimpleIdlingResource idlingResource;

    public RecipeListLoader(@NonNull Context context,
                            @Nullable final SimpleIdlingResource idlingResource) {
        super(context);
        this.idlingResource = idlingResource;
    }

    /**
     * Force triggers a load and sets the idle state of the IdlingResource.
     */
    @Override
    protected void onStartLoading() {
        if (idlingResource != null) idlingResource.setIdleState(false);
        forceLoad();
    }

    /**
     * Retrieves the list of {@link Recipe} objects stored in the application's database.
     *
     * @return The list of recipes fetched from the application database
     */
    @Nullable
    @Override
    public List<Recipe> loadInBackground() {
        return DatabaseQueryUtil.retrieveRecipes(getContext().getContentResolver());
    }

}
