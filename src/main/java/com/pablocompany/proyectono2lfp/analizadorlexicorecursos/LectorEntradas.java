/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pablocompany.proyectono2lfp.analizadorlexicorecursos;

import com.pablocompany.proyectono2lfp.backend.GenerarReportes;
import com.pablocompany.proyectono2lfp.backend.GestorLexer;
import com.pablocompany.proyectono2lfp.backend.GestorSintactico;
import com.pablocompany.proyectono2lfp.excepciones.AnalizadorLexicoException;
import com.pablocompany.proyectono2lfp.excepciones.ErrorPuntualException;
import com.pablocompany.proyectono2lfp.jflexpackage.AnalizadorLexico;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 *
 * @author pablo
 */
public class LectorEntradas {

    //Clase que intercomunica al lexer actual para poderlo operar internamente en UI
    private GestorLexer lexerActual;
    
    //Clase delegada para poder acceder a la referencia del analizador sintactico
    private GestorSintactico sintaxisActual;

    //Atributo que permite conservar la referencia de los reportes
    private GenerarReportes generacionReportes;

    //------------------Subregion de gramatica extraidas del config.json----------------------------
    // private AutomataDeterminista constantesConfig;
    public LectorEntradas() {
        this.generacionReportes = new GenerarReportes();
    }

    //------------------Fin de la Subregion de gramatica extraidas del config.json----------------------------
    //Metodo mas importante para poder analizar el texto y procesarlo
    public void analizarEntradas(JTextPane paneLogEntrada, JTextPane logErrores, JTextPane logSintactico) throws BadLocationException, AnalizadorLexicoException {

        //Valida si el texto de entrada viene vacio
        if (paneLogEntrada.getText().isBlank()) {
            return;
        }

        AnalizadorLexico analizador = new AnalizadorLexico(new StringReader(paneLogEntrada.getText()));
        try {
            analizador.yylex();
            this.lexerActual = new GestorLexer(analizador, paneLogEntrada, logErrores);

            this.lexerActual.pintarLogEdicion();
            
            this.sintaxisActual = new GestorSintactico(this.lexerActual,this.lexerActual.getLogErrores(), logSintactico);

        } catch (IOException ex) {
            throw new AnalizadorLexicoException("Se ha producido un error al interpretar el texto de entrada");
        }
    }

    //Metodo que permite pasar por parametro un arraylist e imprimirlo en un JtextPane
    public void imprimirLog(ArrayList<String> listaExtraida, JTextPane textPane) throws BadLocationException {
        StyledDocument doc = textPane.getStyledDocument();
        for (int i = 0; i < listaExtraida.size(); i++) {
            doc.insertString(doc.getLength(), listaExtraida.get(i) + "\n", null);
        }
    }

    //Metodo que permite retornar el analizador lexico instanciado en el momento
    public AnalizadorLexico getLexerActual() {
        return this.lexerActual.getLexer();
    }

    //Metodo que sirve para retornar la clase maestra delegada para manipular el lexer
    public GestorLexer getGestorLexer() {
        return this.lexerActual;
    }
    
    //Metodo que permite obtener la clase que gestiona el apartado sintactico
    public GestorSintactico getGestorSintactico(){
        return this.sintaxisActual;
    }

    //METODO UTILIZADO PARA EXPORTAR EL TEXTO ESCRITO EN EL LOG DE EDICION
    public String exportarArchivo() throws ErrorPuntualException {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecciona el directorio de Almacenamiento");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        int resultado = chooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            // Actualiza tu clase con el path seleccionado

            File seleccionado = chooser.getSelectedFile();
            if (seleccionado.exists()) {

                return seleccionado.getAbsolutePath() + File.separator;

            } else {
                throw new ErrorPuntualException("El directorio seleccionado no existe");

            }

        }

        return null;
    }

    //Metodo utilizado para obtener la instancia y generar el reporte 
    public GenerarReportes getGenerarReportes() {
        return this.generacionReportes;
    }
}
