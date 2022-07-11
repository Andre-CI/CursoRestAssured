package br.ce.andre.testes.refatorados;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.ce.andre.core.BaseTest;
import br.ce.andre.utils.BarrigaUtils;

public class SaldoTeste extends BaseTest{
	@Test
	public void deveVerificarSaldoDeContas() 
	{
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para saldo");
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
		;
	}

}
