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

    // Guarda o nome da função que foi chamada (ex: "soma")
    private Id nomeProcedimento;
    // Guarda os argumentos passados na chamada (ex: "2" e "w")
    private ListaExpressao parametrosReais;

    // Construtor: Apenas recebe os dados do parser e guarda nos atributos.
    public ChamadaProcedimento(Id nomeProcedimento, ListaExpressao parametrosReais) {
        this.nomeProcedimento = nomeProcedimento;
        this.parametrosReais = parametrosReais;
    }

    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb)
            throws IdentificadorNaoDeclaradoException, IdentificadorJaDeclaradoException,
            EntradaVaziaException, ErroTipoEntradaException {

        AmbienteExecucaoImperativa2 ambiente = (AmbienteExecucaoImperativa2) amb;
        DefProcedimento procedimento = ambiente.getProcedimento(nomeProcedimento);
        ambiente.incrementa();

        // 1. Preparação: Pega as listas de parâmetros e o corpo do procedimento
        ListaDeclaracaoParametro paramsFormais = procedimento.getParametrosFormais(); 
        ListaExpressao paramsReais = this.parametrosReais;
        
        // Guardamos o comando numa variável pois ele pode ser modificado abaixo (na passagem por nome)
        Comando comandoParaExecutar = procedimento.getComando();

        // 2. Loop Principal: Processa um parâmetro por vez (um da lista formal, um da real)
        while (paramsFormais != null && paramsReais != null && 
               paramsFormais.getHead() != null && paramsReais.getHead() != null) {
            
            DeclaracaoParametro decl = paramsFormais.getHead();
            Expressao real = paramsReais.getHead();

            // 3. Decisão: Verifica o tipo de passagem
            if (decl.getModo() == ModoParametro.POR_VALOR) {
                // Passagem por Valor: 
                // Calcula o resultado agora (ex: 2+2=4) e grava na memória local
                ambiente.map(decl.getId(), real.avaliar(ambiente)); 
            
            } else {
                // Passagem por Nome:
                // Não calcula e não grava na memória.
                // Apenas substitui o texto no código: onde tem a variável formal (ex: 'z'), coloca a real (ex: 'w')
                Id idFormal = decl.getId();
                Id idReal = (Id) real; 
                comandoParaExecutar = UtilSubstituicao.substituir(comandoParaExecutar, idFormal, idReal);
            }

            // Avança para o próximo par de parâmetros
            paramsFormais = (ListaDeclaracaoParametro) paramsFormais.getTail();
            paramsReais = (ListaExpressao) paramsReais.getTail();
        }

        try {
            // 4. Execução: Roda o código final (que pode ter sido modificado pela substituição)
            comandoParaExecutar.executar(ambiente);
        } catch (Exception e) {
             if (e instanceof RuntimeException) throw (RuntimeException) e;
             throw new RuntimeException(e);
        }

        ambiente.restaura();
        return ambiente;
    }

    // O método checaTipo sofreu poucas alterações lógicas, focadas apenas na compatibilidade de tipos.
    // --- VALIDAÇÃO DE TIPOS (ESTÁTICA) ---
    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb)
            throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException {
    
        Tipo tipoProcedimento = amb.get(this.nomeProcedimento);
        TipoProcedimento tipoParametrosReais = 
            new TipoProcedimento(parametrosReais.getTipos(amb));
        return tipoProcedimento.eIgual(tipoParametrosReais);
    }
}