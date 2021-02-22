package tblbd.com.ibanking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT=2000;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new ProgressAsyncTask ().execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginintent=new Intent(SplashActivity.this,ActivityLogin.class);
                startActivity(loginintent);
                //finish();
            }
        },SPLASH_TIME_OUT);

    }

    private class ProgressAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog= new ProgressDialog(SplashActivity.this);
            progressDialog.setMessage("Please Wait...."); // Setting Message
            progressDialog.setTitle("iBanking"); // Setting Title
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            try {
                Thread.sleep(1000);
            }catch (Exception exx){}
            return null;
        }

        protected void onPostExecute(Void result) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            //move activity
            super.onPostExecute(result);
        }
    }
}
