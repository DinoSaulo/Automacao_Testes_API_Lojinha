package dataFactory;

import pojo.ComponentePojo;
import pojo.ProdutoPojo;

import java.util.ArrayList;
import java.util.List;

public class ProdutoDataFactory {

    public static ProdutoPojo criarProdutoComumComOvalorIgualA(double valor){
        ProdutoPojo produto = new ProdutoPojo();
        produto.setProdutoNome("Playstation 5");
        produto.setProdutoValor(valor);

        List<String> cores = new ArrayList<>();
        cores.add("preto");
        cores.add("branco");

        produto.setProdutoCores(cores);
        produto.setProdutoUrlMock("");

        List<ComponentePojo> componentes = new ArrayList<>();

        ComponentePojo componente_1 = new ComponentePojo();
        componente_1.setComponenteNome("Controle");
        componente_1.setComponenteQuantidade(1);

        ComponentePojo componente_2 = new ComponentePojo();
        componente_2.setComponenteNome("Jogo Legal");
        componente_2.setComponenteQuantidade(2);

        componentes.add(componente_1);
        componentes.add(componente_2);

        produto.setComponentes(componentes);

        return produto;
    }
}
