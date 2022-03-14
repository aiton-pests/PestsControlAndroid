package com.aiton.pestscontrolandroid.ui.login;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.model.Result;
import com.aiton.pestscontrolandroid.data.model.UcenterMemberOrder;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;
import com.aiton.pestscontrolandroid.utils.SPUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginViewModel extends AndroidViewModel {

    private MutableLiveData<UcenterMemberOrder> loginResult = new MutableLiveData<>();
    private MutableLiveData<Result> result = new MutableLiveData<>();
    SavedStateHandle handle;

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Result> getResult() {
        return result;
    }

    public MutableLiveData<UcenterMemberOrder> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job

        RetrofitUtil.getInstance().getUserService().login(username,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result>() {
                    Disposable disposable;
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Result result) {
                        getResult().setValue(result);
                        if (result.getSuccess()){
//                            Log.e(TAG, "onNext: " + result.toString() );
                            String token = (String) result.getData().get("token");

                            RetrofitUtil.getInstance().getUserService().getMemberInfo(token)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<Result>() {
                                        Disposable disposable;
                                        @Override
                                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                                            disposable = d;
                                        }

                                        @Override
                                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull Result result) {
                                            if (result.getSuccess()){
//                                                Log.e(TAG, "onNext: " + result.toString() );
                                                LinkedTreeMap ltm = (LinkedTreeMap) result.getData().get("userInfo");
                                                Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                                String jsonString = gson.toJson(ltm);
                                                UcenterMemberOrder umm = gson.fromJson(jsonString,UcenterMemberOrder.class);
//                                                umm.setMobile(ltm.get("mobile").toString());
//                                                umm.setPassword(ltm.get("password").toString());
//                                                umm.setAge(((Double)ltm.get("age")).intValue());
//                                                umm.setAvatar(ltm.get("avatar").toString());
//                                                umm.setGmtCreate(ltm.get("gmtCreate").toString());
//                                                umm.setIsDeleted((Boolean) ltm.get("isDeleted"));
//                                                umm.setIsDisabled((Boolean) ltm.get("isDisabled"));
//                                                umm.setOpenid(ltm.get("openid").toString());
//                                                umm.setNickname(ltm.get("nickname").toString());
//                                                umm.setSex(((Double)ltm.get("sex")).intValue());
//                                                umm.setSign(ltm.get("sign").toString());
//                                                umm.setMobile(ltm.get("mobile").toString());
//                                                umm.setMobile(ltm.get("mobile").toString());
//                                                umm.setMobile(ltm.get("mobile").toString());
                                                loginResult.setValue(umm);
                                                SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).setData(AppConstance.UCENTER_MEMBER_MODEL,umm);
                                            }
                                        }

                                        @Override
                                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                            disposable.dispose();
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });


                        }else{
                            getLoginResult().setValue(null);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        disposable.dispose();
                        getResult().setValue(Result.error());
                        if (e.getMessage().equals("HTTP 404 Not Found")){
                            getLoginResult().setValue(null);
                        }else if (e.getMessage().equals("Not Found")){
                            getLoginResult().setValue(null);

                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });


//        UserModel model = new UserModel();
//        model.setId("21");
//        model.setToken("21321eewdfewr23r4e234e31");
//        model.setUsername(username);
//        model.setPassword(password);
//        model.setNickName("AI11191");
        //Result<LoggedInUser> result = loginRepository.login(username, password);
//        RetrofitUtil.getInstance("traffic3.qiter.com.cn","9000").getUserService().login().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<cn.com.qiter.pests.Result>() {
//                    Disposable disposable;
//                    @Override
//                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
//                        disposable = d;
//                    }
//
//                    @Override
//                    public void onNext(cn.com.qiter.pests.@io.reactivex.rxjava3.annotations.NonNull Result result) {
//                        if (result.getSuccess()) {
//                            Map<String, Object> data =  result.getData();
//                            UserModel model = (UserModel) data.get(AppConstance.USER_MODEL);
//                            // TODO 修改内容
//                            SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).setData(AppConstance.USER_MODEL,model);
//                            loginResult.setValue(model);
//                        } else {
//                            loginResult.setValue(null);
//                        }
//                    }
//
//                    @Override
//                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

    }


    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public void logout() {
        loginResult.setValue(null);
        UcenterMemberOrder umm = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.UCENTER_MEMBER_MODEL, UcenterMemberOrder.class);
        if (umm != null){
            SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).removeData(AppConstance.UCENTER_MEMBER_MODEL);
        }
    }
}