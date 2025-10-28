/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.backend;

import com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum;
import static com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum.COMO;
import static com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum.DEFINIR;
import static com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum.IDENTIFICADOR;
import static com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum.INDEFINIDO;
import static com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum.PUNTO_COMA;
import com.pablocompany.proyectono2lfp.excepciones.AnalizadorLexicoException;
import com.pablocompany.proyectono2lfp.excepciones.ConfigException;
import com.pablocompany.proyectono2lfp.excepciones.ErrorSintacticoException;
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
    
    private JTextPane logEdicion;

    //Atributo que permite manejar la instancia compartida del listado de sentencias sintacticas
    private ArrayList<Sintaxis> listadoParser = new ArrayList<>(5000);
    
    public GestorSintactico(GestorLexer gestionLexer, JTextPane logErrores, JTextPane logSintactico, JTextPane logEdicion) {
        this.gestionLexer = gestionLexer;
        this.logSintactico = logSintactico;
        this.logErroresSintacticos = logErrores;
        this.logEdicion = logEdicion;
    }

    //Metodo encargado para poder 
    private ArrayList<Sentencia> getListadoSentencias() {
        return this.gestionLexer.getListadoSentencia();
    }

    //Metodo que permite iniciar el analisis sitactico
    public void iniciarAnalisis() throws AnalizadorLexicoException, ConfigException, BadLocationException, ErrorSintacticoException {

        //Vallida si hay errores registrados
        if (hayErroresLexicos()) {
            throw new AnalizadorLexicoException("No puedes ejecutar el analisis sintactico\nHay errores registrados");
        }

        //Metodo delegado para separar todo lo que tiene significado sintactico
        separarSintaxis();
        evaluarAsignaciones();
        comprobarAsignaciones();
        pintarLogEdicion();
        
    }

    //Metodo utilizado para poder verificar las asignaciones de variables
    private void comprobarAsignaciones() {
        
        for (int i = 0; i < this.listadoParser.size(); i++) {
            Sintaxis sintaxisUbicada = this.listadoParser.get(i);
            
            if (sintaxisUbicada.getTipoOperacion() == TipoOperacionEnum.ASIGNACION_VALORES) {
                
                for (int j = 0; j < sintaxisUbicada.getExpresionSintaxis().size(); j++) {
                    
                    Lexema llave = sintaxisUbicada.getLlaveExpresion(j);
                    
                    verificarAsignacion(llave, sintaxisUbicada);
                    
                }
                
            }
            
        }
        
    }

    //Metodo complementario para poder identificar la asignacion llave valor
    private void verificarAsignacion(Lexema llave, Sintaxis sintaxisActual) {
        
        for (int i = 0; i < this.listadoParser.size(); i++) {
            
            Sintaxis sintaxisUbicada = this.listadoParser.get(i);
            
            if (sintaxisUbicada.getTipoOperacion() == TipoOperacionEnum.DEFINICION_VARIABLE) {
                
                for (int j = 0; j < sintaxisUbicada.getListadoLexemas().size(); j++) {
                    
                    Lexema lexemaUbicado = sintaxisUbicada.getListadoLexemas().get(j);
                    
                    if (lexemaUbicado.getTokenClasificado() == TokenEnum.IDENTIFICADOR && lexemaUbicado.getLexemaGenerado().equals(llave.getLexemaGenerado())) {
                        
                        return;
                        
                    }
                    
                }
                
            }
            
        }
        
        int longitusLexemas = sintaxisActual.getListadoLexemas().size();
        
        sintaxisActual.getListadoLexemas().get(longitusLexemas - 1).setErrorSintactico(true);
        sintaxisActual.setTieneError(true);
        sintaxisActual.setMensajeError("La variable \"" + llave.getLexemaGenerado() + "\" no esta declarada");
        
    }

    //Metodo que sirve para repintar todo el log tras el procesamiento sintactico
    private void pintarLogEdicion() throws BadLocationException, ErrorSintacticoException {
        pintarLogSalida(this.logEdicion);
    }

    //==========================METODOS UNICOS PARA EL RECONOCIMIENTO DE DECLARACION DE VARIABLES================
    //Metodo utilizado para poder buscar asignaciones de variables
    private void evaluarAsignaciones() throws ErrorSintacticoException {
        
        if (this.listadoParser.isEmpty()) {
            throw new ErrorSintacticoException("O ocurrido un error al evaluar la sintaxis");
        }
        
        ArrayList<Sintaxis> listadoParserActual = reconocerVariables();
        
        this.listadoParser = listadoParserActual;
        
    }

    //Metodo especializado delegado para poder obtener el listado de lexemas en el indice que se va 
    private ArrayList<Lexema> obtenerListadoIndexado(ArrayList<Lexema> listadoLexemas, int indice) {
        
        ArrayList<Lexema> listadoLimpiado = new ArrayList<>();
        for (int i = indice; i < listadoLexemas.size(); i++) {
            
            listadoLimpiado.add(listadoLexemas.get(i));
            
        }
        
        return listadoLimpiado;
        
    }

    //Metodo delegado para reconocer la sintaxis que se ubica
    private ArrayList<Sintaxis> reconocerVariables() throws ErrorSintacticoException {
        
        ArrayList<Sintaxis> referenciaSintaxis = this.listadoParser;
        
        ArrayList<Sintaxis> nuevoListadoParser = new ArrayList<>(500);
        
        ArrayList<Lexema> lexemasAuxiliares = new ArrayList<>(1000);

        //Variables que permiten saber si ya se ha reconocido la declaracion de variable
        boolean identificadorUbicado = false;
        
        for (int i = 0; i < referenciaSintaxis.size(); i++) {
            
            Sintaxis sintaxisEvaluada = referenciaSintaxis.get(i);
            
            if (sintaxisEvaluada.getTipoOperacion() == TipoOperacionEnum.DEFINICION_VARIABLE) {
                nuevoListadoParser.add(sintaxisEvaluada);
                continue;
            }
            
            for (int j = 0; j < sintaxisEvaluada.getListadoLexemas().size(); j++) {
                
                Lexema lexemaEvaluado = sintaxisEvaluada.getLexema(j);
                
                if (lexemaEvaluado.getTokenClasificado() == TokenEnum.IDENTIFICADOR) {
                    
                    if (!lexemasAuxiliares.isEmpty()) {
                        
                        nuevoListadoParser.add(new Sintaxis(lexemasAuxiliares, false, "", TipoOperacionEnum.NO_SINTACTICO));
                        lexemasAuxiliares = new ArrayList<>(1000);
                        identificadorUbicado = false;
                    }
                    
                    j = hallarAsignacion(j, nuevoListadoParser, sintaxisEvaluada.getListadoLexemas(), lexemaEvaluado) - 1;
                    
                    if (j < 0) {
                        j = 0;
                    }
                    
                    if (j < sintaxisEvaluada.getListadoLexemas().size()) {
                        
                        if (!identificadorUbicado) {
                            identificadorUbicado = true;
                        }
                    }
                    
                } else if (lexemaEvaluado.getTokenClasificado() == TokenEnum.VACIO) {
                    
                    lexemasAuxiliares.add(lexemaEvaluado);
                    nuevoListadoParser.add(new Sintaxis(lexemasAuxiliares, false, "", TipoOperacionEnum.NO_SINTACTICO));
                    lexemasAuxiliares = new ArrayList<>(1000);
                    identificadorUbicado = false;
                    
                } else {
                    
                    lexemasAuxiliares.add(lexemaEvaluado);
                }
                
            }
            
            if (!lexemasAuxiliares.isEmpty() && identificadorUbicado) {
                nuevoListadoParser.add(new Sintaxis(lexemasAuxiliares, false, "", TipoOperacionEnum.NO_SINTACTICO));
                lexemasAuxiliares = new ArrayList<>(1000);
                identificadorUbicado = false;
            }

            // if(lexemaRecorrido.)
        }
        
        if (!lexemasAuxiliares.isEmpty()) {
            nuevoListadoParser.add(new Sintaxis(lexemasAuxiliares, false, "", TipoOperacionEnum.NO_SINTACTICO));
        }

        //Se reconstruye la referencia
        return nuevoListadoParser;
        
    }

    //Metodo complementario del reconocimiento de variables que va verificando la asignacion de valores
    private int hallarAsignacion(int indiceRecursion, ArrayList<Sintaxis> nuevaSintaxis, ArrayList<Lexema> listadoLexemasSintacticos, Lexema lexemaInicial) throws ErrorSintacticoException {
        
        if (lexemaInicial.getTokenClasificado() != TokenEnum.IDENTIFICADOR) {
            return indiceRecursion + 1;
        }
        
        ArrayList<TokenEnum> estructura = new ArrayList<>();
        estructura.add(TokenEnum.IDENTIFICADOR);
        estructura.add(TokenEnum.IGUAL);
        estructura.add(TokenEnum.INDEFINIDO);
        estructura.add(TokenEnum.PUNTO_COMA);
        
        ArrayList<Lexema> listadoReconstruido = new ArrayList<>(1000);
        
        boolean hayContenido = false;

        //True indica cuando se detecta el indice de que la cadena esta incompleta
        boolean estaIncompleta = false;
        
        boolean igualdadEncontrada = false;

        //Flag que marca si ya se tiene un operador matematico antes
        boolean hayOperador = false;

        //Flag que indica cuando se ha declarado un identificador hallado
        boolean estaIniciado = false;

        //Flag que indica que apenas se esta iniciando la igualacion
        int seInicializo = 0;

        //Flag que indica que ya hay un identificador reconocido
        boolean hayIdentificador = false;
        
        int indiceRetorno = 0;
        
        for (int i = indiceRecursion; i < listadoLexemasSintacticos.size(); i++) {
            
            indiceRetorno = i;
            
            Lexema lexemaUbicado = listadoLexemasSintacticos.get(i);
            
            if (i == listadoLexemasSintacticos.size() - 1
                    && !estructura.isEmpty()
                    && estaIncompleta) {

                //Se ejecuta solo cuando se termina la sentencia entera y se marca como error PENDIENTE 
                lexemaUbicado.setErrorSintactico(true);
                String errorEsperado = hallarErrorAsignacion(estructura);
                listadoReconstruido.add(lexemaUbicado);
                
                nuevaSintaxis.add(new Sintaxis(listadoLexemasSintacticos, true, errorEsperado, TipoOperacionEnum.ASIGNACION_VALORES));
                
                return listadoLexemasSintacticos.size();
                
            }
            
            if ((!estructura.isEmpty() || estructura.isEmpty()) && hayContenido && estaIncompleta) {
                //Significa que la pila ya no tiene nada y por ende es valido
                //Se ejecuta solo cuando se termina la sentencia entera y se marca como error PENDIENTE 
                lexemaUbicado.setErrorSintactico(true);
                String errorEsperado = hallarErrorAsignacion(estructura);
                listadoReconstruido.add(lexemaUbicado);
                
                nuevaSintaxis.add(new Sintaxis(listadoLexemasSintacticos, true, errorEsperado, TipoOperacionEnum.ASIGNACION_VALORES));
                
                return listadoLexemasSintacticos.size();
            }
            
            if (estructura.isEmpty() && hayContenido) {
                //Significa que la pila ya no tiene nada y por ende es valido
                nuevaSintaxis.add(new Sintaxis(listadoReconstruido, false, "", TipoOperacionEnum.ASIGNACION_VALORES));
                
                int indiceSintaxis = nuevaSintaxis.size() - 1;
                
                if (indiceSintaxis < 0) {
                    indiceSintaxis = 0;
                }
                
                nuevaSintaxis.get(indiceSintaxis).setExpresionSintaxis(listadoReconstruido);
                return i;
            }
            
            if (lexemaUbicado.getTokenClasificado() == TokenEnum.ESPACIO || lexemaUbicado.getTokenClasificado() == TokenEnum.TABULACION) {
                
                listadoReconstruido.add(lexemaUbicado);
                
            } else if (lexemaUbicado.getTokenClasificado() == TokenEnum.IDENTIFICADOR
                    || lexemaUbicado.getTokenClasificado() == TokenEnum.NUMERO
                    || lexemaUbicado.getTokenClasificado() == TokenEnum.DECIMAL
                    || lexemaUbicado.getTokenClasificado() == TokenEnum.CADENA) {
                
                if (!estaIniciado && lexemaUbicado.getTokenClasificado() != TokenEnum.IDENTIFICADOR) {
                    
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }

                //Error de doble identificador
                if (!estructura.contains(TokenEnum.IDENTIFICADOR) && !igualdadEncontrada) {
                    
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }

                //Error cuando no hay un signo antes del identificador
                if (!estructura.contains(TokenEnum.IDENTIFICADOR) && !hayOperador && seInicializo == 2) {
                    
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }

                //Error cuando hay dos operadores juntos antes de una igualdad
                if (estructura.contains(TokenEnum.IDENTIFICADOR) && estaIniciado && !hayOperador && !igualdadEncontrada) {
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }
                
                if (!hayContenido) {
                    hayContenido = true;
                }
                
                if (!hayIdentificador) {
                    hayIdentificador = true;
                }
                
                if (hayOperador) {
                    hayOperador = false;
                }
                
                if (estructura.contains(TokenEnum.IDENTIFICADOR)) {
                    estructura.remove(TokenEnum.IDENTIFICADOR);
                    listadoReconstruido.add(lexemaUbicado);
                    estaIniciado = true;
                    seInicializo = 2;
                    continue;
                }
                
                try {
                    i = hallarAgrupacion(i, nuevaSintaxis, listadoReconstruido, estructura, listadoLexemasSintacticos, estaIncompleta) - 1;
                } catch (ErrorSintacticoException ex) {
                    return listadoLexemasSintacticos.size();
                }
                
            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.PARENTESIS_APERTURA) {

                //Error de doble identificador
                if (!estructura.contains(TokenEnum.IDENTIFICADOR) && !igualdadEncontrada) {
                    
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }

                //Error cuando no hay un signo antes del identificador
                if (!estructura.contains(TokenEnum.IDENTIFICADOR) && !hayOperador && seInicializo == 2) {
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }

                //Error cuando hay dos operadores juntos antes de una igualdad
                if (estructura.contains(TokenEnum.IDENTIFICADOR) && !hayOperador && !igualdadEncontrada) {
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }
                
                if (!hayContenido) {
                    hayContenido = true;
                }
                
                if (!hayIdentificador) {
                    hayIdentificador = true;
                }
                
                if (hayOperador) {
                    hayOperador = false;
                }
                
                if (estructura.contains(TokenEnum.INDEFINIDO)) {
                    estructura.remove(TokenEnum.INDEFINIDO);
                }
                
                try {
                    i = hallarAgrupacion(i, nuevaSintaxis, listadoReconstruido, estructura, listadoLexemasSintacticos, estaIncompleta) - 1;
                } catch (ErrorSintacticoException ex) {
                    return listadoLexemasSintacticos.size();
                }
                
            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.IGUAL) {
                
                if (!estructura.contains(TokenEnum.IGUAL) && igualdadEncontrada) {
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }
                
                if (!hayContenido) {
                    hayContenido = true;
                }
                
                if (!igualdadEncontrada) {
                    igualdadEncontrada = true;
                }
                
                listadoReconstruido.add(lexemaUbicado);
                seInicializo = 1;
                estructura.remove(TokenEnum.IGUAL);
                
            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.SUMA
                    || lexemaUbicado.getTokenSintactico() == TokenEnum.RESTA
                    || lexemaUbicado.getTokenSintactico() == TokenEnum.MULTIPLICACION
                    || lexemaUbicado.getTokenSintactico() == TokenEnum.DIVISION) {

                //Error cuando no hay igualdad
                if (!igualdadEncontrada) {
                    
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }

                //Error cuando ya hay igualdad pero se quiere poner un operador a la par de la otra
                if (!estructura.contains(TokenEnum.INDEFINIDO) && igualdadEncontrada && hayOperador) {
                    
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }
                
                if (!estructura.contains(TokenEnum.INDEFINIDO) && igualdadEncontrada && !hayIdentificador) {
                    
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }
                
                if (!hayContenido) {
                    hayContenido = true;
                }
                
                if (!hayOperador) {
                    hayOperador = true;
                }
                
                if (hayIdentificador) {
                    hayIdentificador = false;
                }
                
                listadoReconstruido.add(lexemaUbicado);
                
                if (estructura.contains(TokenEnum.INDEFINIDO)) {
                    estructura.remove(TokenEnum.INDEFINIDO);
                }
                
            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.PUNTO_COMA && lexemaUbicado.getTokenClasificado() == TokenEnum.PUNTUACION) {

                //Error cuando no hay igualdad
                if (!igualdadEncontrada) {
                    
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }

                //Error cuando ya hubo punto y coma
                if (!estructura.contains(TokenEnum.PUNTO_COMA)) {
                    
                    declararErrorAsignacion(listadoLexemasSintacticos.size(), nuevaSintaxis, listadoLexemasSintacticos, estructura);
                    return listadoLexemasSintacticos.size();
                }
                
                if (!hayContenido) {
                    hayContenido = true;
                }
                
                listadoReconstruido.add(lexemaUbicado);
                estructura.remove(TokenEnum.PUNTO_COMA);
                
            } else if (lexemaUbicado.getTokenClasificado() == TokenEnum.VACIO) {
                
                if (!estructura.isEmpty()) {
                    //Cuando no es vacia significa que la sintaxis no es valida por ende hay error

                    int indice = listadoReconstruido.size() - 1;
                    
                    if (indice < 0) {
                        indice = 0;
                    }
                    
                    listadoReconstruido.add(lexemaUbicado);
                    listadoReconstruido.get(indice).setErrorSintactico(true);
                    
                    String errorEsperado = hallarErrorAsignacion(estructura);
                    nuevaSintaxis.add(new Sintaxis(listadoReconstruido, true, errorEsperado, TipoOperacionEnum.ASIGNACION_VALORES));
                    return i + 1;
                } else {
                    
                    listadoReconstruido.add(lexemaUbicado);
                    nuevaSintaxis.add(new Sintaxis(listadoReconstruido, false, "", TipoOperacionEnum.ASIGNACION_VALORES));
                    return i;
                }
                
            } else {

                //Se ejecuta cuanso se detecta un indice de que la cadena esta incompleta 
                estaIncompleta = true;
                
                int indice = listadoLexemasSintacticos.size() - 1;
                
                if (indice < 0) {
                    indice = 0;
                }
                
                lexemaUbicado.setErrorSintactico(true);
                listadoReconstruido.add(lexemaUbicado);
                
            }
            
        }
        
        if (estructura.isEmpty() && hayContenido) {
            //Significa que la pila ya no tiene nada y por ende es valido
            nuevaSintaxis.add(new Sintaxis(listadoReconstruido, false, "", TipoOperacionEnum.ASIGNACION_VALORES));
            
            int indiceSintaxis = nuevaSintaxis.size() - 1;
            
            if (indiceSintaxis < 0) {
                indiceSintaxis = 0;
            }
            
            nuevaSintaxis.get(indiceSintaxis).setExpresionSintaxis(listadoReconstruido);
            
            return indiceRetorno + 1;
        } else {

            //Retorna cuando hay errores registrados busca el indice de error
            estaIncompleta = true;
            int indice = listadoReconstruido.size() - 1;
            
            if (indice < 0) {
                indice = 0;
            }
            
            listadoReconstruido.get(indice).setErrorSintactico(true);
            
            String errorEsperado = hallarErrorAsignacion(estructura);
            nuevaSintaxis.add(new Sintaxis(listadoReconstruido, true, errorEsperado, TipoOperacionEnum.ASIGNACION_VALORES));
            return indiceRetorno + 1;
            
        }
        
    }

    //Metodo complementario que permite ingresar agrupaciones 
    private int hallarAgrupacion(
            int indiceRecorrido,
            ArrayList<Sintaxis> nuevaSintaxis,
            ArrayList<Lexema> listadoReconstruido,
            ArrayList<TokenEnum> estructura,
            ArrayList<Lexema> listadoLexemasSintacticos,
            boolean referenciaError) throws ErrorSintacticoException {
        
        int balanceParentesis = 0;
        boolean hayParentesis = false;
        boolean hayContenido = false;
        boolean hayError = false;
        
        TokenEnum tokenAnterior = null;
        int indiceActual = 0;
        
        for (int i = indiceRecorrido; i < listadoLexemasSintacticos.size(); i++) {
            
            indiceActual = i;
            Lexema lexema = listadoLexemasSintacticos.get(i);
            TokenEnum token = lexema.getTokenSintactico();
            
            if (lexema.getTokenClasificado() == TokenEnum.ESPACIO
                    || lexema.getTokenClasificado() == TokenEnum.TABULACION) {
                listadoReconstruido.add(lexema);
                continue;
            }
            
            if (i == listadoLexemasSintacticos.size() - 1
                    && lexema.getTokenClasificado() != TokenEnum.PUNTUACION
                    && (token == null || token != TokenEnum.PUNTO_COMA)) {
                
                lexema.setErrorSintactico(true);
                hayError = true;
                break;
            }
            
            if (token == TokenEnum.PARENTESIS_APERTURA) {
                listadoReconstruido.add(lexema);
                balanceParentesis++;
                hayParentesis = true;

                //PENDIENTE
                if (tokenAnterior != null
                        && (tokenAnterior == TokenEnum.SUMA || tokenAnterior == TokenEnum.RESTA
                        || tokenAnterior == TokenEnum.MULTIPLICACION || tokenAnterior == TokenEnum.DIVISION)) {
                    lexema.setErrorSintactico(true);
                    hayError = true;
                    referenciaError = true;
                    nuevaSintaxis.add(new Sintaxis(listadoReconstruido, true,
                            "Operador antes de una agrupación inválido", TipoOperacionEnum.ASIGNACION_VALORES));
                    throw new ErrorSintacticoException("Operador antes de una agrupación inválido");
                }
                
            } else if (token == TokenEnum.PARENTESIS_CIERRE) {
                listadoReconstruido.add(lexema);
                balanceParentesis--;
                
                if (balanceParentesis == 0 && hayParentesis) {
                    return i + 1;
                } else if (balanceParentesis < 0) {
                    lexema.setErrorSintactico(true);
                    nuevaSintaxis.add(new Sintaxis(
                            listadoReconstruido,
                            true,
                            "Se encontró un paréntesis de cierre sin apertura correspondiente",
                            TipoOperacionEnum.ASIGNACION_VALORES
                    ));
                    throw new ErrorSintacticoException("Sintaxis inválida: cierre sin apertura");
                }
                
            } // --- Identificadores y valores ---
            else if (lexema.getTokenClasificado() == TokenEnum.IDENTIFICADOR
                    || lexema.getTokenClasificado() == TokenEnum.NUMERO
                    || lexema.getTokenClasificado() == TokenEnum.DECIMAL
                    || lexema.getTokenClasificado() == TokenEnum.CADENA) {
                
                listadoReconstruido.add(lexema);
                hayContenido = true;
                
                if (tokenAnterior != null
                        && (tokenAnterior == TokenEnum.NUMERO
                        || tokenAnterior == TokenEnum.DECIMAL
                        || tokenAnterior == TokenEnum.IDENTIFICADOR
                        || tokenAnterior == TokenEnum.CADENA
                        || tokenAnterior == TokenEnum.PARENTESIS_CIERRE)) {
                    lexema.setErrorSintactico(true);
                    hayError = true;
                    nuevaSintaxis.add(new Sintaxis(listadoReconstruido, true,
                            "Dos operandos consecutivos sin operador", TipoOperacionEnum.ASIGNACION_VALORES));
                    break;
                }
                
            } else if (token == TokenEnum.SUMA || token == TokenEnum.RESTA
                    || token == TokenEnum.MULTIPLICACION || token == TokenEnum.DIVISION) {
                
                listadoReconstruido.add(lexema);
                
                if (tokenAnterior != null
                        && (tokenAnterior == TokenEnum.SUMA || tokenAnterior == TokenEnum.RESTA
                        || tokenAnterior == TokenEnum.MULTIPLICACION || tokenAnterior == TokenEnum.DIVISION)) {
                    lexema.setErrorSintactico(true);
                    hayError = true;
                    nuevaSintaxis.add(new Sintaxis(listadoReconstruido, true,
                            "Uso incorrecto de operadores consecutivos", TipoOperacionEnum.ASIGNACION_VALORES));
                    break;
                }
                
            } else if (token == TokenEnum.PUNTO_COMA) {
                if (hayContenido) {
                    estructura.remove(TokenEnum.INDEFINIDO);
                }
                return indiceActual;
            } else {
                lexema.setErrorSintactico(true);
                referenciaError = true;
                hayError = true;
                nuevaSintaxis.add(new Sintaxis(
                        listadoReconstruido,
                        true,
                        "Token inesperado dentro de la expresión",
                        TipoOperacionEnum.ASIGNACION_VALORES
                ));
                break;
            }
            
            tokenAnterior = token;
        }
        
        if (balanceParentesis > 0) {
            nuevaSintaxis.add(new Sintaxis(
                    listadoReconstruido,
                    true,
                    "La expresión tiene paréntesis sin cerrar",
                    TipoOperacionEnum.ASIGNACION_VALORES
            ));
        }
        
        if (hayError) {
            return indiceActual;
        }
        
        return listadoLexemasSintacticos.size();
    }
    //Metodo utilizado para declarar los errores 
    //Metodo utilizado para poder declarar los errores de los listados

    private void declararErrorAsignacion(int indiceSize, ArrayList<Sintaxis> sintaxisNueva, ArrayList<Lexema> listadoAuxiliar, ArrayList<TokenEnum> estructura) {
        
        int indice = indiceSize - 1;
        
        if (indice < 0) {
            indice = 0;
        }
        
        listadoAuxiliar.get(indice).setErrorSintactico(true);
        String errorEsperado = hallarErrorAsignacion(estructura);
        sintaxisNueva.add(new Sintaxis(listadoAuxiliar, true, errorEsperado, TipoOperacionEnum.DEFINICION_VARIABLE));
        
    }

    //Metodo especializado para poder declarar el error de asignacion
    private String hallarErrorAsignacion(ArrayList<TokenEnum> estructuraActual) {
        
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < estructuraActual.size(); i++) {
            
            TokenEnum token = estructuraActual.get(i);
            
            switch (token) {
                case IDENTIFICADOR:
                    sb.append(", cerca de inicio: se esperaba un identificador");
                    return sb.toString();
                
                case IGUAL:
                    sb.append(", cerca de ");
                    sb.append(TokenEnum.IDENTIFICADOR.getNombreToken());
                    sb.append(": se esperaba el signo ");
                    sb.append(TokenEnum.IGUAL.getNombreToken());
                    return sb.toString();
                
                case INDEFINIDO:
                    sb.append(", cerca de ");
                    sb.append(TokenEnum.IGUAL.getNombreToken());
                    sb.append(": se esperaba una expresión o valor");
                    return sb.toString();
                
                case PUNTO_COMA:
                    sb.append(", cerca de ");
                    
                    sb.append("la expresión o valor");
                    
                    sb.append(": se esperaba ';' al final de la asignación");
                    return sb.toString();
                
                default:
                    return "";
            }
        }
        
        return "";
    }

    //==========================FIN DE LOS METODOS UNICOS PARA EL RECONOCIMIENTO DE DECLARACION DE VARIABLES================
    //Metodo encargado de ir listando todos los lexemas que tengan un significado sintactico
    private void separarSintaxis() {
        
        if (!this.listadoParser.isEmpty()) {
            this.listadoParser.clear();
        }
        
        ArrayList<Lexema> listadoLexemas = new ArrayList<>(1000);
        
        boolean hayContenido = false;
        
        for (int i = 0; i < getListadoSentencias().size(); i++) {
            
            Sentencia sentenciaUbicada = getListadoSentencias().get(i);
            
            for (int j = 0; j < sentenciaUbicada.obtenerListadoLexemas().size(); j++) {
                
                Lexema lexemaUbicado = sentenciaUbicada.getListaLexema(j);
                
                if (lexemaUbicado.getTokenSintactico() == TokenEnum.DEFINIR) {
                    
                    if (!listadoLexemas.isEmpty()) {
                        this.listadoParser.add(new Sintaxis(listadoLexemas, false, "", TipoOperacionEnum.NO_SINTACTICO));
                        listadoLexemas = new ArrayList<>(1000);
                        hayContenido = false;
                    }
                    
                    j = hallarDefinicion(j, sentenciaUbicada.obtenerListadoLexemas(), lexemaUbicado);
                    
                    j--;
                    
                    if (j < sentenciaUbicada.obtenerListadoLexemas().size()) {
                        
                        if (!hayContenido) {
                            hayContenido = true;
                        }
                    }
                    
                } else if (lexemaUbicado.getTokenClasificado() == TokenEnum.VACIO) {
                    
                    listadoLexemas.add(lexemaUbicado);
                    this.listadoParser.add(new Sintaxis(listadoLexemas, false, "", TipoOperacionEnum.NO_SINTACTICO));
                    listadoLexemas = new ArrayList<>(1000);
                    hayContenido = false;
                    
                } else {
                    
                    listadoLexemas.add(lexemaUbicado);
                }
                
            }
            
            if (!listadoLexemas.isEmpty() && hayContenido) {
                this.listadoParser.add(new Sintaxis(listadoLexemas, false, "", TipoOperacionEnum.NO_SINTACTICO));
                listadoLexemas = new ArrayList<>(1000);
                hayContenido = false;
            }
            
        }
        
        if (!listadoLexemas.isEmpty()) {
            this.listadoParser.add(new Sintaxis(listadoLexemas, false, "", TipoOperacionEnum.NO_SINTACTICO));
        }
        
    }

    //Metodo que llama a una serie de metodos para moverse 
    private int hallarDefinicion(int indiceRecorrido, ArrayList<Lexema> listadoLexemasSintacticos, Lexema lexemaInicial) {
        
        if (lexemaInicial.getTokenSintactico() != TokenEnum.DEFINIR) {
            return indiceRecorrido + 1;
        }
        
        boolean hayContenido = false;
        
        ArrayList<TokenEnum> estructura = new ArrayList<>();
        estructura.add(TokenEnum.DEFINIR);
        estructura.add(TokenEnum.IDENTIFICADOR);
        estructura.add(TokenEnum.COMO);
        estructura.add(TokenEnum.INDEFINIDO);
        estructura.add(TokenEnum.PUNTO_COMA);
        
        ArrayList<Lexema> listadoAuxiliar = new ArrayList<>(1000);
        
        String tipoUbicado = "";

        //True indica cuando se detecta el indice de que la cadena esta incompleta
        boolean estaIncompleta = false;
        
        int indiceRetorno = 0;
        
        for (int i = indiceRecorrido; i < listadoLexemasSintacticos.size(); i++) {
            
            indiceRetorno = i;
            
            Lexema lexemaUbicado = listadoLexemasSintacticos.get(i);
            
            if (i == listadoLexemasSintacticos.size() - 1
                    && !estructura.isEmpty()
                    && estaIncompleta) {

                //Se ejecuta solo cuando se termina la sentencia entera y se marca como error PENDIENTE 
                lexemaUbicado.setErrorSintactico(true);
                String errorEsperado = hallarErrorDefinicion(estructura, tipoUbicado);
                listadoAuxiliar.add(lexemaUbicado);
                this.listadoParser.add(new Sintaxis(listadoAuxiliar, true, errorEsperado, TipoOperacionEnum.DEFINICION_VARIABLE));
                return listadoLexemasSintacticos.size();
                
            }
            
            if (estructura.isEmpty() && hayContenido) {
                //Significa que la pila ya no tiene nada y por ende es valido
                this.listadoParser.add(new Sintaxis(listadoAuxiliar, false, "", TipoOperacionEnum.DEFINICION_VARIABLE));
                return i;
            }
            
            if (lexemaUbicado.getTokenClasificado() == TokenEnum.ESPACIO || lexemaUbicado.getTokenClasificado() == TokenEnum.TABULACION) {
                
                listadoAuxiliar.add(lexemaUbicado);
                
            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.DEFINIR && lexemaUbicado.getTokenClasificado() == TokenEnum.PALABRA_RESERVADA) {
                
                if (!estructura.contains(TokenEnum.DEFINIR)) {
                    declararError(listadoAuxiliar.size(), listadoAuxiliar, estructura, tipoUbicado);
                    return i;
                }
                
                if (!hayContenido) {
                    hayContenido = true;
                }
                
                listadoAuxiliar.add(lexemaUbicado);
                estructura.remove(TokenEnum.DEFINIR);
                
            } else if (lexemaUbicado.getTokenClasificado() == TokenEnum.IDENTIFICADOR) {
                
                if (!estructura.contains(TokenEnum.IDENTIFICADOR)) {
                    declararError(listadoAuxiliar.size(), listadoAuxiliar, estructura, tipoUbicado);
                    return i;
                }
                
                if (!hayContenido) {
                    hayContenido = true;
                }
                
                listadoAuxiliar.add(lexemaUbicado);
                estructura.remove(TokenEnum.IDENTIFICADOR);
                
            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.COMO && lexemaUbicado.getTokenClasificado() == TokenEnum.PALABRA_RESERVADA) {
                
                if (!estructura.contains(TokenEnum.COMO)) {
                    
                    declararError(listadoAuxiliar.size(), listadoAuxiliar, estructura, tipoUbicado);
                    return i;
                }
                
                if (!hayContenido) {
                    hayContenido = true;
                }
                
                listadoAuxiliar.add(lexemaUbicado);
                estructura.remove(TokenEnum.COMO);
                
            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.TIPO_ENTERO
                    || lexemaUbicado.getTokenSintactico() == TokenEnum.TIPO_CADENA
                    || lexemaUbicado.getTokenSintactico() == TokenEnum.TIPO_NUMERO) {
                
                if (!estructura.contains(TokenEnum.INDEFINIDO)) {
                    
                    declararError(listadoAuxiliar.size(), listadoAuxiliar, estructura, tipoUbicado);
                    return i;
                }
                
                if (!hayContenido) {
                    hayContenido = true;
                }
                
                tipoUbicado = lexemaUbicado.getTokenSintactico().getNombreToken();
                listadoAuxiliar.add(lexemaUbicado);
                estructura.remove(TokenEnum.INDEFINIDO);
                
            } else if (lexemaUbicado.getTokenSintactico() == TokenEnum.PUNTO_COMA && lexemaUbicado.getTokenClasificado() == TokenEnum.PUNTUACION) {
                
                if (!estructura.contains(TokenEnum.PUNTO_COMA)) {
                    
                    declararError(listadoAuxiliar.size(), listadoAuxiliar, estructura, tipoUbicado);
                    return i;
                }
                
                if (!hayContenido) {
                    hayContenido = true;
                }
                
                listadoAuxiliar.add(lexemaUbicado);
                estructura.remove(TokenEnum.PUNTO_COMA);
                
            } else if (lexemaUbicado.getTokenClasificado() == TokenEnum.VACIO) {
                
                if (!estructura.isEmpty()) {
                    //Cuando no es vacia significa que la sintaxis no es valida por ende hay error

                    int indice = listadoAuxiliar.size() - 1;
                    
                    if (indice < 0) {
                        indice = 0;
                    }
                    
                    listadoAuxiliar.add(lexemaUbicado);
                    listadoAuxiliar.get(indice).setErrorSintactico(true);
                    String errorEsperado = hallarErrorDefinicion(estructura, tipoUbicado);
                    this.listadoParser.add(new Sintaxis(listadoAuxiliar, true, errorEsperado, TipoOperacionEnum.DEFINICION_VARIABLE));
                } else {
                    
                    listadoAuxiliar.add(lexemaUbicado);
                    this.listadoParser.add(new Sintaxis(listadoAuxiliar, false, "", TipoOperacionEnum.DEFINICION_VARIABLE));
                    return i;
                }
                
            } else {

                //Se ejecuta cuanso se detecta un indice de que la cadena esta incompleta 
                estaIncompleta = true;
                
                int indice = listadoAuxiliar.size() - 1;
                
                if (indice < 0) {
                    indice = 0;
                }
                
                lexemaUbicado.setErrorSintactico(true);
                listadoAuxiliar.add(lexemaUbicado);
                
            }
            
        }
        
        if (estructura.isEmpty() && hayContenido) {
            //Significa que la pila ya no tiene nada y por ende es valido
            this.listadoParser.add(new Sintaxis(listadoAuxiliar, false, "", TipoOperacionEnum.DEFINICION_VARIABLE));
            
            return indiceRetorno + 1;
        } else {

            //Retorna cuando hay errores registrados busca el indice de error
            int indice = listadoAuxiliar.size() - 1;
            
            if (indice < 0) {
                indice = 0;
            }
            
            listadoAuxiliar.get(indice).setErrorSintactico(true);
            String errorEsperado = hallarErrorDefinicion(estructura, tipoUbicado);
            
            this.listadoParser.add(new Sintaxis(listadoAuxiliar, true, errorEsperado, TipoOperacionEnum.DEFINICION_VARIABLE));
            return indiceRetorno + 1;
            
        }
        
    }

    //=============METODO SOLAMENTE UTIL PARA HALLAR EL ERROR SINTACTICO DE DEFINICION DE VARIABLES================
    //Metodo encargado de ejecutarse cuando se ponen dos instrucciones de declaracion en el flujo normal de una declaracion
    private String hallarErrorDefinicion(ArrayList<TokenEnum> estructuraActual, String tipoUbicado) {
        
        StringBuilder stringBuilder = new StringBuilder();
        
        ArrayList<TokenEnum> estructuraSintactica = new ArrayList<>();
        estructuraSintactica.add(TokenEnum.DEFINIR);
        estructuraSintactica.add(TokenEnum.IDENTIFICADOR);
        estructuraSintactica.add(TokenEnum.COMO);
        estructuraSintactica.add(TokenEnum.INDEFINIDO);
        estructuraSintactica.add(TokenEnum.PUNTO_COMA);
        
        for (int i = 0; i < estructuraActual.size(); i++) {
            
            TokenEnum token = estructuraActual.get(i);
            
            switch (token) {
                case DEFINIR:
                    
                    stringBuilder.append(", cerca de ");
                    stringBuilder.append(TokenEnum.DEFINIR.getNombreToken());
                    
                    stringBuilder.append(": se esperaba la palabra reservada ");
                    stringBuilder.append(TokenEnum.DEFINIR.getNombreToken());
                    
                    return stringBuilder.toString();
                
                case IDENTIFICADOR:
                    
                    stringBuilder.append(", cerca de ");
                    int indice = estructuraSintactica.indexOf(TokenEnum.IDENTIFICADOR) - 1;
                    
                    if (indice < 0) {
                        indice = 0;
                    }
                    stringBuilder.append(estructuraSintactica.get(indice).getTipo());
                    
                    stringBuilder.append(": se esperaba ");
                    stringBuilder.append(TokenEnum.IDENTIFICADOR.getNombreToken());
                    
                    return stringBuilder.toString();
                
                case COMO:
                    
                    stringBuilder.append(", cerca de ");
                    int indiceComo = estructuraSintactica.indexOf(TokenEnum.COMO) - 1;
                    
                    if (indiceComo < 0) {
                        indiceComo = 0;
                    }
                    stringBuilder.append(estructuraSintactica.get(indiceComo).getTipo());
                    
                    stringBuilder.append(": se esperaba la palabra reservada ");
                    stringBuilder.append(TokenEnum.COMO.getNombreToken());
                    
                    return stringBuilder.toString();
                
                case INDEFINIDO:
                    
                    stringBuilder.append(", cerca de ");
                    int indiceDefinicion = estructuraSintactica.indexOf(TokenEnum.INDEFINIDO) - 1;
                    
                    if (indiceDefinicion < 0) {
                        indiceDefinicion = 0;
                    }
                    
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

    //Metodo utilizado para poder declarar los errores de los listados
    private void declararError(int indiceSize, ArrayList<Lexema> listadoAuxiliar, ArrayList<TokenEnum> estructura, String tipoUbicado) {
        
        int indice = indiceSize - 1;
        
        if (indice < 0) {
            indice = 0;
        }
        
        listadoAuxiliar.get(indice).setErrorSintactico(true);
        String errorEsperado = hallarErrorDefinicion(estructura, tipoUbicado);
        this.listadoParser.add(new Sintaxis(listadoAuxiliar, true, errorEsperado, TipoOperacionEnum.DEFINICION_VARIABLE));
        
    }

    //=============FIN DEL METODO SOLAMENTE UTIL PARA HALLAR EL ERROR SINTACTICO DE DEFINICION DE VARIABLES================
    //Metodo utilizado para ilustrar el log de edicion
    //METODO UNICO QUE SIRVE PARA COLOREAR LOS LOG A CARGO DEL ANALIZADOR LEXICO
    private void pintarLogSalida(JTextPane paneAnalisis) throws BadLocationException, ErrorSintacticoException {
        
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
            
            for (Lexema lexemaDado : sintaxisActiva.getListadoLexemas()) {
                
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
                        
                        if (sintaxisActiva.tieneError()) {
                            
                            insertarTexto(lexemaDado.getLexemaGenerado(), Color.RED, paneAnalisis);
                        } else {
                            
                            insertarTexto(lexemaDado.getLexemaGenerado(), colorTexto, paneAnalisis);
                        }
                    
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
        
        mostrarErrores();
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

    //Metodo encargado de imprimir los errores sintacticos en el log de errores
    private void mostrarErrores() throws BadLocationException, ErrorSintacticoException {
        
        limpiarArea(this.logErroresSintacticos);
        
        for (int i = 0; i < this.listadoParser.size(); i++) {
            
            Sintaxis sintaxisActiva = this.listadoParser.get(i);
            
            if (!sintaxisActiva.tieneError()) {
                continue;
            }
            
            insertarTexto("Error en la linea " + sintaxisActiva.getLineaInicio()
                    + " columna " + sintaxisActiva.getColumnaError() + " " + sintaxisActiva.getMensajeError(), Color.RED, this.logErroresSintacticos);
            insertarTexto("\n", Color.BLACK, this.logErroresSintacticos);
        }
    }

    //Metodo que ayuda a saber si hay errores
    private boolean hayErroresLexicos() {

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
