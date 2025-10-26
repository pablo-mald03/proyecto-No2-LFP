/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.backend;

import com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum;
import com.pablocompany.proyectono2lfp.excepciones.AnalizadorLexicoException;
import com.pablocompany.proyectono2lfp.excepciones.ConfigException;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author pablo
 */
//Clase delegada para poder operar con el log sintactico Y CLASIFICAR LAS OPERACIONES QUE ESTA HARA
public class GestorSintactico {

    private GestorLexer gestionLexer;
    private JTextPane logSintactico;
    private JTextPane logErroresSintacticos;

    //Atributo que permite manejar la instancia compartida del listado de sentencias sintacticas
    private ArrayList<Sintaxis> listadoParser = new ArrayList<>(5000);

    public GestorSintactico(GestorLexer gestionLexer, JTextPane logErrores, JTextPane logSintactico) {
        this.gestionLexer = gestionLexer;
        this.logSintactico = logSintactico;
        this.logErroresSintacticos = logErrores;
    }

    //Metodo encargado para poder 
    private ArrayList<Sentencia> getListadoSentencias() {
        return this.gestionLexer.getListadoSentencia();
    }

    //Metodo que permite calcular el indice en el que va la lista de sentencias
    private int getIndiceListado() {

        if (this.listadoParser.isEmpty()) {
            return 0;
        }

        int indice = this.listadoParser.size() - 1;

        if (indice < 0) {
            return 0;
        } else {
            return indice;
        }
    }

    //Metodo que permite iniciar el analisis sitactico
    public void iniciarAnalisis() throws AnalizadorLexicoException, ConfigException, BadLocationException {

        //Vallida si hay errores registrados
        if (hayErrores()) {
            throw new AnalizadorLexicoException("No puedes ejecutar el analisis sintactico\nHay errores registrados");
        }

        separarSintaxis();

    }

    //Metodo encargado de ir listando todos los lexemas que tengan un significado sintactico
    private void separarSintaxis() {

        if (!this.listadoParser.isEmpty()) {
            this.listadoParser.clear();
        }

        for (int i = 0; i < getListadoSentencias().size(); i++) {

            Sentencia sentenciaUbicada = getListadoSentencias().get(i);

            ArrayList<Lexema> listadoLexemas = new ArrayList<>(1000);

            for (int j = 0; j < sentenciaUbicada.obtenerListadoLexemas().size(); j++) {

                Lexema lexemaUbicado = sentenciaUbicada.getListaLexema(j);

                if (lexemaUbicado.getTokenSintactico() == TokenEnum.DEFINIR) {

                    if (!listadoLexemas.isEmpty()) {
                        this.listadoParser.add(new Sintaxis(listadoLexemas, false, "", TipoOperacionEnum.NO_SINTACTICO));
                        listadoLexemas = new ArrayList<>(1000);
                    }

                    j = hallarDefinicion(i, sentenciaUbicada.obtenerListadoLexemas());

                } else {
                    listadoLexemas.add(lexemaUbicado);
                }

            }

        }

    }

    //Metodo que llama a una serie de metodos para moverse 
    private int hallarDefinicion(int indiceRecorrido, ArrayList<Lexema> listadoLexemasSintacticos) {

        ArrayList<Lexema> listadoAuxiliar = new ArrayList<>(1000);

        for (int i = indiceRecorrido; i < listadoLexemasSintacticos.size(); i++) {

            Lexema lexemaUbicado = listadoLexemasSintacticos.get(i);
            
            if(lexemaUbicado.getTokenClasificado() == TokenEnum.ESPACIO ||lexemaUbicado.getTokenClasificado() == TokenEnum.TABULACION ){
                
                listadoAuxiliar.add(lexemaUbicado);
            }
            
            
            //PENDIENTE TERMINAR ESTA MADRE CUANDO NO TENGA SUENIO LPTM

        }

        return listadoLexemasSintacticos.size();

    }

    //Metodo que ayuda a saber si hay errores
    private boolean hayErrores() {

        //Cuenta los errores para ver si hay 
        for (Sentencia sentencia : getListadoSentencias()) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);
                if (lexemaEvaluado.getTokenClasificado() == TokenEnum.ERROR) {
                    return true;
                }

            }

        }

        return false;

    }

    //Metodo que trabaja en conjunto para poder ir pintando letra a letra
    private void limpiarArea(JTextPane paneAnalisis) throws BadLocationException {
        StyledDocument doc = paneAnalisis.getStyledDocument();
        doc.remove(0, doc.getLength());

    }

    // Método para insertar texto con un color específico
    private void insertarTexto(String texto, Color color, JTextPane paneAnalisis) throws BadLocationException {

        StyledDocument doc = paneAnalisis.getStyledDocument();
        // Crear estilo temporal
        SimpleAttributeSet estilo = new SimpleAttributeSet();
        StyleConstants.setForeground(estilo, color);
        // Inserta al final del documento
        doc.insertString(doc.getLength(), texto, estilo);

    }
}
