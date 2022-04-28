package modulos.produto;

import dataFactory.ProdutoDataFactory;
import dataFactory.UsuarioDataFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojo.ComponentePojo;
import pojo.ProdutoPojo;
import pojo.UsuarioPojo;
// STATIC IMPORTS
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Teste de API Rest do módulo de Produtos")
public class ProdutoTest {

    private String token;

    @BeforeEach
    public void beforeEach(){
        // configurando os dados da API Rest da lojinha
        baseURI = "http://165.227.93.41";
        basePath = "/lojinha";

        UsuarioPojo usuario = UsuarioDataFactory.criarUsuarioAdministrador();

        // obter o token do usuário admin
        this.token = given()
                .contentType(ContentType.JSON)
                .body(usuario)
            .when()
                .post("/v2/login")
            .then()
                .extract()
                .path("data.token");
    }

    @Test
    @DisplayName("Validar que o valor do produto maior que 0.00 não é permitido")
    public void testValidarLimitesZeradoProibidoValorProduto(){

        ProdutoPojo produto = ProdutoDataFactory.criarProdutoComumComOvalorIgualA(0.00);

        // tentar inserir um produto com o valor 0.00 e o validar que a mensagem de erro foi apresentada
        // e o statusCode retornado foi 422
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(produto)
        .when()
            .post("/v2/produtos")
        .then()
            .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .statusCode(422);
    }

    @Test
    @DisplayName("Validar que o valor do produto maior que 7000.00 não é permitido")
    public void testValidarLimiteMaiorSeteMilProibidoValorProduto(){

        ProdutoPojo produto = ProdutoDataFactory.criarProdutoComumComOvalorIgualA(7000.01);

        // tentar inserir um produto com o valor 7000.01 e o validar que a mensagem de erro foi apresentada
        // e o statusCode retornado foi 422
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(produto)
        .when()
            .post("/v2/produtos")
        .then()
            .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .statusCode(422);
    }

    @Test
    @DisplayName("Validar que cadstro do produto e retorno obtido")
    public void testValidarCriarProduto(){

        ProdutoPojo produto = ProdutoDataFactory.criarProdutoComumComOvalorIgualA(4999.99);

        // tentar inserir um produto com o valor 7000.01 e o validar que a mensagem de erro foi apresentada
        // e o statusCode retornado foi 422
        given()
            .contentType(ContentType.JSON)
            .header("token", this.token)
            .body(produto)
        .when()
            .post("/v2/produtos")
        .then()
            .assertThat()
                .body("message", equalTo("Produto adicionado com sucesso"))
                .body("data.produtoId", greaterThanOrEqualTo(0))
                .body("data.produtoNome", equalTo("Playstation 5"))
                .body("data.produtoValor", equalTo(new Float(4999.99)))
                .body("data.produtoCores", equalTo(new ArrayList<>(Arrays.asList("preto", "branco"))))
                .body("data.produtoUrlMock", equalTo(""))
                .body("data.componentes.get(0).componenteNome", equalTo("Controle"))
                .body("data.componentes.get(0).componenteQuantidade", equalTo(1))
                .body("data.componentes.get(1).componenteNome", equalTo("Jogo Legal"))
                .body("data.componentes.get(1).componenteQuantidade", equalTo(2))
                .statusCode(201);

    }

    @Test
    @DisplayName("Validar que não é possível cadastrar um produto caso o usuário não esteja logado")
    public void testCriarProdutoComUsuarioDesologado(){

        ProdutoPojo produto = ProdutoDataFactory.criarProdutoComumComOvalorIgualA(4999.99);

        given()
            .contentType(ContentType.JSON)
            .body(produto)
        .when()
            .post("/v2/produtos")
        .then()
            .assertThat()
                .statusCode(401);

    }

    @Test
    @DisplayName("Validar que não é possível cadastrar um produto Com algum dos campos obrigatórios em setar preenchidos")
    public void testCriarProdutoIncompleto(){

        ProdutoPojo produto = ProdutoDataFactory.criarProdutoComumComOvalorIgualA(4999.99);
        produto.setProdutoNome(null);

        given()
            .contentType(ContentType.JSON)
            .header("token", this.token)
            .body(produto)
        .when()
            .post("/v2/produtos")
        .then()
            .assertThat()
                .body("error", equalTo("produtoNome, produtoValor e produtoCores são campos obrigatórios"))
                .statusCode(400);

    }

    @Test
    @DisplayName("Realizar uma busca simples de um produto pelo ID")
    public void testBuscarProdutoPeloId(){

        ProdutoPojo produto = ProdutoDataFactory.criarProdutoComumComOvalorIgualA(4999.99);

        // cadastrando o produto e salvando o ID
        int produtoId = given()
            .contentType(ContentType.JSON)
            .header("token", this.token)
            .body(produto)
        .when()
            .post("/v2/produtos")
        .then()
            .extract()
            .path("data.produtoId");

        // fazendo a consulta do produto pelo Id
        given()
            .contentType(ContentType.JSON)
            .header("token", this.token)
        .when()
            .get("/v2/produtos/" + produtoId)
        .then()
            .assertThat()
                .body("message", equalTo("Detalhando dados do produto"))
                .body("data.produtoId", greaterThanOrEqualTo(0))
                .body("data.produtoNome", equalTo("Playstation 5"))
                .body("data.produtoValor", equalTo(new Float(4999.99)))
                .body("data.produtoCores", equalTo(new ArrayList<>(Arrays.asList("preto", "branco"))))
                .body("data.produtoUrlMock", equalTo(""))
                .body("data.componentes.get(0).componenteNome", equalTo("Controle"))
                .body("data.componentes.get(0).componenteQuantidade", equalTo(1))
                .body("data.componentes.get(1).componenteNome", equalTo("Jogo Legal"))
                .body("data.componentes.get(1).componenteQuantidade", equalTo(2))
                .statusCode(200);

    }

    @Test
    @DisplayName("Realizar uma busca por um produto sem o possuir um usuário logado")
    public void testBuscarUmProdutoSemEstarLogado(){

        ProdutoPojo produto = ProdutoDataFactory.criarProdutoComumComOvalorIgualA(4999.99);

        // cadastrando o produto e salvando o ID
        int produtoId = given()
            .contentType(ContentType.JSON)
            .header("token", this.token)
            .body(produto)
        .when()
            .post("/v2/produtos")
        .then()
            .extract()
            .path("data.produtoId");

        // buscando pelo produto
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/v2/produtos/" + produtoId)
        .then()
            .assertThat()
                .statusCode(401);

    }

    @Test
    @DisplayName("Realizar uma busca por todos os produtos")
    public void testBuscarTodosOsProdutos(){

        given()
            .contentType(ContentType.JSON)
            .header("token", this.token)
        .when()
            .get("/v2/produtos")
        .then()
            .assertThat()
                .body("message", equalTo("Listagem de produtos realizada com sucesso"))
                .statusCode(200);

    }

    @Test
    @DisplayName("Realizar uma busca por todos os produtos aplicando filtragem pelo nome")
    public void testBuscarProdutosFiltrandoPeloNome(){

        given()
            .contentType(ContentType.JSON)
            .header("token", this.token)
        .when()
            .get("/v2/produtos?produtoNome=Play")
        .then()
            .assertThat()
                .body("message", equalTo("Listagem de produtos realizada com sucesso"))
                .statusCode(200);

    }

    @Test
    @DisplayName("Realizar uma busca por todos os produtos aplicando filtragem pela cor")
    public void testBuscarProdutosFiltrandoPelaCor(){

        given()
            .contentType(ContentType.JSON)
            .header("token", this.token)
        .when()
            .get("/v2/produtos?produtoCor=preto")
        .then()
            .assertThat()
                .body("message", equalTo("Listagem de produtos realizada com sucesso"))
                .statusCode(200);

    }

    @Test
    @DisplayName("Realizar uma busca por produtos sem o possuir um usuário logado")
    public void testBuscarProdutosSemEstarLogado(){

        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/v2/produtos")
        .then()
            .assertThat()
                .statusCode(401);

    }


}
