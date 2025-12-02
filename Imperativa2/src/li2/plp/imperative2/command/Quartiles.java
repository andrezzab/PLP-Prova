package li2.plp.imperative2.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;

public class Quartiles extends ComandoEstatisticoAbstrato {

    public Quartiles(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        Map<String, Double> quartis = calcularQuartis(numeros);
        String resultado = "Q1=" + quartis.get("Q1") + ", Q2=" + quartis.get("Q2") + ", Q3=" + quartis.get("Q3");
        return new ValorString(resultado);
    }

    @Override
    protected String getNomeEstatistica() {
        return "Quartis";
    }
    
    @Override
    protected Tipo getTipoRetorno() {
        return TipoPrimitivo.STRING;
    }

    // Medidas de Posição
    public static Map<String, Double> calcularQuartis(List<Double> numeros) {
        Map<String, Double> quartis = new HashMap<>();
        if (numeros.isEmpty()) return quartis;

        List<Double> sortedList = new ArrayList<>(numeros);
        Collections.sort(sortedList);

        double q2 = Median.calcularMediana(sortedList);
        int meio = sortedList.size() / 2;
        double q1 = Median.calcularMediana(sortedList.subList(0, meio));
        double q3 = Median.calcularMediana(sortedList.subList(sortedList.size() % 2 == 0 ? meio : meio + 1, sortedList.size()));

        quartis.put("Q1", q1);
        quartis.put("Q2", q2);
        quartis.put("Q3", q3);
        return quartis;
    }
}