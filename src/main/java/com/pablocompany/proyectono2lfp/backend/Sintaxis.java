/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.backend;

import com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum;
import com.pablocompany.proyectono2lfp.excepciones.ErrorSintacticoException;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */
//Clase especializada para manejar la sintaxis y los respectivos lexemas
public class Sintaxis {

    //Listado que maneja los lexemas que se registran
    private ArrayList<Lexema> listadoLexemas;

    //Atributos propios que representan el analisis sintactico
    //true si tiene error
    private boolean tieneError;

    //Mensaje de error para especificar el error sintactico que se tuvo
    private String mensajeError;

    //Atributos que permtien identificar que tipo de operacion es
    private TipoOperacionEnum tipoOperacion;

    //Atributo SOLAMENTE UTILIZADO CUANDO LA FUNCION ES ESCRIBIR
    private ArrayList<Expresion> expresionSintaxis = new ArrayList<>();

    //Constructor utilizado simplemente para expresiones que no sean la de escribir
    public Sintaxis(ArrayList<Lexema> listadoLexemas, boolean tieneError, String mensajeError, TipoOperacionEnum tipoOperacion) {
        this.listadoLexemas = listadoLexemas;
        this.tieneError = tieneError;
        this.mensajeError = mensajeError;
        this.tipoOperacion = tipoOperacion;
    }

    //===============Apartado de metodos que son utilizados para generar la instancia de expresiones===============
    public ArrayList<Expresion> getExpresionSintaxis() {
        return expresionSintaxis;
    }
    
    //Metodo que permite obtener la llave de las expresiones asignadas
    public Lexema getLlaveExpresion(int indice){
       return this.expresionSintaxis.get(indice).getLlaveLexema();
    }

    //Metodo que recorre la instancia de expresiones llave valor
    public void setExpresionSintaxis(ArrayList<Lexema> expresionLexica) throws ErrorSintacticoException {

        Lexema llave = expresionLexica.get(0);

        if (llave.getTokenClasificado() != TokenEnum.IDENTIFICADOR) {
            throw new ErrorSintacticoException("Ha ocurrido un error en la clasificacion de errores");
        }

        this.expresionSintaxis.add(new Expresion(llave, expresionLexica));
    }

    //===============Fin del Apartado de metodos que son utilizados para generar la instancia de expresiones===============
    public ArrayList<Lexema> getListadoLexemas() {
        return listadoLexemas;
    }

    public void setListadoLexemas(ArrayList<Lexema> listadoLexemas) {
        this.listadoLexemas = listadoLexemas;
    }

    public boolean tieneError() {
        return tieneError;
    }

    public void setTieneError(boolean tieneError) {
        this.tieneError = tieneError;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public TipoOperacionEnum getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacionEnum tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    //Metodo que sirve para agregar mas lexemas sintacticos
    public void agregarLexemaSintactico(Lexema lexemaEntrante, boolean tieneError) {

        if (tieneError) {
            lexemaEntrante.setErrorSintactico(true);
        }

        this.listadoLexemas.add(lexemaEntrante);
    }

    //Metodo que permite representar en que linea se encuentra el error
    public int getLineaInicio() throws ErrorSintacticoException {

        if (this.listadoLexemas.isEmpty()) {
            throw new ErrorSintacticoException("La sintaxis esta vacia");
        }
        return this.listadoLexemas.get(0).getLineaCoordenada();
    }

    //Metodo que indica dond esta el error sintactico
    public int getColumnaError() throws ErrorSintacticoException {

        if (this.listadoLexemas.isEmpty()) {
            throw new ErrorSintacticoException("La sintaxis esta vacia");
        }

        for (Lexema lexema : listadoLexemas) {

            if (lexema.esErrorSintactico()) {
                return lexema.getColumna();
            }
        }

        throw new ErrorSintacticoException("No se puedo encontrar el error");
    }

    //Metodo utilizado para obtener el lexema
    public Lexema getLexema(int indice) {
        return this.listadoLexemas.get(indice);
    }

}
