package li2.plp.imperative2.declaration;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative2.util.ModoParametro; // <--- Import necessário

public class DeclaracaoParametro {

    private Id id;
    private Tipo tipo;
    private ModoParametro modo; // <--- Novo atributo para guardar o modo

    // Construtor atualizado para receber o modo
    public DeclaracaoParametro(Id id, Tipo tipo, ModoParametro modo) {
        this.id = id;
        this.tipo = tipo;
        this.modo = modo;
    }

    // Caso queira manter compatibilidade com testes antigos (assume Por Valor como default)
    public DeclaracaoParametro(Id id, Tipo tipo) {
        this.id = id;
        this.tipo = tipo;
        this.modo = ModoParametro.POR_VALOR;
    }

    public Id getId() {
        return id;
    }

    public Tipo getTipo() {
        return tipo;
    }

    // <--- Novo Getter necessário para a Lista e para a ChamadaProcedimento
    public ModoParametro getModo() {
        return modo;
    }

    public boolean checaTipo(AmbienteCompilacaoImperativa ambiente) {
        return tipo.eValido();
    }

    /**
     * Cria um mapeamento do identificador para o tipo do parametro desta
     * declaração no AmbienteCompilacaoImperativa2
     */
    public AmbienteCompilacaoImperativa elabora(
            AmbienteCompilacaoImperativa ambiente)
            throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
        // NOTA: O 'elabora' NÃO muda.
        // Mesmo sendo passagem por nome, dentro do corpo do procedimento,
        // a variável 'x' precisa ser reconhecida como do tipo 'int' (ou string, etc).
        // Portanto, ela continua sendo mapeada no ambiente de compilação da mesma forma.
        ambiente.map(id, tipo);
        return ambiente;
    }
}