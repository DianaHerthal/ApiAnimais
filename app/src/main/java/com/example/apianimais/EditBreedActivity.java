package com.example.apianimais;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditBreedActivity extends AppCompatActivity {

    private EditText editId;
    private EditText editDescricao;
    private EditText editIdTipo;
    private Button buttonSave;
    private Button buttonBack;

    private ApiService apiService;
    private Integer currentBreedId;
    private ProgressDialog  progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_breed);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        editId = findViewById(R.id.editId);
        editDescricao = findViewById(R.id.editDescricao);
        editIdTipo = findViewById(R.id.editIdTipo);
        buttonSave = findViewById(R.id.buttonSave);
        buttonBack = findViewById(R.id.buttonBack);

        editId.setEnabled(false); // ID is not editable

        if (getIntent() != null && getIntent().hasExtra("breedId")) {
            currentBreedId = getIntent().getIntExtra("breedId", -1);
            if (currentBreedId != -1) {
                loadBreed(currentBreedId);
            }
        } else {
            Toast.makeText(this, "Raça inválida para edição", Toast.LENGTH_LONG).show();
            finish();
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBreed();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadBreed(int breedId) {
        Call<Raca> call = apiService.getBreed(breedId);
        call.enqueue(new Callback<Raca>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Raca> call, Response<Raca> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful() && response.body() != null) {
                    Raca breed = response.body();
                    editId.setText(String.valueOf(breed.getId()));
                    editDescricao.setText(breed.getDescricao());
                    editIdTipo.setText(String.valueOf(breed.getIdTipo()));
                } else {
                    Toast.makeText(EditBreedActivity.this, "Erro ao carregar raça: " + response.message(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Raca> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(EditBreedActivity.this, "Erro ao carregar raça: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(EditBreedActivity.this, "Aguarde", "Carregando raça...", true);
            }
        });
    }

    private void updateBreed() {
        int currentBreedId = Integer.parseInt(editId.getText().toString().trim());
        if (currentBreedId == -1) {
            Toast.makeText(this, "ID da raça inválido", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "ID Tipo inválido", Toast.LENGTH_LONG).show();
            return;
        }
        Raca breed = new Raca();
        breed.setId(currentBreedId);
        breed.setDescricao(descricao);
        breed.setIdTipo(idTipo);

        Call<Void> call = apiService.updateBreed(currentBreedId, breed);
        call.enqueue(new Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(EditBreedActivity.this, "Raça atualizada com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditBreedActivity.this, "Erro ao atualizar raça: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(EditBreedActivity.this, "Erro ao atualizar raça: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(EditBreedActivity.this, "Aguarde", "Atualizando raça...", true);
            }
        });
    }

}
