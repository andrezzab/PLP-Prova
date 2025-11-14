package li2.plp.imperative2.command;

import java.util.List;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.imperative2.util.CalculadoraEstatisticas;

public class Min extends ComandoEstatisticoAbstrato {

    public Min(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        double minimo = CalculadoraEstatisticas.calcularMinimo(numeros);
        return new ValorDouble(minimo);
    }

    @Override
    protected String getNomeEstatistica() {
        return "Mínimo";
    }
}
