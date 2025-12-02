package li2.plp.imperative2.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;

public class Median extends ComandoEstatisticoAbstrato {

    public Median(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        double mediana = calcularMediana(numeros);
        return new ValorDouble(mediana);
    }

    @Override
    protected String getNomeEstatistica() {
        return "Mediana";
    }

    @Override
    protected Tipo getTipoRetorno() {
        return TipoPrimitivo.DOUBLE;
    }

    public static double calcularMediana(List<Double> numeros) {
        if (numeros.isEmpty()) return Double.NaN;
        List<Double> sortedList = new ArrayList<>(numeros);
        Collections.sort(sortedList);
        int meio = sortedList.size() / 2;
        if (sortedList.size() % 2 == 1) {
            return sortedList.get(meio);
        } else {
            return (sortedList.get(meio - 1) + sortedList.get(meio)) / 2.0;
        }
    }
}