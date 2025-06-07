package com.example.apianimais;

import android.content.Intent;
import android.os.AsyncTask;
import android.app.ProgressDialog;
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

public class DonateAnimalActivity extends AppCompatActivity {

    private EditText editDescricao, editIdade, editValor, editIdTipo, editIdRaca, editIdCidade;
    private Button buttonDonate;
    private Button buttonSearchCity, buttonSearchType, buttonSearchBreed;
    private Button buttonBack;
    private Button buttonEdit;
    private Button buttonDelete;

    private ApiService apiService;
    private Integer currentAnimalId = null;
    private String finalidade = "D";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_animal);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        editDescricao = findViewById(R.id.editDescricao);
        editIdade = findViewById(R.id.editIdade);
        editValor = findViewById(R.id.editValor);
        editIdTipo = findViewById(R.id.editIdTipo);
        editIdRaca = findViewById(R.id.editIdRaca);
        editIdCidade = findViewById(R.id.editIdCidade);
        buttonDonate = findViewById(R.id.buttonDonate);
        buttonSearchCity = findViewById(R.id.buttonSearchCity);
        buttonSearchType = findViewById(R.id.buttonSearchType);
        buttonSearchBreed = findViewById(R.id.buttonSearchBreed);
        buttonBack = findViewById(R.id.buttonBack);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        // Get finalidade from intent extras
        if (getIntent() != null && getIntent().hasExtra("finalidade")) {
            finalidade = getIntent().getStringExtra("finalidade");
        }

        // Hide valor field if finalidade is adoption
        if ("A".equals(finalidade)) {
            buttonDonate.setText("Adotar Animal");
            editValor.setVisibility(View.GONE);
        } else {
            buttonDonate.setText("Doar Animal");
            editValor.setVisibility(View.VISIBLE);
        }

        buttonDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donateAnimal(finalidade);
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAnimal();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAnimal();
            }
        });

        buttonSearchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonateAnimalActivity.this, CityListActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        buttonSearchType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonateAnimalActivity.this, TypeListActivity.class);
                startActivityForResult(intent, 102);
            }
        });

        buttonSearchBreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonateAnimalActivity.this, BreedListActivity.class);
                startActivityForResult(intent, 103);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // If editing existing animal, populate fields and show edit/delete buttons
        if (getIntent() != null && getIntent().hasExtra("animalId")) {
            currentAnimalId = getIntent().getIntExtra("animalId", -1);
            if (currentAnimalId != -1) {
                loadAnimal(currentAnimalId);
                buttonDonate.setVisibility(View.GONE);
                buttonEdit.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String selectedId = data.getStringExtra("selectedId");
            if (selectedId != null) {
                switch (requestCode) {
                    case 101:
                        editIdCidade.setText(selectedId);
                        break;
                    case 102:
                        editIdTipo.setText(selectedId);
                        break;
                    case 103:
                        editIdRaca.setText(selectedId);
                        break;
                }
            }
        }
    }

    private void donateAnimal(String finalidade) {
        String descricao = editDescricao.getText().toString().trim();
        String idadeStr = editIdade.getText().toString().trim();
        String valorStr = editValor.getText().toString().trim();
        String idTipoStr = editIdTipo.getText().toString().trim();
        String idRacaStr = editIdRaca.getText().toString().trim();
        String idCidadeStr = editIdCidade.getText().toString().trim();

        if (descricao.isEmpty() || idadeStr.isEmpty() || idTipoStr.isEmpty() || idRacaStr.isEmpty() || idCidadeStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_LONG).show();
            return;
        }

        int idade;
        double valor = 0.0;
        int idTipo, idRaca, idCidade;

        try {
            idade = Integer.parseInt(idadeStr);
            idTipo = Integer.parseInt(idTipoStr);
            idRaca = Integer.parseInt(idRacaStr);
            idCidade = Integer.parseInt(idCidadeStr);
            if (!valorStr.isEmpty()) {
                valor = Double.parseDouble(valorStr);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_LONG).show();
            return;
        }

        Animal animal = new Animal();
        animal.setDescricao(descricao);
        animal.setIdade(idade);
        animal.setFinalidade(finalidade);
        animal.setValor(valor);
        animal.setIdTipo(idTipo);
        animal.setIdRaca(idRaca);
        animal.setIdCidade(idCidade);

        Call<Void> call = apiService.createAnimal(animal);
        call.enqueue(new retrofit2.Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    String successMessage = "Doador animal com sucesso!";
                    if ("A".equals(finalidade)) {
                        successMessage = "Animal adotado com sucesso!";
                    }
                    Toast.makeText(DonateAnimalActivity.this, successMessage, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(DonateAnimalActivity.this, "Erro ao doar animal: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(DonateAnimalActivity.this, "Erro ao doar animal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(DonateAnimalActivity.this, "Aguarde", "Doando animal...", true);
            }
        });
    }

    private void loadAnimal(int animalId) {
        // This method should load animal details from API or local data and populate the fields
        // For simplicity, assuming animal data is passed via intent extras or implement API call here
        // Example:
        // editDescricao.setText(...);
        // editIdade.setText(...);
        // editValor.setText(...);
        // editIdTipo.setText(...);
        // editIdRaca.setText(...);
        // editIdCidade.setText(...);
    }

    private void updateAnimal() {
        if (currentAnimalId == null) {
            Toast.makeText(this, "Nenhum animal selecionado para editar", Toast.LENGTH_LONG).show();
            return;
        }

        String descricao = editDescricao.getText().toString().trim();
        String idadeStr = editIdade.getText().toString().trim();
        String valorStr = editValor.getText().toString().trim();
        String idTipoStr = editIdTipo.getText().toString().trim();
        String idRacaStr = editIdRaca.getText().toString().trim();
        String idCidadeStr = editIdCidade.getText().toString().trim();

        if (descricao.isEmpty() || idadeStr.isEmpty() || idTipoStr.isEmpty() || idRacaStr.isEmpty() || idCidadeStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_LONG).show();
            return;
        }

        int idade;
        double valor = 0.0;
        int idTipo, idRaca, idCidade;

        try {
            idade = Integer.parseInt(idadeStr);
            idTipo = Integer.parseInt(idTipoStr);
            idRaca = Integer.parseInt(idRacaStr);
            idCidade = Integer.parseInt(idCidadeStr);
            if (!valorStr.isEmpty()) {
                valor = Double.parseDouble(valorStr);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores numéricos inválidos", Toast.LENGTH_LONG).show();
            return;
        }

        Animal animal = new Animal();
        animal.setDescricao(descricao);
        animal.setIdade(idade);
        animal.setFinalidade("D");
        animal.setValor(valor);
        animal.setIdTipo(idTipo);
        animal.setIdRaca(idRaca);
        animal.setIdCidade(idCidade);

        Call<Void> call = apiService.updateAnimal(currentAnimalId, animal);
        call.enqueue(new retrofit2.Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(DonateAnimalActivity.this, "Animal atualizado com sucesso!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(DonateAnimalActivity.this, "Erro ao atualizar animal: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(DonateAnimalActivity.this, "Erro ao atualizar animal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(DonateAnimalActivity.this, "Aguarde", "Atualizando animal...", true);
            }
        });
    }

    private void deleteAnimal() {
        if (currentAnimalId == null) {
            Toast.makeText(this, "Nenhum animal selecionado para deletar", Toast.LENGTH_LONG).show();
            return;
        }

        Call<Void> call = apiService.deleteAnimal(currentAnimalId);
        call.enqueue(new retrofit2.Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(DonateAnimalActivity.this, "Animal deletado com sucesso!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(DonateAnimalActivity.this, "Erro ao deletar animal: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(DonateAnimalActivity.this, "Erro ao deletar animal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(DonateAnimalActivity.this, "Aguarde", "Deletando animal...", true);
            }
        });
    }
}
