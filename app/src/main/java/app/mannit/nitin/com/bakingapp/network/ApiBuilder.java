package app.mannit.nitin.com.bakingapp.network;

import java.util.List;

import app.mannit.nitin.com.bakingapp.models.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiBuilder {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getListOfRecipes();
}
