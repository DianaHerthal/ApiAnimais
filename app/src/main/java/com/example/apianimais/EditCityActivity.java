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

public class EditCityActivity extends AppCompatActivity {

    private EditText editId;
    private EditText editNome;
    private EditText editDdd;
    private Button buttonSave;
    private Button buttonBack;

    private ApiService apiService;
    private Integer currentCityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_city);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        editId = findViewById(R.id.editId);
        editNome = findViewById(R.id.editNome);
        editDdd = findViewById(R.id.editDdd);
        buttonSave = findViewById(R.id.buttonSave);
        buttonBack = findViewById(R.id.buttonBack);

        editId.setEnabled(false); // ID is not editable

        if (getIntent() != null && getIntent().hasExtra("cityId")) {
            currentCityId = getIntent().getIntExtra("cityId", -1);
            if (currentCityId != -1) {
                loadCity(currentCityId);
            }
        } else {
            Toast.makeText(this, "Cidade inválida para edição", Toast.LENGTH_LONG).show();
            finish();
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCity();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                    editId.setText(String.valueOf(cidade.getId()));
                    editNome.setText(cidade.getNome());
                    editDdd.setText(String.valueOf(cidade.getDdd()));
                } else {
                    Toast.makeText(EditCityActivity.this, "Erro ao carregar cidade: " + response.message(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Cidade> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(EditCityActivity.this, "Erro ao carregar cidade: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(EditCityActivity.this, "Aguarde", "Carregando cidade...", true);
            }
        });
    }

    private void updateCity() {
        int currentCityId = Integer.parseInt(editId.getText().toString().trim());
        if (currentCityId == -1) {
            Toast.makeText(this, "ID inválido para atualização", Toast.LENGTH_LONG).show();
            return;
        }
        String nome = editNome.getText().toString().trim();
        String dddStr = editDdd.getText().toString().trim();

        if (nome.isEmpty() || dddStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_LONG).show();
            return;
        }

        int ddd;
        try {
            ddd = Integer.parseInt(dddStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "DDD inválido", Toast.LENGTH_LONG).show();
            return;
        }
        Cidade cidade = new Cidade();
        cidade.setId(currentCityId);
        cidade.setNome(nome);
        cidade.setDdd(String.valueOf(ddd));

        Call<Void> call = apiService.updateCity(currentCityId, cidade);
        call.enqueue(new Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(EditCityActivity.this, "Cidade atualizada com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditCityActivity.this, "Erro ao atualizar cidade: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(EditCityActivity.this, "Erro ao atualizar cidade: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(EditCityActivity.this, "Aguarde", "Atualizando cidade...", true);
            }
        });
    }
}
