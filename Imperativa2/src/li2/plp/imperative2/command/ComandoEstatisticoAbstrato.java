package li2.plp.imperative2.command;

import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.util.TipoDataFrame;
import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.expressions2.expression.ValorInteiro;
import li2.plp.expressions2.expression.ValorDataFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ComandoEstatisticoAbstrato implements Comando {

    protected Id idVariavelCsv;
    protected Id nomeColuna;
    protected Id idVariavelDestino;

    public ComandoEstatisticoAbstrato(Id idCsv, Id col, Id dest) {
        this.idVariavelCsv = idCsv;
        this.nomeColuna = col;
        this.idVariavelDestino = dest;
    }

    protected abstract Valor calcular(List<Double> numeros);
    protected abstract String getNomeEstatistica();
    protected abstract Tipo getTipoRetorno();

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) throws RuntimeException {
        Valor valor = amb.get(idVariavelCsv);
        
        // Duck Typing para evitar instanceof se possível, ou manter cast seguro
        ValorDataFrame df;
        try {
            df = (ValorDataFrame) valor;
        } catch (ClassCastException e) {
            throw new RuntimeException("Erro: Variável '" + idVariavelCsv.getIdName() + "' não é um DataFrame.");
        }
        
        String colName = nomeColuna.getIdName();
        
        // 1. Validação de Coluna (Suporta Schema Dinâmico)
        if (df.getSchema() != null) {
            // Cenário A: Temos Schema (Load Estático)
            if (!df.getSchema().containsKey(colName)) 
                throw new RuntimeException("Erro: Coluna '" + colName + "' não existe no DataFrame.");
            
            Tipo tipoColuna = df.getSchema().get(colName);
            if (!tipoColuna.eInteiro() && !tipoColuna.eDouble()) 
                throw new RuntimeException("Erro: Coluna '" + colName + "' não é numérica (tipo encontrado: " + tipoColuna.getNome() + ").");
        } 
        else {
            // Cenário B: Schema Null (Load Dinâmico)
            if (df.getRows().isEmpty()) {
                System.out.println("Aviso: DataFrame vazio. " + getNomeEstatistica() + " não calculado.");
                return amb; // Retorna sem fazer nada ou salva valor padrão?
            }
            // Verifica na primeira linha de dados se a coluna existe
            if (!df.getRows().get(0).containsKey(colName)) {
                 throw new RuntimeException("Erro: Coluna '" + colName + "' não encontrada nos dados.");
            }
        }

        // 2. Extração de Dados
        List<Double> numeros = new ArrayList<>();
        int linhaAtual = 0;
        for (Map<String, Valor> linha : df.getRows()) {
            linhaAtual++;
            Valor valorLinha = linha.get(colName);
            
            if (valorLinha instanceof ValorInteiro) {
                numeros.add((double) ((ValorInteiro) valorLinha).valor());
            } else if (valorLinha instanceof ValorDouble) {
                numeros.add(((ValorDouble) valorLinha).valor());
            } else {
                // Se chegou aqui num Load Dinâmico, é porque a coluna tinha texto misturado
                throw new RuntimeException("Erro de Tipo na linha " + linhaAtual + ": Esperado número, encontrado " + valorLinha);
            }
        }

        if (numeros.isEmpty()) {
             System.out.println("Aviso: Coluna '" + colName + "' não possui dados numéricos.");
             // Aqui você poderia optar por salvar NaN ou 0, dependendo da regra de negócio
             return amb; 
        }

        // 3. Cálculo e Armazenamento
        Valor resultado = calcular(numeros);
        amb.map(idVariavelDestino, resultado);
        
        // Opcional: Feedback visual
        // System.out.println(">> " + this.getNomeEstatistica() + " (" + colName + "): " + resultado);
        
        return amb;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) {
        // 1. Verifica se a variável do dataset existe
        Tipo tipoVariavel;
        try {
            tipoVariavel = amb.get(idVariavelCsv);
        } catch (Exception e) {
            return false; // Variável não declarada
        }

        if (tipoVariavel == null || !tipoVariavel.eDataFrame()) return false;

        // 2. Verifica Schema
        TipoDataFrame tipoDf = (TipoDataFrame) tipoVariavel;
        
        // CORREÇÃO: Se o Schema for null (Load Dinâmico), aceitamos na confiança.
        if (tipoDf.getSchema() == null) {
            amb.map(idVariavelDestino, this.getTipoRetorno());
            return true;
        }

        // Validação Estrita (se soubermos o schema)
        String nomeCol = nomeColuna.getIdName();
        if (!tipoDf.getSchema().containsKey(nomeCol)) return false;

        Tipo tipoDaColuna = tipoDf.getSchema().get(nomeCol);
        if (!tipoDaColuna.eInteiro() && !tipoDaColuna.eDouble()) return false;

        amb.map(idVariavelDestino, this.getTipoRetorno());
        return true;
    }
}