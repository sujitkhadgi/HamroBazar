package com.shallow.hamrobazar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.shallow.hamrobazar.Adapter.ProductAdapter;
import com.shallow.hamrobazar.Api.UsersAPI;
import com.shallow.hamrobazar.LoginDialog;
import com.shallow.hamrobazar.Model.Products;
import com.shallow.hamrobazar.Model.User;
import com.shallow.hamrobazar.R;
import com.shallow.hamrobazar.Url.Url;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity {


            private CircleImageView imageProfile;
            private RecyclerView recyclerView;
            SwipeRefreshLayout refreshLayout;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_dashboard);

                imageProfile = findViewById(R.id.imgProfile);
                recyclerView = findViewById(R.id.recyclerView);
                refreshLayout = findViewById(R.id.refreshLayout);
                showProducts();
                refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        showProducts();

                    }
                });
                loadCurrentUser();
            }

            @Override
            public void onBackPressed() {
                moveTaskToBack(true);
            }

            private void showProducts() {
                refreshLayout.setRefreshing(false);
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Url.base_url).addConverterFactory(GsonConverterFactory.create()).build();
                UsersAPI usersAPI = retrofit.create(UsersAPI.class);

                Call<List<Products>> proListCall = usersAPI.getAllProducts();

                proListCall.enqueue(new Callback<List<Products>>() {
                    @Override
                    public void onResponse(Call<List<Products>> call, Response<List<Products>> response) {
                        if (!response.isSuccessful()) {
                            refreshLayout.setRefreshing(true);
                            Toast.makeText(DashboardActivity.this, "Error code " + response.code(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<Products> productsList = response.body();
                        ProductAdapter productAdapter = new ProductAdapter(DashboardActivity.this, productsList);
                        recyclerView.setAdapter(productAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(DashboardActivity.this, LinearLayoutManager.HORIZONTAL, false));


                    }

                    @Override
                    public void onFailure(Call<List<Products>> call, Throwable t) {

                        Log.d("Msg", "onFailure" + t.getLocalizedMessage());
                        Toast.makeText(DashboardActivity.this, "Error" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void loadCurrentUser() {
                UsersAPI usersAPI = Url.getInstance().create(UsersAPI.class);
                Call<User> userCall = usersAPI.getUserDetails(Url.token);
                userCall.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, final Response<User> response) {
                        if (!response.isSuccessful()) {
//                    Toast.makeText(DashboardActivity.this, "Code "+response.code(), Toast.LENGTH_SHORT).show();

                            //to show login and registration when user not logged in

                            imageProfile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openDialog();
                                }
                            });

                        } else {
                            String imgPath = Url.imagePath + response.body().getImage();

                            Picasso.get().load(imgPath).into(imageProfile);


                            imageProfile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(DashboardActivity.this, "Full name: " + response.body().getFullName(), Toast.LENGTH_SHORT).show();
                                }

                            });

                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(DashboardActivity.this, "Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                Picasso.get().load(R.drawable.profile).into(imageProfile);
                    }

                });

            }

            private void openDialog() {
                LoginDialog loginDialog = new LoginDialog();

                loginDialog.show(getSupportFragmentManager(), "login dialog");
            }
        }




