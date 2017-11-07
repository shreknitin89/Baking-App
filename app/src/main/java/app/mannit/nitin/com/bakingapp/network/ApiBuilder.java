package app.mannit.nitin.com.bakingapp.network;

import app.mannit.nitin.com.bakingapp.models.Baking;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiBuilder {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<Baking> getListOfRecipes();
}
