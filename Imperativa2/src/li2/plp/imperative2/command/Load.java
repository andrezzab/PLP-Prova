package li2.plp.imperative2.command;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoDataFrame;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.util.ParserCSV;
import li2.plp.expressions2.expression.ValorDataFrame;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.EmptyStackException;

public class Load implements Comando {

    private Expressao pathExpressao;
    private Id idDataFrame; 

    public Load(Expressao path, Id id) {
        this.pathExpressao = path;
        this.idDataFrame = id;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb) {
        // ... (Seu código executar permanece igual, sem alterações necessárias aqui) ...
        try {
            Valor val = this.pathExpressao.avaliar(amb);
            if (!(val instanceof ValorString)) throw new RuntimeException("Caminho inválido");
            String path = ((ValorString) val).valor();

            if (!path.toLowerCase().endsWith(".csv")) throw new RuntimeException("Arquivo deve ser .csv");
            File arquivo = new File(path);
            if (!arquivo.exists()) throw new RuntimeException("Arquivo não encontrado: " + path);

            ParserCSV parser = new ParserCSV(path); 
            ValorDataFrame dataFrame = new ValorDataFrame(parser.getSchema(), parser.getRows());
            
            if (this.idDataFrame != null) {
                amb.map(this.idDataFrame, dataFrame);
                System.out.println(">> Dataset carregado em '" + this.idDataFrame.getIdName() + "' (" + parser.getRows().size() + " linhas)");
            }

        } catch (RuntimeException re) { throw re; 
        } catch (Exception e) { throw new RuntimeException("Erro ao carregar CSV: " + e.toString()); }
        return amb;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb) {
        if (!pathExpressao.checaTipo(amb)) return false;
        if (!pathExpressao.getTipo(amb).eString()) return false;

        // Se tiver ID para salvar
        if (idDataFrame != null) {
            Map<String, Tipo> schema = null;

            // Caso 1: Caminho Estático (String literal) -> Conseguimos inferir Schema
            if (pathExpressao instanceof ValorString) {
                String path = ((ValorString) pathExpressao).valor();
                try {
                    ParserCSV parser = new ParserCSV(path);
                    schema = parser.getSchema();
                } catch (Exception e) {
                    System.out.println("Aviso de Compilação: " + e.toString());
                    // Mesmo com erro de leitura, vamos tentar registrar para não travar o parser
                }
            } 
            // Caso 2: Caminho Dinâmico (Variável) -> Schema desconhecido (null)
            else {
                // schema continua null
            }

            // REGISTRA NO AMBIENTE (Com Schema ou Null)
            // Isso impede o erro "VariavelNaoDeclarada" nos comandos seguintes
            try {
                TipoDataFrame tipoDf = new TipoDataFrame(schema);
                amb.map(idDataFrame, tipoDf);
            } catch (Exception e) {
                System.out.println("Erro ao registrar variável: " + e.toString());
                return false;
            }
        }
        
        return true;
    }
}