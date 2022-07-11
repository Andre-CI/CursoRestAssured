package br.ce.andre.testes.refatorados.suite;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.andre.core.BaseTest;
import br.ce.andre.testes.refatorados.AutenticacaoTeste;
import br.ce.andre.testes.refatorados.ContasTeste;
import br.ce.andre.testes.refatorados.MovimentacaoTeste;
import br.ce.andre.testes.refatorados.SaldoTeste;
import io.restassured.RestAssured;

@RunWith(org.junit.runners.Suite.class)
@SuiteClasses({
	ContasTeste.class,
	MovimentacaoTeste.class,
	SaldoTeste.class,
	AutenticacaoTeste.class
})
public class suite extends BaseTest{
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
		RestAssured.get("/reset").then().statusCode(200);
	}

}
