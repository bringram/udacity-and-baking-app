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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.example.android.baking.model.Ingredient;
import com.example.android.baking.model.Recipe;
import com.example.android.baking.model.Step;
import com.example.android.baking.provider.IngredientColumns;
import com.example.android.baking.provider.RecipeColumns;
import com.example.android.baking.provider.RecipeContentProvider;
import com.example.android.baking.provider.StepColumns;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * This class is a helper class that helps isolate various database operations to a single
 * location.
 */
public class DatabaseQueryUtil {

    /**
     * Retrieves the list of recipes found in the database connected to the given
     * {@link ContentResolver}.
     *
     * @param contentResolver The {@link ContentResolver} instance
     * @return The list of recipes found in the database
     */
    public static List<Recipe> retrieveRecipes(@NonNull ContentResolver contentResolver) {
        List<Recipe> recipes = new ArrayList<>();

        Cursor cursor = contentResolver.query(RecipeContentProvider.Recipes.RECIPE_LIST, null,
                null, null, null);

        try {
            if (cursor != null && cursor.getCount() > 0) {
                int recipeIdIndex = cursor.getColumnIndex(RecipeColumns.RECIPE_ID);
                int nameIndex = cursor.getColumnIndex(RecipeColumns.NAME);
                int servingIndex = cursor.getColumnIndex(RecipeColumns.SERVINGS);
                int imageIndex = cursor.getColumnIndex(RecipeColumns.IMAGE);

                while (cursor.moveToNext()) {
                    Recipe recipe = new Recipe();

                    recipe.setId(cursor.getInt(recipeIdIndex));
                    recipe.setName(cursor.getString(nameIndex));
                    recipe.setServings(cursor.getInt(servingIndex));
                    recipe.setImage(cursor.getString(imageIndex));

                    recipe.setIngredients(retrieveIngredientsForRecipe(contentResolver,
                            recipe.getId()));
                    recipe.setSteps(retrieveStepsForRecipe(contentResolver, recipe.getId()));

                    recipes.add(recipe);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Timber.d("Found " + recipes.size() + " recipes in database");
        return recipes;
    }

    /**
     * Retrieves a single recipe found in the database for the given ID or null if one isn't found.
     *
     * @param contentResolver The {@link ContentResolver} instance
     * @param recipeId        The ID of the recipe to retrieve
     * @return The recipe found in the database for the given ID
     */
    public static Recipe retrieveRecipe(@NonNull ContentResolver contentResolver, int recipeId) {
        Cursor cursor = contentResolver.query(RecipeContentProvider.Recipes.withId(recipeId), null,
                null, null, null);

        try {
            if (cursor != null && cursor.getCount() == 1) {
                int recipeIdIndex = cursor.getColumnIndex(RecipeColumns.RECIPE_ID);
                int nameIndex = cursor.getColumnIndex(RecipeColumns.NAME);
                int servingIndex = cursor.getColumnIndex(RecipeColumns.SERVINGS);
                int imageIndex = cursor.getColumnIndex(RecipeColumns.IMAGE);

                cursor.moveToNext();

                Recipe recipe = new Recipe();

                recipe.setId(cursor.getInt(recipeIdIndex));
                recipe.setName(cursor.getString(nameIndex));
                recipe.setServings(cursor.getInt(servingIndex));
                recipe.setImage(cursor.getString(imageIndex));

                recipe.setIngredients(retrieveIngredientsForRecipe(contentResolver,
                        recipe.getId()));
                recipe.setSteps(retrieveStepsForRecipe(contentResolver, recipe.getId()));

                return recipe;
            } else {
                return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Retrieves the list of recipe IDs found in the database connected to the given
     * {@link ContentResolver}.
     *
     * @param contentResolver The {@link ContentResolver} instance
     * @return The list of recipe IDs found in the database
     */
    public static List<Integer> retrieveRecipeIds(@NonNull ContentResolver contentResolver) {
        List<Integer> recipeIds = new ArrayList<>();

        String[] projectionColumns = {RecipeColumns.RECIPE_ID};
        Cursor cursor = contentResolver.query(RecipeContentProvider.Recipes.RECIPE_LIST,
                projectionColumns, null, null, null);

        try {
            if (cursor != null && cursor.getCount() > 0) {
                int recipeIdIndex = cursor.getColumnIndex(RecipeColumns.RECIPE_ID);

                while (cursor.moveToNext()) {
                    int id = cursor.getInt(recipeIdIndex);
                    recipeIds.add(id);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Timber.d("Found " + recipeIds.size() + " recipes IDs in database");
        return recipeIds;
    }

    /**
     * Retrieves the list of ingredients found in the database connected to the given
     * {@link ContentResolver} for the given recipe ID.
     *
     * @param contentResolver The {@link ContentResolver} instance
     * @param recipeId        The ID of the recipe to find the ingredients for
     * @return The list of ingredients for the given recipe ID
     */
    private static List<Ingredient> retrieveIngredientsForRecipe(@NonNull ContentResolver contentResolver,
                                                                 int recipeId) {
        List<Ingredient> ingredients = new ArrayList<>();

        String selection = IngredientColumns.RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        Cursor cursor = contentResolver.query(RecipeContentProvider.Ingredients.INGREDIENT_LIST,
                null, selection, selectionArgs, null);

        try {
            if (cursor != null && cursor.getCount() > 0) {
                int quantityIndex = cursor.getColumnIndex(IngredientColumns.QUANTITY);
                int measureIndex = cursor.getColumnIndex(IngredientColumns.MEASURE);
                int nameIndex = cursor.getColumnIndex(IngredientColumns.NAME);

                while (cursor.moveToNext()) {
                    Ingredient ingredient = new Ingredient();

                    ingredient.setQuantity(cursor.getInt(quantityIndex));
                    ingredient.setMeasure(cursor.getString(measureIndex));
                    ingredient.setIngredient(cursor.getString(nameIndex));

                    ingredients.add(ingredient);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Timber.d("Found " + ingredients.size() + " ingredients in database for recipeId '"
                + recipeId + "'");
        return ingredients;
    }

    /**
     * Retrieves the list of steps found in the database connected to the given
     * {@link ContentResolver} for the given recipe ID.
     *
     * @param contentResolver The {@link ContentResolver} instance
     * @param recipeId        The ID of the recipe to find the steps for
     * @return The list of steps for the given recipe ID
     */
    private static List<Step> retrieveStepsForRecipe(@NonNull ContentResolver contentResolver,
                                                     int recipeId) {
        List<Step> steps = new ArrayList<>();

        String selection = StepColumns.RECIPE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(recipeId)};
        Cursor cursor = contentResolver.query(RecipeContentProvider.Steps.STEP_LIST,
                null, selection, selectionArgs, null);

        try {
            if (cursor != null && cursor.getCount() > 0) {
                int idIndex = cursor.getColumnIndex(StepColumns.STEP_ID);
                int shortDescriptionIndex = cursor.getColumnIndex(StepColumns.SHORT_DESCRIPTION);
                int descriptionIndex = cursor.getColumnIndex(StepColumns.DESCRIPTION);
                int videoUrlIndex = cursor.getColumnIndex(StepColumns.VIDEO_URL);
                int thumbnailUrlIndex = cursor.getColumnIndex(StepColumns.THUMBNAIL_URL);

                while (cursor.moveToNext()) {
                    Step step = new Step();

                    step.setId(cursor.getInt(idIndex));
                    step.setShortDescription(cursor.getString(shortDescriptionIndex));
                    step.setDescription(cursor.getString(descriptionIndex));
                    step.setVideoUrl(cursor.getString(videoUrlIndex));
                    step.setThumbnailUrl(cursor.getString(thumbnailUrlIndex));

                    steps.add(step);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Timber.d("Found " + steps.size() + " steps in database for recipeId '"
                + recipeId + "'");
        return steps;
    }

    /**
     * Inserts all of the given recipes into the database connected to the given
     * {@link ContentResolver}.
     *
     * @param contentResolver The {@link ContentResolver} instance
     * @param recipes         The list of {@link Recipe} objects to insert
     * @return
     */
    public static int insertRecipes(@NonNull ContentResolver contentResolver,
                                    @NonNull List<Recipe> recipes) {
        ContentValues[] contentValuesArray = new ContentValues[recipes.size()];

        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            ContentValues recipeContentValues = createRecipeContentValues(recipe);
            contentValuesArray[i] = recipeContentValues;
        }

        int rowsInserted = contentResolver.bulkInsert(RecipeContentProvider.Recipes.RECIPE_LIST,
                contentValuesArray);

        // Insert the ingredients and steps for each recipe
        for (Recipe recipe : recipes) {
            insertIngredientsForRecipe(contentResolver, recipe.getId(), recipe.getIngredients());
            insertStepsForRecipe(contentResolver, recipe.getId(), recipe.getSteps());
        }

        Timber.d("Inserted " + rowsInserted + " recipes into database");
        return rowsInserted;
    }

    /**
     * Inserts all of the given recipe ingredients into the database connected to the given
     * {@link ContentResolver}.
     *
     * @param contentResolver The {@link ContentResolver} instance
     * @param recipeId        The ID of the recipe the given ingredients belong to
     * @param ingredients     The list of {@link Ingredient} objects to insert
     * @return
     */
    private static int insertIngredientsForRecipe(@NonNull ContentResolver contentResolver,
                                                  int recipeId,
                                                  @NonNull List<Ingredient> ingredients) {
        ContentValues[] contentValuesArray = new ContentValues[ingredients.size()];

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ContentValues ingredientContentValues = createIngredientContentValues(recipeId, ingredient);
            contentValuesArray[i] = ingredientContentValues;
        }

        int rowsInserted = contentResolver
                .bulkInsert(RecipeContentProvider.Ingredients.INGREDIENT_LIST, contentValuesArray);

        Timber.d("Inserted " + rowsInserted + " ingredients into database for recipeId '"
                + recipeId + "'");
        return rowsInserted;
    }

    /**
     * Inserts all of the given recipe steps into the database connected to the given
     * {@link ContentResolver}.
     *
     * @param contentResolver The {@link ContentResolver} instance
     * @param recipeId        The ID of the recipe the given steps belong to
     * @param steps           The list of {@link Step} objects to insert
     * @return
     */
    private static int insertStepsForRecipe(@NonNull ContentResolver contentResolver, int recipeId,
                                            @NonNull List<Step> steps) {
        ContentValues[] contentValuesArray = new ContentValues[steps.size()];

        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            ContentValues stepContentValues = createStepContentValues(recipeId, step);
            contentValuesArray[i] = stepContentValues;
        }

        int rowsInserted = contentResolver.bulkInsert(RecipeContentProvider.Steps.STEP_LIST,
                contentValuesArray);

        Timber.d("Inserted " + rowsInserted + " steps into database for recipeId '"
                + recipeId + "'");
        return rowsInserted;
    }

    /**
     * Deletes all of the recipes stored in the database connected to the given
     * {@link ContentResolver}.
     *
     * @param contentResolver The {@link ContentResolver} instance
     * @return
     */
    public static int deleteRecipes(@NonNull ContentResolver contentResolver) {
        int rowsDeleted = contentResolver.delete(RecipeContentProvider.Recipes.RECIPE_LIST,
                null, null);

        Timber.d("Deleted " + rowsDeleted + " recipes from database");
        return rowsDeleted;
    }

    /**
     * Deletes all of the ingredients stored in the database connected to the given
     * {@link ContentResolver}.
     *
     * @param contentResolver The {@link ContentResolver} instance
     * @return
     */
    public static int deleteIngredients(@NonNull ContentResolver contentResolver) {
        int rowsDeleted = contentResolver.delete(RecipeContentProvider.Ingredients.INGREDIENT_LIST,
                null, null);

        Timber.d("Deleted " + rowsDeleted + " recipe ingredients from database");
        return rowsDeleted;
    }

    /**
     * Deletes all of the steps stored in the database connected to the given
     * {@link ContentResolver}.
     *
     * @param contentResolver The {@link ContentResolver} instance
     * @return
     */
    public static int deleteSteps(@NonNull ContentResolver contentResolver) {
        int rowsDeleted = contentResolver.delete(RecipeContentProvider.Steps.STEP_LIST,
                null, null);

        Timber.d("Deleted " + rowsDeleted + " recipe steps from database");
        return rowsDeleted;
    }

    /**
     * Helper method that creates a {@link ContentValues} object for the given {@link Recipe}.
     *
     * @param recipe The recipe to generate {@link ContentValues} for
     * @return The generated {@link ContentValues} object for the given {@link Recipe}
     */
    private static ContentValues createRecipeContentValues(Recipe recipe) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(RecipeColumns.RECIPE_ID, recipe.getId());
        contentValues.put(RecipeColumns.NAME, recipe.getName());
        contentValues.put(RecipeColumns.SERVINGS, recipe.getServings());
        contentValues.put(RecipeColumns.IMAGE, recipe.getImage());

        return contentValues;
    }

    /**
     * Helper method that creates a {@link ContentValues} object for the given {@link Ingredient}
     * and the given {@link Recipe} ID.
     *
     * @param recipeId   The ID of the recipe this ingredient belongs to
     * @param ingredient The ingredient to generate {@link ContentValues} for
     * @return The generated {@link ContentValues} object for the given {@link Ingredient}.
     */
    private static ContentValues createIngredientContentValues(int recipeId,
                                                               Ingredient ingredient) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(IngredientColumns.RECIPE_ID, recipeId);
        contentValues.put(IngredientColumns.QUANTITY, ingredient.getQuantity());
        contentValues.put(IngredientColumns.MEASURE, ingredient.getMeasure());
        contentValues.put(IngredientColumns.NAME, ingredient.getIngredient());

        return contentValues;
    }

    /**
     * Helper method that creates a {@link ContentValues} object for the given {@link Step}
     * and the given {@link Recipe} ID.
     *
     * @param recipeId The ID of the recipe this step belongs to
     * @param step     The step to generate {@link ContentValues} for
     * @return The generated {@link ContentValues} object for the given {@link Step}.
     */
    private static ContentValues createStepContentValues(int recipeId, Step step) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(StepColumns.RECIPE_ID, recipeId);
        contentValues.put(StepColumns.STEP_ID, step.getId());
        contentValues.put(StepColumns.SHORT_DESCRIPTION, step.getShortDescription());
        contentValues.put(StepColumns.DESCRIPTION, step.getDescription());
        contentValues.put(StepColumns.VIDEO_URL, step.getVideoUrl());
        contentValues.put(StepColumns.THUMBNAIL_URL, step.getThumbnailUrl());

        return contentValues;
    }

}
