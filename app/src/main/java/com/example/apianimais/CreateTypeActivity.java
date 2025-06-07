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

public class CreateTypeActivity extends AppCompatActivity {

    private EditText editDescricao;
    private Button buttonCreate;
    private Button buttonBack;

    private ApiService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_type);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        editDescricao = findViewById(R.id.editDescricao);
        buttonCreate = findViewById(R.id.buttonCreate);
        buttonBack = findViewById(R.id.buttonBack);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createType();
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateType();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteType();
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

        // If editing existing type, show edit and delete buttons and load data
        if (getIntent() != null && getIntent().hasExtra("typeId")) {
            int typeId = getIntent().getIntExtra("typeId", -1);
            if (typeId != -1) {
                buttonCreate.setVisibility(View.GONE);
                buttonEdit.setVisibility(View.VISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
                loadType(typeId);
            }
        }
    }

    private void createType() {
        String descricao = editDescricao.getText().toString().trim();

        if (descricao.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha a descrição", Toast.LENGTH_LONG).show();
            return;
        }

        Tipo tipo = new Tipo();
        tipo.setDescricao(descricao);

        Call<Void> call = apiService.createType(tipo);
        call.enqueue(new retrofit2.Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(CreateTypeActivity.this, "Tipo criado com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreateTypeActivity.this, "Erro ao criar tipo: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateTypeActivity.this, "Erro ao criar tipo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }


            public void onStart() {
                progressDialog = ProgressDialog.show(CreateTypeActivity.this, "Aguarde", "Criando tipo...", true);
            }
        });
    }

    private void loadType(int typeId) {
        progressDialog = ProgressDialog.show(CreateTypeActivity.this, "Loading", "Loading type details...", true);
        Call<Tipo> call = apiService.getType(typeId);
        call.enqueue(new Callback<Tipo>() {
            @Override
            public void onResponse(Call<Tipo> call, Response<Tipo> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful() && response.body() != null) {
                    Tipo tipo = response.body();
                    editDescricao.setText(tipo.getDescricao());
                } else {
                    Toast.makeText(CreateTypeActivity.this, "Error loading type: " + response.message(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Tipo> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateTypeActivity.this, "Error loading type: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private Button buttonEdit;
    private Button buttonDelete;

    // Removed duplicate onCreate method to fix error

    private void updateType() {
        String descricao = editDescricao.getText().toString().trim();

        if (descricao.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha a descrição", Toast.LENGTH_LONG).show();
            return;
        }

        int typeId = getIntent().getIntExtra("typeId", -1);
        if (typeId == -1) {
            Toast.makeText(this, "Tipo inválido para atualização", Toast.LENGTH_LONG).show();
            return;
        }

        Tipo tipo = new Tipo();
        tipo.setDescricao(descricao);

        Call<Void> call = apiService.updateType(typeId, tipo);
        call.enqueue(new Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(CreateTypeActivity.this, "Tipo atualizado com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreateTypeActivity.this, "Erro ao atualizar tipo: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateTypeActivity.this, "Erro ao atualizar tipo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(CreateTypeActivity.this, "Aguarde", "Atualizando tipo...", true);
            }
        });
    }

    private void deleteType() {
        int typeId = getIntent().getIntExtra("typeId", -1);
        if (typeId == -1) {
            Toast.makeText(this, "Tipo inválido para exclusão", Toast.LENGTH_LONG).show();
            return;
        }

        Call<Void> call = apiService.deleteType(typeId);
        call.enqueue(new Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(CreateTypeActivity.this, "Tipo deletado com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CreateTypeActivity.this, "Erro ao deletar tipo: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CreateTypeActivity.this, "Erro ao deletar tipo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(CreateTypeActivity.this, "Aguarde", "Deletando tipo...", true);
            }
        });
    }
}
