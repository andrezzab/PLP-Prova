package li2.plp.imperative2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CalculadoraEstatisticas {

    // Medidas de Tendência Central
    public static double calcularMedia(List<Double> numeros) {
        if (numeros.isEmpty()) return Double.NaN;
        double soma = 0.0;
        for (double num : numeros) {
            soma += num;
        }
        return soma / numeros.size();
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

    // Medidas de Dispersão
    public static double calcularMinimo(List<Double> numeros) {
        if (numeros.isEmpty()) return Double.NaN;
        return Collections.min(numeros);
    }

    public static double calcularMaximo(List<Double> numeros) {
        if (numeros.isEmpty()) return Double.NaN;
        return Collections.max(numeros);
    }

    public static double calcularVariancia(List<Double> numeros) {
        if (numeros.size() < 2) return 0.0;
        double media = calcularMedia(numeros);
        double somaDosQuadrados = 0.0;
        for (double num : numeros) {
            somaDosQuadrados += Math.pow(num - media, 2);
        }
        return somaDosQuadrados / (numeros.size() - 1);
    }

    public static double calcularDesvioPadrao(List<Double> numeros) {
        return Math.sqrt(calcularVariancia(numeros));
    }

    // Medidas de Posição
    public static Map<String, Double> calcularQuartis(List<Double> numeros) {
        Map<String, Double> quartis = new HashMap<>();
        if (numeros.isEmpty()) return quartis;

        List<Double> sortedList = new ArrayList<>(numeros);
        Collections.sort(sortedList);

        double q2 = calcularMediana(sortedList);
        int meio = sortedList.size() / 2;
        double q1 = calcularMediana(sortedList.subList(0, meio));
        double q3 = calcularMediana(sortedList.subList(sortedList.size() % 2 == 0 ? meio : meio + 1, sortedList.size()));

        quartis.put("Q1", q1);
        quartis.put("Q2", q2);
        quartis.put("Q3", q3);
        return quartis;
    }
}