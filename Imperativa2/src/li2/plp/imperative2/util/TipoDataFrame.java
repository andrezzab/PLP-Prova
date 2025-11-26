package li2.plp.imperative2.util;

import java.util.Map;

import li2.plp.expressions1.util.Tipo;

public class TipoDataFrame implements Tipo {

    // O schema é a única coisa que precisamos saber em tempo de compilação
    private Map<String, Tipo> schema;

    public TipoDataFrame(Map<String, Tipo> schema) {
        this.schema = schema;
    }

    public Map<String, Tipo> getSchema() {
        return schema;
    }

    // --- Implementação da Interface Tipo ---

    @Override
    public String getNome() {
        return "DataFrame";
    }

    @Override
    public boolean eInteiro() { return false; }

    @Override
    public boolean eBooleano() { return false; }

    @Override
    public boolean eString() { return false; }

    @Override
    public boolean eDouble() { return false; }

    @Override
    public boolean eIgual(Tipo tipo) {
        if (tipo instanceof TipoDataFrame) {
            // Opcional: Para ser estrito, dois DataFrames só são iguais
            // se tiverem as mesmas colunas e tipos.
            // Simplificação: apenas verifica se ambos são DataFrames.
            return true; 
        }
        return false;
    }

    @Override
    public boolean eValido() {
        return schema != null;
    }

    @Override
    public Tipo intersecao(Tipo outroTipo) {
        if (outroTipo instanceof TipoDataFrame) {
            return this;
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "DataFrame" + schema.keySet().toString();
    }

    @Override
    public boolean eDataFrame() {
        return true; // É um DataFrame!
    }
}