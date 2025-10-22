/*CABECERA*/

package com.pablocompany.proyectono2lfp.jflexpackage;

import com.pablocompany.proyectono2lfp.backend.GestorLexer;

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
    // Código Java de apoyo
    



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

{Espacio}+          { System.out.println("Token espacio <" + yytext() + ">"); }
{LineaVacia}        {  String lexema = yytext();       
                        String[] lineas = lexema.split("\r\n|\r|\n", -1); 
                        int cantidadSaltos = lineas.length - 1;          
                        String espacios = lineas[0];                       
                        System.out.println("Cantidad de saltos: " + cantidadSaltos);
                        System.out.println("Espacios iniciales: '" + (espacios.length()));}

{Tab}+              { System.out.println("Token tab <" + yytext() + ">"); }



{ErrorComentarioBloque}      {System.out.println("Token ERROR COMENTAIO DE BLOQUE <" + yytext() + ">"); }

{PalabraReservada}      {System.out.println("Token palabra reservada <" + yytext() + ">");  }


{Igual}                 { System.out.println("Token Igual <" + yytext() + ">"); }


{Decimal}               { System.out.println("Token Decimal <" + yytext() + ">"); }
{Numero}                { System.out.println("Token Numero <" + yytext() + ">"); }

{Identificador}         { System.out.println("Token Identificador <" + yytext() + ">"); }

{Puntuacion}            { System.out.println("Token Puntuacion <" + yytext() + ">"); }
{OperadorAritmetico}    { System.out.println("Token operador aritmetico<" + yytext() + ">"); }

{Agrupacion}            { System.out.println("Token agrupacion <" + yytext() + ">"); }



{ComentarioLinea}       { System.out.println("Token COMENTARIO_LINEA <" + yytext() + ">"); }

{DocumentationComment}  {System.out.println("Token COMENTARIO DE BLOQUE <" + yytext() + ">"); }

{ComentarioBloque}      {System.out.println("Token COMENTAIO DE BLOQUE <" + yytext() + ">"); }


(.)          { System.out.println("Caracter no registrado <" + yytext() + ">"); }