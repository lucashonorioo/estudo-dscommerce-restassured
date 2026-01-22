package com.estudo.dscommerce.controllers;

import com.estudo.dscommerce.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class ProductControllerRA {

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private long existingProductId, nonExistingProductId;
    private String nameProduct;

    private Map<String, Object> postProductInstance;

    @BeforeEach
    public void setUp(){
        baseURI = "http://localhost:8080";

        clientUsername = "maria@gmail.com";
        clientPassword = "123456";

        adminUsername = "alex@gmail.com";
        adminPassword = "123456";

        clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
        adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
        invalidToken = adminToken + "xpto";

        existingProductId = 2L;
        nonExistingProductId = 100L;
        nameProduct = "PC Gamer";

        postProductInstance = new HashMap<>();
        postProductInstance.put("name", "Tablet");
        postProductInstance.put("description", "tablet muito ruim");
        postProductInstance.put("price", 50.0);
        postProductInstance.put("imgUrl", "http://imagem.com");

        List<Map<String, Object>> categories = new ArrayList<>();

        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", 2);

        categories.add(category1);

        postProductInstance.put("categories", categories);

    }

    @Test
    public void findByIdShouldReturnProductWhenIdExisting(){

        given()
                .get("/products/{id}", existingProductId)
                .then()
                .statusCode(200)
                .body("id", is(2))
                .body("name", equalTo("Smart TV"))
                .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
                .body("price", is(2190.0F))
                .body("categories.id", hasItems(2,3))
                .body("categories.name", hasItems("Eletrônicos", "Computadores"));

    }

    @Test
    public void findAllShouldReturnPageProductsWhenProductNameIsEmpty() {

        given().get("/products?page=0")
                .then()
                .statusCode(200)
                .body("content.name", hasItems("Macbook Pro", "PC Gamer Tera"));

    }

    @Test
    public void findAllShouldReturnProductNameWhenProductNameExisting(){

        given().get("/products?name={nameProduct}", nameProduct)
                .then()
                .statusCode(200)
                .body("content.id[0]", is(4))
                .body("content.name[0]", equalTo("PC Gamer"));
    }

    @Test
    public void findAllShouldReturnPriceBiggerThanTwoThousand() {

        given().get("/products?page=0&size=50")
                .then()
                .statusCode(200)
                .body("content.findAll { it.price > 2000 }.name", hasItems("Smart TV", "PC Gamer Hera"));

    }

    @Test
    public void insertShouldReturnProductCreatedWhenAdminLogged(){

        JSONObject newProduct = new JSONObject(postProductInstance);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("name", equalTo("Tablet"))
                .body("price", is(50.0F))
                .body("categories.id", hasItems(2));

    }

}
