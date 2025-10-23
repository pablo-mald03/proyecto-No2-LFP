/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.backend;

import com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum;
import com.pablocompany.proyectono2lfp.excepciones.AnalizadorLexicoException;

/**
 *
 * @author pablo
 */
//Clase que permite manejar por completo cada lexema tokenizado por Jflex
public class Lexema {

    //Atributo que representa el lexema completo de la palabra
    private String lexemaGenerado;

    //Representa la linea en la que se encuetra situado el lexema
    private int lineaCoordenada;

    //Representa la columna de inicio donde se encuentra el lexema
    private int columna;

    //---------------------Apartado de atributos que permiten manejar el apartado de tokens-------------------------
    //Atributo principal utilizado como token el del cual forma parte el lexema
    private TokenEnum tokenClasificado;

    //Atributo de token que permitira identificar las palabras reservadas que tienen un segundo contexto de funcionalidad
    private TokenEnum tokenSintactico;

    //Atributo que permite especificar el caracter esperado
    private String cadenaEsperada;

    //Atributo que permite saber el problema de error
    private String mensajeError;

    //---------------------Fin del Apartado de atributos que permiten manejar el apartado de tokens-------------------------
    //Constructor que sirve para poder generar un token comun sin ningun contexto sintactico
    public Lexema(String lexemaGenerado, int lineaCoordenada, int columnaCoordenada, TokenEnum tipoToken) {
        this.lexemaGenerado = lexemaGenerado;
        this.lineaCoordenada = lineaCoordenada;
        this.columna = columnaCoordenada;
        this.tokenClasificado = tipoToken;
        this.mensajeError = "";
    }

    //Constructor que sirve para poder generar un token con un contexto sintactico
    public Lexema(String lexemaGenerado, int lineaCoordenada, int columnaCoordenada, TokenEnum tipoToken, TokenEnum contextoSintactico) {
        this.lexemaGenerado = lexemaGenerado;
        this.lineaCoordenada = lineaCoordenada;
        this.columna = columnaCoordenada;
        this.tokenClasificado = tipoToken;
        this.tokenSintactico = contextoSintactico;
        this.mensajeError = "";
    }

    //Constructor que permite declarar lexemas que tienen un error
    public Lexema(String lexemaGenerado, int lineaCoordenada, int columnaCoordenada, TokenEnum tipoToken, String mensajeErroneo) {
        this.lexemaGenerado = lexemaGenerado;
        this.lineaCoordenada = lineaCoordenada;
        this.columna = columnaCoordenada;
        this.tokenClasificado = tipoToken;
        this.mensajeError = mensajeErroneo;
    }

    //Apartado de getters que sirven para saber informacion sobre el lexema
    public String getLexemaGenerado() {
        return lexemaGenerado;
    }

    public void setLexemaGenerado(String lexemaGenerado) {
        this.lexemaGenerado = lexemaGenerado;
    }

    public int getLineaCoordenada() {
        return lineaCoordenada;
    }

    public void setLineaCoordenada(int lineaCoordenada) {
        this.lineaCoordenada = lineaCoordenada;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public TokenEnum getTokenClasificado() {
        return tokenClasificado;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    //Apartado de Metodos setter para poder utilizarlos en el manejo del objeto
    public void setTokenClasificado(TokenEnum tokenClasificado) {
        this.tokenClasificado = tokenClasificado;
    }

    public TokenEnum getTokenSintactico() {
        return tokenSintactico;
    }

    public void setTokenSintactico(TokenEnum tokenSintactico) {
        this.tokenSintactico = tokenSintactico;
    }

    public String getCadenaEsperada() {
        return cadenaEsperada;
    }

    public void setCadenaEsperada(String cadenaEsperada) {
        this.cadenaEsperada = cadenaEsperada;
    }

    //Apartado de patron experto que permite calcular el limite superior para pintar el lexema en la busqueda
    //Metodo que calcula el indice final en el que se encuentra el lexema
    public int getindiceFinal() throws AnalizadorLexicoException {

        if (this.lexemaGenerado.isBlank()) {
            throw new AnalizadorLexicoException("No se ha definido ningun lexema");
        }
        return this.columna + (this.lexemaGenerado.length() - 1);

    }

}
