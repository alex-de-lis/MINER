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

public class ShareActivity extends AppCompatActivity {

    TextView Promo, Activ,Mods;
    Date OldDate,NewDate;
    String Banner,Inter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Promo= findViewById(R.id.YourPromo);
        Activ= findViewById(R.id.ActivatedApp);
        Mods= findViewById(R.id.Coeff);
        Banner=getResources().getString(R.string.footer);
        Inter=getResources().getString(R.string.banner);
        MobileAds.initialize(getApplicationContext(),Banner);
        AdView myAdView= findViewById(R.id.AdShare);
        AdRequest adRequest=new AdRequest.Builder().build();
        InitInterstitial();
        String time = getIntent().getStringExtra("Date");
        OldDate=new Date(Long.parseLong(time));
        myAdView.loadAd(adRequest);
        Promo.setText(R.string.YourPromo);
        String promoText= Promo.getText().toString()+" "+getIntent().getStringExtra("Promo");
        Promo.setText(promoText);
        Activ.setText(R.string.Activated);
        String activText= Activ.getText().toString()+" "+getIntent().getStringExtra("Ref");
        Activ.setText(activText);
        Mods.setText(R.string.Coeff);
        String modText= Mods.getText().toString()+" "+getIntent().getStringExtra("Mod");
        Mods.setText(modText);
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

    public void BackToMain(View view)
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

    public void SendMyPromo(View view)
    {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String textToSend=getResources().getString(R.string.Text_to_Share)+" "+getIntent().getStringExtra("Promo");
        intent.putExtra(Intent.EXTRA_TEXT, textToSend);
        try
        {
            startActivity(Intent.createChooser(intent, "Описание действия"));
        }
        catch (android.content.ActivityNotFoundException ex)
        {

        }
    }
}
