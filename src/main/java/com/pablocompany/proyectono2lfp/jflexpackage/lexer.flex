/*CABECERA*/

package com.pablocompany.proyectono2lfp.jflexpackage;
import java.util.ArrayList;
import com.pablocompany.proyectono2lfp.backend.Sentencia;
import com.pablocompany.proyectono2lfp.analizadorlexicorecursos.TokenEnum;

%%

/*CONFIGURACIONES*/
%public
%class AnalizadorLexico
%unicode
%line
%column
%state COMENTARIO

/*CODIGO JAVA*/
%{
    //Atributo utilizado para manejar el listado de sentencias
    private ArrayList<Sentencia> listaSentencias = new ArrayList<>(5000); 
    
    //Flag que permite saber si hay salto de linea
    private boolean haySalto;
    
    //Flag que permite automatizar para que el analizador lexico sepa que ya inicio o no el analisis
    private boolean estaIniciando = true;

    //-----------Apartado de metodos que sirven para saber si la flag esta activada---------------

    public void setHaySalto(boolean flag){
        this.haySalto = flag;
    }
    
    //true simboliza si hay salto
    public boolean getHaySalto(){
        return this.haySalto; 
    }

    //-----------Fin del Apartado de metodos que sirven para saber si la flag esta activada---------------



    //-----------Apartado de metodos que sirven para generar los listados de lexemas y sentencias---------------

    //Metodo que permite calcular el indice en el que va la lista de sentencias
    private int getIndiceListado(){
        
        if(this.listaSentencias.isEmpty()){
            return 0;
        }

        int indice = this.listaSentencias.size() - 1;

        if(indice < 0 ){
            return 0;
        }else{
            return indice;
        }
    }

    
    //Metodo que permite instanciar una nueva linea en la sentencia
    private void setNuevaSentencia(int fila){

        if(this.estaIniciando){
            
            this.listaSentencias.add(new Sentencia(new ArrayList<>(5000), fila ));
            this.estaIniciando = false;
            setHaySalto(false);
            return;
        }

        if(!getHaySalto()){
            return;
        }
        setHaySalto(false);
        this.listaSentencias.add(new Sentencia(new ArrayList<>(5000), fila));
    }
    
    //Metodo utilizado para establecer un lexema sin significado sintactico
    private void setNuevoLexemaLexico(TokenEnum tokenLexico, String cadena, int filaCoordenada, int columnaCoordenada ){
        
        int indice = getIndiceListado();
        this.listaSentencias.get(indice).agregarLexemaLexico(cadena, filaCoordenada, columnaCoordenada, tokenLexico );
    }

    //Metodo utilizado para establecer un lexema que tiene un significado sintactico
    private void setNuevoLexemaSintactico(TokenEnum tokenLexico, TokenEnum tokenSintactico, String cadena, int filaCoordenada, int columnaCoordenada ){
    
        int indice = getIndiceListado();
        this.listaSentencias.get(indice).agregarLexemaSintactico(cadena, filaCoordenada, columnaCoordenada, tokenLexico, tokenSintactico);
    }

    //-----------Fin del Apartado de metodos que sirven para generar los listados de lexemas y sentencias---------------
     
    //Metodo que sirve para obtener la lista de sentencias
    public ArrayList<Sentencia> getListaSentencias(){
        return this.listaSentencias;
    }

%}

/*EXPRESIONES REGULARES DEL AUTOMATA*/

Espacio = " "
Tab = \t

Salto = (\r\n | \n | \r)

LineaVacia = [ \t]* {Salto}+



/* Apartado de tokens normales */

Identificador = [:jletter:] [:jletterdigit:]*
Numero = 0 | [1-9][0-9]*
Decimal = {Numero}"."{Numero}
Puntuacion = [.,;:]     
OperadorAritmetico = [+\-*/%]
Agrupacion = [\(\)\[\]\{\}]      

/*Apartado de palabras reservadas */

PalabraReservada = "SI" | "si" |"ENTONCES" | "entonces" |"ENTERO" | "entero" |"NUMERO" | "numero" |"CADENA" | "cadena" |"ESCRIBIR" | "escribir" |"DEFINIR" |"COMO"      



/*Apartado de palabras con un significado sintactico */

Igual = [=]



/* Apartado de comentarios */


ComentarioBloque = "/*" [^*] ~"*/" | "/*" "*"+ "/"

InputCharacter = [^\r\n]
ComentarioLinea = "//" {InputCharacter}* {Salto}?

DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent = ( [^*] | \*+ [^/*] )*

/* Apartado de errores */

ErrorComentarioBloque = "/*"([^*]|\*+[^*/])*



%%


/*ACCIONES*/

{Espacio}+          { setNuevaSentencia(yyline);
                      setNuevoLexemaLexico(TokenEnum.ESPACIO, yytext(), yyline, yycolumn );
                      System.out.println("Token espacio <" + yytext() + " columna " + yycolumn+">"); }

{LineaVacia}        {   setHaySalto(true);
                        setNuevaSentencia(yyline);
                        setNuevoLexemaLexico(TokenEnum.VACIO, yytext(), yyline, yycolumn );

                        String lexema = yytext();       
                        String[] lineas = lexema.split("\r\n|\r|\n", -1); 
                        int cantidadSaltos = lineas.length - 1;          
                        String espacios = lineas[0];                       
                        System.out.println("Cantidad de saltos: " + cantidadSaltos);
                        System.out.println("Espacios iniciales: '" + (espacios.length()));}

{Tab}+              {   setNuevaSentencia(yyline);
                        setNuevoLexemaLexico(TokenEnum.TABULACION, yytext(), yyline, yycolumn );
                        System.out.println("Token tab <" + yytext() + " columna " + yycolumn+">"); }



{ErrorComentarioBloque}      {System.out.println("Token ERROR COMENTAIO DE BLOQUE <" + yytext() + " columna " + yycolumn+">"); }


{Igual}                 { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.OPERADOR, TokenEnum.IGUAL, yytext(), yyline, yycolumn );  
                          System.out.println("Token Igual <" + yytext() + " columna " + yycolumn+">"); }

{PalabraReservada}      { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.PALABRA_RESERVADA, yytext(), yyline, yycolumn );  
                          System.out.println("Token palabra reservada <" + yytext() + " columna " + yycolumn+">");  }





{Decimal}               { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.DECIMAL, yytext(), yyline, yycolumn );  
                          System.out.println("Token Decimal <" + yytext() + " columna " + yycolumn+">"); }


{Numero}                { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.NUMERO, yytext(), yyline, yycolumn );  
                          System.out.println("Token Numero <" + yytext() + " columna " + yycolumn+">"); }

{Identificador}         { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.IDENTIFICADOR, yytext(), yyline, yycolumn ); 
                          System.out.println("Token Identificador <" + yytext() + " columna " + yycolumn+">"); }

{Puntuacion}            { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.PUNTUACION, yytext(), yyline, yycolumn ); 
                          System.out.println("Token Puntuacion <" + yytext() + " columna " + yycolumn+">"); }


{OperadorAritmetico}    { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.OPERADOR, yytext(), yyline, yycolumn ); 
                          System.out.println("Token operador aritmetico<" + yytext() + " columna " + yycolumn+">"); }

{Agrupacion}            { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.AGRUPACION, yytext(), yyline, yycolumn ); 
                          System.out.println("Token agrupacion <" + yytext() + " columna " + yycolumn+">"); }



{ComentarioLinea}       { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.COMENTARIO_LINEA, yytext(), yyline, yycolumn ); 
                          System.out.println("Token COMENTARIO_LINEA <" + yytext() + " columna " + yycolumn+">"); }

{DocumentationComment}  { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.COMENTARIO_BLOQUE, yytext(), yyline, yycolumn ); 
                          System.out.println("Token COMENTARIO DE BLOQUE <" + yytext() + " columna " + yycolumn+">"); }

{ComentarioBloque}      { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.COMENTARIO_BLOQUE, yytext(), yyline, yycolumn ); 
                          System.out.println("Token COMENTAIO DE BLOQUE <" + yytext() + " columna " + yycolumn+">"); }


(.)          {  setNuevaSentencia(yyline);
                setNuevoLexemaLexico(TokenEnum.ERROR, yytext(), yyline, yycolumn ); 
                System.out.println("Caracter no registrado <" + yytext() + " columna " + yycolumn+">"); }