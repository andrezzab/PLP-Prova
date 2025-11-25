package li2.plp.imperative2.command;

// Imports do imperative1 (para a interface e ambiente corretos)
import li2.plp.imperative1.command.Comando; 
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.expressions1.util.Tipo;
// Imports do expressions2 (para Valores e IDs)
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorDouble;
import li2.plp.expressions2.expression.ValorInteiro;
import li2.plp.expressions2.expression.ValorDataFrame; // Importa a "matriz"

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ESTA É A VERSÃO CORRETA.
 * Ela implementa a interface 'Comando' da imperative1 (como seu projeto exige).
 * Ela usa a lógica eficiente do 'ValorDataFrame' (a "matriz").
 */
public abstract class ComandoEstatisticoAbstrato implements Comando { // Implementa a interface de imperative1

    protected Id idVariavelCsv;
    protected Id nomeColuna;
    protected Id idVariavelDestino;

    public ComandoEstatisticoAbstrato(Id idCsv, Id col, Id dest) {
        this.idVariavelCsv = idCsv;
        this.nomeColuna = col;
        this.idVariavelDestino = dest;
    }

    /**
     * Métodos abstratos que as classes filhas (como Mean) devem implementar.
     */
    protected abstract Valor calcular(List<Double> numeros);
    protected abstract String getNomeEstatistica(); // Para mensagens de erro

    /**
     * Este é o método de execução principal.
     * A assinatura 'AmbienteExecucaoImperativa executar(...)' CORRESPONDE
     * à sua interface 'Comando' da imperative1.
     */
    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) throws RuntimeException {

        // 2. Pega o ValorDataFrame do amb
        //    Aqui, assumimos que 'amb' (sendo do expressions2)
        //    pode nos dar um 'Valor'.
        Valor valor = amb.get(idVariavelCsv);
        if (!(valor instanceof ValorDataFrame)) {
            throw new RuntimeException("Erro de Tipo: A variável '" + idVariavelCsv.getIdName() + "' não é um DataFrame. Encontrado: " + valor.getClass().getSimpleName());
        }
        ValorDataFrame df = (ValorDataFrame) valor;
        String colName = nomeColuna.getIdName();

        // 3. Verifica o esquema (se a coluna existe)
        if (!df.getSchema().containsKey(colName)) {
            throw new RuntimeException("Erro: Coluna '" + colName + "' não encontrada no DataFrame '" + idVariavelCsv.getIdName() + "'.");
        }
        
        // 3. Validação de TIPO (Usando sua interface Tipo)
        Tipo tipoColuna = df.getSchema().get(colName);
        
        // Verifica se é numérico usando os métodos da sua interface
        boolean isNumerico = tipoColuna.eInteiro() || tipoColuna.eDouble();
        
        if (!isNumerico) {
            throw new RuntimeException("Erro de Tipo: A estatística '" + getNomeEstatistica() + 
                                       "' só pode ser aplicada em colunas numéricas. " +
                                       "A coluna '" + colName + "' é do tipo " + tipoColuna.getNome());
        }

        // 4. Itera pelas linhas e constrói a List<Double>
        // 4. Extração segura dos dados
        List<Double> numeros = new ArrayList<>();
        int linhaAtual = 0;
        
        for (Map<String, Valor> linha : df.getRows()) {
            linhaAtual++;
            Valor valorLinha = linha.get(colName);
            
            // Tenta converter para double, seja inteiro ou real
            if (valorLinha instanceof ValorInteiro) {
                numeros.add((double) ((ValorInteiro) valorLinha).valor());
            } else if (valorLinha instanceof ValorDouble) {
                numeros.add(((ValorDouble) valorLinha).valor());
            } else {
                // Caso encontre lixo ou string numa coluna marcada como numérica
                throw new RuntimeException("Erro de Consistência: Na linha " + linhaAtual + ", a coluna '" + colName + 
                                           "' contém um valor não numérico: " + valorLinha);
            }
        }

        // 5. Delega o cálculo para a classe filha (Mean, Median, Std...)
        Valor resultado = calcular(numeros);

        // 6. Salva o resultado no amb
        amb.map(idVariavelDestino, resultado);
        
        System.out.println(">> " + this.getNomeEstatistica() + " de " + colName + ": " + resultado.toString());
        
        // 7. Retorna o amb modificado, conforme exigido pela interface
        return amb;
    }
    
    /**
     * Implementação do método checaTipo da interface Comando.
     */
    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) throws RuntimeException {
        // TODO: Implementar a verificação de tipos real.
        // 1. Fazer cast de 'amb' para AmbienteCompilacao (de expressions2)
        // 2. Verificar se idVariavelCsv é do tipo TipoDataFrame em 'amb'
        // 3. Verificar se 'nomeColuna' existe no schema desse TipoDataFrame
        // 4. Verificar se o tipo da coluna é numérico (INT ou FLOAT)
        // 5. Mapear 'idVariavelDestino' para o tipo de resultado (ex: Tipo.FLOAT) em 'amb'
        
        return true;
    }
}