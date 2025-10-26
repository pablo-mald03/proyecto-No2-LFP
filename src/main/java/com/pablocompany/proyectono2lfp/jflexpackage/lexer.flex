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

    //Metodo utilizado para declarar un lexema con error
    private void setNuevoLexemaErroneo(TokenEnum tokenLexico, String cadena, int filaCoordenada, int columnaCoordenada, String mensajeError, String cadenaEsperada ){
        int indice = getIndiceListado();
        this.listaSentencias.get(indice).agregarLexemaErroneo(cadena, filaCoordenada, columnaCoordenada, tokenLexico, mensajeError, cadenaEsperada);
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

CadenaTexto = \"([^\"\n\r])*\" 

/*Apartado de palabras reservadas */

PalabraReservada = "SI" | "si" |"ENTONCES" | "entonces" |"ENTERO" | "entero" |"NUMERO" | "numero" |"CADENA" | "cadena" |"ESCRIBIR" | "escribir" |"DEFINIR" |"COMO"      



/* Apartado de palabras con un significado sintáctico */

Igual              = "="
Como               = "COMO"
Definir            = "DEFINIR"
PuntoComa          = ";"

Entero             = "entero"
Cadena             = "cadena"
NumeroEspecial     = "numero"

Escribir           = "ESCRIBIR"
ParentesisCierre   = ")"
ParentesisApertura = "("


Suma = "+"
Resta = "-"
Multiplicacion = "*"
Division = "/"

/* Apartado de comentarios */


ComentarioBloque = "/*" [^*] ~"*/" | "/*" "*"+ "/"

InputCharacter = [^\r\n]
ComentarioLinea = "//" {InputCharacter}* {Salto}?

DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent = ( [^*] | \*+ [^/*] )*

/* Apartado de errores */

ErrorComentarioBloque = "/*"([^*]|\*+[^*/])*
CadenaError  =  \"([^\"\n\r])*(\n|\r|\r\n)
ErrorDecimal = {Numero}".."

ErrorInicioDecimal = "\."[0-9]+


%%


/*ACCIONES*/

{LineaVacia}        {   setHaySalto(true);
                        setNuevaSentencia(yyline);
                        setNuevoLexemaLexico(TokenEnum.VACIO, yytext(), yyline, yycolumn );
                    }

{Tab}+              {   setNuevaSentencia(yyline);
                        setNuevoLexemaLexico(TokenEnum.TABULACION, yytext(), yyline, yycolumn );
                    }



{CadenaError}       { setNuevaSentencia(yyline);
                      setNuevoLexemaErroneo(TokenEnum.ERROR, yytext(), yyline, yycolumn, "Cadena de texto sin cierre", "Se esperaba un " + TokenEnum.CADENA.getNombreToken() );
                    }


{CadenaTexto}       { setNuevaSentencia(yyline);
                      setNuevoLexemaLexico(TokenEnum.CADENA, yytext(), yyline, yycolumn ); 
                    }



{Espacio}+          { setNuevaSentencia(yyline);
                      setNuevoLexemaLexico(TokenEnum.ESPACIO, yytext(), yyline, yycolumn );
                    }


{ErrorComentarioBloque}      {  setNuevaSentencia(yyline);
                                setNuevoLexemaErroneo(TokenEnum.ERROR, yytext(), yyline, yycolumn, "Comentario de bloque sin cierre", "Se esperaba un " + TokenEnum.COMENTARIO_BLOQUE.getNombreToken());
                             }


{Igual}                 { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.OPERADOR, TokenEnum.IGUAL, yytext(), yyline, yycolumn );  
                        }


{Como}                 { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.PALABRA_RESERVADA, TokenEnum.COMO, yytext(), yyline, yycolumn );  
                       }


{Definir}              { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.PALABRA_RESERVADA, TokenEnum.DEFINIR, yytext(), yyline, yycolumn );  
                       }


{PuntoComa}            { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.PUNTUACION, TokenEnum.PUNTO_COMA, yytext(), yyline, yycolumn );  
                       }


{Entero}               { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.PALABRA_RESERVADA, TokenEnum.TIPO_ENTERO, yytext(), yyline, yycolumn );  
                       }

{Cadena}               { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.PALABRA_RESERVADA, TokenEnum.TIPO_CADENA, yytext(), yyline, yycolumn );  
                       }

{NumeroEspecial}       { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.PALABRA_RESERVADA, TokenEnum.TIPO_NUMERO, yytext(), yyline, yycolumn );  
                       }


{Escribir}             { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.PALABRA_RESERVADA, TokenEnum.ESCRIBIR, yytext(), yyline, yycolumn );  
                       }

{ParentesisCierre}     { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.AGRUPACION, TokenEnum.PARENTESIS_CIERRE, yytext(), yyline, yycolumn );  
                       }

{ParentesisApertura}   { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.AGRUPACION, TokenEnum.PARENTESIS_APERTURA, yytext(), yyline, yycolumn );  
                       }


{PalabraReservada}      { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.PALABRA_RESERVADA, yytext(), yyline, yycolumn );  
                        }


{ErrorDecimal}          { setNuevaSentencia(yyline);
                          setNuevoLexemaErroneo(TokenEnum.ERROR, yytext(), yyline, yycolumn, "Doble punto decimal", "Se esperaba un " + TokenEnum.COMENTARIO_BLOQUE.getNombreToken());
                        }

{ErrorInicioDecimal}    { setNuevaSentencia(yyline);
                          setNuevoLexemaErroneo(TokenEnum.ERROR, yytext(), yyline, yycolumn, "Punto decimal sin numero inicial", "Se esperaba un " + TokenEnum.COMENTARIO_BLOQUE.getNombreToken());
                        }


{Numero}                { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.NUMERO, yytext(), yyline, yycolumn );  
                        }

{Decimal}               { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.DECIMAL, yytext(), yyline, yycolumn );  
                        }


{Identificador}         { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.IDENTIFICADOR, yytext(), yyline, yycolumn ); 
                        }

{Puntuacion}            { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.PUNTUACION, yytext(), yyline, yycolumn ); 
                        }


{Suma}                 { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.OPERADOR, TokenEnum.SUMA, yytext(), yyline, yycolumn );  
                       }

{Resta}                { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.OPERADOR, TokenEnum.RESTA, yytext(), yyline, yycolumn );  
                       }

{Multiplicacion}       { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.OPERADOR, TokenEnum.MULTIPLICACION, yytext(), yyline, yycolumn );  
                       }


{Division}             { setNuevaSentencia(yyline);
                          setNuevoLexemaSintactico(TokenEnum.OPERADOR, TokenEnum.DIVISION, yytext(), yyline, yycolumn );  
                       }


{OperadorAritmetico}    { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.OPERADOR, yytext(), yyline, yycolumn ); 
                        }

{Agrupacion}            { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.AGRUPACION, yytext(), yyline, yycolumn ); 
                        }



{ComentarioLinea}       { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.COMENTARIO_LINEA, yytext(), yyline, yycolumn ); 
                        }

{DocumentationComment}  { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.COMENTARIO_BLOQUE, yytext(), yyline, yycolumn ); 
                          System.out.println("Token COMENTARIO DE BLOQUE <" + yytext() + " columna " + yycolumn+">"); }

{ComentarioBloque}      { setNuevaSentencia(yyline);
                          setNuevoLexemaLexico(TokenEnum.COMENTARIO_BLOQUE, yytext(), yyline, yycolumn ); 
                        }


(.)          {  setNuevaSentencia(yyline);
                setNuevoLexemaErroneo(TokenEnum.ERROR, yytext(), yyline, yycolumn, "Caracter no registrado en la gramatica", "Se esperaba un caracter registrado en la gramatica");
             }