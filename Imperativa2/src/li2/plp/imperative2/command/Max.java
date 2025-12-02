package li2.plp.imperative2.command;

import java.util.Collections;
import java.util.List;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;

public class Max extends ComandoEstatisticoAbstrato {

    public Max(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        double maximo = calcularMaximo(numeros);
        return new ValorDouble(maximo);
    }

    @Override
    protected String getNomeEstatistica() {
        return "Máximo";
    }
    
    @Override
    protected Tipo getTipoRetorno() {
        return TipoPrimitivo.DOUBLE;
    }

    public static double calcularMaximo(List<Double> numeros) {
        if (numeros.isEmpty()) return Double.NaN;
        return Collections.max(numeros);
    }
}