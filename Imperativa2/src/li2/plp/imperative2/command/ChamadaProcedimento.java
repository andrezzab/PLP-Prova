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

    // --- O MÉTODO PRINCIPAL: ONDE A MÁGICA ACONTECE ---
    @Override
    public AmbienteExecucaoImperativa executar(AmbienteExecucaoImperativa amb)
            throws IdentificadorNaoDeclaradoException, IdentificadorJaDeclaradoException,
            EntradaVaziaException, ErroTipoEntradaException {

        // 1. PREPARAÇÃO
        // Faz o cast para o ambiente da Imperativa 2, que é capaz de entender procedimentos.
        AmbienteExecucaoImperativa2 ambiente = (AmbienteExecucaoImperativa2) amb;
        
        // Busca na memória a definição do procedimento. "Quem é 'soma'? O que ele faz?"
        DefProcedimento procedimento = ambiente.getProcedimento(nomeProcedimento);

        // Cria uma nova camada na pilha de memória (escopo).
        // Variáveis criadas aqui dentro morrem quando a função terminar.
        ambiente.incrementa();

        // 2. RECUPERAÇÃO DAS LISTAS
        // Pega a lista de parâmetros que a função espera (Formais: "int x, name int z")
        ListaDeclaracaoParametro paramsFormais = procedimento.getParametrosFormais(); 
        // Pega a lista de valores que enviamos agora (Reais: "2, w")
        ListaExpressao paramsReais = this.parametrosReais;
        
        // Pega o corpo do código da função (ex: "{ z := x + y }")
        // IMPORTANTE: Aqui precisaríamos clonar o comando para não estragar o original nas próximas chamadas.
        Comando comandoParaExecutar = procedimento.getComando();

        // 3. O LOOP DE PROCESSAMENTO (A PARTE DA QUESTÃO 5)
        // Enquanto houver parâmetros nas duas listas para processar...
        while (paramsFormais != null && paramsReais != null && 
               paramsFormais.getHead() != null && paramsReais.getHead() != null) {
            
            // Pega o par atual: (ex: Formal="x", Real="2") ou (Formal="z", Real="w")
            DeclaracaoParametro decl = paramsFormais.getHead();
            Expressao real = paramsReais.getHead();

            // AQUI É A DECISÃO: É POR VALOR OU POR NOME?
            if (decl.getModo() == ModoParametro.POR_VALOR) {
                // --- CAMINHO A: PASSAGEM POR VALOR ---
                // Passo 1: Calcula o valor da expressão AGORA (Eager evaluation). 2+2 vira 4.
                // Passo 2: Guarda na memória local o nome da variável formal com esse valor.
                // É como tirar uma xérox do valor e guardar na gaveta.
                ambiente.map(decl.getId(), real.avaliar(ambiente)); 
            
            } else {
                // --- CAMINHO B: PASSAGEM POR NOME (A Novidade) ---
                // Não calculamos nada agora.
                // Não guardamos na memória local.
                
                Id idFormal = decl.getId(); // O nome usado dentro da função (ex: "z")
                Id idReal = (Id) real;      // O nome da variável passada (ex: "w")
                
                // MÁGICA DA SUBSTITUIÇÃO:
                // Pegamos o código da função e trocamos o texto.
                // Onde estava escrito "z", apagamos e escrevemos "w".
                // O código muda antes de ser executado.
                comandoParaExecutar = UtilSubstituicao.substituir(comandoParaExecutar, idFormal, idReal);
            }

            // Passa para os próximos parâmetros da lista
            paramsFormais = (ListaDeclaracaoParametro) paramsFormais.getTail();
            paramsReais = (ListaExpressao) paramsReais.getTail();
        }

        // 4. EXECUÇÃO
        try {
            // Roda o comando.
            // Se foi por valor, ele usa as variáveis da memória local.
            // Se foi por nome, ele roda o código alterado (com "w" no lugar de "z").
            comandoParaExecutar.executar(ambiente);
        } catch (Exception e) {
            // Tratamento de erros obrigatório do Java
            if (e instanceof RuntimeException) throw (RuntimeException) e;
             throw new RuntimeException(e);
        }

        // 5. LIMPEZA
        // Destrói o escopo local. As variáveis criadas no .map() deixam de existir.
        ambiente.restaura();
        return ambiente;
    }

    // --- VALIDAÇÃO DE TIPOS (ESTÁTICA) ---
    @Override
    public boolean checaTipo(AmbienteCompilacaoImperativa amb)
            throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException {
        
        // Verifica se os tipos batem.
        // Ex: Se a função pede (int, string), eu não posso mandar (boolean, int).
        Tipo tipoProcedimento = amb.get(this.nomeProcedimento);
        TipoProcedimento tipoParametrosReais = 
            new TipoProcedimento(parametrosReais.getTipos(amb));
            
        // Retorna true se tudo estiver igual, false se tiver erro.
        return tipoProcedimento.eIgual(tipoParametrosReais);
    }
}