/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.pablocompany.proyectono2lfp;

import com.pablocompany.proyectono2lfp.frontend.MenuPrincipal;
import com.pablocompany.proyectono2lfp.jflexpackage.AnalizadorLexico;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

/**
 *
 * @author pablo
 */
public class ProyectoNo2LFP {

    //Se encarga de inicializar la aplicacion
    public static void main(String[] args) {

        //generarLexer();
        //Metodo donde inicia la interfaz principal
        
        
        MenuPrincipal menuPrincipal = new MenuPrincipal();
        
        menuPrincipal.setVisible(true);
        
        /*AnalizadorLexico lexer;
        Scanner entradar = new Scanner(System.in);

        while (true) {

            System.out.println("Palabra a analizar: ");
            String texto = entradar.nextLine();
            lexer = new AnalizadorLexico(new StringReader(texto));
            try {
                lexer.yylex();
                System.out.println("");

            } catch (IOException ex) {
                break;
            }

        }*/

    }

    public static void generarLexer() {

        String ruta = "/home/pablo/Escritorio/Lenguajes_Formales/ProyectoNo2LFP/src/main/java/com/pablocompany/proyectono2lfp/jflexpackage/lexer.flex";

        jflex.Main.main(new String[]{ruta});

    }
}
