package li2.plp.imperative2.command;

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

/**
 * Implementa o comando SAVE...AS...
 * Este comando é o oposto do LOAD. Ele pega um ValorDataFrame
 * da memória e o escreve em um arquivo.
 */
public class Save implements Comando {

    private Id idDataFrame;
    private Expressao pathExpressao;

    /**
     * Construtor que o PARSER CORRIGIDO vai chamar.
     * @param id O Id do DataFrame em memória (ex: "seniores")
     * @param path A Expressao (ValorString) do arquivo de destino (ex: "saida.csv")
     */
    public Save(Id id, Expressao path) {
        this.idDataFrame = id;
        this.pathExpressao = path;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) throws RuntimeException {

        // 2. Avalia a expressão do caminho para obter um ValorString
        Valor valorPath = pathExpressao.avaliar(amb);
        if (!(valorPath instanceof ValorString)) {
            throw new RuntimeException("Erro: O caminho do SAVE deve ser uma string.");
        }
        String path = ((ValorString) valorPath).valor();

        // 3. Pega o DataFrame da memória
        Valor valorDf = amb.get(idDataFrame);
        if (!(valorDf instanceof ValorDataFrame)) {
            throw new RuntimeException("Erro: Variável '" + idDataFrame.getIdName() + "' não é um DataFrame.");
        }
        ValorDataFrame df = (ValorDataFrame) valorDf;

        // 4. Escreve o DataFrame no arquivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            // Escreve o cabeçalho
            String[] colunas = df.getSchema().keySet().toArray(new String[0]);
            writer.write(String.join(",", colunas));
            writer.newLine();

            // Escreve as linhas
            for (Map<String, Valor> linha : df.getRows()) {
                String[] celulas = new String[colunas.length];
                for (int i = 0; i < colunas.length; i++) {
                    // Pega o valor e o converte para string (removendo aspas, se for ValorString)
                    Valor val = linha.get(colunas[i]);
                    if (val instanceof ValorString) {
                        celulas[i] = ((ValorString) val).valor();
                    } else {
                        celulas[i] = val.toString();
                    }
                }
                writer.write(String.join(",", celulas));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo '" + path + "': " + e.getMessage());
        }

        System.out.println(">> DataFrame '" + idDataFrame.getIdName() + "' salvo em '" + path + "'.");

        // 5. Retorna o amb (não foi modificado, mas a interface exige)
        return amb;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) throws RuntimeException {
        // TODO: Implementar checagem de tipo
        return true;
    }
}