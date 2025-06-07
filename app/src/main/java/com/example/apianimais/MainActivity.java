package com.example.apianimais;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_DONATE = 1;
    private static final int REQUEST_CODE_FILTER = 2;
    private static final int REQUEST_CODE_DETAIL = 3;

    private ListView listViewAnimals;
    private Button buttonDonateAnimal, buttonFilter, buttonAdoptAnimal;
    // Removed buttonBack as it does not exist in layout

    private List<Animal> animalList = new ArrayList<>();
    private ArrayAdapter<String> animalAdapter;

    private Gson gson = new Gson();

    private String filterIdAnimal = "";
    private String filterIdTipo = "";
    private String filterIdRaca = "";
    private String filterIdCidade = "";
    private String filterDdd = "";
    private String filterFinalidade = "";
    private String idadeInicial = "";
    private String idadeFinal = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewAnimals = findViewById(R.id.listViewAnimals);
        buttonDonateAnimal = findViewById(R.id.buttonDonateAnimal);
        buttonAdoptAnimal = findViewById(R.id.buttonAdoptAnimal);
        buttonFilter = findViewById(R.id.buttonFilter);
        // Removed buttonBack initialization as it does not exist in layout

        animalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, new ArrayList<>());
        listViewAnimals.setAdapter(animalAdapter);
        listViewAnimals.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        buttonDonateAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DonateAnimalActivity.class);
                intent.putExtra("finalidade", "D"); // Donation
                startActivityForResult(intent, REQUEST_CODE_DONATE);
            }
        });

        buttonAdoptAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DonateAnimalActivity.class);
                intent.putExtra("finalidade", "A"); // Adoption
                startActivity(intent);
            }
        });

        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FILTER);
            }
        });

        // Removed buttonBack click listener as buttonBack does not exist in layout

        listViewAnimals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Animal selectedAnimal = animalList.get(position);
                Intent intent = new Intent(MainActivity.this, AnimalDetailActivity.class);
                intent.putExtra("animalJson", gson.toJson(selectedAnimal));
                startActivityForResult(intent, REQUEST_CODE_DETAIL);
            }
        });

        loadAnimals();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Não é possível usar o app sem conexão com a internet.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void loadAnimals() {
        StringBuilder query = new StringBuilder("animal");
        boolean hasParam = false;

        if (filterFinalidade != null && !filterFinalidade.isEmpty()) {
            query.append(hasParam ? "&" : "?");
            query.append("finalidade=").append(filterFinalidade);
            hasParam = true;
        }
        if (!filterIdAnimal.isEmpty()) {
            query.append(hasParam ? "&" : "?");
            query.append("id=").append(filterIdAnimal);
            hasParam = true;
        }
        if (!filterIdTipo.isEmpty()) {
            query.append(hasParam ? "&" : "?");
            query.append("idTipo=").append(filterIdTipo);
            hasParam = true;
        }
        if (!filterIdRaca.isEmpty()) {
            query.append(hasParam ? "&" : "?");
            query.append("idRaca=").append(filterIdRaca);
            hasParam = true;
        }
        if (!filterIdCidade.isEmpty()) {
            query.append(hasParam ? "&" : "?");
            query.append("idCidade=").append(filterIdCidade);
            hasParam = true;
        }
        if (!filterDdd.isEmpty()) {
            query.append(hasParam ? "&" : "?");
            query.append("ddd=").append(filterDdd);
            hasParam = true;
        }

        final String resource = query.toString();

        new FetchAnimalsTask().execute(resource);
    }

    private class FetchAnimalsTask extends AsyncTask<String, Void, String> {
        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Loading", "Fetching animals...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String resource = params[0];
            try {
                URL url = new URL("http://argo.td.utfpr.edu.br/pets/ws/" + resource);
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
                Toast.makeText(MainActivity.this, "Erro ao buscar animais: " + exception.getMessage(), Toast.LENGTH_LONG).show();
            } else {
                updateAnimalList(result);
                applyAgeFilter();
            }
        }
    }

    private void updateAnimalList(String json) {
        try {
            Type listType = new TypeToken<List<Animal>>() {
            }.getType();
            animalList = gson.fromJson(json, listType);

            applyAgeFilter();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao processar dados dos animais", Toast.LENGTH_LONG).show();
        }
    }

    private void applyAgeFilter() {
        List<Animal> filteredList;
        if (idadeInicial.isEmpty() && idadeFinal.isEmpty()) {
            // No age filter applied, show all animals
            filteredList = new ArrayList<>(animalList);
        } else {
            int idadeInicialVal = idadeInicial.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(idadeInicial);
            int idadeFinalVal = idadeFinal.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(idadeFinal);

            filteredList = new ArrayList<>();
            for (Animal animal : animalList) {
                int idade = animal.getIdade(); // Assuming Animal class has getIdade() method
                if (idade >= idadeInicialVal && idade <= idadeFinalVal) {
                    filteredList.add(animal);
                }
            }
        }

        // Apply DDD filter client-side
        if (!filterDdd.isEmpty()) {
            List<Animal> dddFilteredList = new ArrayList<>();
            for (Animal animal : filteredList) {
                if (animal.getCidade() != null && animal.getCidade().getDdd() != null && animal.getCidade().getDdd().equals(filterDdd)) {
                    dddFilteredList.add(animal);
                }
            }
            filteredList = dddFilteredList;
        }

        animalAdapter.clear();
        for (Animal animal : filteredList) {
            String displayText = animal.getDescricao() + " - Tipo: " + animal.getIdTipo() + ", Raça: " + animal.getIdRaca() + ", Cidade: " + animal.getIdCidade() + ", DDD: " + animal.getDdd();
            animalAdapter.add(displayText);
        }
        animalAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_DONATE || requestCode == REQUEST_CODE_DETAIL) {
                loadAnimals();
            } else if (requestCode == REQUEST_CODE_FILTER && data != null) {
                filterIdAnimal = data.getStringExtra("idAnimal");
                filterIdTipo = data.getStringExtra("idTipo");
                filterIdRaca = data.getStringExtra("idRaca");
                filterIdCidade = data.getStringExtra("idCidade");
                filterDdd = data.getStringExtra("ddd");
                filterFinalidade = data.getStringExtra("finalidade");
                idadeInicial = data.getStringExtra("idadeInicial");
                idadeFinal = data.getStringExtra("idadeFinal");
                loadAnimals();
            }
        }
    }
}
