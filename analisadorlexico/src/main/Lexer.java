package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Claudemir
 */
public class Lexer {

    private int tam;
    private ArrayList<String> arquivo;
    private Map<String, TabelaSimbolo> tabela;
    private int pos;
    private String token;
    private boolean erro;

    public Lexer(ArrayList<String> arquivo) {
        this.arquivo = arquivo;
        tam = arquivo.size();
        pos = 0;
        tabela = new HashMap<String, TabelaSimbolo>();
        token = "";
    }
    /**
     * Função responsável por fazer a análise léxica do arquivo
     */
    public void parse() {
        int endereco = 0;
        String lexema = "";
        //boolean fechaLexema = false;

        //Percorre o ArrayList onde estão as linhas do arquivo.
        for (int i = 0; i < tam; i++) {
            //Define o número de caracteres na linha
            int tamLinha = arquivo.get(i).length();
            //Testa se a linha está fazia
            if (tamLinha == 0) {
                continue;
            }
            //Percorre cada caracter de uma determinada linha
            while (pos <= tamLinha) {
                if (pos == tamLinha) {
                    lexema += " ";
                } else {
                    //Adiciona mais um caracter ao lexema
                    lexema += arquivo.get(i).charAt(pos);
                }
                if(lexema.equals(" ")||lexema.equals("\b")||lexema.equals("\t")){
                    pos++;
                    lexema="";
                    continue;
                }
                //Testa se o lexema ainda casa com algum padrão
                erro = testa(lexema);

                if (!erro) {
                    //Evita a variável receber um número negativo
                    //pos = (pos == 0) ? 0 : pos - 1;


                    if (erro) {
                        erro = testa(lexema);
                    } else {
                        //coloca na tabela
                        //if (lexema.charAt(lexema.length() - 1) == ' ') {
                        //    pos++;
                        //}
                        //if (lexema.length() != 1) {
                        //    lexema = lexema.substring(-1);
                        //}
                        lexema = lexema.trim();
                        //Verifica se o lexema já esta na tabela de simbolos
                        if (!tabela.containsKey(lexema)) {
                            //Verifica se o estado é de erro
                            if (token != "erro") {
                                TabelaSimbolo t = new TabelaSimbolo();
                                t.setDefinicao(lexema);
                                t.setToken(token);
                                t.setEndereco(endereco);

                                tabela.put(lexema, t);
                                System.out.println("<" + token + "," + endereco + ">");
                                endereco++;
                            } else {
                                System.out.println("INVALIDO (" + lexema + ") LINHA " + i);
                            }
                        } else {
                            TabelaSimbolo ts = tabela.get(lexema);
                            System.out.println("<" + token + "," + ts.getEndereco() + ">");
                        }
                    }
                    lexema = "";
                }
                pos++;
            }
            pos = 0;
        }

        System.out.println("\nDump da Tabela de Simbolos");
        dumpTabela();
    }
    
    /**
     * Função responsável por verifcar se existe algum casamento de padrão
     * @param lexema: lexema a ser testado
     * @return true casar, e falso caso contrário
     */
    public boolean testa(String lexema) {
        if (comparacao(lexema)) {
            return true;
        }
        if (operadorIgual(lexema)) {
            return true;
        }
        if (operador(lexema)) {
            return true;
        }
        if (comentarioLinha(lexema)) {
            return true;
        }
        if (comentarioMultiplo(lexema)) {
            return true;
        }
        //if (ehDigit(lexema)) {
         //   return true;
        //}
        if (ehFloat(lexema)) {
            return true;
        }
        return false;
    }
    /**
     * Função responsável por fazer o dump da tabela de simbolo
     */
    public void dumpTabela() {
        Iterator<TabelaSimbolo> i = tabela.values().iterator();
        while (i.hasNext()) {
            System.out.println(i.next().toString());
        }
    }

    //Insira as funções aqui.
    public boolean comparacao(String lexema) {
        String estado = "q0";
        char caracter = ' ';
        int tam = lexema.length();
        int i = 0;
        while (i < tam) {
            caracter = lexema.charAt(i);
            if (caracter == ' ') {
                estado = "qErr";
            } else if (caracter == '>') {
                switch (estado) {
                    case "q0":
                        estado = "q1";
                        break;
                    default:
                        estado = "qErr";
                        break;
                }
            } else if (caracter == '=') {
                switch (estado) {
                    case "q1":
                        estado = "q2";
                        break;
                    case "q3":
                        estado = "q4";
                        break;
                    default:
                        estado = "qErr";
                        break;
                }
            } else if (caracter == '<') {
                switch (estado) {
                    case "q0":
                        estado = "q3";
                        break;
                    default:
                        estado = "qErr";
                        break;
                }
            } else {
                estado = "qErr";
            }
            i++;
        }
        if ((estado.equals("q0")) || (estado.equals("qErr"))) {
            return false;
        } else {
            switch (estado) {
                case "q1":
                    token = "GT";
                    break;
                case "q2":
                    token = "GE";
                    break;
                case "q3":
                    token = "LT";
                    break;
                case "q4":
                    token = "LE";
                    break;
            }
            return true;
        }

    }

    public boolean operadorIgual(String lexema) {
        String estado = "q0";
        char caracter = ' ';
        int tam = lexema.length();
        int i = 0;
        while (i < tam) {
            caracter = lexema.charAt(i);
            if (caracter == ' ') {
                estado = "qErr";
            } else if (caracter == '=') {
                switch (estado) {
                    case "q0":
                        estado = "q1";
                        break;
                    case "q1":
                        estado = "q2";
                        break;
                    default:
                        estado = "qErr";
                        break;
                }
            } else {
                estado = "qErr";
            }
            i++;
        }
        if ((estado.equals("q0")) || (estado.equals("qErr"))) {
            return false;
        } else {
            switch (estado) {
                case "q1":
                    token = "ATR";
                    break;
                case "q2":
                    token = "EQ";
                    break;
            }
            return true;
        }

    }

    public Boolean operador(String palavra) {

        String estado = "q0";
        char caracter = ' ';
        int t = palavra.length();
        int p = 0;

        while (p < t) {
            caracter = palavra.charAt(p);
            
            //Se o caracter não é nenhum dos caracteres que me interessam estado de erro encontrado
            if (caracter != '+' && caracter != '/' && caracter != '*') {
                estado = "qerror";
            } else {
                //O caracter é algum dos que deve ser tratados
                
                //Se caracter for igual a +
                if (caracter == '+') {
                    switch (estado) {
                        case "q0":
                            //Estando em q0 vai-se para q1 que estado final para adição
                            estado = "q1";
                            break;
                        case "q1":
                            //Encontrando-se outro sinal de + em q1 vai-se para q2 que é estado final para incremento
                            estado = "q2";
                            break;
                        case "q2":
                            //Caso outro sinal de adição seja encontrado vai-se para estado de erro
                            estado = "qerror";
                            break;
                    }
                }
                //Se caracter e igual a /
                if (caracter == '/') {
                    switch (estado) {
                        case "q0":
                            //Estando-se em q0 vai se para q3 que é estado final de divisão
                            estado = "q3";
                            break;
                        case "q3":
                            //Caso outra barra seja encontrado vai-se para estado de erro
                            estado = "qerror";
                            break;
                    }
                }
                //Se caracter igual a *
                if (caracter == '*') {
                    switch (estado) {
                        case "q0":
                            //Estando em q0 que é estado incial vai se para q4 que é estado final de  multiplicação
                            estado = "q4";
                            break;
                        case "q4":
                            //Encontrando se outro sinal de * em q4 vai se para q5 que estado final de exponenciação
                            estado = "q5";
                            break;
                        case "q5":
                            //Estando em q5 caso encontre outro sinal de * vai se para estado de erro.
                            estado = "qerror";
                            break;
                    }
                }
            }
            p++;
        }

        switch (estado){
            case "q1":
                //token recebe SUM retorna true
                token = "SUM";
                return true;
            case "q2":
                //token recebe INC retorna true
                token = "INC";
                return true;
            case "q3":
                //token recebe DIV retorna true
                token = "DIV";
                return true;
            case "q4":
                //token recebe MUL retorna true
                token = "MUL";
                return true;
            case "q5": 
                //token recebe POW retorna true
                token = "POW";
                return true;
        }
        //Retorna false por não ter casado nenhum padrão.
        return false;
    }

    public Boolean comentarioLinha(String palavra) {

        String estado = "q0";
        char caracter = ' ';
        int t = palavra.length();
        int p = 0;

        while (p < t) {
            caracter = palavra.charAt(p);
            
            //Se caracter diferente de quebra de linha
            if (caracter != '\n') {
                
                //Se o caracter for /
                if (caracter == '/') {
                    switch (estado) {
                        case "q0":
                            //Estando em q0 vai se para q1
                            estado = "q1";
                            break;
                        case "q1":
                            //Estando em q1 vai para q2 e fica aguardando quebra de linha.
                            estado = "q2";
                            break;
                    }
                }
            } else {
                //Se o caracter é quebra de linha
                switch (estado) {
                    case "q2":
                        //Estado recebe q3 que é estado final para quebra de linha
                        estado = "q3";
                        break;
                }
            }
            p++;
        }
        //Se o estado é o estado final q3
        if (estado == "q3") {
            //Token recebe COM retorna-se true
            token = "COM";
            return true;
        } else {
            //Caso contrário retorna se false por não ter casado padrão.
            return false;
        }
    }

    public Boolean comentarioMultiplo(String palavra) {

        String estado = "q0";
        char caracter = ' ';
        int t = palavra.length();
        int p = 0;

        while (p < t) {
            caracter = palavra.charAt(p);
            
            //Se caracter é / ou * que são importantes para comentário multiplo então...
            if (caracter == '/' || caracter == '*') {
                
                //Se o caracter é igual a /
                if (caracter == '/') {
                    switch (estado) {
                        case "q0":
                            //Estando em q0 vai-se pra q1
                            estado = "q1";
                            break;
                        case "q3":
                            //Estando em q3 vai se para q4 que é estado final para comentário multiplo.
                            estado = "q4";
                            break;
                    }
                }

                //Se caracter é igual a *
                if (caracter == '*') {

                    switch (estado) {
                        case "q1":
                            //Estando em q1 pois ja encontrou uma / vai-se para q2
                            estado = "q2";
                            break;
                        case "q2":
                            //Estando em q2 vai-se para q3 e fica aguardando a outra / que fecha o comentário
                            estado = "q3";
                            break;
                    }
                }
            }
            p++;
        }
        //Se o estado é igual a q4 que é estado final para comentário multiplo
        if (estado.equals("q4")) {
            //Token recebe COM e retorna se true
            token = "COM";
            return true;
        } else {
            //Caso contrario retorna-se false por não ter casado padrão
            return false;
        }
    }

    //Comentário ehDigit verifica também se é float
    public static Boolean ehDigit(String palavra) {

        String estado = "q0";
        char caracter = ' ';
        int t = palavra.length();
        int p = 0;

        while (p < t) {
            caracter = palavra.charAt(p);
            //Se o caracter é um digito ou um ponto o que interessa para analise
            if (Character.isDigit(caracter) || caracter == '.') {
                
                //Se o caracter e um digito
                if (Character.isDigit(caracter)) {
                    switch (estado) {
                        case "q0":
                            //Estando em q0 vai-se para q1 
                            estado = "q1";
                            break;
                        case "q1":
                            //Estando em q1 permance-se em q1 até que um ponto se encontrado
                            estado = "q1";
                            break;
                        case "q2":
                            //Estando em q2 permanece-se em q2 pois podem haver mais digitos apos o ponto
                            estado = "q2"
                            break;
                    }
                } else {
                    //Caso o ponto seja encontrado
                    switch (estado) {
                        case "q1":
                            //Estando em q1 pois ja se encontrou um digito vai-se para q2 que é estado final para float
                            estado = "q2";
                            break;
                    }
                }

            } else {
                //Caso não seja digito ou ponto vai-se para estado de erro
                estado = "qerror";
            }
            p++;
        }
        switch (estado) {
            case "q1":
                //token recebe INT caso estado seja q1 estado final para int
                token = "INT";
                return true;
            case "q2":
                //Token recebe float caso em estado q2 estado final para float
                token = "FLOAT";
                return true;
        }
        //Retorna false caso nao case padrões
        return false;
    }
}
