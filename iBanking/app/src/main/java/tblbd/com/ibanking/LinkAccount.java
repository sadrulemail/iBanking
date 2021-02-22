package tblbd.com.ibanking;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LinkAccount extends AppCompatActivity {
    EditText tAccount, tOTP;
    TextView tAccountName;
    Button bConformAccount, bConformLink;
    HttpClient client;
    HttpResponse response;
    int status;
    String CName = "";
    ProgressDialog progressDialog1;
    String IMEINumber, SIMSerialNumber;
    private SharedPreferenceConfig preferenceConfig;
    public JSONObject jsonObject;
    //HTTPPost myAsyncTask = null;
    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItemLinkAccount> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_account);
        recyclerView=findViewById(R.id.RecyclerViewLinkAccountList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems=new ArrayList<>();

        new HTTPPostSaveLinkAccountList().execute();

        tAccount = (EditText) findViewById(R.id.txtaccountnumber);
        tOTP = (EditText) findViewById(R.id.txtOTP);
        tAccountName = (TextView) findViewById(R.id.txtAccountName);
        bConformAccount = (Button) findViewById(R.id.btn_ConformAccount);
        bConformLink = (Button) findViewById(R.id.btn_ConformLink);
        tOTP.setEnabled(false);
        bConformLink.setEnabled(false);
        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        bConformAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tAccount.getText().toString().isEmpty()) {
                    Toast.makeText(LinkAccount.this, "Enter account number.", Toast.LENGTH_LONG).show();
                    return;
                }

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

                String url = getResources().getString(R.string.Parent_Url_https)+"/api/CBSInfo/CBSAccountInfo";
                JSONObject jsonObjectVal = new JSONObject();
                try {
                    jsonObjectVal.put("AccountNo", tAccount.getText().toString());
                } catch (Exception ee) {
                    progressDialog1.dismiss();
                }
                String JString = jsonObjectVal.toString();

                try {
                    new HTTPPostCheckAccount().execute(url, JString);

                } catch (Exception exx) {
                    progressDialog1.dismiss();
                    Toast.makeText(LinkAccount.this, exx.toString(), Toast.LENGTH_LONG).show();
                }

            }
        });
        bConformLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tOTP.getText().toString().isEmpty()) {
                    Toast.makeText(LinkAccount.this, "Enter OTP code.", Toast.LENGTH_LONG).show();

                } else if (tAccountName.getText().toString().isEmpty()) {
                    Toast.makeText(LinkAccount.this, "Account name missing", Toast.LENGTH_LONG).show();
                } else if (tAccount.getText().toString().isEmpty()) {
                    Toast.makeText(LinkAccount.this, "Account number missing", Toast.LENGTH_LONG).show();
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

                    String url = getResources().getString(R.string.Parent_Url_https)+"/api/CBSInfo/LinkAccount";
                    JSONObject jsonObjectVal = new JSONObject();
                    try {
                        //JSONObject jsonObject = new JSONObject();
                        jsonObjectVal.put("AccountNo", tAccount.getText().toString());
                        jsonObjectVal.put("UserID", preferenceConfig.readUserName().toString());
                        jsonObjectVal.put("OTP", tOTP.getText().toString());
                        jsonObjectVal.put("IMEINumber", preferenceConfig.readIMEI().toString());
                        jsonObjectVal.put("SIMSerialNumber", preferenceConfig.readSIMSerial().toString());
                    } catch (Exception ee) {
                        progressDialog1.dismiss();
                    }
                    String JString = jsonObjectVal.toString();

                    try {
                        new HTTPPostSaveLinkAccount().execute(url, JString);

                    } catch (Exception exx) {
                        progressDialog1.dismiss();
                        Toast.makeText(LinkAccount.this, exx.toString(), Toast.LENGTH_LONG).show();
                    }

                }
            }

        });

    }

    public class HTTPPostCheckAccount extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(LinkAccount.this);
            progressDialog1.setMessage("Please wait...."); // Setting Message
            progressDialog1.setTitle("iBanking"); // Setting Title
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog1.show(); // Display Progress Dialog
            progressDialog1.setCancelable(false);

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {

                String PUrl = arg0[0].toString();
                String JStr = arg0[1].toString();

                String accessToken = preferenceConfig.readToken().toString();

                HttpPost post = new HttpPost(PUrl);

                StringEntity entity = new StringEntity(JStr);
                post.setEntity(entity);
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");
                post.setHeader("Authorization", "Bearer " + accessToken);

                response = client.execute(post);
                status = response.getStatusLine().getStatusCode();
            } catch (Exception exx) {
                if (progressDialog1.isShowing())
                    progressDialog1.dismiss();
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

            if (progressDialog1.isShowing())
                progressDialog1.dismiss();
            //move activity
            if (response.getStatusLine().getStatusCode() == 200) {
                try {

                    String json = convertInputStreamToString(response.getEntity().getContent());
                    json = json.replace("[", "");
                    json = json.replace("]", "");
                    jsonObject = new JSONObject(json);
                    if (jsonObject.length() > 0) {

                        tAccountName.setText(jsonObject.getString("customerName"));
                        bConformAccount.setEnabled(false);
                        tAccount.setEnabled(false);
                        bConformLink.setEnabled(true);
                        tOTP.setEnabled(true);
                        response = null;
                    } else {
                        Toast.makeText(LinkAccount.this, "Invalid Account Number", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception exx) {
                    Toast.makeText(LinkAccount.this, exx.toString(), Toast.LENGTH_LONG).show();
                }


            } else if (response.getStatusLine().getStatusCode() == 503) {
                Toast.makeText(LinkAccount.this, "Service Unavailable", Toast.LENGTH_LONG).show();
            } else if (response.getStatusLine().getStatusCode() == 400) {
                Toast.makeText(LinkAccount.this, "Bad Request", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LinkAccount.this, "Undefined Error!", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(Result);
        }
    }

    public class HTTPPostSaveLinkAccount extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(LinkAccount.this);
            progressDialog1.setMessage("Please wait...."); // Setting Message
            progressDialog1.setTitle("iBanking"); // Setting Title
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog1.show(); // Display Progress Dialog
            progressDialog1.setCancelable(false);

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {

                String PUrl = arg0[0].toString();
                String JStr = arg0[1].toString();

                String accessToken = preferenceConfig.readToken().toString();

                HttpPost post = new HttpPost(PUrl);

                StringEntity entity = new StringEntity(JStr);
                post.setEntity(entity);
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");
                post.setHeader("Authorization", "Bearer " + accessToken);

                response = client.execute(post);
                status = response.getStatusLine().getStatusCode();

            } catch (Exception exx) {
                if (progressDialog1.isShowing())
                    progressDialog1.dismiss();
                //Toast.makeText(LinkAccount.this, exx.toString(), Toast.LENGTH_LONG).show();
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

            if (progressDialog1.isShowing())
                progressDialog1.dismiss();

            if (response.getStatusLine().getStatusCode() == 200) {
                try {
                    String json = convertInputStreamToString(response.getEntity().getContent());
                    json = json.replace("[", "");
                    json = json.replace("]", "");
                    jsonObject = new JSONObject(json);

                    if (jsonObject.length() > 0) {

                        String Done = jsonObject.getString("done").toString();
                        String Msg = jsonObject.getString("msg").toString();
                        //tAccountName.setText(jsonObject.getString("customerName"));
                        if (Done.equals("1")) {
                            Toast.makeText(LinkAccount.this, Msg, Toast.LENGTH_LONG).show();
                            tOTP.setEnabled(false);
                            bConformLink.setEnabled(false);
                            bConformAccount.setEnabled(true);
                            tAccount.setEnabled(true);
                            tAccount.setText("");tOTP.setText("");
                            tAccountName.setText("");
                            response = null;
                            new HTTPPostSaveLinkAccountList().execute();
                            //Thread.sleep(2000);

                        } else if (Done.equals("0")) {
                            Toast.makeText(LinkAccount.this, Msg, Toast.LENGTH_LONG).show();
                            tOTP.setEnabled(true);
                            bConformLink.setEnabled(true);
                            bConformAccount.setEnabled(false);
                            tAccount.setEnabled(false);

                        }
                        response = null;
                    } else {
                        Toast.makeText(LinkAccount.this, "System error", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception exx) {
                }
            } else if (response.getStatusLine().getStatusCode() == 503) {
                Toast.makeText(LinkAccount.this, "Service Unavailable", Toast.LENGTH_LONG).show();
            } else if (response.getStatusLine().getStatusCode() == 400) {
                Toast.makeText(LinkAccount.this, "Bad Request", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LinkAccount.this, "Undefined Error!", Toast.LENGTH_LONG).show();
            }
            //move activity
            super.onPostExecute(Result);
        }
    }

    public class HTTPPostSaveLinkAccountList extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(LinkAccount.this);
            progressDialog1.setMessage("Please wait...."); // Setting Message
            progressDialog1.setTitle("iBanking"); // Setting Title
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog1.show(); // Display Progress Dialog
            progressDialog1.setCancelable(false);

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
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

                String PUrl = getResources().getString(R.string.Parent_Url_https)+"/api/CBSInfo/LinkAccountList";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("UserID", preferenceConfig.readUserName().toString());

                } catch (Exception ee) {
                    progressDialog1.dismiss();
                }
                String JString = jsonObject.toString();

                String accessToken = preferenceConfig.readToken().toString();

                HttpPost post = new HttpPost(PUrl);

                StringEntity entity = new StringEntity(JString);
                post.setEntity(entity);
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-type", "application/json");
                post.setHeader("Authorization", "Bearer " + accessToken);

                response = client.execute(post);
                //status = response.getStatusLine().getStatusCode();

            } catch (Exception exx) {
                if (progressDialog1.isShowing())
                    progressDialog1.dismiss();
                //Toast.makeText(LinkAccount.this, exx.toString(), Toast.LENGTH_LONG).show();
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

            if (progressDialog1.isShowing())
                progressDialog1.dismiss();

            if (response.getStatusLine().getStatusCode() == 200) {
                try {
                    listItems.clear();

                    String json = convertInputStreamToString(response.getEntity().getContent());

                    JSONArray jsonArray=new JSONArray(json);

                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            ListItemLinkAccount itemLinkAccount = new ListItemLinkAccount(
                                    jsonObj.getString("account").toString(),
                                    jsonObj.getString("acname").toString(),
                                    jsonObj.getString("actype").toString()

                            );
                            listItems.add(itemLinkAccount);

                        }
                        //adapter.
                        adapter=new LinkAccountAdapter(listItems,getApplicationContext());
                        //recyclerView.setAdapter(null);
                        //recyclerView.removeAllViews();
                        //recyclerView.removeAllViewsInLayout();
                        recyclerView.setAdapter(adapter);
                        response = null;
                    } else {
                        Toast.makeText(LinkAccount.this, "System error", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception exx) {
                }
            } else if (response.getStatusLine().getStatusCode() == 503) {
                Toast.makeText(LinkAccount.this, "Service Unavailable", Toast.LENGTH_LONG).show();
            } else if (response.getStatusLine().getStatusCode() == 400) {
                Toast.makeText(LinkAccount.this, "Bad Request", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LinkAccount.this, "Undefined Error!", Toast.LENGTH_LONG).show();
            }
            //move activity
            super.onPostExecute(Result);
        }
    }


}
