package com.example.apianimais;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apianimais.ApiService;
import com.example.apianimais.RetrofitClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimalDetailActivity extends AppCompatActivity {

    private TextView textNome, textIdade, textFinalidade, textValor, textTipo, textRaca, textCidade;
    private Button buttonAdopt, buttonBack;

    private Animal animal;

    private ApiService apiService;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detail);

        textNome = findViewById(R.id.textNome);
        textIdade = findViewById(R.id.textIdade);
        textFinalidade = findViewById(R.id.textFinalidade);
        textValor = findViewById(R.id.textValor);
        textTipo = findViewById(R.id.textTipo);
        textRaca = findViewById(R.id.textRaca);
        textCidade = findViewById(R.id.textCidade);

        buttonAdopt = findViewById(R.id.buttonAdopt);
        buttonBack = findViewById(R.id.buttonBack);

        String animalJson = getIntent().getStringExtra("animalJson");
        animal = gson.fromJson(animalJson, Animal.class);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        displayAnimalDetails();

        // Set button text and click behavior based on animal finalidade
        if ("A".equals(animal.getFinalidade())) {
            buttonAdopt.setText("Doar Animal");
            buttonAdopt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    donateAnimal();
                }
            });
        } else if ("D".equals(animal.getFinalidade())) {
            buttonAdopt.setText("Adotar Animal");
            buttonAdopt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adoptAnimal();
                }
            });
        } else {
            // Default behavior: keep existing adoptAnimal delete behavior
            buttonAdopt.setText("Adotar Animal");
            buttonAdopt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adoptAnimal();
                }
            });
        }

        buttonBack.setOnClickListener(v -> finish());
    }

    private void displayAnimalDetails() {
        textNome.setText("Descrição: " + animal.getDescricao());
        textIdade.setText("Idade: " + animal.getIdade());

        String finalidadeStr = "Finalidade: ";
        if (animal.getFinalidade() != null) {
            switch (animal.getFinalidade()) {
                case "D":
                    finalidadeStr += "Doação";
                    break;
                case "A":
                    finalidadeStr += "Adoção";
                    break;
                default:
                    finalidadeStr += animal.getFinalidade();
            }
        } else {
            finalidadeStr += "N/A";
        }
        textFinalidade.setText(finalidadeStr);

        textValor.setText("Valor: " + animal.getValor());

        textTipo.setText("Tipo ID: " + animal.getIdTipo());

        if (animal.getRaca() != null && animal.getRaca().getDescricao() != null) {
            textRaca.setText("Raça: " + animal.getRaca().getDescricao());
        } else {
            textRaca.setText("Raça ID: " + animal.getIdRaca());
        }

        if (animal.getCidade() != null && animal.getCidade().getNome() != null) {
            textCidade.setText("Cidade: " + animal.getCidade().getNome());
        } else {
            textCidade.setText("Cidade ID: " + animal.getIdCidade());
        }
    }

    private void adoptAnimal() {
        Call<Void> call = apiService.deleteAnimal(animal.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AnimalDetailActivity.this, "Animal adotado com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AnimalDetailActivity.this, "Erro ao adotar animal: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AnimalDetailActivity.this, "Erro ao adotar animal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void donateAnimal() {
        Call<Void> call = apiService.deleteAnimal(animal.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AnimalDetailActivity.this, "Animal doado com sucesso!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AnimalDetailActivity.this, "Erro ao doar animal: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AnimalDetailActivity.this, "Erro ao doar animal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
