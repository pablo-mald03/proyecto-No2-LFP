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

Espacio = " "
Tab = \t

Salto = (\r\n | \n | \r)



Identificador = [:jletter:] [:jletterdigit:]*
Numero = 0 | [1-9][0-9]*


%%


/*ACCIONES*/

{Espacio}+          { System.out.println("sym.SALTO" + yytext()); }
{Tab}+              { System.out.println("sym.SALTO" + yytext()); }
{Salto}+            { System.out.println("sym.SALTO" + yytext()); }
{Identificador}     { System.out.println("Token Identificador <" + yytext() + ">"); }
{Numero}            { System.out.println("Token Numero <" + yytext() + ">"); }

(.)          { System.out.println("Caracter no registrado <" + yytext() + ">"); }