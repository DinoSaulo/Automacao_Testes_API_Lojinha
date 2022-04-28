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



}
