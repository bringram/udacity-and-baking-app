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

package com.example.android.baking.provider;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = RecipeContentProvider.AUTHORITY, database = RecipeDatabase.class)
public class RecipeContentProvider {

    public static final String AUTHORITY =
            "com.example.android.baking.provider.RecipeContentProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final String RECIPES_PATH = "recipes";
    private static final String INGREDIENTS_PATH = "ingredients";
    private static final String STEPS_PATH = "setps";

    @TableEndpoint(table = RecipeDatabase.RECIPE)
    public static class Recipes {

        @ContentUri(path = RECIPES_PATH, type = "vnd.android.cursor.dir/recipes",
                defaultSort = RecipeColumns.RECIPE_ID + " ASC")
        public static final Uri RECIPE_LIST = buildUri(RECIPES_PATH);

        @InexactContentUri(path = RECIPES_PATH + "/#", type = "vnd.android.cursor.item/recipes",
                name = RecipeColumns.ID, whereColumn = RecipeColumns.ID, pathSegment = 1)
        public static Uri withId(int id) {
            return buildUri(RECIPES_PATH, String.valueOf(id));
        }

    }

    @TableEndpoint(table = RecipeDatabase.INGREDIENT)
    public static class Ingredients {

        @ContentUri(path = INGREDIENTS_PATH, type = "vnd.android.cursor.dir/ingredients",
                defaultSort = IngredientColumns.ID + " ASC")
        public static final Uri INGREDIENT_LIST = buildUri(INGREDIENTS_PATH);

        @InexactContentUri(path = INGREDIENTS_PATH + "/#",
                type = "vnd.android.cursor.item/ingredients", name = IngredientColumns.ID,
                whereColumn = IngredientColumns.ID, pathSegment = 1)
        public static Uri withId(int id) {
            return buildUri(INGREDIENTS_PATH, String.valueOf(id));
        }

    }

    @TableEndpoint(table = RecipeDatabase.STEP)
    public static class Steps {

        @ContentUri(path = STEPS_PATH, type = "vnd.android.cursor.dir/steps", defaultSort = StepColumns.STEP_ID + " ASC")
        public static final Uri STEP_LIST = buildUri(STEPS_PATH);

        @InexactContentUri(path = STEPS_PATH + "/#", type = "vnd.android.cursor.item/steps",
                name = StepColumns.ID, whereColumn = StepColumns.ID, pathSegment = 1)
        public static Uri withId(int id) {
            return buildUri(STEPS_PATH, String.valueOf(id));
        }

    }

    /**
     * Helper method to construct a {@link Uri} from the BASE_CONTENT_URI and the given path
     * segments.
     *
     * @param pathSegments A variable list of path segments to append to the BASE_CONTENT_URI
     * @return A newly constructed Uri
     */
    private static Uri buildUri(String... pathSegments) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();

        for (String pathSegment : pathSegments) {
            builder.appendPath(pathSegment);
        }

        return builder.build();
    }

}
