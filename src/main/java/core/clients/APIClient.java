package core.clients;

import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class APIClient {

    private final String baseUrl;
    private String token;


    public APIClient() {
        this.baseUrl = determineBaseUrl();
    }

    //Определение базового URL на основе файла конфигурации
    private String determineBaseUrl() {
        String environment = System.getProperty("env", "test");
        String configFileName = "application-" + environment + ".properties";

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found: " + configFileName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load configuration file: " + configFileName, e);
        }

        return properties.getProperty("baseUrl");
    }

    //Метод для получения токена
    public void createToken(String username, String password) {
        //Тело запроса для получения токена
        String requestBody = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

        Response response = getRequestSpec()
                .body(requestBody)
                .when()
                .post(ApiEndpoints.AUTH.getPath())
                .then()
                .statusCode(200)
                .extract()
                .response();

        //Извлекаем токен из ответа
        token = response.jsonPath().getString("token");
    }

    //Фильтр для добавления токена в заголовок

    private Filter addAuthTokenFilter() {
        return (FilterableRequestSpecification requestSpec,
                FilterableResponseSpecification responseSpec, FilterContext ctx) -> {
            if (token != null) {
                requestSpec.header("Cookie", "token=" + token);
            }
            return ctx.next(requestSpec, responseSpec); // Продолжает выполнение
        };
    }

    //Настройка базовых параметров HTTP-запросов
    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .filter(addAuthTokenFilter());
    }

    //GET запрос на эндпойнт /ping
    public Response ping() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.PING.getPath())
                .then()
                .statusCode(201)
                .extract()
                .response();
    }

    public Response getBooking() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING.getPath())
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
    }

    public Response getBookingById(int id) {
        return getRequestSpec()
                .when()
                .pathParam("id", id)
                .get(ApiEndpoints.BOOKING.getPath() + "/{id}")
                .then()
                .log().all()
                //.statusCode(200)
                .extract()
                .response();
    }

    public Response deleteBookingById(int id) {
        return getRequestSpec()
                .pathParam("id", id)
                .delete(ApiEndpoints.BOOKING.getPath() + "/{id}")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .response();
    }

    public Response createBooking(String newBooking) {
        return  getRequestSpec()
                .body(newBooking)
                .log().all()
                .when()
                .post(ApiEndpoints.BOOKING.getPath())
                .then()
                .extract()
                .response();
    }

    public Response updateBooking(int id, String updateBooking) {
        return  getRequestSpec()
                .pathParam("id", id)
                .body(updateBooking)
                .log().all()
                .when()
                .put(ApiEndpoints.BOOKING.getPath() + "/{id}")
                .then()
                .extract()
                .response();
    }

    public Response updateFieldBooking(int id, String newUpdateFieldBooking) {
        return  getRequestSpec()
                .pathParam("id", id)
                .body(newUpdateFieldBooking)
                .log().all()
                .when()
                .patch(ApiEndpoints.BOOKING.getPath() + "/{id}")
                .then()
                .extract()
                .response();
    }

}
