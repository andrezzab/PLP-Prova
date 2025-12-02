package li2.plp.imperative2.command;

import java.util.List;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;

public class Range extends ComandoEstatisticoAbstrato {

    public Range(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        double max = Max.calcularMaximo(numeros);
        double min = Min.calcularMinimo(numeros);
        double amplitude = max - min;
        return new ValorDouble(amplitude);
    }

    @Override
    protected String getNomeEstatistica() {
        return "Amplitude";
    }

    @Override
    protected Tipo getTipoRetorno() {
        return TipoPrimitivo.DOUBLE;
    }
}