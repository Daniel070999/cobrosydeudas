package com.example.cobrosydeudas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.icu.number.Precision;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cobrosydeudas.entidades.Cobro;
import com.example.cobrosydeudas.entidades.Permiso;
import com.example.cobrosydeudas.entidades.RegistrodeCobro;
import com.example.cobrosydeudas.utilidades.Utilidades;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DetallesCobro extends AppCompatActivity {

    TextView nombreD, fechacobroD, cantidadD, estadoD, contactosD, contactosModificar, signonuevo;
    BDDcobrosHelper conn;

    ArrayList<Cobro> listaCobro;
    ArrayList<RegistrodeCobro> listaregistrocobro;
    Dialog dialog;
    String idCobro, tip;
    String nuevacantidadingresada;
    RecyclerView recyclerViewregistro;
    Toolbar toolbar;
    DialogStyle dialogStyle;
    DialogType dialogType;
    DialogAnimation dialogAnimation;
    Spinner spinner_aumentarcantidad;
    Spinner spinner_registrarpago;
    

    String nombre = null;
    String apellido = null;
    String fechacobro = null;
    String cantidad = null;
    String descripcion = null;
    String estado = null;
    String contactos = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_cobro);
        toolbar = findViewById(R.id.toobardetallescobro);
        Bundle bundle = this.getIntent().getExtras();
        tip = bundle.getString("tipo");
        toolbar.setTitle("Detalles de "+tip);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listarRegistros();
        cargarValores();
        llenarCampos();
    }


    private void msgregistroeliminado() {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.ERROR;
        dialogAnimation = DialogAnimation.IN_OUT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Eliminado");
        builder.setMessage("El registro ha sido eliminado correctamente");
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    private void msgnohaynumero() {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.ERROR;
        dialogAnimation = DialogAnimation.IN_OUT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Error");
        builder.setMessage("No hay un número registrado");
        builder.setDuration(1500);
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    private void msgmodiicar() {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.SUCCESS;
        dialogAnimation = DialogAnimation.SHRINK;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Modificado");
        builder.setMessage("El registro ha sido modificado correctamente");
        builder.setDuration(1500);
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    private void msgagregarcantidad(String nuevacantidadingresada) {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.SUCCESS;
        dialogAnimation = DialogAnimation.SHRINK;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Cantidad aumentada");
        builder.setMessage("Se aumentado $"+nuevacantidadingresada+" correctamente");
        builder.setDuration(1500);
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    private void msgnuevopago(String nuevacantidadingresada) {
        dialogStyle = DialogStyle.TOASTER;
        dialogType = DialogType.SUCCESS;
        dialogAnimation = DialogAnimation.SHRINK;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Pago registrado");
        builder.setMessage("Se registro el pago de $"+nuevacantidadingresada+" correctamente");
        builder.setDuration(1500);
        builder.setAnimation(dialogAnimation);
        builder.show();
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

    public void cargarValores(){
        Bundle bundle = this.getIntent().getExtras();
        idCobro = bundle.getString("iddetalle");
        contactosD = findViewById(R.id.txtnumerodetalle);
        nombreD = findViewById(R.id.txtnombredetallecobro);
        fechacobroD = findViewById(R.id.txtfechacobrodetalle);
        cantidadD = findViewById(R.id.txtcantidadcobrodetalle);
        estadoD = findViewById(R.id.txtestadodetallecobro);
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
        recyclerViewregistro = (RecyclerView) findViewById(R.id.recyclerviewListaRegistro);
        listaregistrocobro = new ArrayList<>();
    }

    public void  llenarCampos(){
        SQLiteDatabase db = conn.getReadableDatabase();
        listaCobro = new ArrayList<>();
        Cobro usuario = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_COBRO+ " WHERE " +Utilidades.CAMPO_ID + " = "+ idCobro, null);
        while (cursor.moveToNext()) {
            usuario = new Cobro();
            nombre = usuario.setNombre(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_NOMBRE)));
            apellido = usuario.setApellido(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_APELLIDO)));
            fechacobro = usuario.setFechacobro(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_FECHACOBRO)));
            cantidad = usuario.setCantidad(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_CANTIDAD)));
            estado = usuario.setEstado(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_ESTADO)));
            descripcion = usuario.setDescripcion(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_TIPO)));
            contactos = usuario.setContacto(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_CONTACTO)));
            listaCobro.add(usuario);
        }
        db.close();
        nombreD.setText(nombre+" "+apellido);
        fechacobroD.setText(fechacobro);
        cantidadD.setText(cantidad);
        estadoD.setText(estado);
        contactosD.setText(contactos);

    }

    public void listarRegistros(){
        Bundle bundle = this.getIntent().getExtras();
        idCobro = bundle.getString("iddetalle");
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
        SQLiteDatabase db = conn.getReadableDatabase();
        listaregistrocobro = new ArrayList<>();
        RegistrodeCobro usuario = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_REGISTRO_COBRO +" WHERE "+ Utilidades.CAMPO_REGISTRO_IDCOBRO + " = "+ idCobro+ " ORDER BY "+Utilidades.CAMPO_REGISTRO_ID+ " DESC ", null);
        while (cursor.moveToNext()) {
            usuario = new RegistrodeCobro();
            usuario.setCantidadaumentada(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_REGISTRO_CANTIDADAUMENTADA)));
            usuario.setNuevacantidadaumentada(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_REGISTRO_NUEVACANTIDADAUMENTADA)));
            usuario.setDescripcionaumento(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_REGISTRO_DESCRIPCIONAUMENTO)));
            usuario.setFechaaumentada(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_REGISTRO_FECHAAUMENTADA)));
            usuario.setAccionregistro(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_REGISTRO_ACCIONREGISTRO)));
            listaregistrocobro.add(usuario);
        }
        recyclerViewregistro = (RecyclerView) findViewById(R.id.recyclerviewListaRegistro);
        ListaRegristroAdapter adapter = new ListaRegristroAdapter(listaregistrocobro, this);
        recyclerViewregistro.setHasFixedSize(true);
        recyclerViewregistro.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewregistro.setAdapter(adapter);

        }

    public void registrarnuevopago(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_Design_BottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.registrar_un_nuevo_pago,
                (LinearLayout) findViewById(R.id.btnlinearlayoutregistrarpago)
        );
        spinner_registrarpago = bottomSheetView.findViewById(R.id.spinner_registrarpago);
        TextView cantidadactualregistro = bottomSheetView.findViewById(R.id.txtcantidadactualregistrarpago);
        TextView futuracantidadregistro = bottomSheetView.findViewById(R.id.txtnuevacantidadfuturaregistrarpago);
        EditText nuevacantidadregistro = bottomSheetView.findViewById(R.id.txtnuevacantidadregistrarpago);
        EditText descripcionaumentoregistro = bottomSheetView.findViewById(R.id.txtdescripcionregistrarpago);
        cantidadactualregistro.setText(cantidad);

        //creación de variables para el spinner
        ArrayAdapter<CharSequence> adapterspinner = ArrayAdapter.createFromResource(this, R.array.spinner_cantidades, android.R.layout.simple_spinner_item);
        adapterspinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_registrarpago.setAdapter(adapterspinner);
        spinner_registrarpago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cant_seleccionada = parent.getItemAtPosition(position).toString();
                if (cant_seleccionada.equals("Seleccione")){
                    nuevacantidadregistro.setText("");
                }else if(cant_seleccionada.equals("Otro...")) {
                    Toast.makeText(DetallesCobro.this, "Porfavor, ingrese el valor", Toast.LENGTH_SHORT).show();
                    nuevacantidadregistro.setText("");
                }else{
                    nuevacantidadregistro.setText(cant_seleccionada);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button aumentarregistro = bottomSheetView.findViewById(R.id.btnregistrarpago);
        Button cancelarregistro = bottomSheetView.findViewById(R.id.btncancelarregistrarpago);
        nuevacantidadregistro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    nuevacantidadingresada = nuevacantidadregistro.getText().toString();
                    String cantidadacutal = cantidad;
                    String nuevacantidadlocal  = s.toString();
                    Double datouno = Double.parseDouble(cantidadacutal);
                    Double datodos = Double.parseDouble(nuevacantidadlocal);
                    Double suma = (datouno-datodos);
                    DecimalFormat formato = new DecimalFormat("#.00");
                    futuracantidadregistro.setText(formato.format(suma).replace(",", ".")+"");
                }catch (Exception e){
                }
            }
        });

        aumentarregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String validardescripcion = descripcionaumentoregistro.getText().toString();
                nuevacantidadingresada = nuevacantidadregistro.getText().toString();
                if (nuevacantidadingresada.isEmpty()) {
                    nuevacantidadregistro.setError("Debe ingresar un valor");
                } else if(validardescripcion.isEmpty()) {
                    descripcionaumentoregistro.setError("Debe ingresar una descipción");
                }else{
                    String futuracantidadingresada = futuracantidadregistro.getText().toString();
                    String nuevadescripcioningresada = descripcionaumentoregistro.getText().toString();
                    String fechaactual = String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date()));
                    String accionregistro = "Pago registrado:";

                    BDDcobrosHelper conn = new BDDcobrosHelper(DetallesCobro.this, "bd_cobros", null, 1);
                    SQLiteDatabase db = conn.getWritableDatabase();
                    try {
                        ContentValues datos = new ContentValues();
                        datos.put(Utilidades.CAMPO_REGISTRO_CANTIDADAUMENTADA, nuevacantidadingresada);
                        datos.put(Utilidades.CAMPO_REGISTRO_NUEVACANTIDADAUMENTADA, futuracantidadingresada);
                        datos.put(Utilidades.CAMPO_REGISTRO_DESCRIPCIONAUMENTO, nuevadescripcioningresada);
                        datos.put(Utilidades.CAMPO_REGISTRO_FECHAAUMENTADA, fechaactual);
                        datos.put(Utilidades.CAMPO_REGISTRO_ACCIONREGISTRO, accionregistro);
                        datos.put(Utilidades.CAMPO_REGISTRO_IDCOBRO, idCobro);
                        db.insert(Utilidades.TABLA_REGISTRO_COBRO, Utilidades.CAMPO_REGISTRO_IDCOBRO, datos);
                        listaCobro = new ArrayList<>();
                        Cobro usuario = null;
                        Cursor cursor = db.rawQuery("UPDATE " + Utilidades.TABLA_COBRO+  " SET "+Utilidades.CAMPO_CANTIDAD+ " = " +
                                "" +"'"+futuracantidadingresada+"'" + " WHERE " +Utilidades.CAMPO_ID + " = " +
                                ""+ idCobro, null);
                        while (cursor.moveToNext()) {
                            usuario = new Cobro();
                            listaCobro.add(usuario);
                        }
                        listarRegistros();
                        llenarCampos();
                        msgnuevopago(nuevacantidadingresada);
                        db.close();
                    } catch (Exception e) {
                        Toast.makeText(DetallesCobro.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    bottomSheetDialog.dismiss();
                }
            }
        });
        cancelarregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgcancelar();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    public void aumentarcantidad(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_Design_BottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.aumentar_cantidad_cobro,
                (LinearLayout) findViewById(R.id.btnlinearlayoutaumentarcantidad)
        );
        spinner_aumentarcantidad = bottomSheetView.findViewById(R.id.spinner_aumenarcantidad);

        TextView cantidadactual = bottomSheetView.findViewById(R.id.txtcantidadactualaumentarcantidad);
        TextView futuracantidad = bottomSheetView.findViewById(R.id.txtnuevacantidadfuturaaumentarcantidad);
        EditText nuevacantidad = bottomSheetView.findViewById(R.id.txtnuevacantidadaumenarcantidad);
        EditText descripcionaumento = bottomSheetView.findViewById(R.id.txtdescripcionaumentarcantidad);
        cantidadactual.setText(cantidad);

        //creación de variables para el spinner
        ArrayAdapter<CharSequence> adapterspinner = ArrayAdapter.createFromResource(this, R.array.spinner_cantidades, android.R.layout.simple_spinner_item);
        adapterspinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_aumentarcantidad.setAdapter(adapterspinner);
        spinner_aumentarcantidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cant_seleccionada = parent.getItemAtPosition(position).toString();
                if (cant_seleccionada.equals("Seleccione")){
                    nuevacantidad.setText("");
                }else if(cant_seleccionada.equals("Otro...")) {
                    Toast.makeText(DetallesCobro.this, "Porfavor, ingrese el valor", Toast.LENGTH_SHORT).show();
                    nuevacantidad.setText("");
                }else{
                    nuevacantidad.setText(cant_seleccionada);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button aumentar = bottomSheetView.findViewById(R.id.btnaumentarcantidad);
        Button cancelar = bottomSheetView.findViewById(R.id.btncancelaraumentarcantidad);
        nuevacantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    nuevacantidadingresada = nuevacantidad.getText().toString();
                    String cantidadacutal = cantidad;
                    String nuevacantidadlocal  = s.toString();
                    Double datouno = Double.parseDouble(cantidadacutal);
                    Double datodos = Double.parseDouble(nuevacantidadlocal);

                    Double suma = (datouno+datodos);
                    DecimalFormat formato = new DecimalFormat("#.00");
                    futuracantidad.setText(formato.format(suma).replace(",",".")+"");
                }catch (Exception e){
                }
            }
        });
        aumentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String validardescripcion = descripcionaumento.getText().toString();
                nuevacantidadingresada = nuevacantidad.getText().toString();
                if (nuevacantidadingresada.isEmpty()) {
                    nuevacantidad.setError("Debe ingresar un valor");
                 } else if(validardescripcion.isEmpty()) {
                    descripcionaumento.setError("Debe ingresar una descipción");
                }else{
                    String futuracantidadingresada = futuracantidad.getText().toString();
                    String nuevadescripcioningresada = descripcionaumento.getText().toString();
                    String fechaactual = String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date()));
                    String accionregistro = "Cantidad aumentada:";
                    BDDcobrosHelper conn = new BDDcobrosHelper(DetallesCobro.this, "bd_cobros", null, 1);
                    SQLiteDatabase db = conn.getWritableDatabase();
                    try {
                        ContentValues datos = new ContentValues();
                        datos.put(Utilidades.CAMPO_REGISTRO_CANTIDADAUMENTADA, nuevacantidadingresada);
                        datos.put(Utilidades.CAMPO_REGISTRO_NUEVACANTIDADAUMENTADA, futuracantidadingresada);
                        datos.put(Utilidades.CAMPO_REGISTRO_DESCRIPCIONAUMENTO, nuevadescripcioningresada);
                        datos.put(Utilidades.CAMPO_REGISTRO_FECHAAUMENTADA, fechaactual);
                        datos.put(Utilidades.CAMPO_REGISTRO_ACCIONREGISTRO, accionregistro);
                        datos.put(Utilidades.CAMPO_REGISTRO_IDCOBRO, idCobro);
                        db.insert(Utilidades.TABLA_REGISTRO_COBRO, Utilidades.CAMPO_REGISTRO_IDCOBRO, datos);
                        listaCobro = new ArrayList<>();
                        Cobro usuario = null;
                        Cursor cursor = db.rawQuery("UPDATE " + Utilidades.TABLA_COBRO+  " SET "+Utilidades.CAMPO_CANTIDAD+ " = " +
                                "" +"'"+futuracantidadingresada+"'" + " WHERE " +Utilidades.CAMPO_ID + " = " +
                                ""+ idCobro, null);
                        while (cursor.moveToNext()) {
                            usuario = new Cobro();
                            listaCobro.add(usuario);
                        }
                        listarRegistros();
                        llenarCampos();
                        msgagregarcantidad(nuevacantidadingresada);
                        db.close();
                    } catch (Exception e) {
                        Toast.makeText(DetallesCobro.this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
    public void modificardatoscobro(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_Design_BottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.modificar_datos_cobro,
                (LinearLayout) findViewById(R.id.btnlinearlayoutmodificardatos)
        );
        TextView calendariodialog = bottomSheetView.findViewById(R.id.txtfechacobromodificar);
        contactosModificar = bottomSheetView.findViewById(R.id.txtcontactomodificar);
        EditText nombredialog = bottomSheetView.findViewById(R.id.txtnombrecobromodificar);
        EditText apellidodialog = bottomSheetView.findViewById(R.id.txtapellidocobromodificar);
        TextView cantidaddialog = bottomSheetView.findViewById(R.id.txtcantidadcobromodificar);
        EditText descripciondialog = bottomSheetView.findViewById(R.id.txtdescripcioncobromodificar);
        contactosModificar.setText(contactos);
        calendariodialog.setText(fechacobro);
        nombredialog.setText(nombre);
        apellidodialog.setText(apellido);
        cantidaddialog.setText(cantidad);
        descripciondialog.setText(descripcion);
        Button guardar = bottomSheetView.findViewById(R.id.btnguardarcobromodificar);
        Button cancelar = bottomSheetView.findViewById(R.id.btncancelarcobromodificar);
        //abre contactos
        contactosModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });

        //abre calendario
        calendariodialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int anio = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(DetallesCobro.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                        calendariodialog.setText(fecha);
                    }
                }, anio, mes, dia);
                datePickerDialog.show();
            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombrem = nombredialog.getText().toString();
                String apellidom = apellidodialog.getText().toString();
                String calendariom = calendariodialog.getText().toString();
                String cantidadm = cantidaddialog.getText().toString();
                String descripcionm = descripciondialog.getText().toString();
                String contactom = contactosModificar.getText().toString();
                if (nombrem.isEmpty()) {
                    nombredialog.setError("Debe ingresar un nombre");
                } else if (apellidom.isEmpty()) {
                    apellidodialog.setError("Debe ingresar un apellido");
                } else if (calendariom.isEmpty()) {
                    calendariodialog.setError("Debe seleccionar una fecha");
                } else if (descripcionm.isEmpty()) {
                    descripciondialog.setError("Debe ingresar una descripción");
                } else {
                    modificardatoscobroSQLITE(nombrem, apellidom, calendariom, cantidadm, descripcionm, contactom);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()){
                int indiceNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String numero = cursor.getString(indiceNumero);
                numero = numero.replace("+593", "0").replace(" ", "");
                contactosModificar.setText(numero);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalles_cobro, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemeliminarcobro:
                dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialogo_confirmacion);
                Button si = dialog.findViewById(R.id.btnaceptareliminarcobro);
                Button no = dialog.findViewById(R.id.btncancelareliminarcobro);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogo_fondo));
                }
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                dialog.show();
                si.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eliminarCobro();
                        dialog.dismiss();

                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msgcancelar();
                        dialog.dismiss();
                    }
                });

                break;
        }
        return super.onOptionsItemSelected(item);
    }





    public void eliminarCobro(){
        msgregistroeliminado();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String estado = "Eliminado";
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

                Intent intent = new Intent(DetallesCobro.this, Todos.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        }, 1500);
    }
    private void modificardatoscobroSQLITE(String nombre, String apellido, String calendario, String cantidad, String descripcion, String contacto) {
        try {
            String estado=null;
            Date fecharegistrada = null;
            Date fechaactual = null;
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fechaactualuno = String.valueOf(android.text.format.DateFormat.format("dd/MM/yyyy", new java.util.Date()));

            fechaactual = format.parse(fechaactualuno);
            fecharegistrada = (format.parse(calendario));

            if (fecharegistrada.compareTo(fechaactual) <= 0){
                estado = "Vencido";
            }else if(fecharegistrada.compareTo(fechaactual) > 0){
                estado = "Activo";
                }


            SQLiteDatabase db = conn.getReadableDatabase();
            listaCobro = new ArrayList<>();
            Cobro usuario = null;
            Cursor cursor = db.rawQuery("UPDATE " + Utilidades.TABLA_COBRO+  " SET "+Utilidades.CAMPO_NOMBRE+ " = " +
                    "" +"'"+nombre+"'" +" , " +Utilidades.CAMPO_APELLIDO+ " = " +"'"+apellido+"'" +" , "+Utilidades.CAMPO_FECHACOBRO+ " = " +
                    "" +"'"+calendario+"'" +" , " +Utilidades.CAMPO_TIPO + " = " +"'"+descripcion+"'" + " , " + Utilidades.CAMPO_ESTADO+ " = " +
                    "'"+estado+"'" +" , " + Utilidades.CAMPO_CONTACTO+ " = " +"'"+contacto+"'" +  " WHERE " +Utilidades.CAMPO_ID + " = " +
                    ""+ idCobro, null);
            while (cursor.moveToNext()) {
                usuario = new Cobro();
                listaCobro.add(usuario);
            }
            db.close();
            llenarCampos();
            listarRegistros();
            msgmodiicar();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void enviarWhatsapp(View view) {
        String estado = "Sin registrar";
        String mensaje=null;
        if (tip.equals("Cobro")){
            mensaje = "Le recuerdo el cobro de $"+cantidad+" que se encuentra pendiente para la fecha "+fechacobro;
        }else if (tip.equals("Deuda")){
            mensaje = "Estoy pendiente de la deuda de $"+cantidad+" que tengo para la fecha "+fechacobro;
        }
        if ( contactos.equals(estado) || contactos.equals("null")){
            msgnohaynumero();
        }else{
            Intent sendintent = new Intent(Intent.ACTION_VIEW);
            String uri = "whatsapp://send?phone=+593"+contactos+"&text="+mensaje;
            sendintent.setData(Uri.parse(uri));
            startActivity(sendintent);
        }

    }
    public void llamar(View view) {
        String estado = "Sin registrar";
        if ( contactos.equals(estado) || contactos.equals("null")){
            msgnohaynumero();
        }else{
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel: "+contactos));
            startActivity(intent);
        }
    }

    public void generarreportePDF(View view) {

        if (checkPermission()){
            generarPDF();
        }else{
            requestPermissions();
        }

    }

    public void generarPDF(){
        PdfDocument pdfDocument = new PdfDocument();
        String fechaTexto = "";
        String tituloText = "Registros de ";


        Paint paint = new Paint();
        TextPaint tituloPDF = new TextPaint();
        TextPaint fechaPDFdatos = new TextPaint();
        TextPaint cantidadaumentadaPDFdatos = new TextPaint();
        TextPaint nuevacantidadaumentadaPDFdatos = new TextPaint();
        TextPaint accionPDFdatos = new TextPaint();
        TextPaint descripcionPDFdatos = new TextPaint();
        TextPaint lineaPDF = new TextPaint();



        ArrayList arrayListCantidadAumentadaPDF = new ArrayList();
        ArrayList arrayListNuevaCantidadAumentadaPDF = new ArrayList();
        ArrayList arrayListDescripcionPDF = new ArrayList();
        ArrayList arrayListFechaPDF = new ArrayList();
        ArrayList arrayListAccionPDF = new ArrayList();

        Bundle bundle = this.getIntent().getExtras();
        idCobro = bundle.getString("iddetalle");
        conn = new BDDcobrosHelper(this, "bd_cobros", null, 1);
        SQLiteDatabase db = conn.getReadableDatabase();
        RegistrodeCobro usuario = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + Utilidades.TABLA_REGISTRO_COBRO +" WHERE "+ Utilidades.CAMPO_REGISTRO_IDCOBRO + " = "+ idCobro+ " ORDER BY "+Utilidades.CAMPO_REGISTRO_ID+ " DESC ", null);
        while (cursor.moveToNext()) {
            usuario = new RegistrodeCobro();
            usuario.setCantidadaumentada(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_REGISTRO_CANTIDADAUMENTADA)));
            usuario.setNuevacantidadaumentada(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_REGISTRO_NUEVACANTIDADAUMENTADA)));
            usuario.setDescripcionaumento(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_REGISTRO_DESCRIPCIONAUMENTO)));
            usuario.setFechaaumentada(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_REGISTRO_FECHAAUMENTADA)));
            usuario.setAccionregistro(cursor.getString(cursor.getColumnIndex(Utilidades.CAMPO_REGISTRO_ACCIONREGISTRO)));
            arrayListCantidadAumentadaPDF.add(usuario.getCantidadaumentada());
            arrayListNuevaCantidadAumentadaPDF.add(usuario.getNuevacantidadaumentada());
            arrayListDescripcionPDF.add(usuario.getDescripcionaumento());
            arrayListFechaPDF.add(usuario.getFechaaumentada());
            arrayListAccionPDF.add(usuario.getAccionregistro());
        }

        System.out.println(arrayListFechaPDF);

        Bitmap bitmap, bitmapEscala;

        PdfDocument.PageInfo paginaInfo = new PdfDocument.PageInfo.Builder(595,842,1).create();
        PdfDocument.Page pagina1 = pdfDocument.startPage(paginaInfo);

        Canvas canvas = pagina1.getCanvas();


        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_iconodos_app);
        bitmapEscala = Bitmap.createScaledBitmap(bitmap,60,60,false);
        canvas.drawBitmap(bitmapEscala,50,30,paint);

        tituloPDF.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        tituloPDF.setTextSize(20);
        canvas.drawText(tituloText+nombre+" "+apellido+" ("+descripcion+")", 150,70,tituloPDF);

        int y = 130;
        int y1 = 130;
        int y2 = 130;
        int y3 = 130;
        int y4 = 145;
        int y5 = 153;
        int y7 = 120;
        lineaPDF.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        lineaPDF.setTextSize(11);
        canvas.drawText("------------------------------------------------------------------------------------------------------------------------------------------------------"
                , 60, y7, lineaPDF);

        if (arrayListFechaPDF.size() >19){
            for (int i = arrayListFechaPDF.size()-1 ; i >= arrayListFechaPDF.size()-19 ; i--){
                fechaPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                fechaPDFdatos.setTextSize(11);
                canvas.drawText(arrayListFechaPDF.get(i) +" "
                        , 70, y, fechaPDFdatos);
                y = y +35;
                accionPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                accionPDFdatos.setTextSize(11);
                canvas.drawText(arrayListAccionPDF.get(i) +" "
                        , 200, y1, accionPDFdatos);
                y1 = y1 +35;
                cantidadaumentadaPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                cantidadaumentadaPDFdatos.setTextSize(11);
                canvas.drawText(arrayListCantidadAumentadaPDF.get(i) +" "
                        , 345, y2, cantidadaumentadaPDFdatos);
                y2 = y2 +35;
                nuevacantidadaumentadaPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                nuevacantidadaumentadaPDFdatos.setTextSize(11);
                canvas.drawText("Saldo: "+arrayListNuevaCantidadAumentadaPDF.get(i)
                        , 425, y3, nuevacantidadaumentadaPDFdatos);
                y3 = y3 +35;

                descripcionPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                descripcionPDFdatos.setTextSize(11);
                canvas.drawText("Descripción: "+arrayListDescripcionPDF.get(i)
                        , 70, y4, descripcionPDFdatos);
                y4 = y4 +35;
                lineaPDF.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                lineaPDF.setTextSize(11);
                canvas.drawText("------------------------------------------------------------------------------------------------------------------------------------------------------"
                        , 60, y5, lineaPDF);
                y5 = y5 +35;
            }
        }else{
            for (int i = arrayListFechaPDF.size()-1 ; i >= 0 ; i--){
                fechaPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                fechaPDFdatos.setTextSize(11);
                canvas.drawText(arrayListFechaPDF.get(i) +" "
                        , 70, y, fechaPDFdatos);
                y = y +35;
                accionPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                accionPDFdatos.setTextSize(11);
                canvas.drawText(arrayListAccionPDF.get(i) +" "
                        , 200, y1, accionPDFdatos);
                y1 = y1 +35;
                cantidadaumentadaPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                cantidadaumentadaPDFdatos.setTextSize(11);
                canvas.drawText(arrayListCantidadAumentadaPDF.get(i) +" "
                        , 345, y2, cantidadaumentadaPDFdatos);
                y2 = y2 +35;
                nuevacantidadaumentadaPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                nuevacantidadaumentadaPDFdatos.setTextSize(11);
                canvas.drawText("Saldo: "+arrayListNuevaCantidadAumentadaPDF.get(i)
                        , 425, y3, nuevacantidadaumentadaPDFdatos);
                y3 = y3 +35;

                descripcionPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                descripcionPDFdatos.setTextSize(11);
                canvas.drawText("Descripción: "+arrayListDescripcionPDF.get(i)
                        , 70, y4, descripcionPDFdatos);
                y4 = y4 +35;
                lineaPDF.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                lineaPDF.setTextSize(11);
                canvas.drawText("------------------------------------------------------------------------------------------------------------------------------------------------------"
                        , 60, y5, lineaPDF);
                y5 = y5 +35;
            }
        }


        pdfDocument.finishPage(pagina1);

// otra pagina

        if (arrayListFechaPDF.size() >19){
            PdfDocument.PageInfo paginaInfo2 = new PdfDocument.PageInfo.Builder(595,842,2).create();
            PdfDocument.Page pagina2 = pdfDocument.startPage(paginaInfo2);

            Canvas canvas2 = pagina2.getCanvas();



            int yy1 = 80;
            int yy2 = 80;
            int yy3 = 80;
            int yy4 = 80;
            int yy5 = 95;
            int yy6 = 103;
            int yy7 = 70;

            lineaPDF.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            lineaPDF.setTextSize(11);
            canvas2.drawText("------------------------------------------------------------------------------------------------------------------------------------------------------"
                    , 60, yy7, lineaPDF);


            for (int i = arrayListFechaPDF.size()-20 ; i >= 0 ; i--){
                fechaPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                fechaPDFdatos.setTextSize(11);
                canvas2.drawText(arrayListFechaPDF.get(i) +" "
                        , 70, yy1, fechaPDFdatos);
                yy1 = yy1 +35;
                accionPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                accionPDFdatos.setTextSize(11);
                canvas2.drawText(arrayListAccionPDF.get(i) +" "
                        , 200, yy2, accionPDFdatos);
                yy2 = yy2 +35;
                cantidadaumentadaPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                cantidadaumentadaPDFdatos.setTextSize(11);
                canvas2.drawText(arrayListCantidadAumentadaPDF.get(i) +" "
                        , 345, yy3, cantidadaumentadaPDFdatos);
                yy3 = yy3 +35;
                nuevacantidadaumentadaPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                nuevacantidadaumentadaPDFdatos.setTextSize(11);
                canvas2.drawText("Saldo: "+arrayListNuevaCantidadAumentadaPDF.get(i)
                        , 425, yy4, nuevacantidadaumentadaPDFdatos);
                yy4 = yy4 +35;

                descripcionPDFdatos.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                descripcionPDFdatos.setTextSize(11);
                canvas2.drawText("Descripción: "+arrayListDescripcionPDF.get(i)
                        , 70, yy5, descripcionPDFdatos);
                yy5 = yy5 +35;
                lineaPDF.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                lineaPDF.setTextSize(11);
                canvas2.drawText("------------------------------------------------------------------------------------------------------------------------------------------------------"
                        , 60, yy6, lineaPDF);
                yy6 = yy6 +35;
            }
            pdfDocument.finishPage(pagina2);
        }


        File file = new File(getExternalFilesDir(null),"/reporte_"+nombre+".pdf");
        msgPDFcreado(file);


        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            pdfDocument.close();
        }catch (Exception e){
            System.out.println("error"+e);
        }


    }

    private void msgPDFcreado(File carpeta) {

        dialogStyle = DialogStyle.FLAT;
        dialogType = DialogType.SUCCESS;
        dialogAnimation = DialogAnimation.SWIPE_LEFT;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(this, dialogStyle, dialogType);
        builder.setTitle("Registro creado");
        builder.setMessage("Se creó un PDF con los registros en: " + carpeta);
        builder.setAnimation(dialogAnimation);
        builder.show();
    }

    public boolean checkPermission(){
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;

    }

    public void requestPermissions(){
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE},200);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults){
        if (requestCode == 200){
            if (grandResults.length > 0){
                boolean writeStorage = grandResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grandResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage){
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permiso Denegado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(this, Todos.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }



}