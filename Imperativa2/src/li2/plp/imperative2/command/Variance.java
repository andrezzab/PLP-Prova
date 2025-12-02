package li2.plp.imperative2.command;

import java.util.List;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;

public class Variance extends ComandoEstatisticoAbstrato {

    public Variance(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        double variancia = calcularVariancia(numeros);
        return new ValorDouble(variancia);
    }

    @Override
    protected String getNomeEstatistica() {
        return "Variância";
    }

    @Override
    protected Tipo getTipoRetorno() {
        return TipoPrimitivo.DOUBLE;
    }

    public static double calcularVariancia(List<Double> numeros) {
        if (numeros.size() < 2) return 0.0;
        double media = Mean.calcularMedia(numeros);
        double somaDosQuadrados = 0.0;
        for (double num : numeros) {
            somaDosQuadrados += Math.pow(num - media, 2);
        }
        return somaDosQuadrados / (numeros.size() - 1);
    }
}