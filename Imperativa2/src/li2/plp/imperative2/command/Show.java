package li2.plp.imperative2.command;

// Imports da PLP
import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorInteiro;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.imperative2.util.CalculadoraEstatisticas;
import li2.plp.expressions2.expression.ValorDataFrame; // A "Matriz mxn"

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Implementa o comando SHOW...
 * Esta versão foi ATUALIZADA para lidar com a nova sintaxe
 * SHOW MEAN dataframe.coluna
 * SHOW dataframe [LIMIT expressao]
 * SHOW variavel
 */
public class Show implements Comando {

    // Modo 1: SHOW dataframe [LIMIT expressao] OU SHOW variavel
    private final Id idVariavel;
    private final Expressao limiteExpressao; 
    
    // Modo 2: SHOW MEAN dataframe.coluna
    private final Id idDataFrameStats;
    private final Id idColunaStats;
    private final String tipoEstatistica; // "MEAN", "MEDIAN", etc.

    /**
     * Construtor para: SHOW dataframe [LIMIT expressao] E SHOW variavel
     * (Chamado pela última regra em PShow)
     */
    public Show(Id idVariavel, Expressao limite) {
        this.idVariavel = idVariavel;
        this.limiteExpressao = limite;
        
        // Desliga o modo estatístico
        this.idDataFrameStats = null;
        this.idColunaStats = null;
        this.tipoEstatistica = null;
    }

    /**
     * Construtor para: SHOW MEAN dataframe.coluna, SHOW MEDIAN ... etc.
     * (Chamado pelas primeiras regras em PShow)
     */
    public Show(Id idDataFrame, Id idColuna, String tipoEstatistica) {
        this.idDataFrameStats = idDataFrame;
        this.idColunaStats = idColuna;
        this.tipoEstatistica = tipoEstatistica.toUpperCase(); // Garante consistência

        // Desliga o modo de exibição de dados
        this.idVariavel = null;
        this.limiteExpressao = null;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) throws RuntimeException {
        AmbienteExecucaoImperativa2 ambiente = (AmbienteExecucaoImperativa2) amb;

        // Verifica qual modo de operação (SHOW STATS ou SHOW DATA/VAR)
        if (tipoEstatistica != null) {
            // MODO: SHOW MEAN dataframe.coluna
            executarShowStats(ambiente);
        } else {
            // MODO: SHOW dataframe [LIMIT ...] OU SHOW variavel
            executarShowData(ambiente);
        }

        return ambiente;
    }

    /**
     * Lógica para executar o 'SHOW dataframe [LIMIT ...]' ou 'SHOW variavel'
     */
    private void executarShowData(AmbienteExecucaoImperativa2 ambiente) {
        Valor valor = ambiente.get(idVariavel);

        // Caso 1: É um DataFrame (a "matriz mxn")
        if (valor instanceof ValorDataFrame) {
            ValorDataFrame df = (ValorDataFrame) valor;
            int limite = df.getRows().size(); // Padrão é mostrar tudo

            // Se o limite foi fornecido (limiteExpressao != null), avalia-o
            if (limiteExpressao != null) {
                Valor valorLimite = limiteExpressao.avaliar(ambiente);
                if (!(valorLimite instanceof ValorInteiro)) {
                    throw new RuntimeException("Erro: O limite do SHOW deve ser um número inteiro.");
                }
                limite = ((ValorInteiro) valorLimite).valor();
                if (limite < 0) limite = 0;
            }

            System.out.println(">> Mostrando DataFrame '" + idVariavel.getIdName() + "' (limite de " + limite + " linhas):");
            
            // Pega os nomes das colunas (precisamos garantir a ordem)
            List<String> colunas = new ArrayList<>(df.getSchema().keySet());

            // Imprime o Cabeçalho
            System.out.println(String.join("\t|\t", colunas));
            System.out.println(String.join("", Collections.nCopies(colunas.size() * 10, "-")));

            // Imprime as Linhas
            for (int i = 0; i < df.getRows().size() && i < limite; i++) {
                Map<String, Valor> linha = df.getRows().get(i);
                List<String> celulas = new ArrayList<>();
                for (String col : colunas) {
                    Valor val = linha.get(col);
                    if (val == null) {
                        celulas.add("null");
                    } else if (val instanceof ValorString) {
                        celulas.add(((ValorString) val).valor()); // Remove aspas
                    } else {
                        celulas.add(val.toString());
                    }
                }
                System.out.println(String.join("\t|\t", celulas));
            }
        
        // Caso 2: É um valor simples (ex: SHOW total_linhas)
        } else {
            // O comando LIMIT não faz sentido aqui, então o ignoramos.
            System.out.println(">> " + idVariavel.getIdName() + ": " + valor.toString());
        }
    }

    /**
     * Lógica para executar o 'SHOW MEAN dataframe.coluna'
     */
    private void executarShowStats(AmbienteExecucaoImperativa2 ambiente) {
        // Pega o DataFrame da memória
        Valor valorDf = ambiente.get(idDataFrameStats);
        if (!(valorDf instanceof ValorDataFrame)) {
            throw new RuntimeException("Erro: Variável '" + idDataFrameStats.getIdName() + "' não é um DataFrame.");
        }
        ValorDataFrame df = (ValorDataFrame) valorDf;
        String colName = idColunaStats.getIdName();
        
        // 1. Pega a lista de números (lógica idêntica ao ComandoEstatisticoAbstrato)
        if (!df.getSchema().containsKey(colName)) {
             throw new RuntimeException("Erro: Coluna '" + colName + "' não encontrada em SHOW " + tipoEstatistica);
        }
        
        Tipo tipoColuna = df.getSchema().get(colName);
        if (tipoColuna == null || !(tipoColuna.eIgual(TipoPrimitivo.INTEIRO) || tipoColuna.eIgual(TipoPrimitivo.DOUBLE))) {
            throw new RuntimeException("Erro: " + tipoEstatistica + " só pode ser usado em colunas numéricas (INT ou DOUBLE). Coluna '" + colName + "' é " + tipoColuna);
        }
        
        List<Double> numeros = new ArrayList<>();
        for (Map<String, Valor> linha : df.getRows()) {
            Valor valorLinha = linha.get(colName);
            if (valorLinha instanceof ValorInteiro) {
                numeros.add((double) ((ValorInteiro) valorLinha).valor());
            } else if (valorLinha instanceof ValorDouble) {
                numeros.add(((ValorDouble) valorLinha).valor());
            }
        }

        // 2. Calcula a estatística específica
        String output = "N/A";
        switch (tipoEstatistica) {
            case "MEAN":
                output = "" + CalculadoraEstatisticas.calcularMedia(numeros);
                break;
            case "MEDIAN":
                output = "" + CalculadoraEstatisticas.calcularMediana(numeros);
                break;
            case "STD":
                output = "" + CalculadoraEstatisticas.calcularDesvioPadrao(numeros);
                break;
            case "VARIANCE":
                output = "" + CalculadoraEstatisticas.calcularVariancia(numeros);
                break;
            case "MIN":
                output = "" + CalculadoraEstatisticas.calcularMinimo(numeros);
                break;
            case "MAX":
                output = "" + CalculadoraEstatisticas.calcularMaximo(numeros);
                break;
            case "RANGE":
                output = "" + (CalculadoraEstatisticas.calcularMaximo(numeros) - CalculadoraEstatisticas.calcularMinimo(numeros));
                break;
            case "MODE":
                List<Double> moda = CalculadoraEstatisticas.calcularModa(numeros);
                output = (moda.isEmpty() ? "N/A" : moda.stream().map(Object::toString).collect(Collectors.joining(", ")));
                break;
            case "QUARTILES":
                Map<String, Double> q = CalculadoraEstatisticas.calcularQuartis(numeros);
                output = String.format("Q1: %f, Q2: %f, Q3: %f", q.get("Q1"), q.get("Q2"), q.get("Q3"));
                break;
            default:
                throw new RuntimeException("Lógica interna do SHOW não implementada para: " + tipoEstatistica);
        }
        
        System.out.println(String.format(">> %s de %s.%s: %s", 
            tipoEstatistica, idDataFrameStats.getIdName(), colName, output));
    }


    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) throws RuntimeException {
        // TODO: Implementar checagem de tipo
        return true;
    }
}