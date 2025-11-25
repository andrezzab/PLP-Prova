package li2.plp.imperative2.command;

import java.util.List;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;
import li2.plp.imperative2.util.CalculadoraEstatisticas;

public class Mode extends ComandoEstatisticoAbstrato {

    public Mode(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        // Retorna uma lista, pois pode ser bimodal/multimodal
        List<Double> modas = CalculadoraEstatisticas.calcularModa(numeros);
        // Converte a lista para String para poder salvar numa variável simples
        return new ValorString(modas.toString());
    }

    @Override
    protected String getNomeEstatistica() {
        return "Moda";
    }

    @Override
    protected Tipo getTipoRetorno() {
        // Como o calcular retorna ValorString, o tipo aqui TEM de ser STRING
        return TipoPrimitivo.STRING;
    }
}