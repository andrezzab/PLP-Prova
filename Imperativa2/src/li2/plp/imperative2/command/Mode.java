package li2.plp.imperative2.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;

public class Mode extends ComandoEstatisticoAbstrato {

    public Mode(Id idVariavelCsv, Id nomeColuna, Id idVariavelDestino) {
        super(idVariavelCsv, nomeColuna, idVariavelDestino);
    }

    @Override
    protected Valor calcular(List<Double> numeros) {
        // Retorna uma lista, pois pode ser bimodal/multimodal
        List<Double> modas = calcularModa(numeros);
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

    public static List<Double> calcularModa(List<Double> numeros) {
        List<Double> modas = new ArrayList<>();
        if (numeros.isEmpty()) return modas;

        Map<Double, Integer> contagem = new HashMap<>();
        for (double num : numeros) {
            contagem.put(num, contagem.getOrDefault(num, 0) + 1);
        }
        int maxContagem = 0;
        for (int count : contagem.values()) {
            if (count > maxContagem) {
                maxContagem = count;
            }
        }
        if (maxContagem <= 1 && numeros.size() > 1) {
            return modas; // Não há moda
        }
        for (Map.Entry<Double, Integer> entry : contagem.entrySet()) {
            if (entry.getValue() == maxContagem) {
                modas.add(entry.getKey());
            }
        }
        return modas;
    }
}