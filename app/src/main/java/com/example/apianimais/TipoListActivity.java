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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TipoListActivity extends Activity {

    private ListView listViewItems;
    private ProgressDialog progressDialog;
    private List<Tipo> tipoList;
    private ArrayAdapter<String> adapter;
    private Button buttonCreate;
    private Button buttonSelect;
    private Button buttonEdit;
    private Button buttonDelete;
    private Button buttonBack;

    private ApiService apiService;

    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        buttonCreate = findViewById(R.id.buttonCreate);
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

        listViewItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                updateButtonStates();
                return true;
            }
        });

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Tipo selectedTipo = tipoList.get(selectedPosition);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedId", String.valueOf(selectedTipo.getId()));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Tipo selectedTipo = tipoList.get(selectedPosition);
                    Intent intent = new Intent(TipoListActivity.this, CreateTypeActivity.class);
                    intent.putExtra("typeId", selectedTipo.getId());
                    startActivity(intent);
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Tipo selectedTipo = tipoList.get(selectedPosition);
                    deleteTipo(selectedTipo.getId());
                }
            }
        });

        fetchTipos();
    }

    private void updateButtonStates() {
        boolean enabled = selectedPosition != -1;
        buttonSelect.setEnabled(enabled);
        buttonEdit.setEnabled(enabled);
        buttonDelete.setEnabled(enabled);
    }

    private void fetchTipos() {
        progressDialog = ProgressDialog.show(TipoListActivity.this, "Loading", "Fetching types...", true);
        Call<List<Tipo>> call = apiService.getTypes();
        call.enqueue(new Callback<List<Tipo>>() {
            @Override
            public void onResponse(Call<List<Tipo>> call, Response<List<Tipo>> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful() && response.body() != null) {
                    tipoList = response.body();
                    updateTipoList();
                } else {
                    Toast.makeText(TipoListActivity.this, "Error fetching types: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tipo>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(TipoListActivity.this, "Error fetching types: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTipoList() {
        try {
            adapter.clear();
            for (Tipo tipo : tipoList) {
                adapter.add("ID: " + tipo.getId() + ", Descrição: " + tipo.getDescricao());
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Error processing type data", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteTipo(int tipoId) {
        Call<Void> call = apiService.deleteType(tipoId);
        call.enqueue(new Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(TipoListActivity.this, "Tipo deletado com sucesso!", Toast.LENGTH_LONG).show();
                    fetchTipos();
                } else {
                    Toast.makeText(TipoListActivity.this, "Erro ao deletar tipo: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(TipoListActivity.this, "Erro ao deletar tipo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(TipoListActivity.this, "Aguarde", "Deletando tipo...", true);
            }
        });
    }
}
