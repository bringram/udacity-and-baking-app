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

package com.example.android.baking.service;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * This class is a Retrofit service generator that is responsible for isolating the service
 * creation logic to a single place inside the application.
 */
public class RecipeServiceGenerator {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient
            = new OkHttpClient.Builder();

    /**
     * Returns an implementation of the specified Retrofit service class.
     *
     * @param serviceClass The service class of type T
     * @param <T>          The class type
     * @return An implementation of the service class
     */
    public static <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

}
