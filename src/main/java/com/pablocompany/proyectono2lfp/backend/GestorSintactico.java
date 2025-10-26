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
import javax.swing.text.Element;
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

        //Metodo delegado para separar todo lo que tiene significado sintactico
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

                    j = hallarDefinicion(j, sentenciaUbicada.obtenerListadoLexemas(), lexemaUbicado);

                } else {

                    listadoLexemas.add(lexemaUbicado);
                }

            }

        }

    }

    //Metodo que llama a una serie de metodos para moverse 
    private int hallarDefinicion(int indiceRecorrido, ArrayList<Lexema> listadoLexemasSintacticos, Lexema lexemaInicial) {

        if (lexemaInicial.getTokenSintactico() != TokenEnum.DEFINIR) {
            return indiceRecorrido;
        }

        ArrayList<TokenEnum> estructura = new ArrayList<>();
        estructura.add(TokenEnum.DEFINIR);
        estructura.add(TokenEnum.IDENTIFICADOR);
        estructura.add(TokenEnum.COMO);
        estructura.add(TokenEnum.INDEFINIDO);
        estructura.add(TokenEnum.PUNTO_COMA);

        ArrayList<Lexema> listadoAuxiliar = new ArrayList<>(1000);

        String tipoUbicado = "";

        for (int i = indiceRecorrido; i < listadoLexemasSintacticos.size(); i++) {

            Lexema lexemaUbicado = listadoLexemasSintacticos.get(i);

            if (estructura.isEmpty()) {
                //Significa que la pila ya no tiene nada y por ende es valido
                this.listadoParser.add(new Sintaxis(listadoAuxiliar, false, "", TipoOperacionEnum.DEFINICION_VARIABLE));
                return i;
            }

            if (lexemaUbicado.getTokenClasificado() == TokenEnum.ESPACIO || lexemaUbicado.getTokenClasificado() == TokenEnum.TABULACION) {

                listadoAuxiliar.add(lexemaUbicado);

            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.DEFINIR && lexemaUbicado.getTokenClasificado() == TokenEnum.PALABRA_RESERVADA) {

                if (!estructura.contains(TokenEnum.DEFINIR)) {

                    String errorEsperado = hallarErrorDefinicion(estructura, tipoUbicado);
                    this.listadoParser.add(new Sintaxis(listadoAuxiliar, true, errorEsperado, TipoOperacionEnum.DEFINICION_VARIABLE));
                    return i;
                }

                listadoAuxiliar.add(lexemaUbicado);
                estructura.remove(TokenEnum.DEFINIR);

            } else if (lexemaUbicado.getTokenClasificado() == TokenEnum.IDENTIFICADOR) {

                if (!estructura.contains(TokenEnum.IDENTIFICADOR)) {

                    String errorEsperado = hallarErrorDefinicion(estructura, tipoUbicado);
                    this.listadoParser.add(new Sintaxis(listadoAuxiliar, true, errorEsperado, TipoOperacionEnum.DEFINICION_VARIABLE));
                    return i;
                }

                listadoAuxiliar.add(lexemaUbicado);
                estructura.remove(TokenEnum.IDENTIFICADOR);

            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.COMO && lexemaUbicado.getTokenClasificado() == TokenEnum.PALABRA_RESERVADA) {

                if (!estructura.contains(TokenEnum.COMO)) {

                    String errorEsperado = hallarErrorDefinicion(estructura, tipoUbicado);
                    this.listadoParser.add(new Sintaxis(listadoAuxiliar, true, errorEsperado, TipoOperacionEnum.DEFINICION_VARIABLE));
                    return i;
                }

                listadoAuxiliar.add(lexemaUbicado);
                estructura.remove(TokenEnum.COMO);

            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.TIPO_ENTERO
                    && lexemaUbicado.getTokenSintactico() == TokenEnum.TIPO_CADENA
                    && lexemaUbicado.getTokenSintactico() == TokenEnum.TIPO_NUMERO) {

                if (!estructura.contains(TokenEnum.INDEFINIDO)) {

                    String errorEsperado = hallarErrorDefinicion(estructura, tipoUbicado);
                    this.listadoParser.add(new Sintaxis(listadoAuxiliar, true, errorEsperado, TipoOperacionEnum.DEFINICION_VARIABLE));
                    return i;
                }

                tipoUbicado = lexemaUbicado.getTokenSintactico().getNombreToken();
                listadoAuxiliar.add(lexemaUbicado);
                estructura.remove(TokenEnum.INDEFINIDO);
            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.PUNTO_COMA && lexemaUbicado.getTokenClasificado() == TokenEnum.PUNTUACION) {

                if (!estructura.contains(TokenEnum.PUNTO_COMA)) {

                    String errorEsperado = hallarErrorDefinicion(estructura, tipoUbicado);
                    this.listadoParser.add(new Sintaxis(listadoAuxiliar, true, errorEsperado, TipoOperacionEnum.DEFINICION_VARIABLE));
                    return i;
                }

                listadoAuxiliar.add(lexemaUbicado);
                estructura.remove(TokenEnum.PUNTO_COMA);

            } else if (lexemaUbicado.getTokenClasificado() == TokenEnum.VACIO) {

                if (!estructura.isEmpty()) {
                    //Cuando no es vacia significa que la sintaxis no es valida por ende hay error
                    String errorEsperado = hallarErrorDefinicion(estructura, tipoUbicado);
                    this.listadoParser.add(new Sintaxis(listadoAuxiliar, true, errorEsperado, TipoOperacionEnum.DEFINICION_VARIABLE));
                } else {

                    this.listadoParser.add(new Sintaxis(listadoAuxiliar, false, "", TipoOperacionEnum.DEFINICION_VARIABLE));
                    return i + 1;
                }

            }
        }

        return listadoLexemasSintacticos.size();

    }

    //=============METODO SOLAMENTE UTIL PARA HALLAR EL ERROR SINTACTICO DE DEFINICION DE VARIABLES================
    private String hallarErrorDefinicion(ArrayList<TokenEnum> estructuraActual, String tipoUbicado) {

        StringBuilder stringBuilder = new StringBuilder();

        ArrayList<TokenEnum> estructuraSintactica = new ArrayList<>();
        estructuraSintactica.add(TokenEnum.DEFINIR);
        estructuraSintactica.add(TokenEnum.IDENTIFICADOR);
        estructuraSintactica.add(TokenEnum.COMO);
        estructuraSintactica.add(TokenEnum.INDEFINIDO);
        estructuraSintactica.add(TokenEnum.PUNTO_COMA);

        for (TokenEnum token : estructuraActual) {

            switch (token) {
                case DEFINIR:

                    stringBuilder.append(", cerca de ");
                    stringBuilder.append(TokenEnum.DEFINIR.getNombreToken());

                    stringBuilder.append(": se esperaba la palabra reservada ");
                    stringBuilder.append(TokenEnum.DEFINIR.getNombreToken());

                    return stringBuilder.toString();

                case IDENTIFICADOR:

                    stringBuilder.append(", cerca de ");
                    int indice = estructuraSintactica.indexOf(TokenEnum.IDENTIFICADOR);
                    stringBuilder.append(estructuraSintactica.get(indice).getTipo());

                    stringBuilder.append(": se esperaba ");
                    stringBuilder.append(TokenEnum.IDENTIFICADOR.getNombreToken());

                    return stringBuilder.toString();

                case COMO:

                    stringBuilder.append(", cerca de ");
                    int indiceComo = estructuraSintactica.indexOf(TokenEnum.COMO);
                    stringBuilder.append(estructuraSintactica.get(indiceComo).getTipo());

                    stringBuilder.append(": se esperaba la palabra reservada ");
                    stringBuilder.append(TokenEnum.COMO.getNombreToken());

                    return stringBuilder.toString();

                case INDEFINIDO:

                    stringBuilder.append(", cerca de ");
                    int indiceDefinicion = estructuraSintactica.indexOf(TokenEnum.INDEFINIDO);
                    stringBuilder.append(estructuraSintactica.get(indiceDefinicion).getTipo());

                    stringBuilder.append(": se esperaba un tipo de dato");

                    return stringBuilder.toString();

                case PUNTO_COMA:

                    stringBuilder.append(", cerca de ");

                    if (!tipoUbicado.isBlank()) {
                        stringBuilder.append(tipoUbicado);
                    } else {
                        stringBuilder.append("el tipo de dato");
                    }

                    stringBuilder.append(": se esperaba ;");

                    return stringBuilder.toString();

                default:
                    return "";
            }

        }

        return "";

    }

    
    
    
    //=============FIN DEL METODO SOLAMENTE UTIL PARA HALLAR EL ERROR SINTACTICO DE DEFINICION DE VARIABLES================
    
    
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

       

        for (int i = 0; i < this.listadoParser.size(); i++) {

            Sintaxis sintaxisActiva = this.listadoParser.get(i);

           /* for (Sintaxis sintaxis : sentenciaActiva.obtenerListadoLexemas()) {

                Color colorTexto = obtenerColorPorToken(lexemaDado.getTokenClasificado());

                switch (lexemaDado.getTokenClasificado()) {

                    case TokenEnum.VACIO:
                        String lexemaObtenido = lexemaDado.getLexemaGenerado();

                        String[] lineas = lexemaObtenido.split("\r\n|\r|\n", -1);
                        int cantidadSaltos = lineas.length - 1;
                        String espacios = lineas[0];

                        for (int k = 0; k < espacios.length(); k++) {
                            insertarTexto(" ", Color.BLACK, paneAnalisis);
                        }

                        for (int j = 0; j < cantidadSaltos; j++) {
                            insertarTexto("\n", Color.BLACK, paneAnalisis);
                        }

                        break;

                    case TokenEnum.TABULACION:

                        insertarTexto("     ", Color.BLACK, paneAnalisis);

                        break;

                    default:
                        insertarTexto(lexemaDado.getLexemaGenerado(), colorTexto, paneAnalisis);

                }

            }*/

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
