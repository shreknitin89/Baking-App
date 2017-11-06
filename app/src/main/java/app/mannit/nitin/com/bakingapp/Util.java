package app.mannit.nitin.com.bakingapp;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

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
}
