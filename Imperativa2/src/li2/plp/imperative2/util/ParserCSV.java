package li2.plp.imperative2.util;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo; // IMPORT ADICIONADO
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.expressions2.expression.ValorInteiro;
import li2.plp.expressions2.expression.ValorString;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Um parser de CSV simples para o comando LOAD.
 * Ele lê o arquivo e já converte os dados para 'Valor'.
 */
public class ParserCSV {

    private Map<String, Tipo> schema = new HashMap<>();
    private List<Map<String, Valor>> rows = new ArrayList<>();
    private String[] columnNames;

    public ParserCSV(String path) throws IOException {
        parse(path);
    }

    private void parse(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            // 1. Ler Cabeçalho
            if ((line = br.readLine()) != null) {
                columnNames = line.split(",");
                // Inicializa o schema (vamos inferir o tipo na primeira linha de dados)
                for (String name : columnNames) {
                    schema.put(name.trim(), null); // Tipo ainda desconhecido
                }
            } else {
                throw new IOException("Arquivo CSV está vazio.");
            }

            // 2. Ler Linhas de Dados
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cells = line.split(",");
                Map<String, Valor> row = new HashMap<>();
                
                for (int i = 0; i < columnNames.length; i++) {
                    String colName = columnNames[i].trim();
                    String cellValue = (i < cells.length) ? cells[i].trim() : "";

                    // Converte o valor e infere o schema na primeira linha
                    Valor valor = inferirValor(cellValue);
                    row.put(colName, valor);

                    // Inferência de tipo (só na primeira linha de dados)
                    if (rows.isEmpty()) {
                        // --- LINHAS CORRIGIDAS ---
                        // Usa os nomes do seu enum TipoPrimitivo
                        if (valor instanceof ValorInteiro) schema.put(colName, TipoPrimitivo.INTEIRO);
                        else if (valor instanceof ValorDouble) schema.put(colName, TipoPrimitivo.DOUBLE);
                        else schema.put(colName, TipoPrimitivo.STRING);
                    }
                }
                rows.add(row);
            }
        }
    }

    /**
     * Tenta adivinhar o tipo do valor da célula.
     */
    private Valor inferirValor(String cellValue) {
        try {
            // Tenta como Inteiro
            int intVal = Integer.parseInt(cellValue);
            return new ValorInteiro(intVal);
        } catch (NumberFormatException e1) {
            try {
                // Tenta como Double
                double doubleVal = Double.parseDouble(cellValue);
                return new ValorDouble(doubleVal);
            } catch (NumberFormatException e2) {
                // Assume String
                return new ValorString(cellValue);
            }
        }
    }

    // Getters para o Load.java
    public Map<String, Tipo> getSchema() {
        return this.schema;
    }

    public List<Map<String, Valor>> getRows() {
        return this.rows;
    }
}