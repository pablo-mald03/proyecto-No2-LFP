/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.backend;

import com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum;
import com.pablocompany.proyectono2lfp.backendswing.CrearTableros;
import com.pablocompany.proyectono2lfp.backendswing.ModificarTabla;
import com.pablocompany.proyectono2lfp.excepciones.ErrorSintacticoException;
import com.pablocompany.proyectono2lfp.excepciones.ErroresRegistradosException;
import java.awt.Color;
import java.io.File;
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
///Clase delegada para poder generar reportes sintacticos
public class GenerarReportesSintacticos {

    //------------------APARTADO DE MANEJO DE ATRIBUTOS DEL REPORTE--------------
    //Atributo booleano para saber si hay errores
    //true si hay errores
    private boolean hayErrores;

    //Listado que permite guardar los lexemas para poder generar transiciones
    private ArrayList<Sintaxis> listadoSintaxis = new ArrayList<>(2000);

    private ArrayList<String> listadoVariables = new ArrayList<>(200);

    public GenerarReportesSintacticos(ArrayList<Sintaxis> listado) {

        this.hayErrores = false;
        this.listadoSintaxis = listado;
    }

    //Metodo utilizado para generar reportes de errores sintacticos
    public void generarReporteErrores(JTextPane paneErrores) throws ErrorSintacticoException, BadLocationException, ErroresRegistradosException {

        boolean erroresRegistrados = false;

        limpiarArea(paneErrores);

        for (int i = 0; i < this.listadoSintaxis.size(); i++) {

            Sintaxis sintaxisActiva = this.listadoSintaxis.get(i);

            if (sintaxisActiva.tieneError()) {

                if (!erroresRegistrados) {
                    erroresRegistrados = true;
                }

                insertarTexto("-> Error en la linea " + sintaxisActiva.getLineaInicio()
                        + " columna " + sintaxisActiva.getColumnaError() + " " + sintaxisActiva.getMensajeError(), Color.RED, paneErrores);
                insertarTexto("\n", Color.BLACK, paneErrores);
                insertarTexto("\n", Color.BLACK, paneErrores);
            }

        }

        if (!erroresRegistrados) {

            throw new ErroresRegistradosException("No hay errores registrados en el analisis Sintactico");
        }

    }

    //Metodo que sirve para imprimir el listado 
    public void generarReporteVariables(ModificarTabla modificarTabla, CrearTableros crearTablero) throws ErrorSintacticoException, BadLocationException, ErroresRegistradosException {

        if (!this.listadoVariables.isEmpty()) {
            this.listadoVariables.clear();
        }

        for (int i = 0; i < this.listadoSintaxis.size(); i++) {

            Sintaxis sintaxisActiva = this.listadoSintaxis.get(i);

            if (!sintaxisActiva.getExpresionSintaxis().isEmpty()) {

                for (Expresion expresion : sintaxisActiva.getExpresionSintaxis()) {

                    this.listadoVariables.add(expresion.getLlaveLexema().getLexemaGenerado());
                    this.listadoVariables.add(expresion.getValor());
                }

            }

        }

        if (this.listadoVariables.isEmpty()) {
            throw new ErrorSintacticoException("No hay variables declaradas");
        }

        String[] titulos = {"Nombre", "Valor"};
        crearTablero.tableroConTitulo(titulos, this.listadoVariables.size() / 2, 2, true);
        modificarTabla.reendereizarTablero();
        
         int iterador = 0;

        for (int i = 0; i < this.listadoVariables.size(); i += 2) {

            String nombre = this.listadoVariables.get(i);
            String valor = this.listadoVariables.get(i + 1);

            modificarTabla.colocarTextos(iterador, 0, nombre);
            modificarTabla.colocarTextos(iterador, 1, valor);
            iterador++;
        }

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
