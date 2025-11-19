package li2.plp.imperative2.command;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.util.ParserCSV;
import li2.plp.expressions2.expression.ValorDataFrame;

import java.util.List;
import java.util.Map;

public class Load implements Comando {

    private Expressao pathExpressao;
    private Id idDataFrame;

    public Load(Expressao path, Id id) {
        this.pathExpressao = path;
        this.idDataFrame = id;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) {
        // NÃO PRECISA DE CAST!
        // AmbienteExecucaoImperativa já sabe fazer map(Id, Valor)

        try {
            // 1. Avalia a expressão usando o ambiente genérico
            Valor val = this.pathExpressao.avaliar(amb);
            
            if (!(val instanceof ValorString)) {
                throw new RuntimeException("Erro: O caminho do LOAD deve ser uma String.");
            }
            
            // Nota: Verifique se sua classe ValorString usa .valor() ou .getValor()
            String path = ((ValorString) val).valor(); 

            // 2. Lê o CSV
            ParserCSV parser = new ParserCSV(path);
            Map<String, Tipo> schema = parser.getSchema();
            List<Map<String, Valor>> rows = parser.getRows();

            // 3. Cria o DataFrame
            ValorDataFrame dataFrame = new ValorDataFrame(schema, rows);

            // 4. Mapa no ambiente BASE
            // O método map(Id, Valor) existe desde a Imperativa 1 / Expressões 2
            amb.map(this.idDataFrame, dataFrame);

            System.out.println("Dataset '" + this.idDataFrame.toString() + "' carregado com sucesso.");

        } catch (Exception e) {
            // Captura VariavelJaDeclaradaException, IOException, etc.
            throw new RuntimeException("Erro ao executar LOAD: " + e.getMessage());
        }

        return amb;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) {
        return this.pathExpressao.checaTipo(amb);
    }
}