/*CABECERA*/

package com.pablocompany.proyectono2lfp.jflexpackage;

%%

/*CONFIGURACIONES*/
%public
%class AnalizadorLexico
%unicode
%line
%column

/*CODIGO JAVA*/
%{


    // Código Java de apoyo


%}

/*EXPRESIONES REGULARES DEL AUTOMATA*/

Identificador = [:jletter:] [:jletterdigit:]*
Numero = 0 | [1-9][0-9]*



LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]



%%


/*ACCIONES*/

{Identificador}     { System.out.println("Token Identificador <" + yytext() + ">"); }
{Numero}            { System.out.println("Token Numero <" + yytext() + ">"); }
{LineTerminator}    { System.out.println("Terminacion de linea"); }
{WhiteSpace}        { System.out.println("Espacio en blanco"); }

(.)          { System.out.println("Caracter no registrado <" + yytext() + ">"); }