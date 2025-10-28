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
//Clase utilizada solamente para cuando el tipo de operacion sintactica definida es ESCRIBIR
//Clase llave-valor
public class Expresion {

    //Listado que maneja los lexemas que se registran
    private ArrayList<Lexema> listadoExpresiones;

    private Lexema llaveLexema;

    //Utilizado para variables
    public Expresion(Lexema llave, ArrayList<Lexema> listadoExpresiones) {
        this.llaveLexema = llave;
        this.listadoExpresiones = listadoExpresiones;
    }

    //Apartado de metodos especializados en calcular los resultados de las respectivas expresiones
    public ArrayList<Lexema> getListadoExpresiones() {
        return listadoExpresiones;
    }

    public void setListadoExpresiones(ArrayList<Lexema> listadoExpresiones) {
        this.listadoExpresiones = listadoExpresiones;
    }

    public Lexema getLlaveLexema() {
        return llaveLexema;
    }

    public void setLlaveLexema(Lexema llaveLexema) {
        this.llaveLexema = llaveLexema;
    }

    //Metodo que sirve para obtener el listado de texto que posee de asignacion 
    public String getValor() {

        boolean puedeMostrar = false;

        StringBuilder cadena = new StringBuilder();

        for (Lexema expresion : listadoExpresiones) {

            if (expresion.getTokenSintactico() == TokenEnum.PUNTO_COMA) {
                break;
            }

            if (puedeMostrar) {
                cadena.append(expresion.getLexemaGenerado());
            }

            if (expresion.getTokenSintactico() == TokenEnum.IGUAL) {
                puedeMostrar = true;
            }

        }

        return cadena.toString();
    }

}
