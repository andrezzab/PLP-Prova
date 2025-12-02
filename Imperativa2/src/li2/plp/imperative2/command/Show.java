package li2.plp.imperative2.command;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.expressions2.expression.ValorInteiro;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.expressions2.expression.ValorDataFrame;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.util.TipoDataFrame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Show implements Comando {

    private final Id idVariavel;
    private final Id idDataFrameStats;
    private final Id idColunaStats;
    private final String tipoEstatistica; 

    public Show(Id idVariavel) {
        this.idVariavel = idVariavel;
        this.idDataFrameStats = null;
        this.idColunaStats = null;
        this.tipoEstatistica = null;
    }

    public Show(Id idDataFrame, Id idColuna, String tipoEstatistica) {
        this.idDataFrameStats = idDataFrame;
        this.idColunaStats = idColuna;
        this.tipoEstatistica = tipoEstatistica.toUpperCase(); 
        this.idVariavel = null;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) {
        if (tipoEstatistica != null) executarShowStats(amb);
        else executarShowData(amb);
        return amb;
    }

    private void executarShowData(AmbienteExecucaoImperativa ambiente) {
        Valor valor = ambiente.get(idVariavel);

        // Caso 1: É um DataFrame (Exibir Tabela Formatada)
        if (valor instanceof ValorDataFrame) {
            ValorDataFrame df = (ValorDataFrame) valor;
            int totalLinhas = df.getRows().size();

            System.out.println(">> Mostrando DataFrame '" + idVariavel.getIdName() + "' (" + totalLinhas + " linhas):");
            
            // Recupera as colunas do Schema
            List<String> colunas;
            if (df.getSchema() != null) {
                colunas = new ArrayList<>(df.getSchema().keySet());
            } else if (!df.getRows().isEmpty()) {
                // Fallback se schema for null (load dinâmico), tenta pegar da primeira linha
                colunas = new ArrayList<>(df.getRows().get(0).keySet());
            } else {
                System.out.println("(DataFrame vazio)");
                return;
            }

            // 1. Imprime o Cabeçalho
            System.out.println(String.join("\t|\t", colunas));
            
            // 2. Imprime linha separadora
            // Cria uma string de traços proporcional ao número de colunas
            System.out.println(String.join("", Collections.nCopies(colunas.size() * 15, "-")));

            // 3. Imprime as Linhas de Dados
            for (Map<String, Valor> linha : df.getRows()) {
                List<String> celulas = new ArrayList<>();
                for (String col : colunas) {
                    Valor val = linha.get(col);
                    if (val == null) {
                        celulas.add("null");
                    } else if (val instanceof ValorString) {
                        // Remove aspas para ficar mais limpo na tabela
                        celulas.add(((ValorString) val).valor()); 
                    } else {
                        celulas.add(val.toString());
                    }
                }
                // Junta as células com tabulação e barra vertical
                System.out.println(String.join("\t|\t", celulas));
            }
        
        // Caso 2: É um valor simples
        } else {
            System.out.println(">> " + idVariavel.getIdName() + ": " + (valor != null ? valor.toString() : "null"));
        }
    }

    private void executarShowStats(AmbienteExecucaoImperativa ambiente) {
        ValorDataFrame df = (ValorDataFrame) ambiente.get(idDataFrameStats);
        String colName = idColunaStats.getIdName();
        
        // Verifica existência da coluna (mesmo para schema dinâmico/null, verificamos na linha)
        if (df.getSchema() != null && !df.getSchema().containsKey(colName)) {
             throw new RuntimeException("Coluna '" + colName + "' não existe.");
        } 
        // Se schema for null, verificamos se a primeira linha tem a coluna
        else if (df.getSchema() == null && !df.getRows().isEmpty() && !df.getRows().get(0).containsKey(colName)) {
             throw new RuntimeException("Coluna '" + colName + "' não encontrada nos dados.");
        }
        
        List<Double> numeros = new ArrayList<>();
        for (Map<String, Valor> linha : df.getRows()) {
            Valor v = linha.get(colName);
            if (v instanceof ValorInteiro) numeros.add((double)((ValorInteiro)v).valor());
            else if (v instanceof ValorDouble) numeros.add(((ValorDouble)v).valor());
            // Ignora silenciosamente valores não numéricos ou nulos para estatística robusta
        }
        
        if (numeros.isEmpty()) {
            System.out.println(">> " + tipoEstatistica + " (" + colName + "): Sem dados numéricos válidos.");
            return;
        }

        String output = "";
        switch (tipoEstatistica) {
            case "MEAN": output = "" + Mean.calcularMedia(numeros); break;
            case "MEDIAN": output = "" + Median.calcularMediana(numeros); break;
            case "STD": output = "" + Std.calcularDesvioPadrao(numeros); break;
            case "VARIANCE": output = "" + Variance.calcularVariancia(numeros); break;
            case "MIN": output = "" + Min.calcularMinimo(numeros); break;
            case "MAX": output = "" + Max.calcularMaximo(numeros); break;
            case "RANGE": output = "" + (Max.calcularMaximo(numeros) - Min.calcularMinimo(numeros)); break;
            case "MODE": output = Mode.calcularModa(numeros).toString(); break;
            case "QUARTILES": 
                Map<String, Double> q = Quartiles.calcularQuartis(numeros);
                output = String.format("Q1: %.2f, Q2: %.2f, Q3: %.2f", q.get("Q1"), q.get("Q2"), q.get("Q3")); break;
        }
        System.out.println(String.format(">> %s (%s): %s", tipoEstatistica, colName, output));
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) {
        if (tipoEstatistica != null) {
            Tipo tipoDf;
            try { tipoDf = amb.get(idDataFrameStats); } catch(Exception e) { return false; }
            
            if (tipoDf == null || !tipoDf.eDataFrame()) return false;
            
            TipoDataFrame tdf = (TipoDataFrame) tipoDf;
            
            // Aceita Schema Null (Dinâmico) - Passa na confiança
            if (tdf.getSchema() == null) return true;

            String colName = idColunaStats.getIdName();
            if (!tdf.getSchema().containsKey(colName)) return false; 
            Tipo tipoCol = tdf.getSchema().get(colName);
            return tipoCol.eInteiro() || tipoCol.eDouble();
        } else {
            try { return amb.get(idVariavel) != null; } catch(Exception e) { return false; }
        }
    }
}