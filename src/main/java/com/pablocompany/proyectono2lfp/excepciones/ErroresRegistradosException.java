/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.excepciones;

/**
 *
 * @author pablo
 */
//Clase delegada para reportar los errores regitrados
public class ErroresRegistradosException extends Exception{

    public ErroresRegistradosException() {
    }

    public ErroresRegistradosException(String message) {
        super(message);
    }
    
}
