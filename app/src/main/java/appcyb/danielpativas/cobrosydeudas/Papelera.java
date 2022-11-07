package appcyb.danielpativas.cobrosydeudas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import appcyb.danielpativas.cobrosydeudas.entidades.Cobro;
import appcyb.danielpativas.cobrosydeudas.utilidades.Utilidades;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Papelera extends AppCompatActivity {


    RecyclerView recyclerView;
    ArrayList<Cobro> listaCobroEliminado;
    BDDcobrosHelper conn;
    ArrayList<Cobro> listaCobro;
    DialogStyle dialogStyle;
    DialogType dialogType;
    DialogAnimation dialogAnimation;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_papelera);
        Toolbar toolbar = findViewById(R.id.toobarpapelera);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewListapapelera);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadAdBanner();
        loadAdIntersticial();
        cargarListaPapelera();
    }

    private void loadAdIntersticial(){
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-7969764617285750/4094780450", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        //Log.d(TAG, "Ad was clicked.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        //Log.d(TAG, "Ad dismissed fullscreen content.");
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        //Log.e(TAG, "Ad failed to show fullscreen content.");
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        //Log.d(TAG, "Ad recorded an impression.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        //Log.d(TAG, "Ad showed fullscreen content.");
                    }
                });
            }
        });
    }
    private void loadAdBanner(){

        mAdView = findViewById(R.id.adViewPapelera);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });
    }

    private void msgeliminado() {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.ERROR;
        dialogAnimation = DialogAnimation.IN_OUT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Eliminado");
        builder.setMessage("El registro fue eliminado permanentemente");
        builder.setDuration(2000);
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    private void msgrestaurado() {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.SUCCESS;
        dialogAnimation = DialogAnimation.IN_OUT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Restaurado");
        builder.setMessage("El registro fue restaurado exitosamente");
        builder.setDuration(2000);
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    public void cargarListaPapelera() {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobroEliminado = new ArrayList<>();
        Cobro usuario = null;
        String id = null;
        String tip = null;
        String estado = "Eliminado";
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO + " WHERE "+ Utilidades.CAMPO_ESTADO + " = " + "'"+estado+"'", null);
        while (cursor.moveToNext()) {
            usuario = new Cobro();
            id = usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
            usuario.setNombre(cursor.getString(cursor.getColumnIndex("nombre")) + " " + cursor.getString(cursor.getColumnIndex("apellido")));
            usuario.setFechacobro(cursor.getString(cursor.getColumnIndex("fechacobro")));
            usuario.setCantidad(cursor.getString(cursor.getColumnIndex("cantidad")));
            usuario.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
            usuario.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
            listaCobroEliminado.add(usuario);
        }
        ListaCobroAdapter adapter = new ListaCobroAdapter(listaCobroEliminado, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer idcobro = listaCobroEliminado.get(recyclerView.getChildAdapterPosition(view)).getId();
                String fecharegistrada = listaCobroEliminado.get(recyclerView.getChildAdapterPosition(view)).getFechacobro();
                String nombreusuario = listaCobroEliminado.get(recyclerView.getChildAdapterPosition(view)).getNombre();

                Dialog dialog;
                dialog = new Dialog(Papelera.this);
                dialog.setContentView(R.layout.dialogo_papelera);
                Button eliminar = dialog.findViewById(R.id.btneliminardefinitivo);
                Button restaurar = dialog.findViewById(R.id.btnrestaurar);
                TextView usuario = dialog.findViewById(R.id.txtusuarioenpapelera);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogo_fondo));
                }
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                dialog.show();

                usuario.setText(nombreusuario);

                eliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eliminarCobro(idcobro);
                        dialog.dismiss();
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(Papelera.this);
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }

                    }
                });
                restaurar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restaurar(idcobro, fecharegistrada);
                        dialog.dismiss();
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(Papelera.this);
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }
                    }
                });


            }
        });

        recyclerView.setAdapter(adapter);
    }
    public void restaurar(Integer idCobro, String fecha){

        msgrestaurado();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    String estado = null;
                    Date fecharegistrada = null;
                    Date fechaactual = null;
                    DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    String fechaactualuno = String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date()));

                    fechaactual = format.parse(fechaactualuno);
                    fecharegistrada = format.parse(fecha);

                    if (fecharegistrada.compareTo(fechaactual) <= 0){
                        estado = "Vencido";
                    }else if(fecharegistrada.compareTo(fechaactual) > 0){
                        estado = "Activo";
                    }
                    SQLiteDatabase db = conn.getReadableDatabase();
                    listaCobro = new ArrayList<>();
                    Cobro usuario = null;
                    Cursor cursor = db.rawQuery("UPDATE " + Utilidades.TABLA_COBRO+  " SET " + Utilidades.CAMPO_ESTADO+ " = " +
                            "'"+estado+"'" +  " WHERE " +Utilidades.CAMPO_ID + " = " +
                            ""+ idCobro, null);
                    while (cursor.moveToNext()) {
                        usuario = new Cobro();
                        listaCobro.add(usuario);
                    }
                    db.close();

                    Intent intent = new Intent(Papelera.this, Todos.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },6000);
    }
    public void eliminarCobro(Integer idcobro){

        msgeliminado();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String idCobro = idcobro.toString();

                SQLiteDatabase db = conn.getReadableDatabase();
                listaCobro = new ArrayList<>();
                Cobro usuario = null;
                Cursor cursor = db.rawQuery("DELETE FROM " + Utilidades.TABLA_COBRO+ " WHERE " +Utilidades.CAMPO_ID + " = "
                        + idCobro, null);
                while (cursor.moveToNext()) {
                    usuario = new Cobro();
                    listaCobro.add(usuario);
                }
                Cursor cursoregistro = db.rawQuery("DELETE FROM " + Utilidades.TABLA_REGISTRO_COBRO+ " WHERE "
                        +Utilidades.CAMPO_REGISTRO_IDCOBRO + " = "+ idCobro, null);
                while (cursoregistro.moveToNext()) {
                    usuario = new Cobro();
                    listaCobro.add(usuario);
                }
                db.close();

                Intent intent = new Intent(Papelera.this, Todos.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        },6000);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Todos.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }
}