package com.goggmago.DogeFreeMaker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {

    Button SaveBtn,BackBtn;
    TextView ETH_Adr,Your_Money,Int_Lang;
    EditText Wallet;
    MediaType JSON;
    String x,y,wallet="",prevWallet,Lang="Русский";
    Date OldDate, NewDate;
    String Banner,Inter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Banner=getResources().getString(R.string.footer);
        Inter=getResources().getString(R.string.banner);
        MobileAds.initialize(getApplicationContext(),Banner);
        AdView myAdView= findViewById(R.id.AdSettings);
        AdRequest adRequest=new AdRequest.Builder().build();
        myAdView.loadAd(adRequest);
        final Spinner spinner = findViewById(R.id.Language);
        SaveBtn= findViewById(R.id.SaveBtn);
        BackBtn= findViewById(R.id.BackBtn);
        Wallet= findViewById(R.id.Adress);
        ETH_Adr= findViewById(R.id.YourEthAdress);
        Your_Money= findViewById(R.id.SetMes);
        Int_Lang= findViewById(R.id.InterfaceLang);
        InitInterstitial();
        String language=getIntent().getStringExtra("Lang");
        x=getIntent().getStringExtra("x");
        y=getIntent().getStringExtra("y");
        wallet=getIntent().getStringExtra("wallet");
        Wallet.setText(wallet);
        String time = getIntent().getStringExtra("Date");
        OldDate=new Date(Long.parseLong(time));
        String[] items = new String[] {"Русский", "English"};
        ArrayAdapter <String>adapter= new ArrayAdapter<String>(this,R.layout.spinner_item,items);
        spinner.setAdapter(adapter);
        if(language.equals("ru")) spinner.setSelection(0);
        else spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId)
            {
                ((TextView) parent.getChildAt(0)).setTextSize(30);
                Lang=spinner.getSelectedItem().toString();
                if(Lang.equals("Русский")) Lang="ru";
                else Lang="en";
                Locale myLocale = new Locale(Lang);
                Locale.setDefault(myLocale);
                android.content.res.Configuration config = new android.content.res.Configuration();
                config.locale = myLocale;
                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                Intent intent=new Intent();
                intent.putExtra("Lang", Lang);
                setResult(RESULT_OK, intent);
                SaveBtn.setText((getResources().getString(R.string.Save)));
                BackBtn.setText((getResources().getString(R.string.Back)));
                ETH_Adr.setText((getResources().getString(R.string.Your_Adress)));
                Your_Money.setText((getResources().getString(R.string.Your_Money)));
                Int_Lang.setText((getResources().getString(R.string.Choose_Int_Lang)));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    protected boolean isOnline()
    {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }

    public void DownloadSave()
    {
        if(isOnline())
        {
            AsyncTaskForWallet myTask = new AsyncTaskForWallet();
            myTask.execute();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.Alert_Title))
                    .setMessage(getResources().getString(R.string.No_Internet_Connection))
                    .setCancelable(false)
                    .setNegativeButton(getResources().getString(R.string.Try_Again_Btn),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    DownloadSave();
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void Save(View view)
    {
        DownloadSave();
    }

    InterstitialAd mInterstitialAd;
    public void InitInterstitial()
    {
        mInterstitialAd=new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Inter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    int AdPeriod=40;
    public boolean MyTimer()
    {
        NewDate=new Date();
        long result=NewDate.getTime()-OldDate.getTime();
        result=result/1000;
        if(result>=AdPeriod)
        {
            OldDate=NewDate;
            return true;
        }
        else return false;
    }

    public void ShowAd(final Intent intent)
    {
        mInterstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdClosed()
            {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                finish();
            }
        });
        if(mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
        else finish();

    }

    public void BackFromSettings(View view)
    {
        Intent intent=new Intent();
        intent.putExtra("wallet", wallet);
        intent.putExtra("Lang",Lang);
        setResult(RESULT_OK, intent);
        if(MyTimer())
        {
            String time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            ShowAd(intent);
        }
        else
        {
            String time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent=new Intent();
        intent.putExtra("wallet", wallet);
        intent.putExtra("Lang",Lang);
        setResult(RESULT_OK, intent);
        if(MyTimer())
        {
            String time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            ShowAd(intent);
        }
        else
        {
            String time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            finish();
        }
    }

    private class AsyncTaskForWallet extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String myurl = "http://ethonline.site/users/wallet";
            String an = "Doge Free Maker";
            String w=Wallet.getText().toString();
            prevWallet=wallet;
            wallet=w;
            JSON = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();
            JSONObject postdata = new JSONObject();

            try {
                postdata.put("x", x);
                postdata.put("y", y);
                postdata.put("an", an);
                postdata.put("w", w);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, postdata.toString());
            Request request = new Request.Builder()
                    .url(myurl)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(result.contains("true")) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.Success, Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.Fail, Toast.LENGTH_SHORT);
                toast.show();
                wallet=prevWallet;
            }
            SaveBtn.setClickable(true);
            BackBtn.setClickable(true);
        }


        @Override
        protected void onPreExecute() {
            SaveBtn.setClickable(false);
            BackBtn.setClickable(false);
        }
    }
}
