package tblbd.com.ibanking;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceConfig {
    private SharedPreferences sharedPreferences;
    private Context context;
    //private static final String PREFS_NAME = "Login_Pref";

    public SharedPreferenceConfig(Context context)
    {
        this.context=context;
        sharedPreferences=context.getSharedPreferences(context.getResources().getString(R.string.login_preference),Context.MODE_PRIVATE);
    }


    public void writeUserName(String UserName)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.login_username_preference),UserName);
        editor.commit();
    }
    public String readUserName()
    {
        String UserName="";
        UserName=sharedPreferences.getString(context.getResources().getString(R.string.login_username_preference),"");
        return UserName;
    }
    public void writeToken(String Token)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.login_token_preference),Token);
        editor.commit();
    }
    public String readToken()
    {
        String Token="";
        Token=sharedPreferences.getString(context.getResources().getString(R.string.login_token_preference),"");
        return Token;
    }
    public void writeIMEI(String IMEI)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.login_IMEI_preference),IMEI);
        editor.commit();
    }
    public String readIMEI()
    {
        String IMEI="";
        IMEI=sharedPreferences.getString(context.getResources().getString(R.string.login_IMEI_preference),"");
        return IMEI;
    }

    public void writeSIMSerial(String SIMSerial)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.login_SIMSerial_preference),SIMSerial);
        editor.commit();
    }
    public String readSIMSerial()
    {
        String SIMSerial="";
        SIMSerial=sharedPreferences.getString(context.getResources().getString(R.string.login_SIMSerial_preference),"");
        return SIMSerial;
    }

}
