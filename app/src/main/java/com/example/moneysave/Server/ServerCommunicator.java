package com.example.moneysave.Server;

import android.util.Log;

import com.example.moneysave.Server.boundaries.InstanceBoundary;
import com.example.moneysave.Server.boundaries.NewUserBoundary;
import com.example.moneysave.Server.boundaries.UserBoundary;
import com.example.moneysave.Server.server_interface.MyApiServer;
import com.example.moneysave.tools.DataManager;
import com.example.moneysave.tools.MyServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ServerCommunicator  {
    private static ServerCommunicator serverCommunicator = new ServerCommunicator();
    private final int STATUS_OK = 200;
    private final int FOUND = 302;
    private final int NOT_FOUND = 404;
    private MyApiServer myApiServer = RetrofitService.getInstance().getRetrofit().create(MyApiServer.class);

    private Callback<UserBoundary> getUserDetails_callback = new Callback<UserBoundary>() {
        @Override
        public void onResponse(Call<UserBoundary> call, Response<UserBoundary> response) {
            Log.d("myLog", response.code() + "");
            if(response.code() == STATUS_OK){
                Log.d("myLog", response.body().toString());
                DataManager.getDataManager().setUser(response.body());
            }
            else if(response.code() == FOUND){
                MyServices.getInstance().makeToast("User already exists");
                DataManager.getDataManager().failed(FOUND);
            }
            else {
                MyServices.getInstance().makeToast("failed - Wrong details");
                DataManager.getDataManager().failed(response.code());
            }
        }
        @Override
        public void onFailure(Call<UserBoundary> call, Throwable t) {
            MyServices.getInstance().makeToast(t.getMessage());
            Log.d("myLog", t.getMessage());
            DataManager.getDataManager().failed(0);
        }
    };

    private Callback<InstanceBoundary[]> searchInstancesByName_callback = new Callback<InstanceBoundary[]>() {
        @Override
        public void onResponse(Call<InstanceBoundary[]> call, Response<InstanceBoundary[]> response) {
            Log.d("myLog", response.code() + "");
            if(response.code() == STATUS_OK){
                DataManager.getDataManager().getInstancesFromServerByName(response.body());
            }
            else{
                DataManager.getDataManager().failed(response.code());
            }
        }
        @Override
        public void onFailure(Call<InstanceBoundary[]> call, Throwable t) {
            MyServices.getInstance().makeToast(t.getMessage());
            Log.d("myLog", t.getMessage());
            DataManager.getDataManager().failed(0);
        }
    };
    private Callback<InstanceBoundary[]> find_another_User_callback = new Callback<InstanceBoundary[]>() {
        @Override
        public void onResponse(Call<InstanceBoundary[]> call, Response<InstanceBoundary[]> response) {
            Log.d("myLog", response.code() + "");
            if(response.code() == STATUS_OK){
                DataManager.getDataManager().find_another_User(response.body());
            }
        }
        @Override
        public void onFailure(Call<InstanceBoundary[]> call, Throwable t) {
            MyServices.getInstance().makeToast(t.getMessage());
            Log.d("myLog", t.getMessage());
        }
    };
    private Callback<InstanceBoundary> instance_callback = new Callback<InstanceBoundary>() {
        @Override
        public void onResponse(Call<InstanceBoundary> call, Response<InstanceBoundary> response) {
            Log.d("myLog", response.code() + "");
            if(response.code() == STATUS_OK){
                DataManager.getDataManager().getInstance(response.body());
            }
            else{
                MyServices.getInstance().makeToast("Wrong details");
                DataManager.getDataManager().failed(response.code());
            }
        }
        @Override
        public void onFailure(Call<InstanceBoundary> call, Throwable t) {
            MyServices.getInstance().makeToast(t.getMessage());
            Log.d("myLog", t.getMessage());
            DataManager.getDataManager().failed(0);
        }
    };
    private Callback<Void> updateInstance_callback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            Log.d("myLog", response.code() + "");
            if(response.code() != STATUS_OK){
                MyServices.getInstance().makeToast("Update Failed");
            }
        }
        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            MyServices.getInstance().makeToast(t.getMessage());
            Log.d("myLog", t.getMessage());
            DataManager.getDataManager().failed(0);
        }
    };


    private ServerCommunicator() {

    }


    public void getUserDetails(String userDomain, String userEmail) { //"2022b.Lilach.Laniado", "rogygggyn@gmail.com"
        myApiServer.getUserDetails(userDomain , userEmail )
                .enqueue(getUserDetails_callback);
    }


    public void createUser(NewUserBoundary newUserBoundary) {
        myApiServer.createUser(newUserBoundary)
                .enqueue(getUserDetails_callback);
    }



    public void updateInstanceDetails( String instanceDomain,  String instanceId,  String userDomain,String userEmail,InstanceBoundary instanceBoundary){
        myApiServer.updateInstanceDetails(instanceDomain, instanceId, userDomain, userEmail, instanceBoundary)
                .enqueue(updateInstance_callback);
    }



    public void getInstanceDetails(String instanceDomain, String instanceId , String userDomain, String userEmail){
        myApiServer.getInstanceDetails(instanceDomain,instanceId, userDomain, userEmail)
                .enqueue(instance_callback);
    }



    public void createInstance(InstanceBoundary instanceBoundary) {
        myApiServer.createInstance(instanceBoundary)
                .enqueue(instance_callback);
    }

    public void  searchInstancesByName(String name, String userDomain, String userEmail){
        myApiServer.searchInstancesByName(name , userDomain, userEmail)
                .enqueue(searchInstancesByName_callback);
    }


    public static ServerCommunicator getInstance(){
        return serverCommunicator;
    }


    public void searchAnotherUserDetails(String name, String domain, String email) {
        myApiServer.searchInstancesByName(name , domain, email)
                .enqueue(find_another_User_callback);
    }
}
