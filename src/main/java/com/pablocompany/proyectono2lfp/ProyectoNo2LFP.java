/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.pablocompany.proyectono2lfp;

import com.pablocompany.proyectono2lfp.frontend.MenuPrincipal;

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
    }

    //Metodo utilizado para generar el analizador lexico
    public static void generarLexer() {

        String ruta = "/home/pablo/Escritorio/Lenguajes_Formales/ProyectoNo2LFP/src/main/java/com/pablocompany/proyectono2lfp/jflexpackage/lexer.flex";

        jflex.Main.main(new String[]{ruta});

    }
}
