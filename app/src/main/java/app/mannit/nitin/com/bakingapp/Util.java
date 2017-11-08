package app.mannit.nitin.com.bakingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import app.mannit.nitin.com.bakingapp.models.Recipe;
import app.mannit.nitin.com.bakingapp.network.ApiBuilder;
import app.mannit.nitin.com.bakingapp.network.ServiceGenerator;
import retrofit2.Call;

/**
 * Created by nitingeetasagardasari on 11/5/17 for the project BakingApp.
 */

public class Util {
    public static String loadJSONFromAsset(Context activity) {
        String json;
        try {
            InputStream is = activity.getAssets().open("recipe.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnected();
    }

    public static Call<List<Recipe>> loadDataFromNetwork(Context activity) {
        if (isOnline(activity)) {
            return ServiceGenerator.createService(ApiBuilder.class, Constants.BASE_URL).getListOfRecipes();
        }
        return null;
    }
}
