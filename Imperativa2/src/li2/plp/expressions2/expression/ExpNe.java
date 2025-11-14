package li2.plp.expressions2.expression;

import li2.plp.expressions2.memory.AmbienteExecucao;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * Implementa a expressão Diferente (!=)
 */
public class ExpNe extends ExpBinariaNumerica {

    public ExpNe(Expressao esq, Expressao dir) {
        // Chama o construtor da classe pai, passando o operador "!="
        super(esq, dir, "!=");
    }

    @Override
    public Valor avaliar(AmbienteExecucao amb) throws VariavelNaoDeclaradaException {
        Valor valEsq = esq.avaliar(amb);
        Valor valDir = dir.avaliar(amb);

        double numEsq = getValorNumerico(valEsq);
        double numDir = getValorNumerico(valDir);
        
        // Comparação direta de != para floats pode ser imprecisa
        // mas para este projeto, deve ser suficiente.
        return new ValorBooleano(numEsq != numDir);
    }
    
    @Override
    public ExpBinaria clone() {
        return new ExpNe(esq.clone(), dir.clone());
    }
}