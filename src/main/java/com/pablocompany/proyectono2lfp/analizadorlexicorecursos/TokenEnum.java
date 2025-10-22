/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.analizadorlexicorecursos;

/**
 *
 * @author pablo
 */
//Conjunto de tokens que existen 
public enum TokenEnum {

    //------------------------APARTADO DE TOKENS QUE TIENEN UN SIGNIFICADO GENERAL---------------------------
    INDEFINIDO("INDEFINIDO", "la indeterminacion", "Indefinido"),
    IDENTIFICADOR("IDENTIFICADOR", "el identificador", "Identificador"),
    NUMERO("NUMERO", "el numero", "Numero"),
    DECIMAL("DECIMAL", "el decimal", "Numero Decimal"),
    CADENA("CADENA", "la cadena de texto", "Cadena de Texto"),
    PALABRA_RESERVADA("PALABRA_RESERVADA", "la palabra reservada", "Palabra Reservada"),
    PUNTUACION("PUNTUACION", "el signo puntuacion", "Puntuacion"),
    OPERADOR("OPERADOR", "el operador", "Operador"),
    AGRUPACION("AGRUPACION", "el signo de agrupacion", "Agrupacion"),
    COMENTARIO_LINEA("COMENTARIO_LINEA", "el comentario de linea", "Comentario de Linea"),
    COMENTARIO_BLOQUE("COMENTARIO_BLOQUE", "el comentario de bloque", "Comentario de Bloque"),
    ERROR("ERROR", "el error", "error"),
    //------------------------FIN DEL APARTADO DE TOKENS QUE TIENEN UN SIGNIFICADO GENERAL---------------------------

    //------------------------APARTADO DE TOKENS QUE TIENEN UN CONTEXTO DE ACCION---------------------------

    //APARTADO DE DEFINICION DE VARIABLES
    DEFINIR("DEFINIR", "el declarador Definir", "Definir"),
    COMO("COMO", "el declarador Como", "Como"),
    PUNTO_COMA("PUNTO Y COMA", "el declarador Punto y Coma", "Punto y Coma"),
    TIPO_ENTERO("TIPO ENTERO", "el tipo de dato Entero", "Entero"),
    TIPO_CADENA("TIPO CADENA", "el tipo de dato cadena", "Cadena"),
    TIPO_NUMERO("TIPO NUMERO", "el tipo de dato Numero", "Numero"),
    IGUAL("IGUAL", "el declarador Igual", "Igual"),
    //APARTADO DE FUNCIONES DE SALIDA DE DATOS

    ESCRIBIR("ESCRIBIR", "la funcion Escribir", "Escribir"),
    PARENTESIS_CIERRE("PARENTESIS DE CIERRE", "el parentesis de cierre ", "Parentesis de Cierre"),
    PARENTESIS_APERTURA("PARENTESIS DE APERTURA", "el parentesis de apertura", "Parentesis de Apertura"),

    //------------------------FIN DEL APARTADO DE TOKENS QUE TIENEN UN CONTEXTO DE ACCION---------------------------
    
    //--------------------APARTADO DE TOKENS QUE PERMITEN IDENTIFICAR TODO TIPO DE ESPACIOS-----------------------------
    ESPACIO("ESPACIO", "el espacio", "Espcio"),
    TABULACION("TABULACION", "la tabulacion", "Tabulacion"),
    SALTO_LINEA("SALTO_LINEA", "el salto de linea", "Salto de linea");

    

    //--------------------FIN DEL APARTADO DE TOKENS QUE PERMITEN IDENTIFICAR TODO TIPO DE ESPACIOS-----------------------------
    
    
    //Representa el tipo de token
    private String tipo;

    //Atributo que representa el contexto del token
    private String contexto;

    //Atributo que representa el tipo de token en minuscula
    private String nombreToken;

    private TokenEnum(String valor, String contextoDado, String nombreDado) {
        this.tipo = valor;
        this.contexto = contextoDado;
        this.nombreToken = nombreDado;
    }

    public String getTipo() {
        return this.tipo;
    }

    public String getContexto() {
        return this.contexto;
    }

    public String getNombreToken() {
        return nombreToken;
    }

}
