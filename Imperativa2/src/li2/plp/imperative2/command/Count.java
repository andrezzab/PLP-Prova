package li2.plp.imperative2.command;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo; // Importante para tipar o resultado
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorInteiro;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.expressions2.expression.ValorDataFrame;

/**
 * Implementa o comando COUNT.
 * Conta o número de linhas de um DataFrame.
 */
public class Count implements Comando {

    private Id idDataFrame;
    private Id idVariavelDestino; // Opcional, para "COUNT ... AS ..."

    /**
     * Construtor para: COUNT func
     */
    public Count(Id idDataFrame) {
        this.idDataFrame = idDataFrame;
        this.idVariavelDestino = null; 
    }
    
    /**
     * Construtor para: COUNT func AS total
     */
    public Count(Id idDataFrame, Id idVariavelDestino) {
        this.idDataFrame = idDataFrame;
        this.idVariavelDestino = idVariavelDestino;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) throws RuntimeException {
        // 1. Pega o DataFrame original do amb
        Valor val = amb.get(idDataFrame);
        if (!(val instanceof ValorDataFrame)) {
            throw new RuntimeException("Erro: Variável '" + idDataFrame.getIdName() + "' não é um DataFrame.");
        }
        ValorDataFrame df = (ValorDataFrame) val;
        
        // 2. Obtém a contagem
        int contagem = df.getRows().size();
        ValorInteiro resultado = new ValorInteiro(contagem);

        // 3. Salva o resultado ou imprime
        if (idVariavelDestino != null) {
            amb.map(idVariavelDestino, resultado);
            System.out.println(">> Contagem de '" + idDataFrame.getIdName() + "': " + contagem + " (salvo em '" + idVariavelDestino.getIdName() + "')");
        } else {
             System.out.println(">> Contagem de '" + idDataFrame.getIdName() + "': " + contagem);
        }

        return amb;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) throws RuntimeException {
        // 1. Verifica se a variável alvo existe e é um DataFrame
        Tipo tipoDf = amb.get(idDataFrame);
        
        if (tipoDf == null || !tipoDf.eDataFrame()) {
            // Se o tipo for null, a variável não foi declarada.
            // Se eDataFrame() for false, é um Inteiro/String/Etc, não dá pra contar linhas.
            return false;
        }

        // 2. Se houver variável de destino (AS ...), registra ela como INTEIRO
        if (idVariavelDestino != null) {
            // O resultado de um COUNT é sempre um número inteiro
            amb.map(idVariavelDestino, TipoPrimitivo.INTEIRO);
        }

        return true;
    }
}