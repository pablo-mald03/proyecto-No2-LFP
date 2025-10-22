/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.analizadorlexicorecursos;

import com.pablocompany.proyectono2lfp.excepciones.AnalizadorLexicoException;
import com.pablocompany.proyectono2lfp.excepciones.ConfigException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

/**
 *
 * @author pablo
 */
public class ManejadorArchivos {
    //Atributo que guarda el path para la lectura de archivos;

    private String pathEntrada;

    //Metodo que permite elegir el archivo txt y cargarlo directamente a un buffer
    public boolean elegirArchivoEntrada() throws AnalizadorLexicoException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecciona el archivo para Procesar");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int resultado = chooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File seleccionado = chooser.getSelectedFile();
            if (seleccionado.exists()) {

                this.pathEntrada = seleccionado.getAbsolutePath();
                return true;

            } else {
                throw new AnalizadorLexicoException("No se ha encontrado el archivo seleccionado");
            }
        }

        return false;
    }

    //Metodo que sirve para poder guardar el archivo en el destinatario dado
    public void guardarArchivo(String directorio, JTextPane paneEdicion) throws AnalizadorLexicoException {

        if (directorio.isBlank()) {
            throw new AnalizadorLexicoException("No hay ningun archivo subido aun");
        }

        File archivo = new File(directorio);

        if (!archivo.exists()) {
            throw new AnalizadorLexicoException("Aun no se ha definido un path para almacenar el archivo");
        }
        
        if(paneEdicion.getText().isBlank()){
              throw new AnalizadorLexicoException("El texto del log de edicion esta Vacio");
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, false))) {
            String contenido = paneEdicion.getText();

            bw.write(contenido);

        } catch (IOException e) {
            throw new AnalizadorLexicoException("No hay un path definido para reescribir el archivo");
        }
    }

    //Metodo que transforma el path de entrada a un arreglo
    public void convertirEntrada(JTextPane paneEdicion) throws AnalizadorLexicoException, ConfigException {

        if (this.pathEntrada == null || this.pathEntrada.isBlank()) {
            throw new AnalizadorLexicoException("No se ha definido aun un archivo para cargar");
        }

        File archivo = new File(this.pathEntrada);

        if (!archivo.exists() || !archivo.isFile()) {
            throw new AnalizadorLexicoException("El destinatario seleccionado no existe o no es un archivo");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {

            StyledDocument doc = paneEdicion.getStyledDocument();
            SimpleAttributeSet estilo = new SimpleAttributeSet();
            doc.remove(0, doc.getLength());

            String linea;
            while ((linea = br.readLine()) != null) {

                doc.insertString(doc.getLength(), linea, estilo);
                doc.insertString(doc.getLength(), System.lineSeparator(), estilo);
            }

        } catch (IOException ex) {
            throw new AnalizadorLexicoException("No se ha podido procesar el archivo seleccionado");
        } catch (BadLocationException ex) {
            throw new ConfigException("Ha ocurrido un error al repintar el texto de entrada");
        }

    }

    //Metodo que retorna el path seleccionado
    public String getPath() {
        return this.pathEntrada;
    }

    //Metodo que sirve para reiniciar el path de entrada
    public void reiniciarPath() {
        this.pathEntrada = "";
    }

    //Metodo que sirve para poder exportar lo que se escribe en el log
    public void exportarArchivoCreado(String directorio, ArrayList<String> lineas, String nombreArchivo) throws AnalizadorLexicoException {

        if (directorio.isBlank()) {
            throw new AnalizadorLexicoException("No hay ningun archivo subido aun");
        }

        File comprobacion = new File(directorio);
        if (!comprobacion.exists()) {
            throw new AnalizadorLexicoException("Aun no se ha definido un path para almacenar el archivo");
        }

        //Se genera la hora de exportacion para evitar duplicados
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fechaHora = ahora.format(formatter);

        File archivo = new File(directorio + File.separator + nombreArchivo + "_" + fechaHora + ".txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, false))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new AnalizadorLexicoException("No hay un path definido para reescribir el archivo");
        }
    }

}
