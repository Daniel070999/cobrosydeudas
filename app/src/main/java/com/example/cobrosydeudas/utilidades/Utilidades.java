package com.example.cobrosydeudas.utilidades;

public class Utilidades {

    //Constantes campos tabla cobro
    public static String TABLA_COBRO = "cobro";
    public static String CAMPO_ID = "id";
    public static String CAMPO_NOMBRE = "nombre";
    public static String CAMPO_APELLIDO = "apellido";
    public static String CAMPO_FECHACOBRO = "fechacobro";
    public static String CAMPO_CANTIDAD = "cantidad";
    public static String CAMPO_TIPO = "tipo";
    public static String CAMPO_ESTADO = "estado";
    public static String CAMPO_CONTACTO = "contacto";

    //Constantes campos tabla registro cobro
    public static String TABLA_REGISTRO_COBRO = "registro_cobro";
    public static String CAMPO_REGISTRO_ID = "idregistro";
    public static String CAMPO_REGISTRO_CANTIDADAUMENTADA = "cantidadaumentada";
    public static String CAMPO_REGISTRO_NUEVACANTIDADAUMENTADA = "nuevacantidadaumentaa";
    public static String CAMPO_REGISTRO_DESCRIPCIONAUMENTO = "descripcionaumento";
    public static String CAMPO_REGISTRO_FECHAAUMENTADA = "fechaaumentada";
    public static String CAMPO_REGISTRO_ACCIONREGISTRO = "accionregistro";
    public static String CAMPO_REGISTRO_IDCOBRO = "idcobro";

    //Constantes campos tabla permisos
    public static String TABLA_PERMISO = "permiso";
    public static String CAMPO_PERMISO_CLAVE = "clave";



    public static final String CREAR_TABLA_COBRO ="CREATE TABLE " +
            ""+TABLA_COBRO+" ("+CAMPO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CAMPO_NOMBRE+" TEXT, "+CAMPO_APELLIDO+" TEXT, "+CAMPO_FECHACOBRO+" "+
            "TEXT, "+CAMPO_CANTIDAD+" TEXT, "+ CAMPO_TIPO +" TEXT, "+CAMPO_ESTADO+" TEXT, "+CAMPO_CONTACTO+" TEXT)";

    public static final String CREAR_TABLA_REGISTRO_COBRO ="CREATE TABLE " +
            ""+TABLA_REGISTRO_COBRO+" ("+CAMPO_REGISTRO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CAMPO_REGISTRO_CANTIDADAUMENTADA+" TEXT, "+CAMPO_REGISTRO_NUEVACANTIDADAUMENTADA+" TEXT, " +
            ""+CAMPO_REGISTRO_DESCRIPCIONAUMENTO+ " TEXT, "+CAMPO_REGISTRO_FECHAAUMENTADA+" TEXT, "+CAMPO_REGISTRO_ACCIONREGISTRO+" TEXT, " + CAMPO_REGISTRO_IDCOBRO+" INTEGER)";

    public static final String CREAR_TABLA_PERMISO= "CREATE TABLE "+
            ""+TABLA_PERMISO+" ("+CAMPO_PERMISO_CLAVE+" TEXT )";
}
