package com.example.cbdapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WebActivity extends AppCompatActivity {

    public WebView mWebView;
    private ConstraintLayout constraintLayout;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        constraintLayout = (ConstraintLayout) findViewById(R.id.container);
       /* mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            public void onPageFinished(WebView view, String url) {

            }
        });*/
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this))
                //.addPathHandler("/raw/", new WebViewAssetLoader.ResourcesPathHandler(this))
                // .addPathHandler("/res/", new WebViewAssetLoader.ResourcesPathHandler(this))
                .build();
        mWebView.setWebViewClient(new Callback(assetLoader, this));
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDatabaseEnabled(true);

        loadWebsite();
    }
    @SuppressLint("Range")
    private void loadWebsite() {
        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            BufferedReader reader = null;
            String texto="";
            try {
                reader = new BufferedReader(
                        new InputStreamReader(this.getAssets().open("index.html")));

                // do reading, usually loop until end of file reading
                String mLine;
                while ((mLine = reader.readLine()) != null) {
                    texto += mLine;
                }
            }catch(IOException e) {
                    //log the exception
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            //log the exception
                        }
                    }
                }

            DBManager db=new DBManager(this);
            db.open();
            db.drop();
            try {
                db.fillDatabase(this,this.getAssets().open("productos2.csv"));
            }catch(Exception e){
                Toast.makeText(this,"Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Cursor c= db.fetch();
            String all="-----";
            if(!c.moveToNext()){
                Toast.makeText(this,"Error : no hay cursor" , Toast.LENGTH_LONG).show();

            }else {
                while (c.moveToNext()) {
                    int id = c.getInt(c.getColumnIndex(DatabaseHelper._ID));
                    String title = c.getString(c.getColumnIndex(DatabaseHelper.TITLE));
                    String desc = c.getString(c.getColumnIndex(DatabaseHelper.DESC));
                    String cat = c.getString(c.getColumnIndex(DatabaseHelper.CAT));
                    String img = c.getString(c.getColumnIndex(DatabaseHelper.IMG));
                    all += "<div class=\"col mb-5\"><div class=\"card h-100\">" +
                            "<img class=\"card-img-top\" src=\"" + img + "\" alt=\"...\" />" +
                            "<div class=\"card-body p-4\"><div class=\"text-center\">" +
                            "<h5 class=\"fw-bolder\">" + title +"</h5></div></div>" +
                            "<div class=\"card-footer p-4 pt-0 border-top-0 bg-transparent\">" +
                            "<div class=\"text-center\"><a class=\"btn btn-outline-dark mt-auto\" " +
                            "href=\"producto" + id + "\">Ver opciones</a></div></div> </div> </div>";
                    //all+=c.getString(c.getColumnIndex("title"))+"----";// your calculation goes here
                }
            }
            texto=texto.replace("[PRODUCTO]", all);


            String categorias="<li><a class=\"dropdown-item\" href=\"#!\">Todos los productos</a></li>"+
                                "<li><hr class=\"dropdown-divider\" /></li>";
            Cursor d= db.fetchCategorias();

            if(!d.moveToNext()){
                Toast.makeText(this,"Error : no hay cursor" , Toast.LENGTH_LONG).show();

            }else {
                while (d.moveToNext()) {
                    String cat = d.getString(d.getColumnIndex(DatabaseHelper.CAT));
                    categorias += "<li><a class=\"dropdown-item\" href=\"categoria_"+cat+"\">" + cat + "</a></li>";

                }
            }
                texto=texto.replace("[CATEGORIAS]", categorias);

                String encodedHtml = Base64.encodeToString(texto.getBytes(), Base64.NO_PADDING);
            db.close();
            mWebView.loadDataWithBaseURL("https://appassets.androidplatform.net/assets/", texto,"text/html","base64","");
            //mWebView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
        } else {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Please check your internet connection.", Snackbar.LENGTH_LONG);
            snackbar.show();

        }
    }
    public class Callback extends WebViewClientCompat {
        private final WebViewAssetLoader mAssetLoader;
        private  Activity context=null;
        private  WebView wb=null;
        public Callback(WebViewAssetLoader assetLoader, Activity context) {
            this.context = context;
            mAssetLoader = assetLoader;
        }
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
        public WebResourceResponse shouldInterceptRequest(WebView view,
                                                          WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url.contains("inicio.html")) {
                return mAssetLoader.shouldInterceptRequest(request.getUrl());
            }else if (url.contains("categoria")) {
                String categoria=url.replace("https://appassets.androidplatform.net/assets/categoria_", "");
                categoria=categoria.trim();
                categoria=java.net.URLDecoder.decode(categoria);
                BufferedReader reader = null;
                String texto="";
                try {
                    reader = new BufferedReader(
                            new InputStreamReader(this.context.getAssets().open("categoria.html")));

                    // do reading, usually loop until end of file reading
                    String mLine;
                    while ((mLine = reader.readLine()) != null) {
                        texto += mLine;
                    }
                }catch(IOException e) {
                    //log the exception
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            //log the exception
                        }
                    }
                }
                DBManager db=new DBManager(this.context);
                db.open();
                Cursor c= db.fetchProductoCategoria(categoria);
                String all="";
                if(!c.moveToNext()){
                    //Toast.makeText(this.context,"Error : no hay cursor" , Toast.LENGTH_LONG).show();
                }else {
                    while (c.moveToNext()) {
                        @SuppressLint("Range") int id = c.getInt(c.getColumnIndex(DatabaseHelper._ID));
                        @SuppressLint("Range")String title = c.getString(c.getColumnIndex(DatabaseHelper.TITLE));
                        @SuppressLint("Range")String desc = c.getString(c.getColumnIndex(DatabaseHelper.DESC));
                        @SuppressLint("Range")String cat = c.getString(c.getColumnIndex(DatabaseHelper.CAT));
                        @SuppressLint("Range") String img = c.getString(c.getColumnIndex(DatabaseHelper.IMG));
                        all += "<div class=\"col mb-5\"><div class=\"card h-100\">" +
                                "<img class=\"card-img-top\" src=\"" + img + "\" alt=\"...\" />" +
                                "<div class=\"card-body p-4\"><div class=\"text-center\">" +
                                "<h5 class=\"fw-bolder\">" + title +"</h5></div></div>" +
                                "<div class=\"card-footer p-4 pt-0 border-top-0 bg-transparent\">" +
                                "<div class=\"text-center\"><a class=\"btn btn-outline-dark mt-auto\" " +
                                "href=\"producto" + id + "\">Ver opciones</a></div></div> </div> </div>";
                        //all+=c.getString(c.getColumnIndex("title"))+"----";// your calculation goes here
                    }
                }
                texto=texto.replace("[PRODUCTO]", all);

                String categorias="<li><a class=\"dropdown-item\" href=\"#!\">Todos los productos</a></li>"+
                        "<li><hr class=\"dropdown-divider\" /></li>";
                Cursor d= db.fetchCategorias();

                if(!d.moveToNext()){
                  //  Toast.makeText(this,"Error : no hay cursor" , Toast.LENGTH_LONG).show();

                }else {
                    while (d.moveToNext()) {
                        @SuppressLint("Range")String cat = d.getString(d.getColumnIndex(DatabaseHelper.CAT));
                        categorias += "<li><a class=\"dropdown-item\" href=\"categoria_"+cat+"\">" + cat + "</a></li>";

                    }
                }
                texto=texto.replace("[CATEGORIAS]", categorias);
                final String txt=texto;
                db.close();
                String encodedHtml = Base64.encodeToString(texto.getBytes(), Base64.NO_PADDING);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.loadDataWithBaseURL("https://appassets.androidplatform.net/assets/", txt,"text/html","base64","");

                    }
                });
                return  new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream(txt.getBytes()));

                //return mAssetLoader.shouldInterceptRequest(request.getUrl());
            } else  if (url.contains("producto")) {
                String id=url.replace("https://appassets.androidplatform.net/assets/producto", "");
                id=id.trim();
                id=java.net.URLDecoder.decode(id);
                BufferedReader reader = null;
                String texto="";
                try {
                    reader = new BufferedReader(
                            new InputStreamReader(context.getAssets().open("producto.html")));

                    // do reading, usually loop until end of file reading
                    String mLine;
                    while ((mLine = reader.readLine()) != null) {
                        texto += mLine;
                    }
                }catch(IOException e) {
                    //log the exception
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            //log the exception
                        }
                    }
                }
                DBManager db=new DBManager(context);
                db.open();
                Log.d("ADebugTag", "Value: id producto" + id);
                Cursor c= db.fetchProducto(id);
                String all="";
                 Log.d("ADebugTag", "Value: despues" + c.getInt(c.getColumnIndex(DatabaseHelper._ID)));

                    @SuppressLint("Range") int id1 = c.getInt(c.getColumnIndex(DatabaseHelper._ID));
                        @SuppressLint("Range")String title = c.getString(c.getColumnIndex(DatabaseHelper.TITLE));
                        @SuppressLint("Range")String desc = c.getString(c.getColumnIndex(DatabaseHelper.DESC));
                        @SuppressLint("Range")String cati = c.getString(c.getColumnIndex(DatabaseHelper.CAT));
                        @SuppressLint("Range") String img = c.getString(c.getColumnIndex(DatabaseHelper.IMG));

                        all+="<div class=\"row gx-4 gx-lg-5 align-items-center\">"+
                "   <div class=\"col-md-6\"><img class=\"card-img-top mb-5 mb-md-0\" src=\""+img+ "\" alt=\"...\"></div>"+
                "   <div class=\"col-md-6\">"+
                "       <div class=\"small mb-1\">"+id+"</div>"+
                "       <h1 class=\"display-5 fw-bolder\">"+title+"</h1>"+
              /*  "       <div class=\"fs-5 mb-5\">"+
                "           <span class=\"text-decoration-line-through\">$45.00</span>"+
                "   <span>$40.00</span>"+
                        "</div>"+*/
                        "       <p class=\"lead\">"+desc+"</p>"+
               /* "       <div class=\"d-flex\">"+
                "           <input class=\"form-control text-center me-3\" id=\"inputQuantity\" type=\"num\" value=\"1\" style=\"max-width: 3rem\">"+
                "           <button class=\"btn btn-outline-dark flex-shrink-0\" type=\"button\">"+
                "               <i class=\"bi-cart-fill me-1\"></i>"+
                "       Add to cart"+
                "   </button>"+
                        "     </div>"+*/
                                "     </div>"+
                "</div>";
                   /*     all += "<div class=\"col mb-5\"><div class=\"card h-100\">" +
                                "<img class=\"card-img-top\" src=\"" + img + "\" alt=\"...\" />" +
                                "<div class=\"card-body p-4\"><div class=\"text-center\">" +
                                "<h5 class=\"fw-bolder\">" + title +"</h5></div></div>" +
                                "<div class=\"card-footer p-4 pt-0 border-top-0 bg-transparent\">" +
                                "<div class=\"text-center\"><a class=\"btn btn-outline-dark mt-auto\" " +
                                "href=\"producto" + id + "\">Ver opciones</a></div></div> </div> </div>";
                        //all+=c.getString(c.getColumnIndex("title"))+"----";// your calculation goes here
*/

                texto=texto.replace("[PRODUCTO]", all);
                String categorias="<li><a class=\"dropdown-item\" href=\"#!\">Todos los productos</a></li>"+
                        "<li><hr class=\"dropdown-divider\" /></li>";
                Cursor d= db.fetchCategorias();

                if(!d.moveToNext()){
                    //  Toast.makeText(this,"Error : no hay cursor" , Toast.LENGTH_LONG).show();

                }else {
                    while (d.moveToNext()) {
                        @SuppressLint("Range")String cat = d.getString(d.getColumnIndex(DatabaseHelper.CAT));
                        categorias += "<li><a class=\"dropdown-item\" href=\"categoria_"+cat+"\">" + cat + "</a></li>";

                    }
                }
                texto=texto.replace("[CATEGORIAS]", categorias);
                final String txt=texto;
                db.close();

                String encodedHtml = Base64.encodeToString(texto.getBytes(), Base64.NO_PADDING);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.loadDataWithBaseURL("https://appassets.androidplatform.net/assets/", txt,"text/html","base64","");

                    }
                });
                return  new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream(txt.getBytes()));

                //return  new WebResourceResponse("text/html", "UTF-8", new ByteArrayInputStream(texto.getBytes()));

            }
            return mAssetLoader.shouldInterceptRequest(request.getUrl());

        }

        /*public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.endsWith(".pdf")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }else if (url.contains("mailto:")) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }else if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            }else {
                view.loadUrl(url);
                return true;
            }
        }*/

        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        }


        public void onPageFinished(WebView view, String url) {

        }
    }


    public void onRefresh() {
        mWebView.reload();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}
