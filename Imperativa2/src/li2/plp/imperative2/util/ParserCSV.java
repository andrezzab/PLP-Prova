package li2.plp.imperative2.util;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo; 
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.expressions2.expression.ValorInteiro;
import li2.plp.expressions2.expression.ValorString;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap; 
import java.util.List;
import java.util.Map;

/**
 * Utilitário para ler arquivos CSV e converter para estruturas da PLP.
 */
public class ParserCSV {

    private String filePath;
    private Map<String, Tipo> schema = new LinkedHashMap<>(); 
    private List<Map<String, Valor>> rows = new ArrayList<>();
    private String[] columnNames;

    public ParserCSV(String filePath) throws IOException {
        this.filePath = filePath;
        parse();
    }

    private void parse() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // 1. Ler Cabeçalho (Nomes das Colunas)
            if ((line = br.readLine()) != null) {
                if (line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                
                columnNames = line.split(",");
                for (int i = 0; i < columnNames.length; i++) {
                    columnNames[i] = columnNames[i].trim().replace("\"", "");
                    schema.put(columnNames[i], null);
                }
            } else {
                throw new IOException("Arquivo CSV vazio (sem cabeçalho): " + filePath);
            }

            // 2. Ler Linhas de Dados
            boolean hasData = false; // Flag para verificar se achamos dados
            
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] cells = line.split(","); 
                Map<String, Valor> row = new LinkedHashMap<>(); 

                for (int i = 0; i < columnNames.length; i++) {
                    String colName = columnNames[i];
                    String cellValue = (i < cells.length) ? cells[i].trim() : "";

                    Valor valor = inferirValor(cellValue);
                    row.put(colName, valor);

                    // Inferência de tipo na primeira linha de dados encontrada
                    if (!hasData) {
                        if (valor instanceof ValorInteiro) schema.put(colName, TipoPrimitivo.INTEIRO);
                        else if (valor instanceof ValorDouble) schema.put(colName, TipoPrimitivo.DOUBLE);
                        else schema.put(colName, TipoPrimitivo.STRING);
                    }
                }
                rows.add(row);
                hasData = true; // Confirmamos que existe pelo menos uma linha de dados
            }
            
            // --- VALIDAÇÃO DE DADOS ---
            // Se saiu do loop e hasData continua false, significa que só tinha cabeçalho.
            if (!hasData) {
                throw new RuntimeException("Erro de Dados: A planilha '" + filePath + "' contém apenas o cabeçalho e nenhum dado.");
            }
        }
    }

    private Valor inferirValor(String val) {
        val = val.replace("\"", "");
        try {
            int i = Integer.parseInt(val);
            return new ValorInteiro(i);
        } catch (NumberFormatException e1) {
            try {
                double d = Double.parseDouble(val);
                return new ValorDouble(d);
            } catch (NumberFormatException e2) {
                return new ValorString(val);
            }
        }
    }

    public Map<String, Tipo> getSchema() {
        return schema;
    }

    public List<Map<String, Valor>> getRows() {
        return rows;
    }
}