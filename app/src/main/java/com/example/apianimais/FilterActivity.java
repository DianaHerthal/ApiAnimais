package com.example.apianimais;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class FilterActivity extends Activity {

    private EditText editFilterIdAnimal, editFilterIdTipo, editFilterIdRaca, editFilterIdCidade, editFilterDdd;
    private EditText editFilterIdadeInicial, editFilterIdadeFinal;
    private Button buttonApplyFilter, buttonFetchCities, buttonFetchBreeds, buttonFetchTypes;
    private android.widget.Spinner spinnerFinalidade;
    private Button buttonBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        editFilterIdAnimal = findViewById(R.id.editFilterIdAnimal);
        editFilterIdTipo = findViewById(R.id.editFilterIdTipo);
        editFilterIdRaca = findViewById(R.id.editFilterIdRaca);
        editFilterIdCidade = findViewById(R.id.editFilterIdCidade);
        editFilterDdd = findViewById(R.id.editFilterDdd);
        editFilterIdadeInicial = findViewById(R.id.editFilterIdadeInicial);
        editFilterIdadeFinal = findViewById(R.id.editFilterIdadeFinal);
        spinnerFinalidade = findViewById(R.id.spinnerFinalidade);
        buttonApplyFilter = findViewById(R.id.buttonApplyFilter);
        buttonFetchCities = findViewById(R.id.buttonFetchCities);
        buttonFetchBreeds = findViewById(R.id.buttonFetchBreeds);
        buttonFetchTypes = findViewById(R.id.buttonFetchTypes);
        buttonBack = findViewById(R.id.buttonBack);

        // Setup spinner with options A and D
        android.widget.ArrayAdapter<CharSequence> adapter = android.widget.ArrayAdapter.createFromResource(this,
                R.array.finalidade_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFinalidade.setAdapter(adapter);

        buttonApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("idAnimal", editFilterIdAnimal.getText().toString().trim());
                resultIntent.putExtra("idTipo", editFilterIdTipo.getText().toString().trim());
                resultIntent.putExtra("idRaca", editFilterIdRaca.getText().toString().trim());
                resultIntent.putExtra("idCidade", editFilterIdCidade.getText().toString().trim());
                resultIntent.putExtra("ddd", editFilterDdd.getText().toString().trim());
                resultIntent.putExtra("finalidade", spinnerFinalidade.getSelectedItem().toString());
                resultIntent.putExtra("idadeInicial", editFilterIdadeInicial.getText().toString().trim());
                resultIntent.putExtra("idadeFinal", editFilterIdadeFinal.getText().toString().trim());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        buttonFetchCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterActivity.this, CityListActivity.class);
                startActivityForResult(intent, 1001);
            }
        });

        buttonFetchBreeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterActivity.this, BreedListActivity.class);
                startActivityForResult(intent, 1002);
            }
        });

        buttonFetchTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterActivity.this, TypeListActivity.class);
                startActivityForResult(intent, 1003);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1001) {
                String selectedId = data.getStringExtra("selectedId");
                editFilterIdCidade.setText(selectedId);
            } else if (requestCode == 1002) {
                String selectedId = data.getStringExtra("selectedId");
                editFilterIdRaca.setText(selectedId);
            } else if (requestCode == 1003) {
                String selectedId = data.getStringExtra("selectedId");
                editFilterIdTipo.setText(selectedId);
            }
        }
    }
}
