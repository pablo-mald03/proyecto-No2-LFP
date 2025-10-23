/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.backend;

import com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */
//Clase que permite guardar las sentencias generadas por Jflex
public class Sentencia {
    
    private ArrayList<Lexema> listaLexemas;
    private int filaSentencia;

    public Sentencia(ArrayList<Lexema> listaLexema, int fila) {
        this.listaLexemas = listaLexema;
        this.filaSentencia = fila;

    }

    //Metodo que retorna la linea en la que se encuentra la sentencia
    public int getFilaSentencia() {
        return this.filaSentencia;
    }
    
    //Metodo que sirve para agregar mas lexemas
    public void agregarLexemaLexico(String cadena, int fila, int columna, TokenEnum token){
        this.listaLexemas.add(new Lexema(cadena, fila,columna,token));
    }
    
    //Metodo que sirve para agregar mas lexemas sintacticos
    public void agregarLexemaSintactico(String cadena, int fila, int columna, TokenEnum tokenLexico,TokenEnum tokenSintactico){
        this.listaLexemas.add(new Lexema(cadena, fila,columna,tokenLexico, tokenSintactico));
    }
    
    //Metodo que sirve para agregar lexemas con un error
    public void agregarLexemaErroneo(String cadena, int fila, int columna, TokenEnum tokenLexico, String mensajeError){
        this.listaLexemas.add(new Lexema(cadena, fila,columna,tokenLexico, mensajeError));
    }

    //Metodo que permite acceder al lexema almacenado en la lista
    public Lexema getListaLexema(int indice) {
        return this.listaLexemas.get(indice);
    }

    //Metodo que retorna el limite de los lexemas almacenados
    public int limiteLexemas() {
        return this.listaLexemas.size();
    }
    
    //Metodo que sirve para obtener el indice actual en el que va el listado de lexemas
    

    public ArrayList<Lexema> obtenerListadoLexemas() {
        return this.listaLexemas;
    }
    
}
