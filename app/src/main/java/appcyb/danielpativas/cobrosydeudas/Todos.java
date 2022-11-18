package appcyb.danielpativas.cobrosydeudas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Todos extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Cobro> listaCobro;
    BDDcobrosHelper myDB;
    BDDcobrosHelper conn;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText buscarcobros;
    TextView contactoscobros;
    TextView contactosdeudas;

    DialogStyle dialogStyle;
    DialogType dialogType;
    DialogAnimation dialogAnimation;
    Spinner spinner_nuevocobro;
    Spinner spinner_nuevadeuda;

    private  AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos);
        Toolbar toolbar = findViewById(R.id.toolbarcobro);
        setSupportActionBar(toolbar);
        swipeRefreshLayout = findViewById(R.id.refreshLayout);
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
        myDB = new BDDcobrosHelper(getApplicationContext(), "db_cobros", null, 1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewListacobros);
        buscarcobros = findViewById(R.id.txtbuscarcobros);
//para ad banner
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadAdBanner();
        cargarListCobros();
        recargar();
        buscarcobrosmetodo();
    }
    private void loadAdBanner(){

        mAdView = findViewById(R.id.adView);
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
    private void msgcancelar() {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.WARNING;
        dialogAnimation = DialogAnimation.IN_OUT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Cancelado");
        builder.setMessage("No se realizó ninguna acción");
        builder.setDuration(1500);
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    public void msgagregadocobroodeuda(String tipo, String nombre, String apellido) {
        String tip = null;
        if (tipo.equals("Cobro")){
            tip = "registrado";
        }else if (tipo.equals("Deuda")){
            tip = "registrada";
        }
        dialogStyle = DialogStyle.RAINBOW;
        dialogType = DialogType.SUCCESS;
        dialogAnimation = DialogAnimation.WINDMILL;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle(tipo+" "+tip);
        builder.setMessage("Usuario "+nombre +" "+apellido+" registrado con éxito");
        builder.setAnimation(dialogAnimation);
        builder.setDuration(2000);
        builder.show();
    }


    public void buscarcobrosmetodo(){
        buscarcobros.addTextChangedListener(new TextWatcher() {
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

        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        Cobro usuario = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO+"" +
                " WHERE "+ Utilidades.CAMPO_NOMBRE+ " like '"+palabra+"%' "+"" +
                " OR "+ Utilidades.CAMPO_APELLIDO+ " like '"+palabra+"%'" ,null);
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
                    Intent intent = new Intent(Todos.this, DetallesCobro.class);
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
                cargarListCobros();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void cargarListCobros() {
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        Cobro usuario = null;
        String id = null;
        String tip = null;
        String estadoActivo = "Activo";
        String estadoVencido = "Vencido";
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO
                + " WHERE " + Utilidades.CAMPO_ESTADO + " = " + "'" + estadoActivo + "'" + " OR "
                + Utilidades.CAMPO_ESTADO + " = " + "'" + estadoVencido + "'"  , null);
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
                    Intent intent = new Intent(Todos.this, DetallesCobro.class);
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

    private void guardardatosnuevocobro(String nombre, String apellido, String calendario, String cantidad, String descripcionregistro) {
        try {
            String tipo = "Cobro";
            String estado=null;
            String numeroContacto = contactoscobros.getText().toString();
            if (numeroContacto.isEmpty()){
                numeroContacto = "Sin registrar";
            }
            Date fecharegistrada = null;
            Date fechaactualnuevocobro = null;
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fechaactualuno = String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date()));

            fechaactualnuevocobro = format.parse(fechaactualuno);

            fecharegistrada = (format.parse(calendario));

            if (fecharegistrada.compareTo(fechaactualnuevocobro) <= 0){
                estado = "Vencido";
            }else if(fecharegistrada.compareTo(fechaactualnuevocobro) > 0){
                estado = "Activo";
            }
            BDDcobrosHelper conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
            SQLiteDatabase db = conn.getWritableDatabase();
            try {
                ContentValues datos = new ContentValues();
                datos.put(Utilidades.CAMPO_NOMBRE, nombre);
                datos.put(Utilidades.CAMPO_APELLIDO, apellido);
                datos.put(Utilidades.CAMPO_FECHACOBRO, calendario);
                datos.put(Utilidades.CAMPO_CANTIDAD, cantidad);
                datos.put(Utilidades.CAMPO_TIPO, tipo);
                datos.put(Utilidades.CAMPO_ESTADO, estado);
                datos.put(Utilidades.CAMPO_CONTACTO, numeroContacto);
                Long idResultante = db.insert(Utilidades.TABLA_COBRO, Utilidades.CAMPO_NOMBRE, datos);

                msgagregadocobroodeuda(tipo, nombre, apellido);
                    db.close();
                SQLiteDatabase dbregistro = conn.getReadableDatabase();
                RegistrodeCobro usuario = null;
                String idnuevo = null;
                Cursor cursorregistro = dbregistro.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO , null);
                while (cursorregistro.moveToNext()) {
                    usuario = new RegistrodeCobro();
                    idnuevo = usuario.setIdcobro(cursorregistro.getString(cursorregistro.getColumnIndex("id")));
                }

                //creacion del primer registro
                String fechaactual = String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date()));
                String accionregistro = "Pago registrado:";
                BDDcobrosHelper connregistro = new BDDcobrosHelper(Todos.this, "bd_cobros", null, 1);
                db = connregistro.getWritableDatabase();
                ContentValues datosregistro = new ContentValues();
                datosregistro.put(Utilidades.CAMPO_REGISTRO_CANTIDADAUMENTADA, "0");
                datosregistro.put(Utilidades.CAMPO_REGISTRO_NUEVACANTIDADAUMENTADA, cantidad);
                datosregistro.put(Utilidades.CAMPO_REGISTRO_DESCRIPCIONAUMENTO, descripcionregistro);
                datosregistro.put(Utilidades.CAMPO_REGISTRO_FECHAAUMENTADA, fechaactual);
                datosregistro.put(Utilidades.CAMPO_REGISTRO_ACCIONREGISTRO, accionregistro);
                datosregistro.put(Utilidades.CAMPO_REGISTRO_IDCOBRO, idnuevo);
                db.insert(Utilidades.TABLA_REGISTRO_COBRO, Utilidades.CAMPO_REGISTRO_IDCOBRO, datosregistro);
                db.close();
                    cargarListCobros();
                } catch (Exception e) {
                    Toast.makeText(this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void guardardatosnuevadeuda(String nombre, String apellido, String calendario, String cantidad, String descripcionregistro) {
        try {
            String tipo = "Deuda";
            String estado=null;
            String numeroContacto = contactosdeudas.getText().toString();
            if (numeroContacto.isEmpty()){
                numeroContacto = "Sin registrar";
            }
            Date fecharegistrada = null;
            Date fechaactualnuevdeuda = null;
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fechaactualuno = String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date()));
            fechaactualnuevdeuda = format.parse(fechaactualuno);
            fecharegistrada = (format.parse(calendario));
            if (fecharegistrada.compareTo(fechaactualnuevdeuda) <= 0){
                estado = "Vencido";
            }else if(fecharegistrada.compareTo(fechaactualnuevdeuda) > 0){
                estado = "Activo";
            }
            BDDcobrosHelper conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
            SQLiteDatabase db = conn.getWritableDatabase();
            try {
                ContentValues datos = new ContentValues();
                datos.put(Utilidades.CAMPO_NOMBRE, nombre);
                datos.put(Utilidades.CAMPO_APELLIDO, apellido);
                datos.put(Utilidades.CAMPO_FECHACOBRO, calendario);
                datos.put(Utilidades.CAMPO_CANTIDAD, cantidad);
                datos.put(Utilidades.CAMPO_TIPO, tipo);
                datos.put(Utilidades.CAMPO_ESTADO, estado);
                datos.put(Utilidades.CAMPO_CONTACTO, numeroContacto);
                Long idResultante = db.insert(Utilidades.TABLA_COBRO, Utilidades.CAMPO_NOMBRE, datos);
                msgagregadocobroodeuda(tipo, nombre, apellido);
                db.close();
                SQLiteDatabase dbregistro = conn.getReadableDatabase();
                RegistrodeCobro usuario = null;
                String idnuevo = null;
                Cursor cursorregistro = dbregistro.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO , null);
                while (cursorregistro.moveToNext()) {
                    usuario = new RegistrodeCobro();
                    idnuevo = usuario.setIdcobro(cursorregistro.getString(cursorregistro.getColumnIndex("id")));
                }

                //creacion del primer registro
                String fechaactual = String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date()));
                String accionregistro = "Pago registrado:";
                BDDcobrosHelper connregistro = new BDDcobrosHelper(Todos.this, "bd_cobros", null, 1);
                db = connregistro.getWritableDatabase();
                ContentValues datosregistro = new ContentValues();
                datosregistro.put(Utilidades.CAMPO_REGISTRO_CANTIDADAUMENTADA, "0");
                datosregistro.put(Utilidades.CAMPO_REGISTRO_NUEVACANTIDADAUMENTADA, cantidad);
                datosregistro.put(Utilidades.CAMPO_REGISTRO_DESCRIPCIONAUMENTO, descripcionregistro);
                datosregistro.put(Utilidades.CAMPO_REGISTRO_FECHAAUMENTADA, fechaactual);
                datosregistro.put(Utilidades.CAMPO_REGISTRO_ACCIONREGISTRO, accionregistro);
                datosregistro.put(Utilidades.CAMPO_REGISTRO_IDCOBRO, idnuevo);
                db.insert(Utilidades.TABLA_REGISTRO_COBRO, Utilidades.CAMPO_REGISTRO_IDCOBRO, datosregistro);
                db.close();
                cargarListCobros();
            } catch (Exception e) {
                Toast.makeText(this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutodos, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemsolocobros:
                Intent intentcobros = new Intent(this, SoloCobros.class);
                startActivity(intentcobros);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                break;
            case R.id.itemsolodeudas:
                Intent intentdeudas = new Intent(this, SoloDeudas.class);
                startActivity(intentdeudas);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                break;
            case R.id.itemdatos:
                Intent intentdatos = new Intent(this, Datos.class);
                startActivity(intentdatos);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                break;
            case R.id.itempapelera:
                Intent intentpapelera = new Intent(this, Papelera.class);
                startActivity(intentpapelera);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                break;
            case R.id.itemconfiguraciones:
                Intent intentconfiguraciones = new Intent(this, Importar_exportar.class);
                startActivity(intentconfiguraciones);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                break;
            case R.id.itemacercade:
                Intent intentacercade = new Intent(this, AcercadelaApp.class);
                startActivity(intentacercade);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void agregarnuevocobro(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_Design_BottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.agregar_nuevo_cobro,
                (LinearLayout) findViewById(R.id.btnlinearlayout)
        );

        spinner_nuevocobro = bottomSheetView.findViewById(R.id.spinner_nuevocobro);
        TextView calendario = bottomSheetView.findViewById(R.id.txtfechacobro);
        contactoscobros = bottomSheetView.findViewById(R.id.txtcontactocobro);
        EditText nombre = bottomSheetView.findViewById(R.id.txtnombrecobro);
        EditText apellido = bottomSheetView.findViewById(R.id.txtapellidocobro);
        EditText cantidad = bottomSheetView.findViewById(R.id.txtcantidadcobro);
        EditText descripcion = bottomSheetView.findViewById(R.id.txtdescripcioncobro);

        Button guardar = bottomSheetView.findViewById(R.id.btnguardarnuevocobro);
        Button cancelar = bottomSheetView.findViewById(R.id.btncancelarnuevocobro);
        //creación de variables para el spinner
        ArrayAdapter<CharSequence> adapterspinner = ArrayAdapter.createFromResource(this, R.array.spinner_cantidades, android.R.layout.simple_spinner_item);
        adapterspinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nuevocobro.setAdapter(adapterspinner);
        spinner_nuevocobro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cant_seleccionada = parent.getItemAtPosition(position).toString();
                if (cant_seleccionada.equals("Seleccione")){
                    cantidad.setText("");
                }else if(cant_seleccionada.equals("Otro...")) {
                    Toast.makeText(Todos.this, "Porfavor, ingrese el valor", Toast.LENGTH_SHORT).show();
                    cantidad.setText("");
                }else{
                    cantidad.setText(cant_seleccionada);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //abre contactos
        contactoscobros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });

        //abre calendario
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int anio = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Todos.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                        calendario.setText(fecha);


                    }
                }, anio, mes, dia);
                datePickerDialog.show();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreg = nombre.getText().toString();
                String apellidog = apellido.getText().toString();
                String calendariog = calendario.getText().toString();
                String cantidadg = cantidad.getText().toString();
                String descripciong = descripcion.getText().toString();

                if (nombreg.isEmpty()) {
                    nombre.setError("Debe ingresar un nombre");
                } else if (apellidog.isEmpty()) {
                    apellido.setError("Debe ingresar un apellido");
                } else if (calendariog.isEmpty()) {
                    calendario.setError("Debe seleccionar una fecha");
                } else if (cantidadg.isEmpty()) {
                    cantidad.setError("Debe ingresar la cantidad");
                } else if (descripciong.isEmpty()) {
                    descripcion.setError("Debe ingresar una descripción");
                } else {
                    guardardatosnuevocobro(nombreg, apellidog, calendariog, cantidadg, descripciong);
                    bottomSheetDialog.dismiss();
                }
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgcancelar();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    //para los contactos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()){
                int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String numero = cursor.getString(indiceNumero);
                //numero = numero.replace("+593", "").replace(" ", "");
                contactoscobros.setText(numero);
            }
        }else if (requestCode == 2 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()){
                int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String numero = cursor.getString(indiceNumero);
                //numero = numero.replace("+593", "0").replace(" ", "");
                contactosdeudas.setText(numero);
            }
        }
    }

    public void agregarnuevadeuda(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_Design_BottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.agregar_nueva_deuda,
                (LinearLayout) findViewById(R.id.btnlinearlayout)
        );

        spinner_nuevadeuda = bottomSheetView.findViewById(R.id.spinner_nuevadeuda);
        TextView calendario = bottomSheetView.findViewById(R.id.txtfechadeuda);
        contactosdeudas = bottomSheetView.findViewById(R.id.txtcontactodeuda);
        EditText nombre = bottomSheetView.findViewById(R.id.txtnombredeuda);
        EditText apellido = bottomSheetView.findViewById(R.id.txtapellidodeuda);
        EditText cantidad = bottomSheetView.findViewById(R.id.txtcantidaddeuda);
        EditText descripcion = bottomSheetView.findViewById(R.id.txtdescripciondeuda);

        Button guardardeuda = bottomSheetView.findViewById(R.id.btnguardarnuevodeuda);
        Button cancelardeuda = bottomSheetView.findViewById(R.id.btncancelarnuevodeuda);

        //creación de variables para el spinner
        ArrayAdapter<CharSequence> adapterspinner = ArrayAdapter.createFromResource(this, R.array.spinner_cantidades, android.R.layout.simple_spinner_item);
        adapterspinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_nuevadeuda.setAdapter(adapterspinner);
        spinner_nuevadeuda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cant_seleccionada = parent.getItemAtPosition(position).toString();
                if (cant_seleccionada.equals("Seleccione")){
                    cantidad.setText("");
                }else if(cant_seleccionada.equals("Otro...")) {
                    Toast.makeText(Todos.this, "Porfavor, ingrese el valor", Toast.LENGTH_SHORT).show();
                    cantidad.setText("");
                }else{
                    cantidad.setText(cant_seleccionada);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //abre contactos
        contactosdeudas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 2);
            }
        });
        //abre calendario
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int anio = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Todos.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                        calendario.setText(fecha);
                    }
                }, anio, mes, dia);
                datePickerDialog.show();
            }
        });

        guardardeuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreg = nombre.getText().toString();
                String apellidog = apellido.getText().toString();
                String calendariog = calendario.getText().toString();
                String cantidadg = cantidad.getText().toString();
                String descripciong = descripcion.getText().toString();

                if (nombreg.isEmpty()) {
                    nombre.setError("Debe ingresar un nombre");
                } else if (apellidog.isEmpty()) {
                    apellido.setError("Debe ingresar un apellido");
                } else if (calendariog.isEmpty()) {
                    calendario.setError("Debe seleccionar una fecha");
                } else if (cantidadg.isEmpty()) {
                    cantidad.setError("Debe ingresar la cantidad");
                } else if (descripciong.isEmpty()) {
                    descripcion.setError("Debe ingresar una descripción");
                } else {
                    guardardatosnuevadeuda(nombreg, apellidog, calendariog, cantidadg, descripciong);
                    bottomSheetDialog.dismiss();
                }
            }
        });
        cancelardeuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgcancelar();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}