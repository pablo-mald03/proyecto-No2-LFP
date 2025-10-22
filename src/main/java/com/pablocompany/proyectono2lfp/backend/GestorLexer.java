/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.backend;

import com.pablocompany.proyectono2lfp.jflexpackage.AnalizadorLexico;
import javax.swing.JTextPane;

/**
 *
 * @author pablo
 */
//Clase encargada de hacer todas las funciones que genera el analizador lexico
public class GestorLexer {
    
    //Atributo que permite manejar la instancia del analizador lexico
    private AnalizadorLexico lexer;
    
    //Atributos que permiten manejar 
    private JTextPane paneEdicionArchivo;
    
    private JTextPane logErrores;

    public GestorLexer(AnalizadorLexico lexerGenerado, JTextPane paneEdicionArchivo, JTextPane logErrores) {
        this.lexer = lexerGenerado;
        this.paneEdicionArchivo = paneEdicionArchivo;
        this.logErrores = logErrores;
    }
    

    //Retorna la referencia de lexer
    public AnalizadorLexico getLexer() {
        return lexer;
    }

    //Permite hacer el set del lexer
    public void setLexer(AnalizadorLexico lexer) {
        this.lexer = lexer;
    }
    
    //=========================APARTADO DE METODOS UTILIZADOS PARA GENERAR LAS FUNCIONALIDADES DEL ANALIZADOR LEXICO=======================
    
    //Metodo utilizado para ilustrar el log de edicion
    
    
    
    
    
    //=========================FIN DEL APARTADO DE METODOS UTILIZADOS PARA GENERAR LAS FUNCIONALIDADES DEL ANALIZADOR LEXICO=======================
    
    
    
    
}
