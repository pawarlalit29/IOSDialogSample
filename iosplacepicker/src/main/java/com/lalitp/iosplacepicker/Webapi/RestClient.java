package com.lalitp.iosplacepicker.Webapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.lalitp.iosplacepicker.BuildConfig;
import com.lalitp.iosplacepicker.Utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.google.android.gms.cast.CastRemoteDisplayLocalService.getInstance;
import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

/**
 * Created by atulsia on 19/2/16.
 */
public class RestClient {

    public static final String MAP_SERVER_APIURL = "https://maps.googleapis.com/maps/api/";
    public static final String SERVER_ERROR = "Servers cannot be reached.\n Please try again";
    private static String TAG = RestClient.class.getSimpleName();

    private static final String CACHE_CONTROL = "Cache-Control";

    private static RestInterface apiService = null;


    public static RestInterface getMapClient() {
        return getMapRetrofit().create(RestInterface.class);
    }

    private static Retrofit getMapRetrofit() {
        GsonBuilder gBuilder = new GsonBuilder();
        gBuilder.registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory<>());
        Gson gson = gBuilder.create();

        return new Retrofit.Builder()
                .baseUrl(MAP_SERVER_APIURL)
                .client(provideOkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    /*****************************
     * Interceptor
     *********************************/

    private static OkHttpClient provideOkHttpClient() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .cache(provideCache())
                .build();

        return okHttpClient;
    }

    private static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(getInstance().getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            LogUtils.LOGE(e, "Could not create Cache!");
        }
        return cache;
    }

    private static HttpLoggingInterceptor provideHttpLoggingInterceptor() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        LogUtils.LOGD("provideHttpLoggingInterceptor", message);
                    }
                });
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HEADERS : NONE);
        return logging;
    }

    public static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(1, TimeUnit.SECONDS)
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    public static Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
                return chain.proceed(request);
            }
        };
    }

    public static class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringAdapter();
        }
    }

    public static class StringAdapter extends TypeAdapter<String> {
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                System.out.println("null  updated to space");

                return "";
            }
            return reader.nextString();
        }

        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }

    public static final String ErrorMessage = "Servers cannot be reached. Please try again";

    public static String parseErrorThrow(Throwable throwable) {

        if (throwable != null &&
                isNotNullOrEmpty(throwable.getMessage())) {
            LogUtils.LOGD(TAG, throwable.getMessage());
            return throwable.getMessage();
        } else
            return SERVER_ERROR;

    }

    public static boolean isNotNullOrEmpty(String string) {

        if (string != null &&
                string.length() != 0 &&
                !string.equalsIgnoreCase("") &&
                !string.equalsIgnoreCase(" ") &&
                !string.equalsIgnoreCase("\n")) {
            return true;
        } else {
            return false;
        }
    }

}
