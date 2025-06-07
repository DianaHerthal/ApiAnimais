package com.example.apianimais;

import com.example.apianimais.Animal;
import com.example.apianimais.Raca;
import com.example.apianimais.Cidade;
import com.example.apianimais.Tipo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Animals
    @GET("animal")
    Call<List<Animal>> getAnimals(
        @Query("finalidade") String finalidade,
        @Query("id") String id,
        @Query("idTipo") String idTipo,
        @Query("idRaca") String idRaca,
        @Query("idCidade") String idCidade
    );

    @POST("animal")
    Call<Void> createAnimal(@Body Animal animal);

    @PUT("animal/{id}")
    Call<Void> updateAnimal(@Path("id") int id, @Body Animal animal);

    @DELETE("animal/{id}")
    Call<Void> deleteAnimal(@Path("id") int id);

    // Breeds
    @GET("raca")
    Call<List<Raca>> getBreeds();

    @GET("raca/{id}")
    Call<Raca> getBreed(@Path("id") int id);

    @POST("raca")
    Call<Void> createBreed(@Body Raca raca);

    @PUT("raca/{id}")
    Call<Void> updateBreed(@Path("id") int id, @Body Raca raca);

    @DELETE("raca/{id}")
    Call<Void> deleteBreed(@Path("id") int id);

    // Cities
    @GET("cidade")
    Call<List<Cidade>> getCities();

    @GET("cidade/{id}")
    Call<Cidade> getCity(@Path("id") int id);

    @POST("cidade")
    Call<Void> createCity(@Body Cidade cidade);

    @PUT("cidade/{id}")
    Call<Void> updateCity(@Path("id") int id, @Body Cidade cidade);

    @DELETE("cidade/{id}")
    Call<Void> deleteCity(@Path("id") int id);

    // Types
    @GET("tipo")
    Call<List<Tipo>> getTypes();

    @GET("tipo/{id}")
    Call<Tipo> getType(@Path("id") int id);

    @POST("tipo")
    Call<Void> createType(@Body Tipo tipo);

    @PUT("tipo/{id}")
    Call<Void> updateType(@Path("id") int id, @Body Tipo tipo);

    @DELETE("tipo/{id}")
    Call<Void> deleteType(@Path("id") int id);
}
