package com.example.apianimais;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BreedListActivity extends Activity {

    private ListView listViewItems;
    private ProgressDialog progressDialog;
    private List<Raca> breedList;
    private ArrayAdapter<String> adapter;
    private Gson gson = new Gson();
    private Button buttonCreate;
    private Button buttonBack;
    private Button buttonSelect;
    private Button buttonEdit;
    private Button buttonDelete;

    private ApiService apiService;

    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        buttonCreate = findViewById(R.id.buttonCreate);
        buttonBack = findViewById(R.id.buttonBack);
        buttonSelect = findViewById(R.id.buttonSelect);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BreedListActivity.this, CreateBreedActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listViewItems = findViewById(R.id.listViewItems);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listViewItems.setAdapter(adapter);

        listViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                updateButtonStates();
            }
        });

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Raca selectedBreed = breedList.get(selectedPosition);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedId", String.valueOf(selectedBreed.getId()));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Raca selectedBreed = breedList.get(selectedPosition);
                    Intent intent = new Intent(BreedListActivity.this, EditBreedActivity.class);
                    intent.putExtra("breedId", selectedBreed.getId());
                    startActivityForResult(intent, 1);
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Raca selectedBreed = breedList.get(selectedPosition);
                    deleteBreed(selectedBreed.getId());
                }
            }
        });

        fetchBreeds();
    }

    private void updateButtonStates() {
        boolean enabled = selectedPosition != -1;
        buttonSelect.setEnabled(enabled);
        buttonEdit.setEnabled(enabled);
        buttonDelete.setEnabled(enabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchBreeds();
        }
    }

    private void deleteBreed(int breedId) {
        Call<Void> call = apiService.deleteBreed(breedId);
        call.enqueue(new Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(BreedListActivity.this, "Raça deletada com sucesso!", Toast.LENGTH_LONG).show();
                    fetchBreeds();
                } else {
                    Toast.makeText(BreedListActivity.this, "Erro ao deletar raça: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(BreedListActivity.this, "Erro ao deletar raça: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(BreedListActivity.this, "Aguarde", "Deletando raça...", true);
            }
        });
    }

    private void fetchBreeds() {
        progressDialog = ProgressDialog.show(BreedListActivity.this, "Loading", "Fetching breeds...", true);
        Call<List<Raca>> call = apiService.getBreeds();
        call.enqueue(new retrofit2.Callback<List<Raca>>() {
            @Override
            public void onResponse(Call<List<Raca>> call, retrofit2.Response<List<Raca>> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful() && response.body() != null) {
                    breedList = response.body();
                    updateBreedList();
                } else {
                    Toast.makeText(BreedListActivity.this, "Error fetching breeds: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Raca>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(BreedListActivity.this, "Error fetching breeds: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateBreedList() {
        try {
            adapter.clear();
            for (Raca breed : breedList) {
                String tipoDescricao = breed.getTipo() != null ? breed.getTipo().getDescricao() : "N/A";
                adapter.add("ID: " + breed.getId() + ", Descrição: " + breed.getDescricao() + ", ID Tipo: " + breed.getIdTipo() + ", Tipo: " + tipoDescricao);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Error processing breed data", Toast.LENGTH_LONG).show();
        }
    }
}
