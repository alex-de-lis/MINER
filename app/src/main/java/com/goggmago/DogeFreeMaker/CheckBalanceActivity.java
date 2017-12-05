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

public class CheckBalanceActivity extends AppCompatActivity {

    TextView Balance;
    String Rate, YourBalance;
    Date OldDate,NewDate;
    String Inter,Banner, CUR=" DOGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);
        Balance = findViewById(R.id.TextBalance);
        Rate=getIntent().getStringExtra("json");
        YourBalance=getIntent().getStringExtra("balance")+CUR;
        Balance.setText(YourBalance);
        String time = getIntent().getStringExtra("Date");
        OldDate=new Date(Long.parseLong(time));
        Banner=getResources().getString(R.string.footer);
        Inter=getResources().getString(R.string.banner);
        InitInterstitial();
        MobileAds.initialize(getApplicationContext(),Banner);
        AdView myAdView= findViewById(R.id.AdBalance);
        AdRequest adRequest=new AdRequest.Builder().build();
        myAdView.loadAd(adRequest);
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

    public void BackFromBalance(View view)
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

    public void ShowAdTodayRate(final Intent intent)
    {
        mInterstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdClosed()
            {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                startActivityForResult(intent,1);
            }
        });
        if(mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
        else startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        if(requestCode==1)
        {
            String newtime= data.getStringExtra("Date");
            OldDate=new Date(Long.parseLong(newtime));
        }
    }

    public void TodayRate(View view)
    {
        Intent intent = new Intent(CheckBalanceActivity.this,Your_rate.class);
        intent.putExtra("json",Rate);
        intent.putExtra("balance",YourBalance);
        String time;
        if(MyTimer())
        {
            time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            ShowAdTodayRate(intent);
        }
        else
        {
            time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            startActivityForResult(intent,1);
        }
    }

}
