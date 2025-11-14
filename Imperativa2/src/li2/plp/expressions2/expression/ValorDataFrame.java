package li2.plp.expressions2.expression;

import li2.plp.expressions1.util.Tipo; // Supondo este pacote
import li2.plp.expressions2.memory.AmbienteCompilacao;
import li2.plp.expressions2.memory.AmbienteExecucao;

import java.util.List;
import java.util.Map;

/**
 * Esta é a sua "matriz mxn".
 * É uma classe Valor que armazena a planilha em memória.
 * É IMUTÁVEL: uma vez criado, não pode ser alterado.
 */
public class ValorDataFrame implements Valor {

    // O "esquema" (nomes e tipos das colunas)
    private final Map<String, Tipo> schema;
    
    // Os dados (lista de linhas, onde cada linha é um mapa de [coluna -> valor])
    private final List<Map<String, Valor>> rows;

    public ValorDataFrame(Map<String, Tipo> schema, List<Map<String, Valor>> rows) {
        this.schema = schema;
        this.rows = rows;
    }

    // --- MÉTODOS QUE OS COMANDOS USARÃO ---

    /**
     * Retorna o número de linhas (para o comando COUNT)
     */
    public int getRowCount() {
        return rows.size();
    }
    
    /**
     * Retorna a lista de todas as linhas.
     * O ComandoEstatisticoAbstrato usará isso para iterar.
     */
    public List<Map<String, Valor>> getRows() {
        return this.rows;
    }

    /**
     * Retorna o esquema (tipos das colunas).
     * Usado para verificações de tipo.
     */
    public Map<String, Tipo> getSchema() {
        return this.schema;
    }
    
    /**
     * Cria um NOVO ValorDataFrame com novas linhas (usado pelo FILTER).
     */
    public ValorDataFrame createNew(List<Map<String, Valor>> newRows) {
        // Retorna um novo objeto, mantendo o original imutável
        return new ValorDataFrame(this.schema, newRows);
    }

    // --- MÉTODOS OBRIGATÓRIOS DA INTERFACE Expressao ---

    @Override
    public Valor avaliar(AmbienteExecucao amb) {
        // Já é um valor, apenas retorna a si mesmo.
        return this;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacao amb) {
        return true; // Um valor é sempre bem-tipado.
    }

    @Override
    public Tipo getTipo(AmbienteCompilacao amb) {
        // Você precisará criar uma classe TipoDataFrame que implementa Tipo
        // return new TipoDataFrame(this.schema); 
        return null; // TODO: Implementar TipoDataFrame
    }

    @Override
    public Expressao reduzir(AmbienteExecucao ambiente) {
        return this; // Já é reduzido (é um valor).
    }

    @Override
    public Expressao clone() {
        // Como é imutável, podemos retornar a si mesmo.
        // É seguro e instantâneo.
        return this;
    }
}