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

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.android.baking.util.DatabaseQueryUtil;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RecipeServiceSyncUtil {

    private static final int SYNC_INTERVAL_DAYS = 1;
    private static final int SYNC_INTERVAL_SECONDS =
            (int) TimeUnit.DAYS.toSeconds(SYNC_INTERVAL_DAYS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static final String RECIPE_SYNC_TAG = "recipe-api-sync";

    private static boolean isInitialized;

    synchronized public static void initialize(@NonNull final Context context) {
        // Immediately return if we have already initialized
        if (isInitialized) return;

        isInitialized = true;

        scheduleFirebaseJobDispatcherSync(context);

        // Check to see if we already have data - if not, immediately sync with the API.
        new Thread(new Runnable() {

            @Override
            public void run() {
                List<Integer> recipeIds = DatabaseQueryUtil
                        .retrieveRecipeIds(context.getContentResolver());

                if (recipeIds.size() == 0) {
                    startImmediateSync(context);
                }
            }

        }).start();
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The {@link Context} used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, RecipeSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job recipeSyncJob = dispatcher.newJobBuilder()
                .setService(RecipeFirebaseJobService.class)
                .setTag(RECIPE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(recipeSyncJob);
    }

}
