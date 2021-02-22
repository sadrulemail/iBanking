package tblbd.com.ibanking;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class AccountList extends AppCompatActivity {

    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItemLinkAccountList> listItems;

    ProgressDialog progressDialog1;
    private SharedPreferenceConfig preferenceConfig;
    public JSONObject jsonObject;
    HttpClient client;
    HttpResponse response;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);

        recyclerView=findViewById(R.id.RecyclerViewLinkAccountList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems=new ArrayList<>();
        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        new HTTPPostSaveLinkAccountList().execute();


    }

    public class HTTPPostSaveLinkAccountList extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(AccountList.this);
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


                //String PUrl = getResources().getString(R.string.Parent_Url)+"/api/CBSInfo/LinkAccountList";
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
                    String json = convertInputStreamToString(response.getEntity().getContent());

                    JSONArray jsonArray=new JSONArray(json);

                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            ListItemLinkAccountList itemLinkAccount = new ListItemLinkAccountList(
                                    jsonObj.getString("account").toString(),
                                    jsonObj.getString("acname").toString(),
                                    jsonObj.getString("actype").toString(),
                                    jsonObj.getString("balance").toString()+"/="

                            );
                            listItems.add(itemLinkAccount);

                        }
                        adapter=new LinkAccountListAdapter(listItems,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        response = null;
                    } else {
                        Toast.makeText(AccountList.this, "System error", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception exx) {
                }
            } else if (response.getStatusLine().getStatusCode() == 503) {
                Toast.makeText(AccountList.this, "Service Unavailable", Toast.LENGTH_LONG).show();
            } else if (response.getStatusLine().getStatusCode() == 400) {
                Toast.makeText(AccountList.this, "Bad Request", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(AccountList.this, "Undefined Error!", Toast.LENGTH_LONG).show();
            }
            //move activity
            super.onPostExecute(Result);
        }
    }
}
