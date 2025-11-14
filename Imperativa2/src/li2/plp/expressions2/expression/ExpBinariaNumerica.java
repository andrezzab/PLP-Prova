package li2.plp.expressions2.expression;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;
import li2.plp.expressions2.memory.AmbienteCompilacao;
import li2.plp.expressions2.memory.AmbienteExecucao;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;

/**
 * Classe pai para expressões de comparação (>, <, >=, <=, !=).
 * ELA AGORA ESTENDE A SUA ExpBinaria CORRETAMENTE.
 */
public abstract class ExpBinariaNumerica extends ExpBinaria {

    public ExpBinariaNumerica(Expressao esq, Expressao dir, String operador) {
        // Chama o construtor da sua classe pai (ExpBinaria)
        super(esq, dir, operador);
    }

    /**
     * Implementa o método abstrato de ExpBinaria.
     * A verificação de tipo é a MESMA para todas as comparações numéricas:
     * Ambas as sub-expressões (esquerda e direita) devem ser numéricas.
     */
    @Override
    protected boolean checaTipoElementoTerminal(AmbienteCompilacao amb)
            throws VariavelNaoDeclaradaException {
        
        Tipo tipoEsq = getEsq().getTipo(amb);
        Tipo tipoDir = getDir().getTipo(amb);

        boolean esqNumerico = tipoEsq.eIgual(TipoPrimitivo.INTEIRO) || tipoEsq.eIgual(TipoPrimitivo.DOUBLE);
        boolean dirNumerico = tipoDir.eIgual(TipoPrimitivo.INTEIRO) || tipoDir.eIgual(TipoPrimitivo.DOUBLE);
        
        return esqNumerico && dirNumerico;
    }

    /**
     * Implementa o método getTipo (da interface Expressao).
     * O resultado de uma expressão de comparação é SEMPRE um booleano.
     */
    @Override
    public Tipo getTipo(AmbienteCompilacao amb) throws VariavelNaoDeclaradaException {
        return TipoPrimitivo.BOOLEANO;
    }
    
    /**
     * Helper para extrair o valor double de um Valor (seja Inteiro ou Double)
     */
    protected double getValorNumerico(Valor v) {
        if (v instanceof ValorInteiro) {
            return (double) ((ValorInteiro) v).valor();
        } else if (v instanceof ValorDouble) {
            return ((ValorDouble) v).valor();
        }
        // Isso não deve acontecer se checaTipo() foi chamado
        throw new RuntimeException("Erro de tipo: esperado valor numérico.");
    }

    // A avaliar() e clone() continuam abstratas,
    // para que as classes filhas (ExpGt, ExpLt) as implementem.
    
    @Override
    public abstract Valor avaliar(AmbienteExecucao amb) throws VariavelNaoDeclaradaException;

    @Override
    public abstract ExpBinaria clone();
}