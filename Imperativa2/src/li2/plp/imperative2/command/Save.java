package li2.plp.imperative2.command;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.expressions2.expression.ValorDataFrame;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class Save implements Comando {

    private Id idDataFrame;
    private Expressao pathExpressao;

    public Save(Id id, Expressao path) {
        this.idDataFrame = id;
        this.pathExpressao = path;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) {
        // 1. Avalia o caminho
        Valor valPath = pathExpressao.avaliar(amb);
        if (!(valPath instanceof ValorString)) {
             throw new RuntimeException("Erro: O caminho para salvar o arquivo deve ser uma String.");
        }
        String path = ((ValorString) valPath).valor();

        // 2. Recupera o DataFrame
        Valor valDf = amb.get(idDataFrame);
        if (!(valDf instanceof ValorDataFrame)) {
            throw new RuntimeException("Erro: Variável '" + idDataFrame.getIdName() + "' não é um DataFrame.");
        }
        ValorDataFrame df = (ValorDataFrame) valDf;

        // 3. Escreve no disco
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            // Cabeçalho
            String[] colunas = df.getSchema().keySet().toArray(new String[0]);
            writer.write(String.join(",", colunas));
            writer.newLine();

            // Linhas
            for (Map<String, Valor> linha : df.getRows()) {
                String[] celulas = new String[colunas.length];
                for (int i = 0; i < colunas.length; i++) {
                    Valor v = linha.get(colunas[i]);
                    // Se for string, pega o valor cru. Se for outro, usa toString.
                    celulas[i] = (v instanceof ValorString) ? ((ValorString)v).valor() : v.toString();
                }
                writer.write(String.join(",", celulas));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo '" + path + "': " + e.getMessage());
        }

        System.out.println(">> DataFrame '" + idDataFrame.getIdName() + "' salvo em '" + path + "'.");
        return amb;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) {
        // 1. Verifica se a variável do DataFrame existe e é do tipo correto
        Tipo tipoDf = amb.get(idDataFrame);
        if (tipoDf == null || !tipoDf.eDataFrame()) {
            System.out.println("Erro de Compilação: Variável '" + idDataFrame.getIdName() + "' não é um DataFrame válido para salvar.");
            return false;
        }

        // 2. Verifica se a expressão do caminho é uma String válida
        if (!pathExpressao.checaTipo(amb)) {
            return false;
        }
        Tipo tipoPath = pathExpressao.getTipo(amb);
        if (!tipoPath.eString()) {
             System.out.println("Erro de Compilação: O caminho do arquivo deve ser uma String.");
             return false;
        }

        return true;
    }
}