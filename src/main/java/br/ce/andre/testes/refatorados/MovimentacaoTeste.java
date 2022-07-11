package br.ce.andre.testes.refatorados;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import br.ce.andre.core.BaseTest;
import br.ce.andre.testes.Movimentacao;
import br.ce.andre.utils.BarrigaUtils;
import br.ce.andre.utils.DataUtils;

public class MovimentacaoTeste extends BaseTest {
	
	@Test
	public void DeveIncluirMovimentacaoComSucesso() 
	{
		Movimentacao mov = getMovimentacao();	
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(201)
		;
	}
	
	@Test
	public void DeveValidarCamposObrigatoriosDaMovimentacao() 
	{	
		given()
			.body("{}")
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
					"Data da Movimenta��o � obrigat�rio",
					"Data do pagamento � obrigat�rio",
					"Descri��o � obrigat�rio",
					"Interessado � obrigat�rio",
					"Valor � obrigat�rio",
					"Valor deve ser um n�mero",
					"Conta � obrigat�rio",
					"Situa��o � obrigat�rio"
					))
		;
	}
	
	@Test
	public void naoDeveIncluirMovimentacaoFutura() 
	{
		Movimentacao mov = getMovimentacao();
		mov.setData_transacao(DataUtils.getDataDiferencaDias(2));
		
		given()
			.body(mov)
		.when()
			.post("/transacoes")
		.then()
			.statusCode(400)
			.body("msg", hasItem("Data da Movimenta��o deve ser menor ou igual � data atual"))
			.body("$", hasSize(1))
		;
	}
	

	@Test
	public void naoDeveRemoverContaComMovimentacao() 
	{
		Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta com movimentacao");
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
	public void deveRemoverMovimentacao() 
	{
		given()
			.pathParam("id", BarrigaUtils.getIdMovimentacaoPeloNome("Movimentacao para exclusao"))
		.when()
			.delete("/transacoes/{id}")
		.then()
			.statusCode(204)
		;
	}
	

	
	private Movimentacao getMovimentacao() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
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
