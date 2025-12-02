package li2.plp.imperative2.command;

import java.util.Collections;
import java.util.List;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;

public class Min extends ComandoEstatisticoAbstrato {

    public Min(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        double minimo = calcularMinimo(numeros);
        return new ValorDouble(minimo);
    }

    @Override
    protected String getNomeEstatistica() {
        return "Mínimo";
    }

    @Override
    protected Tipo getTipoRetorno() {
        return TipoPrimitivo.DOUBLE;
    }

    // Medidas de Dispersão
    public static double calcularMinimo(List<Double> numeros) {
        if (numeros.isEmpty()) return Double.NaN;
        return Collections.min(numeros);
    }
}