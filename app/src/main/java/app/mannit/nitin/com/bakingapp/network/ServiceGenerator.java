package app.mannit.nitin.com.bakingapp.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nitingeetasagardasari on 11/6/17 for the project Baking-App.
 */

public class ServiceGenerator {

    private static int sConnectionTimeout = 60;
    private static int sReadTimeout = 60;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    // No need to instantiate this class.
    private ServiceGenerator() {
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        return createService(serviceClass, baseUrl, null);
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl, Interceptor interceptor) {

        Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = httpClient.connectTimeout(sConnectionTimeout, TimeUnit.SECONDS)
                .readTimeout(sReadTimeout, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(serviceClass);
    }
}
