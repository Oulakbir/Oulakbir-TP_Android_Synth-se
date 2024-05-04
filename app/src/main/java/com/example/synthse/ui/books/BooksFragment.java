package com.example.synthse.ui.books;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.synthse.R;
import com.example.synthse.databinding.FragmentBooksBinding;
import com.example.synthse.ui.books.activities.BookDetailActivity;
import com.example.synthse.ui.books.adapters.BookAdapter;
import com.example.synthse.ui.books.models.Book;
import com.example.synthse.ui.books.models.GoogleBooksResponse;
import com.example.synthse.ui.books.services.GoogleBooksService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BooksFragment extends Fragment {

    private FragmentBooksBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBooksBinding.inflate(inflater, container, false);
        Context context = binding.getRoot().getContext();

        List<Book> books = new ArrayList<>();
        BookAdapter bookAdapter = new BookAdapter(context, R.layout.list_book_item, books);

        binding.listViewBooks.setAdapter(bookAdapter);

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        GoogleBooksService booksApi = retrofit.create(GoogleBooksService.class);

        binding.buttonSearch.setOnClickListener(view -> {
            String query = binding.editTextQuery.getText().toString();
            Call<GoogleBooksResponse> call = booksApi.searchBooks(query);
            call.enqueue(new Callback<GoogleBooksResponse>() {
                @Override
                public void onResponse(Call<GoogleBooksResponse> call, Response<GoogleBooksResponse> response) {
                    GoogleBooksResponse booksResponse = response.body();
                    // Log.i("Info", booksResponse.getKind() + " " + booksResponse.getTotalItems());
                    books.clear();
                    books.addAll(booksResponse.getItems());
                    bookAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<GoogleBooksResponse> call, Throwable throwable) {
                    throwable.printStackTrace();
                    Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT);
                }
            });
        });
        binding.listViewBooks.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("book", books.get(i));
            startActivity(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}