package com.estudo.dscommerce.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class ProductControllerRA {

    private long existingProductId, nonExistingProductId;
    private String nameProduct;

    @BeforeEach
    public void setUp(){
        baseURI = "http://localhost:8080";

        existingProductId = 2L;
        nonExistingProductId = 100L;
        nameProduct = "PC Gamer";

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


}
