package li2.plp.imperative2.command;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.ValorBooleano;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.util.TipoDataFrame;
import li2.plp.expressions2.expression.ValorDataFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Filter implements Comando {

    private Id idDataFrameOriginal;
    private Id idDataFrameNovo;
    private Expressao condicao;

    public Filter(Id idOrigem, Id idDestino, Expressao condicao) {
        this.idDataFrameOriginal = idOrigem;
        this.idDataFrameNovo = idDestino;
        this.condicao = condicao;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) {
        Valor val = amb.get(idDataFrameOriginal);
        if (!(val instanceof ValorDataFrame)) throw new RuntimeException("Não é DataFrame.");
        ValorDataFrame dfOriginal = (ValorDataFrame) val;
        
        List<Map<String, Valor>> linhasFiltradas = new ArrayList<>();
        for (Map<String, Valor> linha : dfOriginal.getRows()) {
            amb.incrementa();
            for (Map.Entry<String, Valor> entry : linha.entrySet()) {
                amb.map(new Id(entry.getKey()), entry.getValue());
            }
            try {
                Valor resultado = condicao.avaliar(amb);
                if (!(resultado instanceof ValorBooleano)) throw new RuntimeException("Filtro deve ser booleano");
                if (((ValorBooleano) resultado).valor()) linhasFiltradas.add(linha);
            } finally {
                amb.restaura();
            }
        }
        ValorDataFrame dfNovo = new ValorDataFrame(dfOriginal.getSchema(), linhasFiltradas);
        amb.map(idDataFrameNovo, dfNovo);
        return amb;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) {
        Tipo tipoOrigem;
        try {
            tipoOrigem = amb.get(idDataFrameOriginal);
        } catch (Exception e) { return false; }

        if (tipoOrigem == null || !tipoOrigem.eDataFrame()) return false;
        
        TipoDataFrame tdf = (TipoDataFrame) tipoOrigem;
        amb.incrementa();

        // Se tiver schema, injeta variáveis para checar a expressão.
        // Se for NULL (dinâmico), não injetamos nada, e a checagem da expressão 
        // vai depender se ela usa variáveis que não existem no escopo global.
        // Isso é uma limitação do carregamento dinâmico.
        if (tdf.getSchema() != null) {
            for (Map.Entry<String, Tipo> entry : tdf.getSchema().entrySet()) {
                amb.map(new Id(entry.getKey()), entry.getValue());
            }
            
            if (!condicao.checaTipo(amb)) {
                amb.restaura();
                return false;
            }
            
            if (!condicao.getTipo(amb).eBooleano()) {
                amb.restaura();
                return false; 
            }
        } else {
             // Schema desconhecido: Confiamos cegamente ou pulamos a validação fina da expressão
             // Para permitir compilação, vamos assumir ok, mas cuidado com variáveis não declaradas na expressão.
        }

        amb.restaura();
        
        // Mapeia o novo DF (herdando o schema, mesmo que seja null)
        amb.map(idDataFrameNovo, tdf);

        return true;
    }
}