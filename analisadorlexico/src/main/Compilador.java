/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.io.IOException;

/**
 *
 * @author Claudemir
 */
public class Compilador {
    
    
    public static void main(String[] args) throws IOException{
        System.out.println("Executando Analisador Léxico\n");
        
        Arquivo arq = new Arquivo("teste.txt");
        //arq.parseArquivo();
        Lexer lex = new Lexer(arq.parseArquivo());
        lex.parse();
        //boolean i = "\n".equals("\n");
        //System.out.println(i);
    }   
}
