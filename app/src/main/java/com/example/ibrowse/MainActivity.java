package com.example.ibrowse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    String URL = "https://www.google.com";

    EditText Search_Bar;
    DatabaseHandler databaseHandler;
    DatabaseBookmark database_bookmark ;
    ImageButton Mic, Search_Button, google_btn, bookmarks , Refresh ;
    ProgressBar ProgressBar;
    WebView Web_Views;
    private final int Desktop_Mode_values = 0;
    private java.util.Timer Timers = new Timer();
    private double back_press = 0;
    private TimerTask Timer;

//------------------------------------------------------------------- BottomNavigationView --------------------------------------//

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.Home:
                    Intent home = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(home);
                    return true;
                case R.id.Back:
                    onBackPressed();
                    return true;
                case R.id.Forward:
                    onForward_pressed();
                    return true;
                case R.id.Me:
                    return true;
                case R.id.New_Tabs:
                    return true;
            }
            return false;
        }
    };

//------------------------------------------------------------- ON_CREATE -------------------------------------------------------//

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView nav_view = findViewById(R.id.navigation);
        final androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Refresh = findViewById(R.id.refresh);
        bookmarks = findViewById(R.id.bookmarks);
        Search_Bar = findViewById(R.id.Search_Bar);
        google_btn = findViewById(R.id.google_btn);
        Mic = findViewById(R.id.Mic);
        Search_Button = findViewById(R.id.Search_Button);
        Web_Views = findViewById(R.id.Web_Views);
        ProgressBar = findViewById(R.id.ProgressBar);

        databaseHandler = new DatabaseHandler(this,null, null,1);
        database_bookmark = new DatabaseBookmark(this,null,null,1);

        Search_Bar.setOnEditorActionListener(editorListener);
        Web_Views.setWebViewClient(new WebViewClient());
        WebSettings Web_setting = Web_Views.getSettings();

        Web_setting.setJavaScriptEnabled(true);
        Web_setting.setDisplayZoomControls(false);
        Web_setting.setSupportZoom(true);

        Web_setting.setBuiltInZoomControls(true);
        Web_setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        Web_setting.setLoadWithOverviewMode(true);
        Web_setting.setUseWideViewPort(true);
        Web_Views.clearHistory();
        Web_Views.clearCache(true);

        WebSettings webSetting = Web_Views.getSettings();
        webSetting.setJavaScriptEnabled(true);

        Web_Views.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ProgressBar.setVisibility(View.VISIBLE);

                final String urls = url;

                if (urls.contains("mailto:") || urls.contains("sms:") || urls.contains("tel:")) {
                    Web_Views.stopLoading();
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(urls));
                    startActivity(i);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ProgressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

//---------------------------------------------------------  WEB_VIEWS  ------------------------------------------------------------------//
        Web_Views.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                ProgressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        back_press = 1;
        Timer = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        back_press = 1;
                        Timer.cancel();
                    }
                });
            }
        };
        Timers.schedule(Timer, (int) (3000));
        back_press = 1;
//----------------------------------------------------------------------- MIC ------------------------------------------------------//

        Mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mic_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                mic_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                mic_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


                if (mic_intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(mic_intent, 10);

                } else {
                    Toast.makeText(MainActivity.this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
                }
            }

        });

        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                onBookPressed();
                Toast.makeText(MainActivity.this, " Bookmarked ", Toast.LENGTH_SHORT).show();
            }
        });
//------------------------------------------------------------------- GOOGLE BUTTON  ----------------------------------------------------//
        google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Web_Views.loadUrl(URL);
                Web_Views.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        ProgressBar.setVisibility(View.VISIBLE);
                        Search_Bar.setText(url);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        ProgressBar.setVisibility(View.GONE);
                    }
                });

                Web_Views.loadUrl(URL);
                WebSettings webSetting = Web_Views.getSettings();

                webSetting.setJavaScriptEnabled(true);
                ProgressBar.setProgress(100);
            }
        });
//---------------------------------------------------------------------- SEARCH_BUTTON -----------------------------------------------------------//
        Search_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search_Bar = findViewById(R.id.Search_Bar);
                String URLS = Search_Bar.getText().toString();
                if (!URLS.startsWith("https://")) {
                }
                URLS = "https://www.google.com/search?q=" + URLS;

                Web_Views.loadUrl(URLS);
                Web_Views.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        ProgressBar.setVisibility(View.VISIBLE);
                        Search_Bar.setText(url);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        ProgressBar.setVisibility(View.GONE);
                    }
                });

                Web_Views.loadUrl(URLS);
                WebSettings webSetting = Web_Views.getSettings();

                webSetting.setJavaScriptEnabled(true);

            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

//-------------------------------------------------------------------- REFRESH BUTTON ---------------------------------------//


    //------------------------------------------------------------- ON BOOK PRESSED --------------------------------------//
private void onBookPressed()
{
    Websites WEB =new Websites(Web_Views.getUrl());
    database_bookmark.addUrl(WEB);
    saveData();
}
    private void saveData()
    {
        Websites web=new Websites(Web_Views.getUrl());
        databaseHandler.addUrl(web);
    }

//---------------------------------------------------------------- ACTION GO -----------------------------------------//
    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent Event) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_GO:
                    String URLS = Search_Bar.getText().toString();

                    if (!URLS.startsWith("https://")) {
                        URLS = "https://www.google.com/search?q=" + URLS;
                    }
                    Web_Views.loadUrl(URLS);
            }
            return false;
        }
    };

//--------------------------------------SetDesktopMode---------------------------------------------//

     private void SetDesktopMode(WebView web_views, boolean enabled) {

       String newUserAgent = Web_Views.getSettings().getUserAgentString();
     if(enabled){
       try{
               String ua = Web_Views.getSettings().getUserAgentString();
                String Dos_String = Web_Views.getSettings().getUserAgentString().substring(ua.indexOf("("),ua.indexOf(")")+1);
                newUserAgent = Web_Views.getSettings().getUserAgentString().replace(Dos_String,"X11; Linux x86_64");
            }
            catch (Exception e ){
                e.printStackTrace();
            }
        }
        else{
             newUserAgent=null;
        }
        Web_Views.getSettings().setUserAgentString(newUserAgent);
        Web_Views.getSettings().setUseWideViewPort(enabled);
        Web_Views.getSettings().setLoadWithOverviewMode(enabled);
        Web_Views.reload();
    }
//---------------------------------------------------------- SetDesktopMode ----------------------------------------------------------------------------//

    private void setSupportActionBar(Toolbar toolbar) {
    }


    //---------------------------------------------------------------    onBackPressed     --------------------------------------------------------------------//
    @Override
    public void onBackPressed() {

        if (Web_Views.canGoBack()) {
            Web_Views.goBack();
        } else {
            if (back_press == 1) {
                Toast.makeText(this, "Press Again to Exit ", Toast.LENGTH_SHORT).show();
                Timer = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                back_press = 2;
                            }
                        });
                    }
                };
                Timers.schedule(Timer, (int) (0));
            }
            Timer = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            back_press = 1;
                        }
                    });
                }
            };
            Timers.schedule(Timer, (int) (3000));
            if (back_press == 2) {
                finish();
            }
        }
    }
//----------------------------------------------------------------  END onBackPressed  ----------------------------------------------//

    //----------------------------------------------------------------   onForward_pressed --------------------------------------------------------//
    public void onForward_pressed() {
        if (Web_Views.canGoForward()) {
            Web_Views.goForward();
        } else Toast.makeText(this, "Can't Go Forward", Toast.LENGTH_SHORT).show();
    }
//----------------------------------------------------------- END onForward_pressed ---------------------------------------------------------------//


    //---------------------------------------------------------------------  onActivityResult  ----------------------------------------------------------//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Search_Bar.setText(result.get(0));

                    String URLS = Search_Bar.getText().toString();

                    if (!URLS.startsWith("https://")) {
                        URLS = "https://www.google.com/search?q=" + URLS;
                    }
                    Web_Views.loadUrl(URLS);
                    Web_Views.setWebViewClient(new WebViewClient() {

                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            super.onPageStarted(view, url, favicon);
                            ProgressBar.setVisibility(View.VISIBLE);
                            Search_Bar.setText(url);
                        }

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            ProgressBar.setVisibility(View.GONE);
                        }
                    });

                    Web_Views.loadUrl(URLS);
                    WebSettings webSetting = Web_Views.getSettings();

                    webSetting.setJavaScriptEnabled(true);

                }
        }

    }
//----------------------------------------------------------------  onCreateOptionsMenu  ----------------------------------------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.three_dot,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.bookmarks:
                Intent book = new Intent(MainActivity.this, Bookmark.class);
                startActivity(book);
                break;
            case R.id.desktop:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

