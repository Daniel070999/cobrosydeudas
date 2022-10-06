package com.example.cobrosydeudas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cobrosydeudas.entidades.Permiso;
import com.example.cobrosydeudas.entidades.RegistrodeCobro;
import com.example.cobrosydeudas.utilidades.Utilidades;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DarPermiso extends AppCompatActivity {

    TextView clavepermiso;
    String clave;
    DialogStyle dialogStyle;
    DialogType dialogType;
    DialogAnimation dialogAnimation;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dar_permiso);
        cargarvalores();
        msgcargarAcuerdos();
    }

    private void msgcargarAcuerdos() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogo_acuerdos_de_uso);
        Button si = dialog.findViewById(R.id.btnaceptaracuerdo);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogo_fondo));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.show();
        si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

    }

    private void msgmensajeuno() {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.ERROR;
        dialogAnimation = DialogAnimation.DIAGONAL;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(DarPermiso.this, dialogStyle, dialogType);
        builder.setTitle("Error");
        builder.setMessage("Primero debe escanear el código QR");
        builder.setAnimation(dialogAnimation);
        builder.setDuration(1500);
        builder.show();
    }

    private void msgbienvenido() {
        dialogStyle = DialogStyle.FLASH;
        dialogType = DialogType.SUCCESS;
        dialogAnimation = DialogAnimation.CARD;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(DarPermiso.this, dialogStyle, dialogType);
        builder.setTitle("Bienvenido");
        builder.setMessage("Bienvenido a la aplicación Cobros y Deudas");
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    private void msgclaveincorrecta() {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.ERROR;
        dialogAnimation = DialogAnimation.DIAGONAL;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(DarPermiso.this, dialogStyle, dialogType);
        builder.setTitle("Error");
        builder.setMessage("El código QR no es válido");
        builder.setAnimation(dialogAnimation);
        builder.setDuration(1500);
        builder.show();
    }

    public void cargarvalores(){
        clavepermiso = findViewById(R.id.txtclavepermiso);
    }

    public void abrircamara(View view) {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String datos = result.getContents();
        clavepermiso.setText(datos);
    }

    public void guardarpermiso(View view) {




        clave = clavepermiso.getText().toString();
        if (clave.isEmpty()){
            msgmensajeuno();
        }else if (clave.equals("https://clave@unica#deinicio$a%Cobros&Deudas.com")){
            BDDcobrosHelper conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
            SQLiteDatabase db = conn.getWritableDatabase();
            try {
                ContentValues datos = new ContentValues();
                datos.put(Utilidades.CAMPO_PERMISO_CLAVE, clave);
                db.insert(Utilidades.TABLA_PERMISO, Utilidades.CAMPO_PERMISO_CLAVE, datos);
                msgbienvenido();
                db.close();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                Intent intent = new Intent(DarPermiso.this, Home.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        }, 2500);

            } catch (Exception e) {
                Toast.makeText(this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            msgclaveincorrecta();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void enviarcorreoPermiso(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setData(Uri.parse("email"));
        String[] s={"danielixo99@gmail.com"};
        i.putExtra(Intent.EXTRA_EMAIL,s);
        i.putExtra(Intent.EXTRA_SUBJECT,"App Ccobros y Deudas");
        i.putExtra(Intent.EXTRA_TEXT,"Deseo adquirir la aplicación");
        i.setType("message/rfc822");
        Intent chooser = Intent.createChooser(i,"Launch Email");
        startActivity(chooser);
    }
}