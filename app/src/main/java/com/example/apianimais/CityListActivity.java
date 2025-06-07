package com.example.apianimais;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class CityListActivity extends Activity {

    private ListView listViewItems;
    private ProgressDialog progressDialog;
    private List<Cidade> cityList;
    private ArrayAdapter<String> adapter;
    private Gson gson = new Gson();
    private Button buttonCreate;
    private Button buttonSelect;
    private Button buttonEdit;
    private Button buttonDelete;
    private Button buttonBack;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        buttonCreate = findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CityListActivity.this, CreateCityActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        buttonBack = findViewById(R.id.buttonBack);
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
                // Select the item without finishing the activity
                selectedPosition = position;
                updateButtonStates();
            }
        });

        listViewItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Select the item on long click as well
                selectedPosition = position;
                updateButtonStates();
                return true;
            }
        });

        buttonSelect = findViewById(R.id.buttonSelect);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Cidade selectedCity = cityList.get(selectedPosition);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedId", String.valueOf(selectedCity.getId()));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Cidade selectedCity = cityList.get(selectedPosition);
                    Intent intent = new Intent(CityListActivity.this, EditCityActivity.class);
                    intent.putExtra("cityId", selectedCity.getId());
                    startActivityForResult(intent, 1);
                    
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Cidade selectedCity = cityList.get(selectedPosition);
                    deleteCity(selectedCity.getId());
                }
            }
            
        });
        fetchCities();
    }

    private int selectedPosition = -1;

    private void updateButtonStates() {
        boolean enabled = selectedPosition != -1;
        buttonSelect.setEnabled(enabled);
        buttonEdit.setEnabled(enabled);
        buttonDelete.setEnabled(enabled);
    }

    private void deleteCity(int cityId) {
        Call<Void> call = apiService.deleteCity(cityId);
        call.enqueue(new Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(CityListActivity.this, "Cidade deletada com sucesso!", Toast.LENGTH_LONG).show();
                    fetchCities();
                } else {
                    Toast.makeText(CityListActivity.this, "Erro ao deletar cidade: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CityListActivity.this, "Erro ao deletar cidade: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(CityListActivity.this, "Aguarde", "Deletando cidade...", true);
            }
        });
    }

    private void fetchCities() {
        progressDialog = ProgressDialog.show(CityListActivity.this, "Loading", "Fetching cities...", true);
        Call<List<Cidade>> call = apiService.getCities();
        call.enqueue(new retrofit2.Callback<List<Cidade>>() {
            @Override
            public void onResponse(Call<List<Cidade>> call, retrofit2.Response<List<Cidade>> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful() && response.body() != null) {
                    cityList = response.body();
                    updateCityList();
                } else {
                    Toast.makeText(CityListActivity.this, "Error fetching cities: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Cidade>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CityListActivity.this, "Error fetching cities: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateCityList() {
        try {
            adapter.clear();
            for (Cidade city : cityList) {
                adapter.add("DDD: " + city.getDdd() + ", ID: " + city.getId() + ", Nome: " + city.getNome());
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Error processing city data", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchCities();
        }
    }
}
