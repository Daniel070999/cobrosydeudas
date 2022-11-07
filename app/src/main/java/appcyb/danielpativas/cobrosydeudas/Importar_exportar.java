package appcyb.danielpativas.cobrosydeudas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import appcyb.danielpativas.cobrosydeudas.entidades.Cobro;
import appcyb.danielpativas.cobrosydeudas.entidades.RegistrodeCobro;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Importar_exportar extends AppCompatActivity {

    List<Cobro> listaCobros = new ArrayList<>();
    List<RegistrodeCobro> listaRegistros = new ArrayList<>();

    DialogStyle dialogStyle;
    DialogType dialogType;
    DialogAnimation dialogAnimation;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importar_exportar);
        Toolbar toolbar = findViewById(R.id.toobarconfiguraciones);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadAdBanner();
        loadAdIntersticial();
        pedirPermisos();


    }
    private void loadAdIntersticial(){
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-7969764617285750/5048731481", adRequest, new InterstitialAdLoadCallback() {
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

        mAdView = findViewById(R.id.adViewImportarExportar);
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
    private void msgyatienedatos() {
        dialogStyle = DialogStyle.FLAT;
        dialogType = DialogType.WARNING;
        dialogAnimation = DialogAnimation.SWIPE_LEFT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Alerta");
        builder.setMessage("Usted ya tiene datos registrados");
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    private void msgdatosexportados(File carpeta) {

        dialogStyle = DialogStyle.FLAT;
        dialogType = DialogType.SUCCESS;
        dialogAnimation = DialogAnimation.SWIPE_LEFT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Datos exportados");
        builder.setMessage("Se exportaron los datos en la carpeta: " + carpeta);
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    private void msgnohaydatos() {
        dialogStyle = DialogStyle.FLAT;
        dialogType = DialogType.ERROR;
        dialogAnimation = DialogAnimation.SWIPE_LEFT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("No se exporto");
        builder.setMessage("No hay datos registrados");
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    private void msgnoseimporto(File carpeta) {
        dialogStyle = DialogStyle.FLAT;
        dialogType = DialogType.ERROR;
        dialogAnimation = DialogAnimation.SWIPE_LEFT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("No se importó");
        builder.setMessage("No se encontraron copias de seguridad en la carpeta: " + carpeta);
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    private void msgdatosimportados() {
        dialogStyle = DialogStyle.FLAT;
        dialogType = DialogType.SUCCESS;
        dialogAnimation = DialogAnimation.SWIPE_LEFT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Se importó");
        builder.setMessage("Se importo con éxito la copia de seguridad");
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    public void pedirPermisos() {
        if (ContextCompat.checkSelfPermission(Importar_exportar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Importar_exportar.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }


    public void exportarSCV() {


        File carpeta = new File(getExternalFilesDir(null), "/DatosCobrosyDeudas");
        String cobrosydeudas = carpeta.toString() + "/" + "datoscobrosydeudas.csv";
        String registros = carpeta.toString() + "/" + "datosregistros.csv";
        //cobros y deudas
        try {
            BDDcobrosHelper admin = new BDDcobrosHelper(Importar_exportar.this, "bd_cobros", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();

            Cursor fila = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO, null);
            if (fila != null && fila.getCount() != 0) {
                FileWriter fileWriter = new FileWriter(cobrosydeudas);
                fila.moveToFirst();
                do {
                    fileWriter.append(fila.getString(0));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(1));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(2));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(3));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(4));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(5));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(6));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(7));
                    fileWriter.append("\n");
                } while (fila.moveToNext());
                fileWriter.close();
            } else {
            }
            db.close();

        } catch (Exception e) {
            Log.e("error", "" + e.getMessage());
        }
        //registros
        try {

            BDDcobrosHelper admin = new BDDcobrosHelper(Importar_exportar.this, "bd_cobros", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();

            Cursor fila = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_REGISTRO_COBRO, null);
            if (fila != null && fila.getCount() != 0) {
                fila.moveToFirst();
                FileWriter fileWriter = new FileWriter(registros);
                do {
                    fileWriter.append(fila.getString(0));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(1));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(2));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(3));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(4));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(5));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(6));
                    fileWriter.append("\n");
                } while (fila.moveToNext());
                fileWriter.close();
                msgdatosexportados(carpeta);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(Importar_exportar.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            } else {
                msgnohaydatos();
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(Importar_exportar.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
            db.close();

        } catch (Exception e) {
            Log.e("error", "" + e.getMessage());
            Toast.makeText(this, "No se pudo hacer la copia de seguridad", Toast.LENGTH_SHORT).show();
        }

    }

    public void importarCSV() {
        BDDcobrosHelper conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
        SQLiteDatabase dbcon = conn.getReadableDatabase();
        Cobro usuario = null;
        Cursor cursor = dbcon.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO, null);
        while (cursor.moveToNext()) {
            usuario = new Cobro();
            usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
            listaCobros.add(usuario);
        }
        dbcon.close();
        if (listaCobros.size() != 0) {
            msgyatienedatos();
            if (mInterstitialAd != null) {
                mInterstitialAd.show(Importar_exportar.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
        } else {
            File carpeta = new File(getExternalFilesDir(null), "/DatosCobrosyDeudas");
            String archivoCobrosyDeudas = carpeta.toString() + "/" + "datoscobrosydeudas.csv";
            String archivoRegistros = carpeta.toString() + "/" + "datosregistros.csv";
            //importar cobros y deudas
            String cadena;
            String[] arreglo;
            String cadenaregistro;
            String[] arregloregistro;
            try {
                FileReader fileReader = new FileReader(archivoCobrosyDeudas);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((cadena = bufferedReader.readLine()) != null) {
                    arreglo = cadena.split(",");

                    BDDcobrosHelper admin = new BDDcobrosHelper(Importar_exportar.this, "bd_cobros", null, 1);
                    SQLiteDatabase db = admin.getWritableDatabase();

                    ContentValues registro = new ContentValues();

                    registro.put(Utilidades.CAMPO_ID, arreglo[0]);
                    registro.put(Utilidades.CAMPO_NOMBRE, arreglo[1]);
                    registro.put(Utilidades.CAMPO_APELLIDO, arreglo[2]);
                    registro.put(Utilidades.CAMPO_FECHACOBRO, arreglo[3]);
                    registro.put(Utilidades.CAMPO_CANTIDAD, arreglo[4]);
                    registro.put(Utilidades.CAMPO_TIPO, arreglo[5]);
                    registro.put(Utilidades.CAMPO_ESTADO, arreglo[6]);
                    registro.put(Utilidades.CAMPO_CONTACTO, arreglo[7]);

                    listaCobros.add(
                            new Cobro(
                                    arreglo[0],
                                    arreglo[1],
                                    arreglo[2],
                                    arreglo[3],
                                    arreglo[4],
                                    arreglo[5],
                                    arreglo[6],
                                    arreglo[7]
                            )
                    );

                    // los inserto en la base de datos
                    db.insert(Utilidades.TABLA_COBRO, null, registro);
                    db.close();
                }
            } catch (Exception e) {
            }
            try {
                FileReader fileReader = new FileReader(archivoRegistros);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((cadenaregistro = bufferedReader.readLine()) != null) {

                    arregloregistro = cadenaregistro.split(",");

                    BDDcobrosHelper admin = new BDDcobrosHelper(Importar_exportar.this, "bd_cobros", null, 1);
                    SQLiteDatabase db = admin.getWritableDatabase();

                    ContentValues registro = new ContentValues();


                    registro.put(Utilidades.CAMPO_REGISTRO_ID, arregloregistro[0]);
                    registro.put(Utilidades.CAMPO_REGISTRO_CANTIDADAUMENTADA, arregloregistro[1]);
                    registro.put(Utilidades.CAMPO_REGISTRO_NUEVACANTIDADAUMENTADA, arregloregistro[2]);
                    registro.put(Utilidades.CAMPO_REGISTRO_DESCRIPCIONAUMENTO, arregloregistro[3]);
                    registro.put(Utilidades.CAMPO_REGISTRO_FECHAAUMENTADA, arregloregistro[4]);
                    registro.put(Utilidades.CAMPO_REGISTRO_ACCIONREGISTRO, arregloregistro[5]);
                    registro.put(Utilidades.CAMPO_REGISTRO_IDCOBRO, arregloregistro[6]);

                    listaRegistros.add(
                            new RegistrodeCobro(
                                    arregloregistro[0],
                                    arregloregistro[1],
                                    arregloregistro[2],
                                    arregloregistro[3],
                                    arregloregistro[4],
                                    arregloregistro[5],
                                    arregloregistro[6]
                            )
                    );

                    // los inserto en la base de datos
                    db.insert(Utilidades.TABLA_REGISTRO_COBRO, null, registro);

                    db.close();
                }
                msgdatosimportados();
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(Importar_exportar.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            } catch (Exception e) {
                Log.e("error al importar", "" + e.getMessage());
                msgnoseimporto(carpeta);
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(Importar_exportar.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        }
    }

    public void exportarCSV(View view) {
        exportarSCV();
    }

    public void importarCSV(View view) {
        importarCSV();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Todos.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }
}
