package fakestore;

import fakestore.model.Product;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ProductCatalogTest extends BaseTest {

    @Test
    public void getAllProductsTest() {

        Response response =
                given()
                .when()
                        .get("/products")
                .then()
                        .statusCode(200)
                        .extract()
                        .response();

        List<Map<String, Object>> products = response.jsonPath().getList("$");

        Assert.assertTrue(products.size() >= 10, "Product count is less than 10");

        for (Map<String, Object> product : products) {
            System.out.println(
                    "Name: " + product.get("title") +
                            " | Price: " + product.get("price")
            );
        }
    }

    private void validateProductById(int id) {
        given()
        .when()
                .get("/products/" + id)
        .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("category", notNullValue())
                .body("price", notNullValue());
    }

    @Test
    @Parameters({"productId"})
    public void getProductByIdTest(int id) {

        validateProductById(id);

    }

    @Test
    public void addNewProductTest() {

        Product product = new Product(
                "New Product",
                500 ,
                "Some New Product",
                "https://marji-random.cc",
                "fashion"
        );

        Response response =
                given()
                        .header("Content-Type", "application/json")
                        .body(product)
                .when()
                        .post("/products")
                .then()
                        .statusCode(201)
                        .body("title", equalTo(product.getTitle()))
                        .body("price", equalTo( (int)product.getPrice()))
                        .body("category", equalTo(product.getCategory()))
                        .extract()
                        .response();

        int createdProductId = response.jsonPath().getInt("id");

        System.out.println("Created Product ID: " + createdProductId);
        System.out.println(response.asPrettyString());

    }
}
