package li2.plp.expressions2.expression;

import li2.plp.expressions2.memory.AmbienteExecucao;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * Implementa a expressão Menor Que (<)
 */
public class ExpLt extends ExpBinariaNumerica {

    public ExpLt(Expressao esq, Expressao dir) {
        // Chama o construtor da classe pai, passando o operador "<"
        super(esq, dir, "<");
    }

    @Override
    public Valor avaliar(AmbienteExecucao amb) throws VariavelNaoDeclaradaException {
        Valor valEsq = esq.avaliar(amb);
        Valor valDir = dir.avaliar(amb);

        double numEsq = getValorNumerico(valEsq);
        double numDir = getValorNumerico(valDir);
        
        return new ValorBooleano(numEsq < numDir);
    }
    
    @Override
    public ExpBinaria clone() {
        return new ExpLt(esq.clone(), dir.clone());
    }
}