/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.backend;

import com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum;
import com.pablocompany.proyectono2lfp.jflexpackage.AnalizadorLexico;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

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

    //Metodo encargado de pintar el log de edicion de archivo
    public void pintarLogEdicion() throws BadLocationException{
        pintarLogSalida(this.paneEdicionArchivo, true);
    }
    
    //=========================APARTADO DE METODOS UTILIZADOS PARA GENERAR LAS FUNCIONALIDADES DEL ANALIZADOR LEXICO=======================
    //Metodo utilizado para ilustrar el log de edicion
    //METODO UNICO QUE SIRVE PARA COLOREAR LOS LOG A CARGO DEL ANALIZADOR LEXICO
    public void pintarLogSalida(JTextPane paneAnalisis, boolean enAnalisis) throws BadLocationException {

        int caretOffset = paneAnalisis.getCaretPosition();
        StyledDocument doc = paneAnalisis.getStyledDocument();

        // Línea y columna reales antes de limpiar
        int lineaCaret = doc.getDefaultRootElement().getElementIndex(caretOffset);
        int columnaCaret = caretOffset - doc.getDefaultRootElement()
                .getElement(lineaCaret)
                .getStartOffset();

        limpiarArea(paneAnalisis);

        ArrayList<Sentencia> listaSentencias = this.lexer.getListaSentencias();

        for (int i = 0; i < listaSentencias.size(); i++) {

            Sentencia sentenciaActiva = listaSentencias.get(i);

            for (Lexema lexemaDado : sentenciaActiva.obtenerListadoLexemas()) {

                Color colorTexto = obtenerColorPorToken(lexemaDado.getTokenClasificado());

                insertarToken(lexemaDado.getLexemaGenerado(), colorTexto, paneAnalisis);
                
                if(lexemaDado.getTokenClasificado() == TokenEnum.VACIO){
                    insertarToken("\n", Color.BLACK, paneAnalisis);
                }
            }

            

        }

        //Se restaura la posicion del caret
        StyledDocument newDoc = paneAnalisis.getStyledDocument();
        Element root = newDoc.getDefaultRootElement();
        if (lineaCaret < root.getElementCount()) {
            Element lineElem = root.getElement(lineaCaret);
            int start = lineElem.getStartOffset();
            int end = lineElem.getEndOffset();
            int nuevaPos = start + Math.min(columnaCaret, end - start - 1);
            paneAnalisis.setCaretPosition(nuevaPos);
        } else {
            paneAnalisis.setCaretPosition(newDoc.getLength());
        }

        mostrarErrores(enAnalisis);

    }

    // Método que mapea el token a su color
    private Color obtenerColorPorToken(TokenEnum tipo) {
        switch (tipo) {
            case PALABRA_RESERVADA:
                return Color.BLUE;
            case IDENTIFICADOR:
                return new Color(0x6B4627);
            case NUMERO:
                return new Color(0x1FC23B);
            case DECIMAL:
                return Color.BLACK;
            case CADENA:
                return new Color(0xF0760E);
            case COMENTARIO_LINEA:
            case COMENTARIO_BLOQUE:
                return new Color(0x1B6615);
            case OPERADOR:
                return new Color(0xB5AB2D);
            case AGRUPACION:
                return new Color(0x991CB8);
            case PUNTUACION:
                return new Color(0x329481);
            case ERROR:
                return Color.RED;
            default:
                return new Color(0x9E7A7A);
        }
    }

    //Metodo que trabaja en conjunto para poder ir pintando letra a letra
    private void limpiarArea(JTextPane paneAnalisis) throws BadLocationException {
        StyledDocument doc = paneAnalisis.getStyledDocument();
        doc.remove(0, doc.getLength());

    }

    // Método para insertar texto con un color específico
    private void insertarToken(String texto, Color color, JTextPane paneAnalisis) throws BadLocationException {

        StyledDocument doc = paneAnalisis.getStyledDocument();
        // Crear estilo temporal
        SimpleAttributeSet estilo = new SimpleAttributeSet();
        StyleConstants.setForeground(estilo, color);
        // Inserta al final del documento
        doc.insertString(doc.getLength(), texto, estilo);

    }

    //Metodo encargado de imprimir los errores en el log de errores
    //True refresca todo el log con el lexer
    //False no hace nada porque esta en busquedas
    private void mostrarErrores(boolean enAnalisis) {

        if (!enAnalisis) {
            return;
        }

        /*for (int i = 0; i < this.listaSentencias.size(); i++) {

            Sentencia sentenciaActiva = this.listaSentencias.get(i);

            for (Lexema lexemaDado : sentenciaActiva.obtenerListadoLexemas()) {

                if (lexemaDado.getLexema().isBlank()) {
                    continue;
                }

                if (!lexemaDado.getCadenaError().isBlank()) {
                    this.logErrores.setText(this.logErrores.getText() + lexemaDado.getLexema() + " <- Error, en " + lexemaDado.getCadenaError() + "\n");
                }

            }

        }*/

    }

    //=========================FIN DEL APARTADO DE METODOS UTILIZADOS PARA GENERAR LAS FUNCIONALIDADES DEL ANALIZADOR LEXICO=======================
}
