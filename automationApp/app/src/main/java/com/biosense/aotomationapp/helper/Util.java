package com.biosense.aotomationapp.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.biosense.aotomationapp.model.Json_model;
import com.biosense.aotomationapp.model.info_model;
import org.apache.http.conn.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class Util {

    public Util(Context mContext) {
        this.mContext = mContext;
    }
    Context mContext;
    public String sendJSONPOST2(String URL,String node,String Key1,String Value1,String Key2,String Value2) throws Exception {
        int code = 0;
        String str = null;
        HttpsURLConnection connection = null;
        InputStream is = null;
        BufferedReader in = null;
        OutputStreamWriter out = null;

        try {
            KeyStore trustStore = null;
            try {
                trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            } catch (KeyStoreException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                trustStore.load(null, null);
            } catch (NoSuchAlgorithmException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (CertificateException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            MySSLSocketFactory sf = null;
            try {
                sf = new MySSLSocketFactory(trustStore);
            } catch (KeyManagementException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (UnrecoverableKeyException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchAlgorithmException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (KeyStoreException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                SSLContext ctx = null;
                try {
                    ctx = SSLContext.getInstance("SSL");
                } catch (NoSuchAlgorithmException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustStore);
                ctx.init(null, tmf.getTrustManagers(), null);
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                // set your desired log level
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                HttpLoggingInterceptor.Logger fileLogger = new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String s) {
                       // writeFile(s);
                    }
                };
                Interceptor fileLoggerInterceptor = new HttpLoggingInterceptor(fileLogger);

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.sslSocketFactory(sf, (X509TrustManager) trustAllCerts[0]);
                builder.followSslRedirects(true);
                builder.readTimeout(1, TimeUnit.MINUTES);
                builder.writeTimeout(1, TimeUnit.MINUTES);
                builder.hostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                builder.addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        // Request customization: add request headers
                        Request.Builder requestBuilder = original.newBuilder()
                                .addHeader("Accept", "application/json");

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
                builder.addInterceptor(logging);
                builder.addInterceptor(fileLoggerInterceptor);

                OkHttpClient client = builder.build();


                retrofit2.Retrofit restAdapter = new retrofit2.Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();


                URLTestInfo PayLoadInfo = restAdapter.create(URLTestInfo.class);

                Json_model jsonModel =new Json_model();

                jsonModel.setId(Key1);
                jsonModel.setValue(Value1);

                Call<Json_model> call = PayLoadInfo.profilePicture(node,jsonModel);
                Response<Json_model> patientInfo_modelResponse = call.execute();
                System.out.println("PRINTING " + patientInfo_modelResponse.code());
                return patientInfo_modelResponse.code()+"";


            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;

    }

    public void setDynamic(String colName, String data, List<info_model> list) {
        info_model model = new info_model();
        if(colName.contains("rawid"))
        {
            model.setDbCol(colName);
            model.setRawid(Integer.parseInt(data));
            list.add(model);
        }
        else {
            model.setDbCol(colName);
            model.setData(data);
            list.add(model);
        }
    }


    public String generateUUID() {
        String finalUID = "" + System.currentTimeMillis();
        return finalUID;
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }


}
