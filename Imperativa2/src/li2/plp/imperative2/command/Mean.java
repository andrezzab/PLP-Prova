package li2.plp.imperative2.command;

import java.util.List;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble; // Supondo que você tenha uma classe ValorDouble
import li2.plp.imperative2.util.CalculadoraEstatisticas;

public class Mean extends ComandoEstatisticoAbstrato {

    public Mean(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        double media = CalculadoraEstatisticas.calcularMedia(numeros);
        // Você precisará de uma classe 'ValorDouble' para encapsular o resultado.
        // Se não tiver, pode usar ValorString ou adaptar para ValorInteiro.
        return new ValorDouble(media); 
    }

    @Override
    protected String getNomeEstatistica() {
        return "Média";
}
}