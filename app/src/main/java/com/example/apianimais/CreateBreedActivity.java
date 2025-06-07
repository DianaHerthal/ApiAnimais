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
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateBreedActivity extends AppCompatActivity {

    private EditText editDescricao, editIdTipo;
    private Button buttonCreate;
    private Button buttonBack;
    private Button buttonEdit;
    private Button buttonDelete;
    private ProgressDialog progressDialog;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_breed);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        editDescricao = findViewById(R.id.editDescricao);
        editIdTipo = findViewById(R.id.editIdTipo);
        buttonCreate = findViewById(R.id.buttonCreate);
        buttonBack = findViewById(R.id.buttonBack);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                createBreed();
                                            }
                                        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBreed();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBreed();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Hide edit and delete buttons initially
        buttonEdit.setVisibility(View.GONE);
        buttonDelete.setVisibility(View.GONE);

        // If editing existing breed, show edit and delete buttons and load data
        if (getIntent() != null && getIntent().hasExtra("breedId")) {
            int breedId = getIntent().getIntExtra("breedId", -1);
            if (breedId != -1) {
                buttonCreate.setVisibility(View.GONE);
                buttonEdit.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
                loadBreed(breedId);
            }
        }
    }

    private void loadBreed(int breedId) {
        progressDialog = ProgressDialog.show(CreateBreedActivity.this, "Loading", "Loading breed details...", true);
        Call<Raca> call = apiService.getBreed(breedId);
        call.enqueue(new Callback<Raca>() {
            @Override
            public void onResponse(Call<Raca> call, Response<Raca> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful() && response.body() != null) {
                    Raca breed = response.body();
                    editDescricao.setText(breed.getDescricao());
                    if (breed.getIdTipo() != 0) {
                        editIdTipo.setText(String.valueOf(breed.getIdTipo()));
                    } else {
                        editIdTipo.setText("");
                    }
                } else {
                    Toast.makeText(CreateBreedActivity.this, "Error loading breed: " + response.message(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Raca> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateBreedActivity.this, "Error loading breed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void createBreed() {
        String descricao = editDescricao.getText().toString().trim();
        String idTipoStr = editIdTipo.getText().toString().trim();

        if (descricao.isEmpty() || idTipoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_LONG).show();
            return;
        }

        int idTipo;
        try {
            idTipo = Integer.parseInt(idTipoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID do Tipo inválido", Toast.LENGTH_LONG).show();
            return;
        }

        Raca raca = new Raca();
        raca.setDescricao(descricao);
        raca.setIdTipo(idTipo);

        Call<Void> call = apiService.createBreed(raca);
        call.enqueue(new retrofit2.Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(CreateBreedActivity.this, "Raça criada com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreateBreedActivity.this, "Erro ao criar raça: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateBreedActivity.this, "Erro ao criar raça: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(CreateBreedActivity.this, "Aguarde", "Criando raça...", true);
            }
        });
    }

    private void updateBreed() {
        if (!getIntent().hasExtra("breedId")) {
            Toast.makeText(this, "Nenhuma raça selecionada para editar", Toast.LENGTH_LONG).show();
            return;
        }
        int breedId = getIntent().getIntExtra("breedId", -1);
        if (breedId == -1) {
            Toast.makeText(this, "Raça inválida para atualização", Toast.LENGTH_LONG).show();
            return;
        }

        String descricao = editDescricao.getText().toString().trim();
        String idTipoStr = editIdTipo.getText().toString().trim();

        if (descricao.isEmpty() || idTipoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_LONG).show();
            return;
        }

        int idTipo;
        try {
            idTipo = Integer.parseInt(idTipoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID do Tipo inválido", Toast.LENGTH_LONG).show();
            return;
        }

        Raca raca = new Raca();
        raca.setDescricao(descricao);
        raca.setIdTipo(idTipo);

        Call<Void> call = apiService.updateBreed(breedId, raca);
        call.enqueue(new retrofit2.Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(CreateBreedActivity.this, "Raça atualizada com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreateBreedActivity.this, "Erro ao atualizar raça: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateBreedActivity.this, "Erro ao atualizar raça: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(CreateBreedActivity.this, "Aguarde", "Atualizando raça...", true);
            }
        });
    }

    private void deleteBreed() {
        if (!getIntent().hasExtra("breedId")) {
            Toast.makeText(this, "Nenhuma raça selecionada para deletar", Toast.LENGTH_LONG).show();
            return;
        }
        int breedId = getIntent().getIntExtra("breedId", -1);
        if (breedId == -1) {
            Toast.makeText(this, "Raça inválida para exclusão", Toast.LENGTH_LONG).show();
            return;
        }

        Call<Void> call = apiService.deleteBreed(breedId);
        call.enqueue(new retrofit2.Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(CreateBreedActivity.this, "Raça deletada com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreateBreedActivity.this, "Erro ao deletar raça: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateBreedActivity.this, "Erro ao deletar raça: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(CreateBreedActivity.this, "Aguarde", "Deletando raça...", true);
            }
        });
    }
}

