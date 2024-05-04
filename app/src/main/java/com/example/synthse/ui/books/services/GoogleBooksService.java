package com.example.synthse.ui.books.services;

import com.example.synthse.ui.books.models.GoogleBooksResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleBooksService {
    @GET("v1/volumes") //URL de base
    Call<GoogleBooksResponse> searchBooks(@Query("q") String query);
}
