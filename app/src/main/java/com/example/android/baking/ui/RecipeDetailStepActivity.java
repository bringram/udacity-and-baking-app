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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.android.baking.R;
import com.example.android.baking.model.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity is responsible for displaying the details of the selected recipe step, including
 * it's image and/or video, if they exist. This activity also allows navigation between the various
 * steps via "Previous" and "Next" buttons.
 */
public class RecipeDetailStepActivity extends AppCompatActivity {

    public static final String STEP_POSITION_EXTRAS = "step_position";

    @BindView(R.id.button_next)
    Button nextStepButton;

    @BindView(R.id.button_previous)
    Button previousStepButton;

    private int currentStepPosition = 0;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_step);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(RecipeDetailActivity.RECIPE_EXTRAS)) {
                recipe = bundle.getParcelable(RecipeDetailActivity.RECIPE_EXTRAS);
            }

            if (bundle.containsKey(STEP_POSITION_EXTRAS)) {
                currentStepPosition = bundle.getInt(STEP_POSITION_EXTRAS);
            }

            RecipeStepFragment fragment = new RecipeStepFragment();
            fragment.setCurrentStep(recipe.getSteps().get(currentStepPosition));

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_step_fragment_container, fragment)
                    .commit();
        } else {
            if (savedInstanceState.containsKey(RecipeDetailActivity.RECIPE_EXTRAS)) {
                recipe = savedInstanceState.getParcelable(RecipeDetailActivity.RECIPE_EXTRAS);
            }

            if (savedInstanceState.containsKey(STEP_POSITION_EXTRAS)) {
                currentStepPosition = savedInstanceState.getInt(STEP_POSITION_EXTRAS);
            }
        }

        setTitle(recipe.getName());
        initializeStepNavigationButtons();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(RecipeDetailActivity.RECIPE_EXTRAS, recipe);
        outState.putInt(STEP_POSITION_EXTRAS, currentStepPosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initializeStepNavigationButtons() {
        // If this is not the last step, initialize the next button
        if (currentStepPosition != recipe.getSteps().size() - 1) {
            nextStepButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    navigateToNextStep();
                }

            });
        } else {
            nextStepButton.setVisibility(View.GONE);
        }

        // If this is not the first step, initialize the previous button
        if (currentStepPosition != 0) {
            previousStepButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    navigateToPreviousStep();
                }

            });
        } else {
            previousStepButton.setVisibility(View.GONE);
        }
    }

    private void navigateToNextStep() {
        prepareIntent(currentStepPosition + 1);
    }

    private void navigateToPreviousStep() {
        prepareIntent(currentStepPosition - 1);
    }

    private void prepareIntent(int position) {
        Intent recipeStepIntent = new Intent(RecipeDetailStepActivity.this,
                RecipeDetailStepActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable(RecipeDetailActivity.RECIPE_EXTRAS, recipe);
        bundle.putInt(RecipeDetailStepActivity.STEP_POSITION_EXTRAS, position);

        recipeStepIntent.putExtras(bundle);
        startActivity(recipeStepIntent);
    }

}
