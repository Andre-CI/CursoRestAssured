package br.ce.andre.core;

import io.restassured.http.ContentType;

public interface Constantes {
	String APP_BASE_URL ="https://barrigarest.wcaquino.me";
	Integer APP_PORT = 443;//Para http ->80
	ContentType APP_CONTENT_TYPE = ContentType.JSON;
	
	Long MAX_TIMEOUT = 10000L;
}
