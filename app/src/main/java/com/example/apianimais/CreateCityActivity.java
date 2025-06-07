package com.example.apianimais;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateCityActivity extends AppCompatActivity {

    private EditText editNome, editDdd;
    private Button buttonCreate;
    private Button buttonBack;
    private Button buttonEdit;
    private Button buttonDelete;

    private ApiService apiService;
    private Integer currentCityId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_city);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        editNome = findViewById(R.id.editNome);
        editDdd = findViewById(R.id.editDdd);
        buttonCreate = findViewById(R.id.buttonCreate);
        buttonBack = findViewById(R.id.buttonBack);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCity();
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCity();
            }
        });

        buttonBack.setOnClickListener(v -> finish());

        // Show edit/save buttons and hide delete button
        if (getIntent() != null && getIntent().hasExtra("cityId")) {
            currentCityId = getIntent().getIntExtra("cityId", -1);
            if (currentCityId != -1) {
                buttonCreate.setVisibility(View.GONE);
                buttonEdit.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
                loadCity(currentCityId);
            } else {
                buttonCreate.setVisibility(View.VISIBLE);
                buttonEdit.setVisibility(View.GONE);
                buttonDelete.setVisibility(View.GONE);
            }
        } else {
            buttonCreate.setVisibility(View.VISIBLE);
            buttonEdit.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
        }
    }

    private void createCity() {
        String nome = editNome.getText().toString().trim();
        String ddd = editDdd.getText().toString().trim();

        if (nome.isEmpty() || ddd.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_LONG).show();
            return;
        }

        Cidade cidade = new Cidade();
        cidade.setNome(nome);
        cidade.setDdd(String.valueOf(ddd));

        Call<Void> call = apiService.createCity(cidade);
        call.enqueue(new retrofit2.Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(CreateCityActivity.this, "Cidade criada com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreateCityActivity.this, "Erro ao criar cidade: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateCityActivity.this, "Erro ao criar cidade: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(CreateCityActivity.this, "Aguarde", "Criando cidade...", true);
            }
        });
    }

    private void loadCity(int cityId) {
        Call<Cidade> call = apiService.getCity(cityId);
        call.enqueue(new Callback<Cidade>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Cidade> call, Response<Cidade> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful() && response.body() != null) {
                    Cidade cidade = response.body();
                    editNome.setText(cidade.getNome());
                    editDdd.setText(cidade.getDdd());
                } else {
                    Toast.makeText(CreateCityActivity.this, "Erro ao carregar cidade: " + response.message(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Cidade> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateCityActivity.this, "Erro ao carregar cidade: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(CreateCityActivity.this, "Aguarde", "Carregando cidade...", true);
            }
        });
    }

    private void updateCity() {
        if (currentCityId == null) {
            Toast.makeText(this, "Nenhuma cidade selecionada para editar", Toast.LENGTH_LONG).show();
            return;
        }
        String nome = editNome.getText().toString().trim();
        String ddd = editDdd.getText().toString().trim();

        if (nome.isEmpty() || ddd.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_LONG).show();
            return;
        }

        Cidade cidade = new Cidade();
        cidade.setNome(nome);
        cidade.setDdd(ddd);

        Call<Void> call = apiService.updateCity(currentCityId, cidade);
        call.enqueue(new retrofit2.Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(CreateCityActivity.this, "Cidade atualizada com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreateCityActivity.this, "Erro ao atualizar cidade: " + response.message(), Toast.LENGTH_LONG).show();
                }
                // Log response code for debugging
                android.util.Log.d("CreateCityActivity", "Update response code: " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateCityActivity.this, "Erro ao atualizar cidade: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(CreateCityActivity.this, "Aguarde", "Atualizando cidade...", true);
            }
        });
    }

    private void deleteCity() {
        if (currentCityId == null) {
            Toast.makeText(this, "Nenhuma cidade selecionada para deletar", Toast.LENGTH_LONG).show();
            return;
        }

        Call<Void> call = apiService.deleteCity(currentCityId);
        call.enqueue(new retrofit2.Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(CreateCityActivity.this, "Cidade deletada com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreateCityActivity.this, "Erro ao deletar cidade: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateCityActivity.this, "Erro ao deletar cidade: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(CreateCityActivity.this, "Aguarde", "Deletando cidade...", true);
            }
        });
    }
}
