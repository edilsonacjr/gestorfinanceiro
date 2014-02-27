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
                //Testa se o lexema ainda casa com algum padrão
                erro = testa(lexema);
                
                if (!erro) {
                    //Evita a variável receber um número negativo
                    pos = (pos == 0) ? 0 : pos - 1;
                    
                    
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
                        if (!tabela.containsKey(lexema)) {
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

        System.out.println("Dump da Tabela de Simbolos\n");
        dumpTabela();
    }

    public boolean testa(String lexema) {
        if (comparacao(lexema)) {
            return true;
        }
        return false;
    }

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
                    this.token = "GT";
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

    public static boolean operadorIgual(String lexema) {
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
                    //token = ATR;
                    break;
                case "q2":
                    //token = EQ;
                    break;
            }
            return true;
        }

    }

    public static Boolean operador(String palavra) {

        String estado = "q0";
        char caracter = ' ';
        int t = palavra.length();
        int p = 0;

        while (p < t) {
            caracter = palavra.charAt(p);

            if (caracter != '+' && caracter != '/' && caracter != '*') {
                estado = "qerror";
            } else {

                if (caracter == '+') {
                    switch (estado) {
                        case "q0":
                            estado = "q1";
                            break;
                        case "q1":
                            estado = "q2";
                            break;
                        case "q2":
                            estado = "qerror";
                            break;
                    }
                }

                if (caracter == '/') {
                    switch (estado) {
                        case "q0":
                            estado = "q1";
                            break;
                        case "q1":
                            estado = "qerror";
                            break;
                    }
                }

                if (caracter == '*') {
                    switch (estado) {
                        case "q0":
                            estado = "q1";
                            break;
                        case "q1":
                            estado = "q2";
                            break;
                        case "q2":
                            estado = "qerror";
                            break;
                    }
                }
            }
            p++;
        }

        if (estado.equals("q1") || estado.equals("q2")) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean comentarioLinha(String palavra) {

        String estado = "q0";
        char caracter = ' ';
        int t = palavra.length();
        int p = 0;

        while (p < t) {
            caracter = palavra.charAt(p);

            if (caracter != '\n') {

                if (caracter == '/') {
                    switch (estado) {
                        case "q0":
                            estado = "q1";
                            break;
                        case "q1":
                            estado = "q2";
                            break;
                    }
                }
            } else {
                switch (estado) {
                    case "q2":
                        estado = "q3";
                        break;
                }
            }
            p++;
        }
        if (estado == "q3") {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean comentarioMultiplo(String palavra) {

        String estado = "q0";
        char caracter = ' ';
        int t = palavra.length();
        int p = 0;

        while (p < t) {
            caracter = palavra.charAt(p);

            if (caracter == '/' || caracter == '*') {

                if (caracter == '/') {
                    switch (estado) {
                        case "q0":
                            estado = "q1";
                            break;
                        case "q3":
                            estado = "q4";
                            break;
                    }
                }

                if (caracter == '*') {

                    switch (estado) {
                        case "q1":
                            estado = "q2";
                            break;
                        case "q2":
                            estado = "q3";
                            break;
                    }
                }
            }
            p++;
        }
        if (estado.equals("q4")) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean ehDigit(String palavra) {

        String estado = "q0";
        char caracter = ' ';
        int t = palavra.length();
        int p = 0;

        while (p < t) {
            caracter = palavra.charAt(p);
            if (Character.isDigit(caracter)) {

                switch (estado) {
                    case "q0":
                        estado = "q1";
                        break;
                    case "q1":
                        estado = "q1";
                        break;
                }

            } else {
                estado = "qerror";
            }
            p++;
        }
        if (estado.equals("q1")) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean ehFloat(String palavra) {

        String estado = "q0";
        char caracter = ' ';
        int t = palavra.length();
        int p = 0;

        while (p < t) {
            caracter = palavra.charAt(p);

            if (Character.isDigit(caracter) || caracter == '.') {

                if (Character.isDigit(caracter)) {
                    switch (estado) {
                        case "q0":
                            estado = "q1";
                            break;
                        case "q1":
                            estado = "q1";
                            break;
                        case "q2":
                            estado = "q3";
                            break;
                        case "q3":
                            estado = "q3";
                            break;
                    }
                } else {
                    switch (estado) {
                        case "q1":
                            estado = "q2";
                            break;
                        case "q2":
                            estado = "qerror";
                            break;

                    }
                }
            } else {
                estado = "qerror";
            }
            p++;
        }

        if (estado.equals("q3")) {
            return true;
        } else {
            return false;
        }
    }
}
