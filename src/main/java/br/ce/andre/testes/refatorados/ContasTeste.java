package br.ce.andre.testes.refatorados;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import br.ce.andre.core.BaseTest;
import br.ce.andre.utils.BarrigaUtils;

public class ContasTeste extends BaseTest {
	
	@Test
	public void DeveIncluirUmaContaComSucesso() 
	{
		given()
			.body("{ \"nome\" : \"Conta nova\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.body("nome",is("Conta nova"))
		;
	}

	@Test
	public void deveAlterarContaComSucesso() 
	{
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para alterar");
		given()
			.body("{ \"nome\" : \"Conta alterada\" }")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is("Conta alterada"))
		;
	}
	
	@Test
	public void naoDeveIncluirContaComMesmoNome() 
	{
		given()
			.body("{ \"nome\" : \"Conta mesmo nome\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}	
}
