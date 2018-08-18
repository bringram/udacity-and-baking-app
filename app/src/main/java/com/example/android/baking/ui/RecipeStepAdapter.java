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

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.baking.R;
import com.example.android.baking.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.ViewHolder> {

    private final OnClickHandler clickHandler;

    private Context context;
    private List<Step> steps;
    private int currentStepPosition = -1;

    RecipeStepAdapter(@NonNull Context context, @NonNull OnClickHandler clickHandler,
                      @NonNull List<Step> steps) {
        this.context = context;
        this.clickHandler = clickHandler;
        this.steps = steps;
    }

    public interface OnClickHandler {
        void onClick(int position);
    }

    public void setCurrentStepPosition(int currentStepPosition) {
        this.currentStepPosition = currentStepPosition;
        notifyDataSetChanged();
    }

    /**
     * Creates a new instance of a {@link ViewHolder} and returns it.
     *
     * @param parent   The parent {@link ViewGroup}
     * @param viewType The type of view to use
     * @return A new instance of a {@link ViewHolder}
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recipe_step_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Populates the given {@link ViewHolder} with the data of the {@link Step} found at the
     * given position.
     *
     * @param holder   The {@link ViewHolder} to populate
     * @param position The position of the given {@link ViewHolder}
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Step step = steps.get(position);

        if (currentStepPosition == position) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.stepShortDescription.setText(step.getShortDescription());
    }

    /**
     * Retrieves the number of steps stored in this adapter.
     *
     * @return The number of steps stored in this adapter.
     */
    @Override
    public int getItemCount() {
        if (steps == null) return 0;
        return steps.size();
    }

    /**
     * This class is the implementation of the {@link RecyclerView.ViewHolder} that will display
     * a single {@link Step} from the list.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_recipe_step_item_description)
        TextView stepShortDescription;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            clickHandler.onClick(adapterPosition);
        }

    }

}
