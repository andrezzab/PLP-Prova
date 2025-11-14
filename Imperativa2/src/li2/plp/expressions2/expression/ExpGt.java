package li2.plp.expressions2.expression;

import li2.plp.expressions2.memory.AmbienteExecucao;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * Implementa a expressão Maior Que (>)
 * Estende a nova classe pai ExpBinariaNumerica.
 */
public class ExpGt extends ExpBinariaNumerica {

    public ExpGt(Expressao esq, Expressao dir) {
        // Chama o construtor da classe pai, passando o operador ">"
        super(esq, dir, ">");
    }

    /**
     * Implementa o método abstrato avaliar()
     */
    @Override
    public Valor avaliar(AmbienteExecucao amb) throws VariavelNaoDeclaradaException {
        Valor valEsq = esq.avaliar(amb);
        Valor valDir = dir.avaliar(amb);

        double numEsq = getValorNumerico(valEsq);
        double numDir = getValorNumerico(valDir);
        
        return new ValorBooleano(numEsq > numDir);
    }
    
    /**
     * Implementa o método abstrato clone()
     */
    @Override
    public ExpBinaria clone() {
        return new ExpGt(esq.clone(), dir.clone());
    }
}