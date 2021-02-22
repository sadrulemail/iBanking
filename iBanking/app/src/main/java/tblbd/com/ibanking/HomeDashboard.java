package tblbd.com.ibanking;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomeDashboard extends AppCompatActivity implements View.OnClickListener {
    private CardView cardAccount,
            cardAccountStatement, cardFundTransfer, cardBillPay, cardSignOut,
    cardLinkAccount;
    TextView tCustomerName;
    private SharedPreferenceConfig preferenceConfig;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dashboard);

        new ProgressAsyncTask().execute();

        //defining cards
        cardLinkAccount=(CardView) findViewById(R.id.cardview_LinkAccount);
        cardAccount = (CardView) findViewById(R.id.cardview_AccountList);
        cardAccountStatement = (CardView) findViewById(R.id.cardview_AccountStatement);
        cardFundTransfer = (CardView) findViewById(R.id.cardview_FundTransfer);
        cardBillPay = (CardView) findViewById(R.id.cardview_BillPay);
        cardSignOut = (CardView) findViewById(R.id.cardview_SignOut);
        tCustomerName=(TextView) findViewById(R.id.txtCustomerName);


        //Add Click listener to the cards
        cardLinkAccount.setOnClickListener(this);
        cardAccount.setOnClickListener(this);
        cardAccountStatement.setOnClickListener(this);
        cardFundTransfer.setOnClickListener(this);
        cardBillPay.setOnClickListener(this);
        cardSignOut.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent cardIntent;
        switch (v.getId()) {
            case R.id.cardview_LinkAccount:
                cardIntent =
                        new Intent(this, LinkAccount.class);
                startActivity(cardIntent);
                break;
            case R.id.cardview_AccountList:
                cardIntent =
                        new Intent(this, AccountList.class);
                startActivity(cardIntent);
                break;
            case R.id.cardview_AccountStatement:
                cardIntent =
                        new Intent(this, AccountStatement.class);
                startActivity(cardIntent);
                break;
            case R.id.cardview_FundTransfer:
                cardIntent =
                        new Intent(this, FundTransfer.class);
                startActivity(cardIntent);
                break;
            case R.id.cardview_BillPay:
                cardIntent =
                        new Intent(this, BillPay.class);
                startActivity(cardIntent);
                break;
            case R.id.cardview_SignOut: {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                // Setting Dialog Title
                alertDialog.setTitle("Logout !");
                // Setting Dialog Message
                alertDialog.setMessage("Do you want to logout from iBanking?");
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.sign_out);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Write your code here to invoke YES event
                        //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                        try {
                            //preferenceConfig.writeToken("");
                            //Thread.sleep(2000);
                            finishAffinity();
                            System.exit(1);
                        } catch (Exception exx) {
                        }
                    }
                });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            }
            break;
            default:
                break;
        }

    }

    private class ProgressAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog= new ProgressDialog(HomeDashboard.this);
            progressDialog.setTitle("iBanking"); // Setting Title
            progressDialog.setMessage("Initializing Dashboard...."); // Setting Message
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            try {
                Thread.sleep(3000);
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
