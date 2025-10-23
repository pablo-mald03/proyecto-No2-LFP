/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.backend;

import com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum;
import com.pablocompany.proyectono2lfp.excepciones.AnalizadorLexicoException;
import com.pablocompany.proyectono2lfp.excepciones.ErrorEncontradoException;
import com.pablocompany.proyectono2lfp.excepciones.ErrorPuntualException;
import com.pablocompany.proyectono2lfp.jflexpackage.AnalizadorLexico;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
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
    public void pintarLogEdicion() throws BadLocationException {
        pintarLogSalida(this.paneEdicionArchivo, true);
    }
    
    //Metodo que permite acceder facilmente al listado de sentencias
    public ArrayList<Sentencia> getListadoSentencia(){
        return this.lexer.getListaSentencias();
    }
    
    //Metodo que sirve para retornar la referencia del log de errores
    public JTextPane getLogErrores(){
        return this.logErrores;
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

                switch (lexemaDado.getTokenClasificado()) {

                    case TokenEnum.VACIO:
                        String lexemaObtenido = lexemaDado.getLexemaGenerado();

                        String[] lineas = lexemaObtenido.split("\r\n|\r|\n", -1);
                        int cantidadSaltos = lineas.length - 1;
                        String espacios = lineas[0];

                        for (int k = 0; k < espacios.length(); k++) {
                            insertarToken(" ", Color.BLACK, paneAnalisis);
                        }

                        for (int j = 0; j < cantidadSaltos; j++) {
                            insertarToken("\n", Color.BLACK, paneAnalisis);
                        }

                        break;

                    case TokenEnum.TABULACION:

                        insertarToken("     ", Color.BLACK, paneAnalisis);

                        break;

                    default:
                        insertarToken(lexemaDado.getLexemaGenerado(), colorTexto, paneAnalisis);

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
    private void mostrarErrores(boolean enAnalisis) throws BadLocationException {

        if (!enAnalisis) {
            return;
        }

        limpiarArea(this.logErrores);

        ArrayList<Sentencia> listaSentencias = this.lexer.getListaSentencias();

        for (int i = 0; i < listaSentencias.size(); i++) {

            Sentencia sentenciaActiva = listaSentencias.get(i);

            for (int j = 0; j < sentenciaActiva.limiteLexemas(); j++) {

                Lexema lexemaUbicado = sentenciaActiva.getListaLexema(j);

                String lexemaPrimitivo = lexemaUbicado.getLexemaGenerado().replace("\r", "").replace("\n", "");

                if (lexemaUbicado.getTokenClasificado() == TokenEnum.ERROR) {

                    int indice = j + 1;

                    if (indice < sentenciaActiva.limiteLexemas()) {

                        Lexema lexemaPosterior = sentenciaActiva.getListaLexema(indice);

                        if (lexemaPosterior.getTokenClasificado() != TokenEnum.ERROR) {

                            String trasecendencia = hallarTrascendencia(lexemaPrimitivo, sentenciaActiva, indice);

                            insertarToken(trasecendencia + " <- Error, en " + lexemaPrimitivo + " - " + lexemaUbicado.getMensajeError(), Color.RED, this.logErrores);

                        } else {
                            insertarToken(lexemaPrimitivo + " <- Error, en " + lexemaPrimitivo + " - " + lexemaUbicado.getMensajeError(), Color.RED, this.logErrores);
                        }

                    } else {

                        insertarToken(lexemaPrimitivo + " <- Error, en " + lexemaPrimitivo + " - " + lexemaUbicado.getMensajeError(), Color.RED, this.logErrores);
                    }

                    insertarToken("\n", Color.BLACK, this.logErrores);

                }

            }

        }
    }

    //Metodo auxiliar que sirve para hallar la cadena tope de lexema de error
    private String hallarTrascendencia(String cadenaPrimitiva, Sentencia sentenciaActiva, int inicio) {

        StringBuilder cadenaTrascendencia = new StringBuilder();

        cadenaTrascendencia.append(cadenaPrimitiva);

        for (int i = inicio; i < sentenciaActiva.limiteLexemas(); i++) {

            Lexema lexemaUbicado = sentenciaActiva.getListaLexema(i);

            if (lexemaUbicado.getTokenClasificado() == TokenEnum.VACIO || lexemaUbicado.getTokenClasificado() == TokenEnum.ESPACIO || lexemaUbicado.getTokenClasificado() == TokenEnum.TABULACION) {
                break;
            }

            cadenaTrascendencia.append(lexemaUbicado.getLexemaGenerado());

        }

        return cadenaTrascendencia.toString();
    }

    //Metodo que sirve para operar la busqueda de patrones
    public void busquedaPatrones(JTextPane paneBusqueda, String palabraBuscada) throws BadLocationException, ErrorEncontradoException, ErrorPuntualException, AnalizadorLexicoException {

        pintarLogSalida(paneBusqueda, false);

        if (this.lexer.getListaSentencias().isEmpty()) {
            throw new ErrorEncontradoException("El texto esta vacio");
        }

        Highlighter highlighter = paneBusqueda.getHighlighter();
        highlighter.removeAllHighlights();

        //Variable importante para saber si minimo hay una coincidencia
        boolean hayCoincidencia = false;

        hayCoincidencia = busquedaSofisticada(palabraBuscada, paneBusqueda);

        //busqueda sofisticada para encontrar cualquier palabra
        if (hayCoincidencia) {
            return;
        }

        //busqueda sofisticada para encontrar cualquier palabra
        if (!hayCoincidencia) {
            throw new ErrorPuntualException("No existen patrones relacionados");
        }

    }

    //Metodo encargado de llevar a cabo la busqueda sofisticada de patrones
    private boolean busquedaSofisticada(String palabraBuscada, JTextPane paneBusqueda) throws BadLocationException, AnalizadorLexicoException {
        boolean encontrado = false;
        for (int i = 0; i < this.lexer.getListaSentencias().size(); i++) {
            Sentencia sentenciaBuscada = this.lexer.getListaSentencias().get(i);

            for (int j = 0; j < sentenciaBuscada.limiteLexemas(); j++) {

                Lexema lexemaActual = sentenciaBuscada.getListaLexema(j);

                String textoLexema = lexemaActual.getLexemaGenerado();

                int index = textoLexema.indexOf(palabraBuscada);
                while (index != -1) {

                    int fila = Math.max(0, lexemaActual.getLineaCoordenada());
                    int columnaInicioLexema = Math.max(0, lexemaActual.getColumna());
                    int columnaMatchInicio = columnaInicioLexema + index;
                    int columnaMatchFin = columnaMatchInicio + palabraBuscada.length() - 1;

                    resaltar(paneBusqueda, fila, columnaMatchInicio, columnaMatchFin);
                    encontrado = true;

                    index = textoLexema.indexOf(palabraBuscada, index + palabraBuscada.length());
                }

            }
        }

        return encontrado;

    }

    //Metodo que se encarga de resaltar la palabra que se busca
    public void resaltar(JTextPane textPane, int linea, int startColumna, int endColumna) throws BadLocationException {
        Highlighter highlighter = textPane.getHighlighter();
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

        int startOffset = getLineaInicioFin(textPane, linea, startColumna);
        int endOffset = getLineaInicioFin(textPane, linea, endColumna) + 1;

        highlighter.addHighlight(startOffset, endOffset, painter);
    }

    //Metodo que permite ubicar la coordenada para resaltar
    private int getLineaInicioFin(JTextPane textPane, int linea, int columna) throws BadLocationException {
        Element root = textPane.getDocument().getDefaultRootElement();
        Element lineElem = root.getElement(linea);
        int start = lineElem.getStartOffset();
        return start + columna;
    }

    //=========================FIN DEL APARTADO DE METODOS UTILIZADOS PARA GENERAR LAS FUNCIONALIDADES DEL ANALIZADOR LEXICO=======================
}
