/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.backend;

import com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum;
import com.pablocompany.proyectono2lfp.backendswing.CrearTableros;
import com.pablocompany.proyectono2lfp.backendswing.ModificarTabla;
import com.pablocompany.proyectono2lfp.excepciones.ErrorPuntualException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */
public class GenerarReportes {

    //------------------APARTADO DE MANEJO DE DIRECTORIOS--------------
    private File guardarArchivo;

    private File directorioArchivo;

    private String pathDefinitivo = "Reportes" + File.separator;

    private final String PATH_PREDETERMINADO = "Reportes" + File.separator;
    //------------------FIN DEL APARTADO DE MANEJO DE DIRECTORIOS--------------

    //------------------APARTADO DE MANEJO DE ATRIBUTOS DEL REPORTE--------------
    //Atributo booleano para saber si hay errores
    //true si hay errores
    private boolean hayErrores;

    //Lista de errores 
    private ArrayList<String> listaErrores = new ArrayList<>(5000);

    //Listado de lexemas
    private ArrayList<String> listadoLexemas = new ArrayList<>(2000);

    //Listado de tokens
    private ArrayList<String> listadoTokens = new ArrayList<>(2000);

    //Listado que permite guardar los lexemas para poder generar transiciones
    private ArrayList<Lexema> listadoLexemasSentencia = new ArrayList<>(2000);

    //------------------FIN DEL APARTADO DE MANEJO DE ATRIBUTOS DEL REPORTE--------------
    public GenerarReportes() {

        this.hayErrores = false;
        setPathPredeterminado();
    }

    //Metodo que retorna si el directorio predeterminado existe 
    //False no existe
    public boolean directorioExiste() {
        return this.directorioArchivo.exists();
    }

    //Metodo util para reestablecer el directorio predeterminado
    public final void setPathPredeterminado() {

        File folder = new File("Reportes");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        this.guardarArchivo = new File(PATH_PREDETERMINADO);
        this.directorioArchivo = folder;
        this.pathDefinitivo = PATH_PREDETERMINADO;

    }

    //Metodo util para poder mostrar las tokenizaciones de lexemas tokens normales
    public void generarReporteConteoLexemas(ArrayList<Sentencia> sentenciasListado, ModificarTabla modificarTabla, CrearTableros crearTablero) throws ErrorPuntualException {

        this.hayErrores = false;
        //Cuenta los errores para ver si hay 
        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);
                if (lexemaEvaluado.getTokenClasificado() == TokenEnum.ERROR) {
                    this.hayErrores = true;
                    break;
                }

            }

            if (this.hayErrores) {
                break;
            }

        }

        if (this.hayErrores) {
            throw new ErrorPuntualException("No se puede generar el reporte porque hay errores registrados");
        }

        crearTablero.vaciarTablero();

        if (!this.listadoLexemas.isEmpty()) {
            this.listadoLexemas.clear();
        }

        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);

                if (!lexemaEvaluado.getLexemaGenerado().isBlank()) {

                    /*this.listadoLexemas.add(lexemaEvaluado.getlexema().getTipo());
                    this.listadoLexemas.add(lexemaEvaluado.getLexema());

                    String coordenada = "\"F(";

                    coordenada += String.valueOf(lexemaEvaluado.getFilaCoordenada()) + ") , C( ";

                    coordenada += String.valueOf(lexemaEvaluado.getValorNodo(0).getColumna()) + "-" + String.valueOf(lexemaEvaluado.getValorNodo(lexemaEvaluado.getLongitudNodo() - 1).getColumna()) + " )\"";

                    this.listadoLexemas.add(coordenada);*/
                }

            }

        }

        if (this.listadoLexemas.isEmpty()) {
            throw new ErrorPuntualException("No se ha encontrado ningun lexema ");
        }

        cargarSentenciasLexema(sentenciasListado);

        String[] titulos = {"Nombre Token", "Lexema", "Posicion"};
        crearTablero.tableroConTitulo(titulos, this.listadoLexemas.size() / 3, 3, true);
        modificarTabla.reendereizarTablero();

        int iterador = 0;

        for (int i = 0; i < this.listadoLexemas.size(); i += 3) {

            String simbolo = this.listadoLexemas.get(i);
            String lexema = this.listadoLexemas.get(i + 1);
            String posicion = this.listadoLexemas.get(i + 2);

            modificarTabla.colocarTextos(iterador, 0, simbolo);
            modificarTabla.colocarTextos(iterador, 1, lexema);
            modificarTabla.colocarTextos(iterador, 2, posicion);
            iterador++;
        }

    }

    //Submetodo que se carga al generar el reporte de Lexemas para poder generar las transiciones del afd
    public void cargarSentenciasLexema(ArrayList<Sentencia> sentenciasListado) {

        if (!this.listadoLexemasSentencia.isEmpty()) {
            this.listadoLexemasSentencia.clear();
        }

        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);

                if (!lexemaEvaluado.getLexemaGenerado().isBlank()) {

                    this.listadoLexemasSentencia.add(lexemaEvaluado);

                }

            }

        }

    }

    //Metodo que permite saber si la lista de lexemas de las sentencias esta vacia
    public boolean esVaciaListaLexemas() {
        return this.listadoLexemasSentencia.isEmpty();
    }

    //Metodo que retorna la referencia viva del lexema
    public Lexema getLexemaSeleccionado(int indice) {
        return this.listadoLexemasSentencia.get(indice);
    }

    //Metodo util para reiniciar todas las listas al entrar
    public void reiniciarListas() {
        if (!this.listadoLexemas.isEmpty()) {
            this.listadoLexemas.clear();
        }

        if (!this.listaErrores.isEmpty()) {
            this.listaErrores.clear();
        }
        if (!this.listadoTokens.isEmpty()) {
            this.listadoTokens.clear();
        }

        if (!this.listadoLexemasSentencia.isEmpty()) {
            this.listadoLexemasSentencia.clear();
        }
    }

    //Metodo que permite saber si el reporte esta generado o no
    public boolean reporteLexemasGenerado() {
        return this.listadoLexemas.isEmpty();
    }

    //Metodo util para poder mostrar los conteos de lexemas las veces que aparecen
    public void generarReporteTokenizacionLexemas(ArrayList<Sentencia> sentenciasListado, ModificarTabla modificarTabla, CrearTableros crearTablero) throws ErrorPuntualException {

        this.hayErrores = false;
        //Cuenta los errores para ver si hay 
        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);
                if (lexemaEvaluado.getTokenClasificado() == TokenEnum.ERROR) {
                    this.hayErrores = true;
                    break;
                }

            }

            if (this.hayErrores) {
                break;
            }

        }

        if (this.hayErrores) {
            throw new ErrorPuntualException("No se puede generar el reporte porque hay errores registrados");
        }

        crearTablero.vaciarTablero();

        if (!this.listadoTokens.isEmpty()) {
            this.listadoTokens.clear();
        }

        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);

                /*if (!lexemaEvaluado.getLexema().isBlank()) {

                    if (!tokenYaExistente(lexemaEvaluado.getLexema(), lexemaEvaluado.getEstadoAnalisis().getTipo())) {

                        this.listadoTokens.add(lexemaEvaluado.getLexema());
                        this.listadoTokens.add(lexemaEvaluado.getEstadoAnalisis().getTipo());

                        int cantidadVeces = contarLexemas(lexemaEvaluado, sentenciasListado);

                        this.listadoTokens.add(String.valueOf(cantidadVeces));
                    }

                }*/
            }

        }

        if (this.listadoTokens.isEmpty()) {
            throw new ErrorPuntualException("No se ha encontrado ningun lexema ");
        }

        String[] titulos = {"Lexema", "Tipo de Token", "Cantidad"};
        crearTablero.tableroConTitulo(titulos, this.listadoTokens.size() / 3, 3, true);
        modificarTabla.reendereizarTablero();

        int iterador = 0;

        for (int i = 0; i < this.listadoTokens.size(); i += 3) {

            String lexema = this.listadoTokens.get(i);
            String tipo = this.listadoTokens.get(i + 1);
            String cantidad = this.listadoTokens.get(i + 2);

            modificarTabla.colocarTextos(iterador, 0, lexema);
            modificarTabla.colocarTextos(iterador, 1, tipo);
            modificarTabla.colocarTextos(iterador, 2, cantidad);
            iterador++;
        }

    }

    //Metodo que valida que el pese al lexema que se encuentre sea de diferente tipo de token 
    private boolean tokenYaExistente(String palabra, String token) {

        for (int i = 0; i < this.listadoTokens.size(); i += 3) {

            String lexema = this.listadoTokens.get(i);
            String tipo = this.listadoTokens.get(i + 1);

            if (palabra.equals(lexema) && token.equals(tipo)) {
                return true;
            }

        }

        return false;
    }

    //Metodo que ayuda a contar la cantidad de veces que aparecen los lexemas en el texto
    private int contarLexemas(Lexema lexemaUbicado, ArrayList<Sentencia> sentenciasListado) {

        int contadorVeces = 0;
        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaComparado = sentencia.getListaLexema(i);

                /* if (!lexemaComparado.getLexema().isBlank()) {

                    if (lexemaUbicado.getLexema().equals(lexemaComparado.getLexema()) && lexemaUbicado.getEstadoAnalisis() == lexemaComparado.getEstadoAnalisis()) {
                        contadorVeces++;
                    }

                }*/
            }

        }

        return contadorVeces;
    }

    //Metodo util para poder mostrar los errores en pantalla en la tabla 
    public void generarReporteErrores(ArrayList<Sentencia> sentenciasListado, ModificarTabla modificarTabla, CrearTableros crearTablero) throws ErrorPuntualException {

        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);
                if (lexemaEvaluado.getTokenClasificado() == TokenEnum.ERROR) {
                    this.hayErrores = true;
                    break;
                }

            }

            if (this.hayErrores) {
                break;
            }

        }

        if (!this.hayErrores) {
            throw new ErrorPuntualException("No hay ningun error registrado en el analisis");
        }

        if (!this.listaErrores.isEmpty()) {
            this.listaErrores.clear();
        }

        crearTablero.vaciarTablero();

        for (Sentencia sentencia : sentenciasListado) {

            for (int i = 0; i < sentencia.limiteLexemas(); i++) {

                Lexema lexemaEvaluado = sentencia.getListaLexema(i);

                if (!lexemaEvaluado.getMensajeError().isBlank() && lexemaEvaluado.getTokenClasificado() == TokenEnum.ERROR) {

                    this.listaErrores.add(lexemaEvaluado.getLexemaGenerado());

                    String coordenada = "\"[Fila: ";
                    coordenada += String.valueOf(lexemaEvaluado.getLineaCoordenada()) + " , ";

                    coordenada += "Columna: " + String.valueOf(lexemaEvaluado.getColumna()) + "]\"";

                    this.listaErrores.add(coordenada);

                    this.listaErrores.add(lexemaEvaluado.getCadenaEsperada());

                }

            }

        }

        if (this.listaErrores.isEmpty()) {
            throw new ErrorPuntualException("No se han encontrado errores");
        }

        String[] titulos = {"Cadena de Error", "Posicion", "Descripcion"};
        crearTablero.tableroConTitulo(titulos, this.listaErrores.size() / 3, 3, true);
        modificarTabla.reendereizarTablero();

        int iterador = 0;

        for (int i = 0; i < this.listaErrores.size(); i += 3) {

            String simbolo = this.listaErrores.get(i);
            String posicion = this.listaErrores.get(i + 1);
            String descripcion = this.listaErrores.get(i + 2);

            modificarTabla.colocarTextos(iterador, 0, simbolo);
            modificarTabla.colocarTextos(iterador, 1, posicion);
            modificarTabla.colocarTextos(iterador, 2, descripcion);
            iterador++;
        }

    }

    //Metodo que permite comunicar a la UI con la interaccion para generar reporte de errores
    public void generarReporteErrores() throws ErrorPuntualException {
        reportarErroresCSV(this.listaErrores, "ReporteErrores", "Cadena_Error,Posicion");
    }

    //Metodo que permite exportar .csv de los errores
    public void reportarErroresCSV(ArrayList<String> lista, String nombreArchivo, String headersArchivo) throws ErrorPuntualException {

        if (this.listaErrores.isEmpty()) {
            throw new ErrorPuntualException("No hay reporte de errores cargado aun\nGenere primero el reporte para poder exportarlo");
        }

        if (!this.hayErrores) {
            throw new ErrorPuntualException("No hay ningun error registrado en el analisis");
        }

        if (!directorioExiste()) {
            setPathPredeterminado();
        }

        //Se genera la hora de exportacion para evitar duplicados
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fechaHora = ahora.format(formatter);

        try (FileWriter writer = new FileWriter(this.pathDefinitivo + nombreArchivo + "_" + fechaHora + ".csv")) {
            writer.append(headersArchivo + "\n");

            for (int i = 0; i < lista.size(); i += 3) {
                String campo1 = lista.get(i);
                String campo2 = lista.get(i + 1);
                String campo3 = lista.get(i + 2);

                writer.append(campo1).append(",")
                        .append(campo2).append(",")
                        .append(campo3).append(",")
                        .append("\n");

            }

        } catch (IOException e) {
            throw new ErrorPuntualException("No se ha podido exportar el reporte" + e.getMessage());
        }
    }

}
