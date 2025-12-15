package li2.plp.imperative2.command;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative1.memory.EntradaVaziaException;
import li2.plp.imperative1.memory.ErroTipoEntradaException;
import li2.plp.imperative2.declaration.DeclaracaoParametro;
import li2.plp.imperative2.declaration.DefProcedimento;
import li2.plp.imperative2.declaration.ListaDeclaracaoParametro;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.imperative2.util.ModoParametro;
import li2.plp.imperative2.util.TipoProcedimento;
import li2.plp.imperative2.util.UtilSubstituicao;

public class ChamadaProcedimento implements Comando {

    private Id nomeProcedimento;
    private ListaExpressao parametrosReais;

    public ChamadaProcedimento(Id nomeProcedimento, ListaExpressao parametrosReais) {
        this.nomeProcedimento = nomeProcedimento;
        this.parametrosReais = parametrosReais;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb)
            throws IdentificadorNaoDeclaradoException, IdentificadorJaDeclaradoException,
            EntradaVaziaException, ErroTipoEntradaException {

		// Converte o ambiente genérico para o ambiente da Imperativa 2, que sabe lidar com procedimentos.
        AmbienteExecucaoImperativa2 ambiente = (AmbienteExecucaoImperativa2) amb;
        DefProcedimento procedimento = ambiente.getProcedimento(nomeProcedimento);

        ambiente.incrementa();

		//recupera duas listas
		//A lista da definição (ex: int x, name int y).
        ListaDeclaracaoParametro paramsFormais = procedimento.getParametrosFormais(); 
		//A lista do que foi passado agora (ex: 5, variavelK).
        ListaExpressao paramsReais = this.parametrosReais;
        
        Comando comandoParaExecutar = procedimento.getComando();

        // CORREÇÃO: Verifica se paramsFormais e paramsReais não são nulos antes de checkar getHead()
        while (paramsFormais != null && paramsReais != null && 
               paramsFormais.getHead() != null && paramsReais.getHead() != null) {
            
            DeclaracaoParametro decl = paramsFormais.getHead();
            Expressao real = paramsReais.getHead();

            if (decl.getModo() == ModoParametro.POR_VALOR) {
                // Passagem por Valor
			// real.avaliar(...): Calcula o valor da expressão agora. Se for 2 + 2, vira 4.
			// ambiente.map(...): Cria uma variável na memória local com o nome do parâmetro formal e o valor calculado.  
            ambiente.map(decl.getId(), real.avaliar(ambiente)); 
            } else {
                // Passagem por Nome
				Id idFormal = decl.getId();
                Id idReal = (Id) real; 
				// Chamamos o utilitário que percorre o código do procedimento e troca textualmente 
				// toda ocorrência de idFormal (ex: z) pelo idReal (ex: w).
				// Efeito: O código muda antes de rodar. Onde estava escrito z := z + 1 vira w := w + 1.
                comandoParaExecutar = UtilSubstituicao.substituir(comandoParaExecutar, idFormal, idReal);
            }

            // Avança as listas
            paramsFormais = (ListaDeclaracaoParametro) paramsFormais.getTail();
            paramsReais = (ListaExpressao) paramsReais.getTail();
        }

        try {
            comandoParaExecutar.executar(ambiente);
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
             throw new RuntimeException(e);
        }

        ambiente.restaura();
        return ambiente;
    }

    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb)
            throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException {
        
        // Validação básica existente
		// Se a função espera (int, boolean) e você passou (string, int), 
		// o eIgual retorna falso e o compilador dá erro antes mesmo de tentar rodar.
        Tipo tipoProcedimento = amb.get(this.nomeProcedimento);
        TipoProcedimento tipoParametrosReais = 
            new TipoProcedimento(parametrosReais.getTipos(amb));
            
        return tipoProcedimento.eIgual(tipoParametrosReais);
    }
}