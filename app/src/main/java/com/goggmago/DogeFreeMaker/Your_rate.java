package com.goggmago.DogeFreeMaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Date;

public class Your_rate extends AppCompatActivity {

    TextView YourRateText;
    String Banner,Inter;
    Date OldDate,NewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_rate);
        YourRateText= findViewById(R.id.YourRateText);
        String Rate=getIntent().getStringExtra("json");
        String TextBalance=getIntent().getStringExtra("balance");
        TextBalance=TextBalance.substring(0,TextBalance.indexOf("DOGE"));
        float balance=Float.parseFloat(TextBalance);
        YourRateText.setText(Parse(Rate,balance));
        Banner=getResources().getString(R.string.footer);
        Inter=getResources().getString(R.string.banner);
        InitInterstitial();
        String time = getIntent().getStringExtra("Date");
        OldDate=new Date(Long.parseLong(time));
        MobileAds.initialize(getApplicationContext(),Banner);
        AdView myAdView= findViewById(R.id.AdYourRate);
        AdRequest adRequest=new AdRequest.Builder().build();
        myAdView.loadAd(adRequest);
    }

    private String Parse(String json, float balance)
    {
        float factor;
        String usd="USD:",eur="EUR:",gbp="GBP:",chf="CHF:",cny="CNY:",jpy="JPY:";
        String result="";
        factor=Find(json,usd);
        result+=usd+" "+ToString(factor,balance)+"\n";
        factor=Find(json,eur);
        result+=eur+" "+ToString(factor,balance)+"\n";
        factor=Find(json,gbp);
        result+=gbp+" "+ToString(factor,balance)+"\n";
        factor=Find(json,chf);
        result+=chf+" "+ToString(factor,balance)+"\n";
        factor=Find(json,cny);
        result+=cny+" "+ToString(factor,balance)+"\n";
        factor=Find(json,jpy);
        result+=jpy+" "+ToString(factor,balance);
        return result;
    }

    private String ToString(float factor, float balance)
    {
        String StringRes=String.format("%.2f", balance*factor);
        StringRes=StringRes.replace(',','.');
        return StringRes;
    }

    private float Find(String json,String Cur)
    {
        int start=json.indexOf(Cur)+Cur.length();
        int end=json.indexOf("\n",start);
        return Float.parseFloat(json.substring(start,end));
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

    public void BackFromYourRate (View view)
    {
        Intent intent=new Intent();
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
}
