package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Работа с данными")
@Feature("Получение информации о бронированиях")
public class GetBookingTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;

    //Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setUp() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    @Story("Получение списка бронирований")
    @Description("Получение данных списка бронирований на странице 1")
    @Test
    public void testGetBooking() throws Exception {
        step("Выполняется запрос к эндпойту /booking через APIClient");
        Response response = apiClient.getBooking();

        step("Проверяем что статус код ответа равен 200");
        assertThat(response.getStatusCode()).isEqualTo(200);

        step("Десериализуем тело ответа в список объектов Booking");
        String responseBody = response.getBody().asString();
        List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {});

        step("Проверяем, что тело ответа содержит Booking");
        assertThat(bookings).isNotEmpty();

        step("Проверяем, что каждый объект содержит значение bookingid");
        for (Booking booking : bookings) {
            assertThat(booking.getBookingid()).isGreaterThan(0);
        }
    }
}
