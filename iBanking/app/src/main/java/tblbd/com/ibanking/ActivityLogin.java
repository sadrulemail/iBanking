package tblbd.com.ibanking;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ActivityLogin extends AppCompatActivity {
    static final int PERMISSION_READ_STATE = 123;
    HttpClient client;
    HttpResponse response;
    EditText txtUser, txtPassword;
    //TextView txttoken;
    Button btnLogin;
    String TokenID = "";
    TextView txtToken;
    int status;
    ProgressDialog progressDialog;
    String IMEINumber, SIMSerialNumber;
    private SharedPreferenceConfig preferenceConfig;
    HTTPPostLogin mAsyancTask = null;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_STATE: {
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    BasicInfo();
                } else {
                    Toast.makeText(this,
                            "You don't have permission to make the action.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void BasicInfo() {
        TelephonyManager telecomManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEINumber = telecomManager.getDeviceId();
        SIMSerialNumber = telecomManager.getSimSerialNumber();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtUser = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        txtToken = findViewById(R.id.txtview1);
        btnLogin = findViewById(R.id.button_signin);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        if (preferenceConfig.readUserName().length() > 1) {
            //startActivity(new Intent(this, SuccessActivity.class));
            String UserN = preferenceConfig.readUserName().toString();
            txtUser.setText(UserN);
            txtPassword.requestFocus();
            // txtToken.setText(preferenceConfig.readToken().toString());
            //finish();
        }


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            BasicInfo();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
        }
        //Toast.makeText(ActivityLogin.this, IMEINumber+"/"+SIMSerialNumber, Toast.LENGTH_LONG).show();


        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (txtUser.getText().toString().isEmpty()) {
                    Toast.makeText(ActivityLogin.this, "Please type user name.", Toast.LENGTH_SHORT).show();

                } else if (txtPassword.getText().toString().isEmpty()) {
                    Toast.makeText(ActivityLogin.this, "Please type password.", Toast.LENGTH_SHORT).show();

                } else {


                    HttpParams httpParams = new BasicHttpParams();

                    int timeoutSocket = 20000;
                    int timeoutConnection = 20000;

                    HttpConnectionParams.setConnectionTimeout(httpParams, timeoutSocket);
                    HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
                    client = new DefaultHttpClient(httpParams);

                    //For https connection
                    SSLSocketFactory sf = (SSLSocketFactory)client.getConnectionManager()
                            .getSchemeRegistry().getScheme("https").getSocketFactory();
                    sf.setHostnameVerifier(new AllowAllHostnameVerifier());

                    //End https connection

                    String url = getResources().getString(R.string.Parent_Url_https)+"/login2";
                    JSONObject jsonObject = new JSONObject();
                    try {
                        //JSONObject jsonObject = new JSONObject();
                        jsonObject.put("UserID", txtUser.getText().toString());
                        jsonObject.put("Password", txtPassword.getText().toString());
                    } catch (Exception ee) {
                        progressDialog.dismiss();
                    }
                    String JString = jsonObject.toString();

                    try {
                        mAsyancTask = new HTTPPostLogin();
                        mAsyancTask.execute(url, JString);
                        //Thread.sleep(2000);
                        /*
                        if (status == 200) {

                            preferenceConfig.writeToken(TokenID.toString());
                            preferenceConfig.writeUserName(txtUser.getText().toString());
                            txtPassword.setText("");
                            txtUser.setText("");

                            Intent homeDashboard = new Intent(ActivityLogin.this, HomeDashboard.class);
                            homeDashboard.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            homeDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(homeDashboard);

                        }
                        */

                    } catch (Exception exx) {
                        progressDialog.dismiss();
                        Toast.makeText(ActivityLogin.this, exx.toString(), Toast.LENGTH_LONG).show();
                    }


                }
            }
        });
    }

    public class HTTPPostLogin extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ActivityLogin.this);
            progressDialog.setMessage("Please wait...."); // Setting Message
            progressDialog.setTitle("iBanking"); // Setting Title
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {

                String PUrl = arg0[0].toString();
                String JStr = arg0[1].toString();

                HttpPost post = new HttpPost(PUrl);

                StringEntity entity = new StringEntity(JStr);
                post.setEntity(entity);
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");

                response = client.execute(post);
                //status = response.getStatusLine().getStatusCode();
            } catch (Exception exx) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                //Toast.makeText(ActivityLogin.this, exx.toString(), Toast.LENGTH_LONG).show();
            }

            return "";
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

        protected void onPostExecute(String Result) {

            try {
                status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    String json = convertInputStreamToString(response.getEntity().getContent());
                    json = json.replace("[", "");
                    json = json.replace("]", "");
                    JSONObject obj = new JSONObject(json);
                    TokenID = obj.getString("token");
                   // Toast.makeText(ActivityLogin.this, "Login success", Toast.LENGTH_LONG).show();

                    preferenceConfig.writeToken(TokenID.toString());
                    preferenceConfig.writeUserName(txtUser.getText().toString());
                    preferenceConfig.writeIMEI(IMEINumber);
                    preferenceConfig.writeSIMSerial(SIMSerialNumber);
                    txtPassword.setText("");
                    txtUser.setText("");

                    Intent homeDashboard = new Intent(ActivityLogin.this, HomeDashboard.class);
                    homeDashboard.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    homeDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(homeDashboard);

                } else if (status == 503) {
                    Toast.makeText(ActivityLogin.this, "Service Unavailable", Toast.LENGTH_LONG).show();
                } else if (status == 400) {
                    Toast.makeText(ActivityLogin.this, "Bad Request", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ActivityLogin.this, "Undefined Error!", Toast.LENGTH_LONG).show();
                }

            } catch (Exception exx) {
                Toast.makeText(ActivityLogin.this, exx.toString(), Toast.LENGTH_LONG).show();
            }
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            //move activity
            super.onPostExecute(Result);
        }
    }
}
