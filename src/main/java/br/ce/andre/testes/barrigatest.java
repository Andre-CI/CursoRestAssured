package br.ce.andre.testes;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import org.junit.runners.MethodSorters;

import br.ce.andre.core.BaseTest;
import br.ce.andre.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.FilterableRequestSpecification;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class barrigatest extends BaseTest {
	//private String TOKEN;
	private static String CONTA_NAME = "Conta"+ System.nanoTime();
	private static Integer CONTA_ID;
	private static Integer MOVIMENTACAO_ID;
	
	@BeforeClass
	public static void login() 
	{
		Map<String, String> login = new HashMap<String, String>();
		login.put("email","andrepieri@bol.com.br");
		login.put("senha","10nagoya");
		
		String TOKEN = given()
				.body(login)
			.when()
				.post("/signin")
			.then()
				.statusCode(200)
				.extract().path("token")
			;
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
	}
	
	@Test
	public void t02_DeveIncluirUmaContaComSucesso() 
	{
		CONTA_ID = given()
			.body("{ \"nome\" : \""+CONTA_NAME+"\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t03_DeveAlterarContaComSucesso() 
	{
		given()
			.body("{ \"nome\" : \""+CONTA_NAME+" alterada\" }")
			.pathParam("id", CONTA_ID)
		.when()
			.put("/contas/{id}")
		.then()
			.statusCode(200)
			.body("nome", is(""+CONTA_NAME+" alterada"))
		;
	}
	
	@Test
	public void t04_naoDeveIncluirContaComMesmoNome() 
	{
		given()
			.body("{ \"nome\" : \""+CONTA_NAME+" alterada\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void t05_DeveIncluirMovimentacaoComSucesso() 
	{
		Movimentacao mov = getMovimentacao();	
		MOVIMENTACAO_ID = given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t06_DeveValidarCamposObrigatoriosDaMovimentacao() 
	{	
		given()
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"
					))
		;
	}
	
	@Test
	public void t07_naoDeveIncluirMovimentacaoFutura() 
	{
		Movimentacao mov = getMovimentacao();
		mov.setData_transacao(DataUtils.getDataDiferencaDias(2));
		
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
			.body("$", hasSize(1))
		;
	}
	
	@Test
	public void t08_naoDeveRemoverContaComMovimentacao() 
	{
		given()
			.pathParam("id", CONTA_ID)
		.when()
			.delete("/contas/{id}")
		.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void t09_deveVerificarSaldoDeContas() 
	{
		given()
		.when()
			.get("/saldo")
		.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("150.00"))
		;
	}
	
	@Test
	public void t10_DeveRemoverMovimentacao() 
	{
		given()
			.pathParam("id", MOVIMENTACAO_ID)
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	
	@Test
	public void t11_naoDeveAcessarAPISemToken() 
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
	private Movimentacao getMovimentacao() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(CONTA_ID);
		mov.setDescricao("descricao da movimentacao");
		mov.setEnvolvido("Envolvido");
		mov.setTipo("REC");
		mov.setData_transacao(DataUtils.getDataDiferencaDias(0));
		mov.setData_pagamento(DataUtils.getDataDiferencaDias(5));
		mov.setValor(150F);
		mov.setStatus(true);
		return mov;
	}
	

}
