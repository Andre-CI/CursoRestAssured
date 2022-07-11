package br.ce.andre.testes.refatorados;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import br.ce.andre.core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;

public class AutenticacaoTeste extends BaseTest  {
	
	@Test
	public void naoDeveAcessarAPISemToken() 
	{
		FilterableRequestSpecification req= (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
		;
	}

}
