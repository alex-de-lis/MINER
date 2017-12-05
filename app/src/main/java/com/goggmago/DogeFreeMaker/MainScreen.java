package com.goggmago.DogeFreeMaker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainScreen extends AppCompatActivity {

    GraphView graph;
    MediaType JSON;
    Button btn;
    Timer timer;
    int onClick=0;
    String Balance,promo,ref,mod,Language,x,y,PrevBalance,ExtRate,wallet;
    ProgressBar PB;
    InterstitialAd mInterstitialAd;
    Date OldDate,NewDate;
    int AdPeriod=40,Showed=0;
    String Banner,Inter,an = "Doge Free Maker",CUR="DOGE",DefStr;
    Menu MyMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        graph = findViewById(R.id.MyGraph);
        graph.setBackgroundColor(getResources().getColor(R.color.blue));
        btn = findViewById(R.id.PlayBtn);
        PB= findViewById(R.id.MyProgressBar);
        Language=getResources().getConfiguration().locale.getLanguage();
        Banner=getResources().getString(R.string.footer);
        Inter=getResources().getString(R.string.banner);
        InitInterstitial();
        Banner();
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Encoding(android_id);
        DownloadAll();
        NewDate = new Date();
        OldDate = new Date(NewDate.getTime() - AdPeriod * 1010);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.MyMenu=menu;
        getMenuInflater().inflate(R.menu.popupmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Setting();
                return true;
            case R.id.share:
                Share();
                return true;
            case R.id.CheckBalance:
                CheckBalance();
                return true;
            case R.id.ExtRate:
                ExtRates();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void DownloadAll()
    {
        if(isOnline())
        {
            AsyncTaskCheck check = new AsyncTaskCheck();
            check.execute();
            AsyncTaskForRates newTask = new AsyncTaskForRates();
            newTask.execute();
            AsyncTaskForGraph task = new AsyncTaskForGraph();
            task.execute();
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
                                    MainScreen.this.recreate();
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void InitInterstitial()
    {
        mInterstitialAd=new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Inter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public void Banner()
    {
        MobileAds.initialize(getApplicationContext(),Banner);
        AdView myAdView= findViewById(R.id.AdMainScr);
        AdRequest adRequest=new AdRequest.Builder().build();
        myAdView.loadAd(adRequest);
    }

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

    public void CheckBalance()
    {
        Intent intent = new Intent(MainScreen.this,CheckBalanceActivity.class);
        intent.putExtra("json",ExtRate);
        intent.putExtra("balance", Balance);
        String time;
        if(MyTimer())
        {
            time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            ShowAd(intent,3);
        }
        else
        {
            time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            startActivityForResult(intent,3);
        }
    }

    public void Share()
    {
        Intent intent = new Intent(MainScreen.this,ShareActivity.class);
        intent.putExtra("Promo", promo);
        intent.putExtra("Ref",ref);
        intent.putExtra("Mod",mod);
        String time;
        if(MyTimer())
        {
            time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            ShowAd(intent,3);
        }
        else
        {
            time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            startActivityForResult(intent,3);
        }
    }

    public void ExtRates()
    {
        Intent intent = new Intent(MainScreen.this,ExRate.class);
        intent.putExtra("json",ExtRate);
        String time;
        if(MyTimer())
        {
            time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            ShowAd(intent,3);
        }
        else
        {
            time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            startActivityForResult(intent,3);
        }
    }

    public void Setting()
    {
        Intent intent=new Intent(MainScreen.this,SettingsActivity.class);
        intent.putExtra("Lang",Language);
        intent.putExtra("x",x);
        intent.putExtra("y",y);
        intent.putExtra("wallet",wallet);
        String time;
        if(MyTimer())
        {
            time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            ShowAd(intent,1);
        }
        else
        {
            time=OldDate.getTime()+"";
            intent.putExtra("Date",time);
            startActivityForResult(intent,1);
        }
    }

    public void MiningActivity(View view)
    {
        if(onClick==0)
        {
            Showed=0;
            DownloadMining();
        }
        else
        {
            if(!DefStr.equals("+0.00000000")&&!DefStr.equals("+0,00000000")&&Showed==0)
            {
                Toast toast = Toast.makeText(getApplicationContext(), DefStr, Toast.LENGTH_SHORT);
                toast.show();
            }
            onClick=0;
            timer.cancel();
            btn.setText(R.string.Maining);
        }
    }

    public void DownloadMining()
    {
        if(isOnline())
        {
            onClick++;
            timer = new Timer();
            timer.scheduleAtFixedRate(new MyTask(), 0, 60 * 1000);
            btn.setText(R.string.Stop);
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
                                    DownloadMining();
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void ShowAd(final Intent intent, final int reqestCode)
    {
        mInterstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdClosed()
            {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                if(reqestCode!=1) startActivityForResult(intent,3);
                else startActivityForResult(intent,1);
            }
        });
        if(mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
        else startActivityForResult(intent,reqestCode);

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

    private class AsyncTaskForRates extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String myurl = "https://min-api.cryptocompare.com/data/price?fsym="+CUR+"&tsyms=USD,EUR,GBP,CHF,CNY,JPY";
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(myurl)
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
            ExtRate=ParseJson(result);
        }


        @Override
        protected void onPreExecute()
        {

        }

    }

    private String  ParseJson(String json)
    {
        String USD = "\"USD\":",End=",\"",EUR="\"EUR\":",GBP="\"GBP\":",CHF="\"CHF\":",CNY="\"CNY\":";
        String JPY="\"JPY\":", EndJpy="}";
        int startUSD=json.indexOf(USD)+USD.length();
        int endUSD=json.indexOf(End,startUSD);
        int startEUR=json.indexOf(EUR)+EUR.length();
        int endEUR=json.indexOf(End,startEUR);
        int startGBP=json.indexOf(GBP)+GBP.length();
        int endGBP=json.indexOf(End,startGBP);
        int startCHF=json.indexOf(CHF)+USD.length();
        int endCHF=json.indexOf(End,startCHF);
        int startCNY=json.indexOf(CNY)+USD.length();
        int endCNY=json.indexOf(End,startCNY);
        int startJPY=json.indexOf(JPY)+USD.length();
        int endJPY=json.indexOf(EndJpy,startJPY);
        String result="";
        result+="USD: "+json.substring(startUSD,endUSD)+"\n";
        result+="EUR: "+json.substring(startEUR,endEUR)+"\n";
        result+="GBP: "+json.substring(startGBP,endGBP)+"\n";
        result+="CHF: "+json.substring(startCHF,endCHF)+"\n";
        result+="CNY: "+json.substring(startCNY,endCNY)+"\n";
        result+="JPY: "+json.substring(startJPY,endJPY)+"\n";
        return result;
    }

    private class AsyncTaskCheck extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String myurl = "http://ethonline.site/users/login";
            JSON = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();
            JSONObject postdata = new JSONObject();

            try {
                postdata.put("x", x);
                postdata.put("y", y);
                postdata.put("an", an);
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
        protected void onPostExecute(String result) {
            if(!result.contains("{\"result\":false}"))
            {
                Balance=Params(result,true);
                PrevBalance=Balance;
            }
            else
            {
                Intent intent = new Intent(MainScreen.this,MainActivity.class);
                intent.putExtra("x",x);
                intent.putExtra("y",y);
                startActivityForResult(intent,2);
            }
        }

        @Override
        protected void onPreExecute() {

        }
    }

    private void Encoding(String MyId)
    {
        x = "1";
        y = "1";
    }

    private String MyRevers(String s)
    {
        return new StringBuilder(s).reverse().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        if(requestCode==1)
        {
            String Lang = data.getStringExtra("Lang");
            wallet=data.getStringExtra("wallet");
            String newtime= data.getStringExtra("Date");
            OldDate=new Date(Long.parseLong(newtime));
            ChangeLocale(Lang);
        }
        if(requestCode==2)
        {
            Balance=data.getStringExtra("balance");
            PrevBalance=Balance;
            ref=data.getStringExtra("ref");
            promo=data.getStringExtra("promo");
            mod=data.getStringExtra("mod");
        }
        if(requestCode==3)
        {
            String newtime= data.getStringExtra("Date");
            OldDate=new Date(Long.parseLong(newtime));
        }
    }

    void ChangeLocale(String Lang)
    {
        Locale myLocale = new Locale(Lang);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        updateView();
        Language=Lang;
    }

    void updateView()
    {
        if(onClick==0) btn.setText(getResources().getString(R.string.Maining));
        else btn.setText(getResources().getString(R.string.Stop));
        MyMenu.getItem(0).setTitle(R.string.Settings);
        MyMenu.getItem(1).setTitle(R.string.Share);
        MyMenu.getItem(2).setTitle(R.string.Check_Balance);
        MyMenu.getItem(3).setTitle(R.string.Exchange_Rates);
    }

    private class AsyncTaskForGraph extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String myurl = "https://min-api.cryptocompare.com/data/histoday?fsym="+CUR+"&tsym=USD&limit=30";
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(myurl)
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
        protected void onPostExecute(String result) {
            StringChange(result);
            PB.setVisibility(ProgressBar.INVISIBLE);
            graph.setVisibility(GraphView.VISIBLE);
            btn.setVisibility(Button.VISIBLE);
        }


        @Override
        protected void onPreExecute()
        {

        }

        private Date StringFormat(String time) {
            Date dt = new Date(Long.parseLong(time + "000"));
            return dt;
        }

        private void StringChange(String json) {
            String mas[][] = new String[2][32];
            int i = 0;
            Date FTime;
            String time = "{\"time\":", Sep = ",\"", close = "\"close\":";
            int startTime = json.indexOf(time) + time.length();
            int endTime = json.indexOf(Sep, startTime);
            int startClose = json.indexOf(close) + close.length();
            int endClose = json.indexOf(Sep, startClose);

            mas[0][0] = json.substring(startTime, endTime);
            Date dt = new Date(Long.parseLong(mas[0][0] + "000"));
            mas[1][0] = json.substring(startClose, endClose);

            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(dt, Float.parseFloat(mas[1][0]))});
            while (i != 30) {
                startTime = json.indexOf(time, startTime) + time.length();
                endTime = json.indexOf(Sep, startTime);
                startClose = json.indexOf(close, endClose) + close.length();
                endClose = json.indexOf(Sep, startClose);
                i++;
                mas[0][i] = json.substring(startTime, endTime);
                FTime = StringFormat(mas[0][i]);
                mas[1][i] = json.substring(startClose, endClose);
                DataPoint dp = new DataPoint(FTime, Float.parseFloat(mas[1][i]));

                series.appendData(dp, true, 31);
            }
            series.setDrawDataPoints(true);
            series.setColor(getResources().getColor(R.color.black));
            graph.addSeries(series);
            Iterator<DataPoint> iter=series.getValues(series.getLowestValueX(),series.getHighestValueX());
            for(i=0;i<21;i++)
            {
                iter.next();
            }
            DataPoint ndp=iter.next();
            graph.getViewport().setScrollable(true);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMinX(ndp.getX());
            graph.getViewport().setMaxX(series.getHighestValueX());

            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setMinY(series.getLowestValueY());
            graph.getViewport().setMaxY(series.getHighestValueY());

            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        // show normal x values
                        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM");
                        return format1.format(value);
                    } else {
                        // show currency for y values
                        return super.formatLabel(value, isValueX);
                    }
                }
            });
        }

    }

    private class AsyncTaskForMining extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String myurl = "http://ethonline.site/users/activity";
            JSON = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client = new OkHttpClient();
            JSONObject postdata = new JSONObject();

            try {
                postdata.put("x", x);
                postdata.put("y", y);
                postdata.put("an", an);
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
            if(result.contains("true"))
            {
                Balance = Params(result, false);
                Balance = Balance.replace(',', '.');
                float oldBalance = Float.parseFloat(PrevBalance);
                float NewBalance = Float.parseFloat(Balance);
                float Def = NewBalance - oldBalance;
                PrevBalance = Balance;
                DefStr = String.format("%.8f", Def);
                DefStr = "+" + DefStr;
                if(!DefStr.equals("+0.00000000")&&!DefStr.equals("+0,00000000"))
                {
                    if(Showed==0)
                    {
                        Toast toast = Toast.makeText(getApplicationContext(), DefStr, Toast.LENGTH_SHORT);
                        toast.show();
                        Showed=1;
                    } else Showed=0;
                }

            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.No_Internet_Connection), Toast.LENGTH_SHORT);
                toast.show();
            }
        }


        @Override
        protected void onPreExecute()
        {
            Showed=0;
        }
    }

    private String Params(String result, boolean flag)
    {
        String balance = ",\"balanse\":",End=",\"";
        String Promo="promo_code\":", Ref="\"refs_count\":",Mod="\"modifier\":",Wallet="\"wallet\":\"";
        int startBalance = result.indexOf(balance) + balance.length();
        int endBalance = result.indexOf(End, startBalance);
        int startRef,endRef,startMod,endMod,startPromo,endPromo,startWal,endWal;
        startRef=result.indexOf(Ref)+Ref.length();
        endRef=result.indexOf(End,startRef);
        startMod=result.indexOf(Mod)+Mod.length();
        endMod=result.indexOf(End,startMod);
        startPromo=result.indexOf(Promo)+Promo.length();
        endPromo=result.indexOf(End,startPromo);
        startWal=result.indexOf(Wallet)+Wallet.length();
        endWal=result.indexOf(End,startWal)-1;
        promo=result.substring(startPromo,endPromo);
        ref=result.substring(startRef,endRef);
        mod=result.substring(startMod,endMod);
        wallet=result.substring(startWal,endWal);
        if(!flag)
        {
            balance=result.substring(startBalance,endBalance);
            if(balance.contains("\"")) balance=result.substring(startBalance+1,endBalance-1);
            balance = String.format("%.8f", Float.parseFloat(balance));
        }
        else balance=result.substring(startBalance+1,endBalance-1);
        return balance;
    }

    private class MyTask extends TimerTask
    {
        public void run()
        {
            AsyncTaskForMining AT= new AsyncTaskForMining();
            AT.execute();
        }
    }
}
