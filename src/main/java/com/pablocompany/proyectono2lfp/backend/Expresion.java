/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.backend;

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
    public Expresion(Lexema llave,ArrayList<Lexema> listadoExpresiones) {
        this.llaveLexema = llave;
        this.listadoExpresiones = listadoExpresiones;
    }
    
    //Apartado de metodos especializados en calcular los resultados de las respectivas expresiones
}
