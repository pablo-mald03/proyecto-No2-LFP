/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.excepciones;

/**
 *
 * @author pablo
 */
//Clase encargada de reportar los errores del analizador sintactico
public class ErrorSintacticoException extends  Exception{

    public ErrorSintacticoException() {
    }

    public ErrorSintacticoException(String message) {
        super(message);
    }
    
    
    
}
