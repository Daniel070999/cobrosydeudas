package appcyb.danielpativas.cobrosydeudas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import appcyb.danielpativas.cobrosydeudas.entidades.Cobro;
import appcyb.danielpativas.cobrosydeudas.utilidades.Utilidades;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.util.ArrayList;



public class SoloCobros extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    ArrayList<Cobro> listaCobro;
    BDDcobrosHelper conn;
    BDDcobrosHelper myDB;
    EditText buscarsolocobros;
    TextView contactoscobros;
    Spinner spinner_nuevocobro;
    Spinner spinner_nuevadeuda;

    DialogStyle dialogStyle;
    DialogType dialogType;
    DialogAnimation dialogAnimation;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_cobros);
        Toolbar toolbar = findViewById(R.id.toolbarsolocobros);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cargarComponentes();
        listarSoloCobros();
        recargar();
        buscarSoloCobroMetodo();
        loadAdBanner();
    }

    private void loadAdBanner(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adViewSoloCobros);
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

    public void cargarComponentes(){
        swipeRefreshLayout = findViewById(R.id.refreshLayoutSoloCobros);
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
        myDB = new BDDcobrosHelper(getApplicationContext(), "db_cobros", null, 1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewSoloCobros);
        buscarsolocobros = findViewById(R.id.txtbuscarsolocobros);
    }

    private void msglistaactualizada() {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.SUCCESS;
        dialogAnimation = DialogAnimation.IN_OUT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Actualizado");
        builder.setMessage("Su lista de cobros ha sido actualizada");
        builder.setDuration(1500);
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    private void buscarSoloCobroMetodo() {
        buscarsolocobros.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    public void filter(String palabra){
        try {
            String solocobro = "Cobro";
            SQLiteDatabase db = conn.getReadableDatabase();
            listaCobro = new ArrayList<>();
            Cobro usuario = null;
            Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO+"" +
                    " WHERE ("+ Utilidades.CAMPO_NOMBRE+ " like '"+palabra+"%' "+"" +
                    " OR "+ Utilidades.CAMPO_APELLIDO+ " like '"+palabra+"%') AND "+Utilidades.CAMPO_TIPO+ " = '"+ solocobro+"'",null);
            while (cursor.moveToNext()) {
                usuario = new Cobro();
                usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
                usuario.setNombre(cursor.getString(cursor.getColumnIndex("nombre")) + " " + cursor.getString(cursor.getColumnIndex("apellido")));
                usuario.setFechacobro(cursor.getString(cursor.getColumnIndex("fechacobro")));
                usuario.setCantidad(cursor.getString(cursor.getColumnIndex("cantidad")));
                usuario.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
                usuario.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
                listaCobro.add(usuario);
            }
            ListaCobroAdapter adapter = new ListaCobroAdapter(listaCobro, this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer iddetalle = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getId();
                    String tip = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getTipo();
                    Intent intent = new Intent(SoloCobros.this, DetallesCobro.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("iddetalle", String.valueOf(iddetalle));
                    bundle.putString("tipo", tip);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);

                }
            });
            recyclerView.setAdapter(adapter);
        }catch (Exception e){
            Toast.makeText(this, "El cobro no existe", Toast.LENGTH_SHORT).show();
            Log.e("error",e.getMessage().toString());
        }
    }

    public void recargar() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listarSoloCobros();
                swipeRefreshLayout.setRefreshing(false);
                msglistaactualizada();
            }
        });
    }

    public void listarSoloCobros(){
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        String solocobro = "Cobro";
        String estadoActivo = "Activo";
        String estadoVencido = "Vencido";
        Cobro usuario = null;
        String id = null;
        String tip = null;
        Cursor cursor = db.rawQuery("SELECT * FROM "
                + Utilidades.TABLA_COBRO
                +" WHERE "
                +Utilidades.CAMPO_TIPO
                + " = '"+solocobro+"'"
                + "AND (("
                +Utilidades.CAMPO_ESTADO
                + " = 'Activo') OR ( "
                + Utilidades.CAMPO_ESTADO
                +" = ' Vencido'))", null);
        while (cursor.moveToNext()) {
            usuario = new Cobro();
            id = usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
            usuario.setNombre(cursor.getString(cursor.getColumnIndex("nombre")) + " " + cursor.getString(cursor.getColumnIndex("apellido")));
            usuario.setFechacobro(cursor.getString(cursor.getColumnIndex("fechacobro")));
            usuario.setCantidad(cursor.getString(cursor.getColumnIndex("cantidad")));
            usuario.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
            usuario.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
            listaCobro.add(usuario);
        }
        ListaCobroAdapter adapter = new ListaCobroAdapter(listaCobro, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer iddetalle = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getId();
                String tip = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getTipo();
                Intent intent = new Intent(SoloCobros.this, DetallesCobro.class);
                Bundle bundle = new Bundle();
                bundle.putString("iddetalle", String.valueOf(iddetalle));
                bundle.putString("tipo", tip);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        });

        recyclerView.setAdapter(adapter);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()){
                int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String numero = cursor.getString(indiceNumero);
                //numero = numero.replace("+593", "0").replace(" ", "");
                contactoscobros.setText(numero);
            }
        }
    }



    public void listarCobrosActivos(View view) {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        String solocobro = "Cobro";
        Cobro usuario = null;
        String id = null;
        String tip = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO +" WHERE "+Utilidades.CAMPO_TIPO+ " = '"+solocobro+"' AND "+Utilidades.CAMPO_ESTADO +" = 'Activo'", null);
        while (cursor.moveToNext()) {
            usuario = new Cobro();
            id = usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
            usuario.setNombre(cursor.getString(cursor.getColumnIndex("nombre")) + " " + cursor.getString(cursor.getColumnIndex("apellido")));
            usuario.setFechacobro(cursor.getString(cursor.getColumnIndex("fechacobro")));
            usuario.setCantidad(cursor.getString(cursor.getColumnIndex("cantidad")));
            usuario.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
            usuario.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
            listaCobro.add(usuario);
        }
        ListaCobroAdapter adapter = new ListaCobroAdapter(listaCobro, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer iddetalle = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getId();
                String tip = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getTipo();
                Intent intent = new Intent(SoloCobros.this, DetallesCobro.class);
                Bundle bundle = new Bundle();
                bundle.putString("iddetalle", String.valueOf(iddetalle));
                bundle.putString("tipo", tip);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public void listarCobrosVencidos(View view) {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        String solocobro = "Cobro";
        Cobro usuario = null;
        String id = null;
        String tip = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO +" WHERE "+Utilidades.CAMPO_TIPO+ " = '"+solocobro+"' AND "+Utilidades.CAMPO_ESTADO +" = 'Vencido'", null);
        while (cursor.moveToNext()) {
            usuario = new Cobro();
            id = usuario.setId(cursor.getInt(cursor.getColumnIndex("id")));
            usuario.setNombre(cursor.getString(cursor.getColumnIndex("nombre")) + " " + cursor.getString(cursor.getColumnIndex("apellido")));
            usuario.setFechacobro(cursor.getString(cursor.getColumnIndex("fechacobro")));
            usuario.setCantidad(cursor.getString(cursor.getColumnIndex("cantidad")));
            usuario.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
            usuario.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
            listaCobro.add(usuario);
        }
        ListaCobroAdapter adapter = new ListaCobroAdapter(listaCobro, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer iddetalle = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getId();
                String tip = listaCobro.get(recyclerView.getChildAdapterPosition(view)).getTipo();
                Intent intent = new Intent(SoloCobros.this, DetallesCobro.class);
                Bundle bundle = new Bundle();
                bundle.putString("iddetalle", String.valueOf(iddetalle));
                bundle.putString("tipo", tip);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this , Todos.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }


}