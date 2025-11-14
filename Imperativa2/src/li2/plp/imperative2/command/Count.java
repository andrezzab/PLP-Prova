package li2.plp.imperative2.command;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorInteiro;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.expressions2.expression.ValorDataFrame;

/**
 * Implementa o comando COUNT...
 * Este comando usa a lógica de "matriz mxn" (ValorDataFrame).
 */
public class Count implements Comando {

    private Id idDataFrame;
    private Id idVariavelDestino; // Opcional, para "COUNT ... AS ..."

    /**
     * Construtor para: COUNT func
     * (O parser antigo só suportava este)
     */
    public Count(Id idDataFrame) {
        this.idDataFrame = idDataFrame;
        this.idVariavelDestino = null; // O resultado só será impresso
    }
    
    /**
     * Construtor para: COUNT func AS total
     * (A BNF precisaria ser atualizada para suportar "AS")
     */
    public Count(Id idDataFrame, Id idVariavelDestino) {
        this.idDataFrame = idDataFrame;
        this.idVariavelDestino = idVariavelDestino;
    }


    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) throws RuntimeException {
        // 1. Faz o cast para o ambiente correto
        AmbienteExecucaoImperativa2 ambiente = (AmbienteExecucaoImperativa2) amb;

        // 2. Pega o DataFrame original do ambiente
        Valor val = ambiente.get(idDataFrame);
        if (!(val instanceof ValorDataFrame)) {
            throw new RuntimeException("Erro: Variável '" + idDataFrame.getIdName() + "' não é um DataFrame.");
        }
        ValorDataFrame df = (ValorDataFrame) val;
        
        // 3. Obtém a contagem (operação em memória, rápida)
        int contagem = df.getRows().size();
        ValorInteiro resultado = new ValorInteiro(contagem);

        // 4. Salva o resultado no ambiente SE um 'AS' foi fornecido
        if (idVariavelDestino != null) {
            ambiente.map(idVariavelDestino, resultado);
            System.out.println(">> Contagem de '" + idDataFrame.getIdName() + "': " + contagem + " (salvo em '" + idVariavelDestino.getIdName() + "')");
        } else {
             System.out.println(">> Contagem de '" + idDataFrame.getIdName() + "': " + contagem);
        }

        // 5. Retorna o ambiente modificado
        return ambiente;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) throws RuntimeException {
        // TODO: Implementar checagem de tipo
        return true;
    }
}