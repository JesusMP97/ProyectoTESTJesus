package org.izv.proyecto.model.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.izv.proyecto.model.data.Empleado;
import org.izv.proyecto.model.data.Producto;
import org.izv.proyecto.model.rest.EmployeeClient;
import org.izv.proyecto.model.rest.InvoiceClient;
import org.izv.proyecto.model.rest.ProductClient;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductRepository implements Repository.Data<Producto> {
    private static final long EMPTY = 0;
    private static final String TAG = ProductRepository.class.getName();
    private MutableLiveData<List<Producto>> all = new MutableLiveData<>();
    private ProductClient client;
    private Retrofit retrofit;
    private Repository.OnFailureListener onFailureListener;

    public ProductRepository(String url) {
        retrieveApiClient(url);
        fetchAll();
    }

    private void retrieveApiClient(String url) {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + url + "/ProyectoFinal/public/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        client = retrofit.create(ProductClient.class);
    }

    public void setUrl(String url) {
        retrieveApiClient(url);
    }

    @Override
    public void add(Producto object) {
        Call<Long> call = client.post(object);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                long result = response.body();
                Log.v(TAG, String.valueOf(result));
                if (result > EMPTY) {
                    fetchAll();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    onFailureListener.onConnectionFailure();
                }
            }
        });
    }

    @Override
    public void delete(Producto object) {
        Call<Long> call = client.delete(object.getId());
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                long result = response.body();
                Log.v(TAG, String.valueOf(result));
                if (result > EMPTY) {
                    fetchAll();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    onFailureListener.onConnectionFailure();
                }
            }
        });
    }

    @Override
    public void fetchAll() {
        Call<List<Producto>> call = client.getAll();
        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                Log.v(TAG, response.raw().toString());
                all.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    onFailureListener.onConnectionFailure();
                }
                all = new MutableLiveData<>();
            }
        });
    }

    @Override
    public MutableLiveData<List<Producto>> getAll() {
        fetchAll();
        return all;
    }

    @Override
    public void update(Producto object) {
        Call<Long> call = client.put(object.getId(), object);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                long result = response.body();
                Log.v(TAG, String.valueOf(result));
                if (result > EMPTY) {
                    fetchAll();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    onFailureListener.onConnectionFailure();
                }
            }
        });
    }

    public void setOnFailureListener(Repository.OnFailureListener onFailureListener) {
        this.onFailureListener = onFailureListener;
    }

    @Override
    public void upload(File file) {

    }
}
