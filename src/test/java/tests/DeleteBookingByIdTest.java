package tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Работа с данными")
@Feature("Удаление бронирования")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Метод для последовательного запуска тестов
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Один экземпляр класса для всех тестов
public class DeleteBookingByIdTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private int firstId;

    @BeforeEach
    public void setUp() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin","password123");

    }

    @Story("Получение списка бронирований")
    @Description("Получение списка, сохранение первого id в списке")
    @Test
    @Order(1)
    public void testGetBookingId() throws Exception {
        Response response = apiClient.getBooking();

        //Проверяем что статус код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {});

        //Проверяем, что тело ответа содержит Booking
        assertThat(bookings).isNotEmpty();

        //Получаем первое значение элемента в списке
        firstId = bookings.getFirst().getBookingid();
        assertThat(firstId).isGreaterThan(0);
    }

    @Story("Удаление бронирования с ранее полученным id")
    @Test
    @Order(2)
    public void testDeleteBookingById() throws Exception {
        Response response = apiClient.deleteBookingById(firstId);

        //Проверяем что статус код ответа равен 201
        assertThat(response.getStatusCode()).isEqualTo(201);
    }

    @Story("Проверка что бронирование удалено")
    @Description("Получение бронирования по id")
    @Test
    @Order(3)
    public void testCheckDeleteId() throws Exception {
        Response response = apiClient.getBookingById(firstId);

        //Проверяем что статус код ответа равен 404
        assertThat(response.getStatusCode()).isEqualTo(404);

    }
}
