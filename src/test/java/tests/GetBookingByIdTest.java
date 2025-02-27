package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingId;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Работа с данными")
@Feature("Получение данных бронирования по id")
public class GetBookingByIdTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;

    @Description ("Инициализация API клиента перед каждым тестом")
    @BeforeEach
    public void setUp() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    @Story("Получение банных бронирования")
    @Description("Получение банных бронирования по id")
    @Test
    public void testBookingById() throws Exception {
        Response response = apiClient.getBookingById(1563);

        //Проверяем статус-код ответа
        assertThat(response.getStatusCode()).isEqualTo(200);

        String responseBody = response.getBody().asString();
        BookingId bookingId = objectMapper.readValue(responseBody, BookingId.class);

        //Проверяем что ответ не пустой
        assertThat(bookingId).isNotNull();

        //Проверяем соответствия значений
        assertThat(bookingId.firstname).isEqualTo("James");
        assertThat(bookingId.lastname).isEqualTo("LeBron");
        assertThat(bookingId.totalprice).isEqualTo(1450);
        assertThat(bookingId.depositpaid).isTrue();
        assertThat(bookingId.bookingdates.checkin).isEqualTo("2025-06-06");
        assertThat(bookingId.bookingdates.checkout).isEqualTo("2025-06-12");
        assertThat(bookingId.additionalneeds).isEqualTo("Launch");

    }
}
