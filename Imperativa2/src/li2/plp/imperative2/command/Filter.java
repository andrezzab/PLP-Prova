package li2.plp.imperative2.command;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.ValorBooleano;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.expressions2.expression.ValorDataFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementa o comando FILTER...WHERE...
 * Este comando usa a lógica de "matriz mxn" (ValorDataFrame)
 * e o avaliador de expressões da I2.
 */
public class Filter implements Comando {

    private Id idDataFrameOriginal;
    private Id idDataFrameNovo;
    private Expressao condicao;

    /**
     * Construtor que o parser (Imp2Parser.jj) vai chamar.
     * @param idOrigem O Id da variável do DataFrame original (ex: "func")
     * @param idDestino O Id para o novo DataFrame filtrado (ex: "seniores")
     * @param condicao A expressão de filtro (ex: "idade > 30")
     */
    public Filter(Id idOrigem, Id idDestino, Expressao condicao) {
        this.idDataFrameOriginal = idOrigem;
        this.idDataFrameNovo = idDestino;
        this.condicao = condicao;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) throws RuntimeException {

        // 2. Pega o DataFrame original do amb
        Valor val = amb.get(idDataFrameOriginal);
        if (!(val instanceof ValorDataFrame)) {
            throw new RuntimeException("Erro: Variável '" + idDataFrameOriginal.getIdName() + "' não é um DataFrame.");
        }
        ValorDataFrame dfOriginal = (ValorDataFrame) val;

        // 3. Prepara para criar o novo DataFrame
        List<Map<String, Valor>> linhasFiltradas = new ArrayList<>();
        Map<String, Tipo> schema = dfOriginal.getSchema();

        // 4. Itera pelas linhas do DataFrame original
        for (Map<String, Valor> linha : dfOriginal.getRows()) {
            
            // 5. A MÁGICA: Injeta as colunas da linha no amb
            amb.incrementa(); // Cria novo escopo
            for (String nomeColuna : schema.keySet()) {
                // Mapeia a coluna (ex: "idade") para seu valor (ex: ValorInteiro(25))
                amb.map(new Id(nomeColuna), linha.get(nomeColuna));
            }

            // 6. Avalia a expressão (ex: "idade > 30") NESSE amb
            Valor resultado = condicao.avaliar(amb);
            if (!(resultado instanceof ValorBooleano)) {
                 amb.restaura(); // Limpa o escopo antes de lançar o erro
                 throw new RuntimeException("Erro: A expressão WHERE deve resultar em um booleano.");
            }
            boolean condicaoSatisfeita = ((ValorBooleano) resultado).valor();

            // 7. Limpa o amb (remove o escopo da linha)
            amb.restaura();

            // 8. Se a condição for verdadeira, salva a linha
            if (condicaoSatisfeita) {
                linhasFiltradas.add(linha);
            }
        }

        // 9. Cria o novo ValorDataFrame com as linhas filtradas
        ValorDataFrame dfNovo = dfOriginal.createNew(linhasFiltradas);
        
        // 10. Mapeia o novo DataFrame no amb
        amb.map(idDataFrameNovo, dfNovo);

        System.out.println(">> Filtro aplicado. Novo DataFrame '" + idDataFrameNovo.getIdName() + "' criado com " + linhasFiltradas.size() + " linhas.");

        // 11. Retorna o amb modificado
        return amb;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) throws RuntimeException {
        // TODO: Implementar checagem de tipo
        return true;
    }
}