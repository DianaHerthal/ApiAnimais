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

public class TypeListActivity extends Activity {

    private ListView listViewItems;
    private ProgressDialog progressDialog;
    private List<Tipo> typeList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Gson gson = new Gson();
    private Button buttonCreate;
    private Button buttonBack;
    private Button buttonSelect;
    private Button buttonEdit;
    private Button buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        buttonCreate = findViewById(R.id.buttonCreate);
        buttonBack = findViewById(R.id.buttonBack);
        buttonSelect = findViewById(R.id.buttonSelect);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TypeListActivity.this, CreateTypeActivity.class);
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
                // Select the item without finishing the activity
                selectedPosition = position;
                updateButtonStates();
            }
        });

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Tipo selectedType = typeList.get(selectedPosition);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedId", String.valueOf(selectedType.getId()));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Tipo selectedType = typeList.get(selectedPosition);
                    Intent intent = new Intent(TypeListActivity.this, EditTypeActivity.class);
                    intent.putExtra("typeId", selectedType.getId());
                    startActivityForResult(intent, 1);
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Tipo selectedType = typeList.get(selectedPosition);
                    deleteType(selectedType.getId());
                }
            }
        });

        fetchTypes();
    }

    private int selectedPosition = -1;

    private void updateButtonStates() {
        boolean enabled = selectedPosition != -1;
        buttonSelect.setEnabled(enabled);
        buttonEdit.setEnabled(enabled);
        buttonDelete.setEnabled(enabled);
    }

    private void deleteType(int typeId) {
        Call<Void> call = RetrofitClient.getClient().create(ApiService.class).deleteType(typeId);
        call.enqueue(new Callback<Void>() {
            ProgressDialog progressDialog;

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Toast.makeText(TypeListActivity.this, "Tipo deletado com sucesso!", Toast.LENGTH_LONG).show();
                    fetchTypes();
                } else {
                    Toast.makeText(TypeListActivity.this, "Erro ao deletar tipo: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(TypeListActivity.this, "Erro ao deletar tipo: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

            public void onStart() {
                progressDialog = ProgressDialog.show(TypeListActivity.this, "Aguarde", "Deletando tipo...", true);
            }
        });
    }

    private void fetchTypes() {
        progressDialog = ProgressDialog.show(TypeListActivity.this, "Loading", "Fetching types...", true);
        Call<List<Tipo>> call = RetrofitClient.getClient().create(ApiService.class).getTypes();
        call.enqueue(new Callback<List<Tipo>>() {
            @Override
            public void onResponse(Call<List<Tipo>> call, Response<List<Tipo>> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful() && response.body() != null) {
                    typeList = response.body();
                    updateTypeList();
                } else {
                    Toast.makeText(TypeListActivity.this, "Error fetching types: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tipo>> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(TypeListActivity.this, "Error fetching types: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }




    private void updateTypeList() {
        try {
            adapter.clear();
            for (Tipo type : typeList) {
                adapter.add("ID: " + type.getId() + ", Descrição: " + type.getDescricao());
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Error processing type data", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchTypes();
        }
    }

    private class FetchTypesTask extends AsyncTask<Void, Void, String> {
        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TypeListActivity.this, "Loading", "Fetching types...", true);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://argo.td.utfpr.edu.br/pets/ws/tipo");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new Exception("GET request failed with code: " + responseCode);
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                conn.disconnect();

                return response.toString();
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (exception != null) {
                Toast.makeText(TypeListActivity.this, "Error fetching types: " + exception.getMessage(), Toast.LENGTH_LONG).show();
            } else {
                updateTypeList(result);
            }
        }
    }

    private void updateTypeList(String json) {
        try {
            Type listType = new TypeToken<List<Tipo>>() {}.getType();
            typeList = gson.fromJson(json, listType);

            adapter.clear();
            for (Tipo type : typeList) {
                adapter.add("ID: " + type.getId() + ", Descrição: " + type.getDescricao());
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Error processing type data", Toast.LENGTH_LONG).show();
        }
    }
}
