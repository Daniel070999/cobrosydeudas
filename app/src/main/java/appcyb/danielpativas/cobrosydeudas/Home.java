package appcyb.danielpativas.cobrosydeudas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

import appcyb.danielpativas.cobrosydeudas.entidades.Cobro;
import appcyb.danielpativas.cobrosydeudas.entidades.Permiso;
import appcyb.danielpativas.cobrosydeudas.utilidades.Utilidades;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Home extends AppCompatActivity {

    ArrayList<Cobro> listaCobro;
    ArrayList<Permiso> listapermiso;
    BDDcobrosHelper conn;
    Date fechaactual = null;
    Date fechapasada = null;
    Date fecharegistrada = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        ImageView imageView = findViewById(R.id.home_tema);
        LottieAnimationView lottieAnimationView = findViewById(R.id.home_form);

        lottieAnimationView.setAnimation(animation1);
        imageView.setAnimation(animation2);
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
        File carpeta = new File(getExternalFilesDir(null), "/DatosCobrosyDeudas");
        carpeta.mkdir();
        verificarEstado();
        redireccionar();
    }
    public void redireccionar(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Home.this, Todos.class);
                startActivity(intent);
                finish();
            }
        }, 2500);

    }
    public void verificarEstado(){
        try {
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fechaactualuno = String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date()));
            fechaactual = format.parse(fechaactualuno);

            SQLiteDatabase db = conn.getReadableDatabase();
            listaCobro = new ArrayList<>();
            String id;
            String fecha;
            String estado;
            String estadoData;
            Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO, null);
            while (cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex("id"));
                fecha = cursor.getString(cursor.getColumnIndex("fechacobro"));
                estadoData = cursor.getString(cursor.getColumnIndex("estado"));
                fecharegistrada = format.parse(fecha);
                if ((estadoData.contains("Vencido")) || (estadoData.contains("Activo"))){
                    if (fecharegistrada.compareTo(fechaactual) <= 0){
                        estado = "Vencido";
                        actualizardatos(estado, id);
                    }else if (fecharegistrada.compareTo(fechaactual) > 0){
                        estado = "Activo";
                        actualizardatos(estado, id);
                    }
                }else if (estadoData.contains("Eliminado")){

                }


            }
            db.close();
        } catch (ParseException e) {
        e.printStackTrace();
    }
    }
    public void actualizardatos(String estado, String id){
        SQLiteDatabase dbactualizar = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        Cobro usuario = null;
        Cursor cursoractualizar = dbactualizar.rawQuery("UPDATE " + Utilidades.TABLA_COBRO+  " SET "+ Utilidades.CAMPO_ESTADO+ " = " +"'"+estado+"'" + " WHERE " +Utilidades.CAMPO_ID + " = " +
                ""+ id, null);
        while (cursoractualizar.moveToNext()) {
            usuario = new Cobro();
            listaCobro.add(usuario);
        }
        dbactualizar.close();
    }
}