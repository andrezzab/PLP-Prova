package li2.plp.imperative2.command;

import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.expressions2.expression.ValorDataFrame;
import li2.plp.expressions1.util.Tipo;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.imperative2.util.ParserCSV;

import java.util.List;
import java.util.Map;

/**
 * ESTA É A IMPLEMENTAÇÃO CORRETA DO COMANDO LOAD.
 * Ele implementa a interface 'Comando' da imperative1.
 * Ele lê o arquivo CSV e armazena um 'ValorDataFrame' no ambiente.
 */
public class Load implements Comando {

    private Expressao pathExpressao; // O caminho do arquivo (ex: "dados.csv")
    private Id idDataFrame;          // O nome da variável (ex: "dados")

    public Load(Expressao path, Id id) {
        this.pathExpressao = path;
        this.idDataFrame = id;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) throws RuntimeException {
        // 1. Faz o cast para o ambiente correto, que pode armazenar 'Valor'
        AmbienteExecucaoImperativa2 ambiente = (AmbienteExecucaoImperativa2) amb;

        // 2. Avalia a expressão do caminho para obter a String
        Valor val = this.pathExpressao.avaliar(ambiente);
        if (!(val instanceof ValorString)) {
            throw new RuntimeException("Erro: O caminho do LOAD deve ser uma String.");
        }
        String path = ((ValorString) val).valor();

        try {
            // 3. CHAMA O PARSER DE CSV
            ParserCSV parser = new ParserCSV(path);
            
            // 4. Obtém os dados do parser
            Map<String, Tipo> schema = parser.getSchema();
            List<Map<String, Valor>> rows = parser.getRows();

            // 5. CRIA O OBJETO "MATRIZ MXN" (ValorDataFrame)
            ValorDataFrame dataFrame = new ValorDataFrame(schema, rows);

            // 6. MAPA O OBJETO NO AMBIENTE
            // Esta é a linha que corrige o seu bug!
            ambiente.map(this.idDataFrame, dataFrame);

            // 7. Imprime a mensagem de sucesso que você já via
            System.out.println("Dataset '" + this.idDataFrame.getIdName() + "' carregado com sucesso.");

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar CSV '" + path + "': " + e.getMessage());
        }

        // 8. Retorna o ambiente modificado
        return ambiente;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) throws RuntimeException {
        // TODO: Implementar checagem de tipo (ex: path é String)
        // e mapear 'idDataFrame' para o novo 'TipoDataFrame' no 'amb'
        return true;
    }
}