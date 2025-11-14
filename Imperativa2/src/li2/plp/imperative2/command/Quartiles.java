package li2.plp.imperative2.command;

import java.util.List;
import java.util.Map;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.imperative2.util.CalculadoraEstatisticas;

public class Quartiles extends ComandoEstatisticoAbstrato {

    public Quartiles(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        Map<String, Double> quartis = CalculadoraEstatisticas.calcularQuartis(numeros);
        String resultado = "Q1=" + quartis.get("Q1") + ", Q2=" + quartis.get("Q2") + ", Q3=" + quartis.get("Q3");
        return new ValorString(resultado);
    }

    @Override
    protected String getNomeEstatistica() {
        return "Quartis";
    }
}
