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

public class EditTypeActivity extends AppCompatActivity {

    private EditText editId;
    private EditText editDescricao;
    private Button buttonSave;
    private Button buttonBack;

    private ApiService apiService;
    private Integer currentTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_type);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        editId = findViewById(R.id.editId);
        editDescricao = findViewById(R.id.editDescricao);
        buttonSave = findViewById(R.id.buttonSave);
        buttonBack = findViewById(R.id.buttonBack);

        editId.setEnabled(false); // ID is not editable

        if (getIntent() != null && getIntent().hasExtra("typeId")) {
            currentTypeId = getIntent().getIntExtra("typeId", -1);
            if (currentTypeId != -1) {
                loadType(currentTypeId);
            }
        } else {
            Toast.makeText(this, "Tipo inválido para edição", Toast.LENGTH_LONG).show();
            finish();
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateType();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadType(int typeId) {
        Call<Tipo> call = apiService.getType(typeId);
        call.enqueue(new Callback<Tipo>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Tipo> call, Response<Tipo> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful() && response.body() != null) {
                    Tipo tipo = response.body();
                    editId.setText(String.valueOf(tipo.getId()));
                    editDescricao.setText(tipo.getDescricao());
                } else {
                    Toast.makeText(EditTypeActivity.this, "Erro ao carregar tipo: " + response.message(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Tipo> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(EditTypeActivity.this, "Erro ao carregar tipo: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(EditTypeActivity.this, "Aguarde", "Carregando tipo...", true);
            }
        });
    }

    private void updateType() {
        String descricao = editDescricao.getText().toString().trim();
        int typeId = getIntent().getIntExtra("typeId", -1);
        if (typeId == -1) {
            Toast.makeText(this, "Tipo inválido para exclusão", Toast.LENGTH_LONG).show();
            return;
        }

        if (descricao.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha a descrição", Toast.LENGTH_LONG).show();
            return;
        }
        Tipo tipo = new Tipo();
        tipo.setId(typeId);
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
                    Toast.makeText(EditTypeActivity.this, "Tipo atualizado com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditTypeActivity.this, "Erro ao atualizar tipo: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(EditTypeActivity.this, "Erro ao atualizar tipo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(EditTypeActivity.this, "Aguarde", "Atualizando tipo...", true);
            }
        });
    }
}
