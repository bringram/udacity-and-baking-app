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

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.baking.R;
import com.example.android.baking.model.Step;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class RecipeStepFragment extends Fragment {

    public static final String STEP_EXTRAS = "current_step";
    public static final String PLAYER_POSITION_EXTRAS = "exoplayer_position";
    public static final String PLAY_WHEN_READY_EXTRAS = "play_when_ready";

    @BindView(R.id.tv_step_short_description)
    TextView stepShortDescriptionView;

    @BindView(R.id.exo_player_view)
    SimpleExoPlayerView exoPlayerView;

    @BindView(R.id.tv_step_description)
    TextView stepDescriptionView;

    @BindView(R.id.iv_step_thumbnail_image)
    ImageView thumbnailImage;

    private Unbinder unbinder;
    private Step currentStep;
    private SimpleExoPlayer exoPlayer;

    private long exoPlayerCurrentPosition = 0;
    private boolean playWhenReady = true;

    public RecipeStepFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STEP_EXTRAS)) {
                currentStep = savedInstanceState.getParcelable(STEP_EXTRAS);
            }

            if (savedInstanceState.containsKey(PLAYER_POSITION_EXTRAS)) {
                exoPlayerCurrentPosition = savedInstanceState.getLong(PLAYER_POSITION_EXTRAS);
            }

            if (savedInstanceState.containsKey(PLAY_WHEN_READY_EXTRAS)) {
                playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY_EXTRAS);
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STEP_EXTRAS, currentStep);
        outState.putLong(PLAYER_POSITION_EXTRAS, exoPlayerCurrentPosition);
        outState.putBoolean(PLAY_WHEN_READY_EXTRAS, playWhenReady);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(currentStep.getShortDescription())) {
            stepShortDescriptionView.setText(currentStep.getShortDescription());
            stepDescriptionView.setText(currentStep.getDescription());
        }

        if (!TextUtils.isEmpty(currentStep.getVideoUrl())) {
            initializePlayer(Uri.parse(currentStep.getVideoUrl()));
        } else {
            exoPlayerView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(currentStep.getThumbnailUrl())) {
            Picasso.get().load(Uri.parse(currentStep.getThumbnailUrl())).into(thumbnailImage,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            thumbnailImage.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Timber.e("Error encountered loading image: %s", e.getMessage());
                        }
                    });
        } else {
            thumbnailImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        unbinder.unbind();
    }

    public void setCurrentStep(Step currentStep) {
        this.currentStep = currentStep;
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (exoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();

            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            exoPlayerView.setPlayer(exoPlayer);

            DataSource.Factory dataSourceFactory =
                    new DefaultDataSourceFactory(Objects.requireNonNull(getContext()),
                            Util.getUserAgent(getContext(), getString(R.string.app_name)),
                            null);
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaUri);
            exoPlayer.prepare(mediaSource);

            if (exoPlayerCurrentPosition != 0)
                exoPlayer.seekTo(exoPlayerCurrentPosition);

            exoPlayer.setPlayWhenReady(playWhenReady);
            exoPlayerView.setVisibility(View.VISIBLE);
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayerCurrentPosition = exoPlayer.getCurrentPosition();
            playWhenReady = exoPlayer.getPlayWhenReady();

            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

}
