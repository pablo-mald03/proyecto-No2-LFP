/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.excepciones;

/**
 *
 * @author pablo
 */
//Excepcion encargada de reportar cualquier error de configuracion
public class ConfigException extends Exception{

    public ConfigException() {
    }

    public ConfigException(String message) {
        super(message);
    }
    
    
    
}
