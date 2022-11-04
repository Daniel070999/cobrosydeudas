package appcyb.danielpativas.cobrosydeudas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;


import appcyb.danielpativas.cobrosydeudas.entidades.Cobro;
import appcyb.danielpativas.cobrosydeudas.utilidades.Utilidades;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Datos extends AppCompatActivity {

    TextView totalcobros, totaldeudas;
    ArrayList<Cobro> listaCobro;
    BDDcobrosHelper conn;
    String estadoActivo = "Activo";
    String estadoVencido = "Vencido";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);
        Toolbar toolbar = findViewById(R.id.toolbardatos);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cargarcomponentes();
        cargarTotalCobros();
        cargarTotalDeudas();
    }

    private void cargarcomponentes() {
        totalcobros = findViewById(R.id.txttotalcobros);
        totaldeudas = findViewById(R.id.txttotaldeudas);
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
    }

    private void cargarTotalCobros() {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        String solocobro = "Cobro";
        Double cantidad = 0.0;
        Double cantidadaux = 0.0;
        DecimalFormat formato = new DecimalFormat("#.00");
        Cursor cursor = db.rawQuery("SELECT "+ Utilidades.CAMPO_CANTIDAD+" FROM " + Utilidades.TABLA_COBRO
                +" WHERE "+Utilidades.CAMPO_TIPO+ " = '"+solocobro+"'"
                +" AND (" + Utilidades.CAMPO_ESTADO + " = " + "'" + estadoActivo +"'"
                +" OR " + Utilidades.CAMPO_ESTADO + " = " + "'" + estadoVencido +"')"
                , null);
        while (cursor.moveToNext()) {
            cantidad = Double.valueOf(cursor.getString(cursor.getColumnIndex("cantidad")));
            Double suma = (cantidad+cantidadaux);
            cantidadaux = Double.valueOf(formato.format(suma).replace(",",".")+"");
        }
        totalcobros.setText("$"+cantidadaux);
    }
    private void cargarTotalDeudas() {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        String solodeuda = "Deuda";
        Double cantidad = 0.0;
        Double cantidadaux = 0.0;
        DecimalFormat formato = new DecimalFormat("#.00");
        Cursor cursor = db.rawQuery("SELECT "+Utilidades.CAMPO_CANTIDAD+" FROM " + Utilidades.TABLA_COBRO
                +" WHERE "+Utilidades.CAMPO_TIPO+ " = '"+solodeuda+"'"
                +" AND (" + Utilidades.CAMPO_ESTADO + " = " + "'" + estadoActivo +"'"
                        +" OR " + Utilidades.CAMPO_ESTADO + " = " + "'" + estadoVencido +"')"
                , null);
        while (cursor.moveToNext()) {
            cantidad = Double.valueOf(cursor.getString(cursor.getColumnIndex("cantidad")));
            Double suma = (cantidad+cantidadaux);
            cantidadaux = Double.valueOf(formato.format(suma).replace(",",".")+"");
        }
        totaldeudas.setText("$"+cantidadaux);
    }
}