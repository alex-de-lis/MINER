package com.goggmago.DogeFreeMaker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText edText;
    MediaType JSON;
    String promo,ref,mod,Balance;
    String x,y;
    String Banner;
    int With=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edText= findViewById(R.id.ET);
        x=getIntent().getStringExtra("x");
        y=getIntent().getStringExtra("y");
        Banner=getResources().getString(R.string.footer);
        MobileAds.initialize(getApplicationContext(),Banner);
        AdView myAdView= findViewById(R.id.Adregistr);
        AdRequest adRequest=new AdRequest.Builder().build();
        myAdView.loadAd(adRequest);
    }

    private void Params(String result)
    {
        String balance = ",\"balanse\":",End=",\"";
        String Promo="promo_code\":", Ref="\"refs_count\":",Mod="\"modifier\":";
        int startBalance = result.indexOf(balance) + balance.length();
        int endBalance = result.indexOf(End, startBalance);
        int startRef,endRef,startMod,endMod,startPromo,endPromo;
        startRef=result.indexOf(Ref)+Ref.length();
        endRef=result.indexOf(End,startRef);
        startMod=result.indexOf(Mod)+Mod.length();
        endMod=result.indexOf(End,startMod);
        startPromo=result.indexOf(Promo)+Promo.length();
        endPromo=result.indexOf(End,startPromo);
        promo=result.substring(startPromo,endPromo);
        ref=result.substring(startRef,endRef);
        mod=result.substring(startMod,endMod);
        Balance=result.substring(startBalance,endBalance);
    }

    private class AsyncTaskRegistr extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String myurl = "http://ethonline.site/users/register";
            String an = "Doge Free Maker";
            String cur="doge";
            JSON = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();
            JSONObject postdata = new JSONObject();

            try
            {
                if(With==0)
                {
                    postdata.put("x", x);
                    postdata.put("y", y);
                    postdata.put("an", an);
                    postdata.put("cur", cur);
                }
                else
                {
                    String pc=edText.getText().toString();
                    postdata.put("x", x);
                    postdata.put("y", y);
                    postdata.put("an", an);
                    postdata.put("cur", cur);
                    postdata.put("pc",pc);
                }
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
            if(result.contains("\"result\":true}"))
            {
                Intent intent = new Intent();
                Params(result);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("balance", Balance);
                intent.putExtra("promo", promo);
                intent.putExtra("ref", ref);
                intent.putExtra("mod", mod);
                setResult(RESULT_OK, intent);
                finish();
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(),R.string.Wrong_Promo, Toast.LENGTH_SHORT);
                toast.show();
            }
        }


        @Override
        protected void onPreExecute() {

        }
    }

    public void WithKod(View view)
    {
        With=1;
        AsyncTaskRegistr reg= new AsyncTaskRegistr();
        reg.execute();
    }

    public void WithoutKod(View view)
    {
        With=0;
        AsyncTaskRegistr reg= new AsyncTaskRegistr();
        reg.execute();
    }
}