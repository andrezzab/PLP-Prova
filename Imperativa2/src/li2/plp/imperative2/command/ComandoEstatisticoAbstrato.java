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
        // ... (Implementação do executar igual à anterior) ...
        Valor valor = amb.get(idVariavelCsv);
        if (!(valor instanceof ValorDataFrame)) throw new RuntimeException("Erro: Variável não é DataFrame");
        
        ValorDataFrame df = (ValorDataFrame) valor;
        String colName = nomeColuna.getIdName();
        if (!df.getSchema().containsKey(colName)) throw new RuntimeException("Coluna '" + colName + "' não existe.");
        
        Tipo tipoColuna = df.getSchema().get(colName);
        if (!tipoColuna.eInteiro() && !tipoColuna.eDouble()) throw new RuntimeException("Coluna não numérica.");

        List<Double> numeros = new ArrayList<>();
        int linhaAtual = 0;
        for (Map<String, Valor> linha : df.getRows()) {
            linhaAtual++;
            Valor valorLinha = linha.get(colName);
            if (valorLinha instanceof ValorInteiro) numeros.add((double) ((ValorInteiro) valorLinha).valor());
            else if (valorLinha instanceof ValorDouble) numeros.add(((ValorDouble) valorLinha).valor());
            else throw new RuntimeException("Valor inválido na linha " + linhaAtual);
        }

        Valor resultado = calcular(numeros);
        amb.map(idVariavelDestino, resultado);
        System.out.println(">> " + this.getNomeEstatistica() + " (" + colName + "): " + resultado);
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