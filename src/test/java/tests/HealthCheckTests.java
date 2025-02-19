package tests;

import core.clients.APIClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HealthCheckTests {
    private APIClient apiClient;

    //Инициализация Api клиента перед каждым тестом
    @BeforeEach
    public void setUp() {
        apiClient = new APIClient();
    }

    //Тест на метод ping
    @Test
    public void testPing() {
        //Выполняем GET запрос на /ping через APIClient
        Response response = apiClient.ping();
        assertThat(response.getStatusCode()).isEqualTo(201);
    }
}
