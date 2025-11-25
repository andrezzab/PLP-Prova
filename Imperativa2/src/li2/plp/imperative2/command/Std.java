package li2.plp.imperative2.command;

import java.util.List;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;
import li2.plp.imperative2.util.CalculadoraEstatisticas;

public class Std extends ComandoEstatisticoAbstrato {

    public Std(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        double desvioPadrao = CalculadoraEstatisticas.calcularDesvioPadrao(numeros);
        return new ValorDouble(desvioPadrao);
    }

    @Override
    protected String getNomeEstatistica() {
        return "Desvio Padrão";
    }

    @Override
    protected Tipo getTipoRetorno() {
        return TipoPrimitivo.DOUBLE;
    }
}