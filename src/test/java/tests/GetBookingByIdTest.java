package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingId;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingByIdTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;

    //Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setUp() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testBookingById() throws Exception {
        Response response = apiClient.getBookingById();

        //Проверяем статус-код ответа
        assertThat(response.getStatusCode()).isEqualTo(200);

        String responseBody = response.getBody().asString();
        BookingId bookingId = objectMapper.readValue(responseBody, BookingId.class);

        //Проверяем что ответ не пустой
        assertThat(bookingId).isNotNull();

        //Проверяем соответствия значений
        assertThat(bookingId.firstname).isEqualTo("John");
        assertThat(bookingId.lastname).isEqualTo("Smith");
        assertThat(bookingId.totalprice).isEqualTo(111);
        assertThat(bookingId.depositpaid).isTrue();
        assertThat(bookingId.bookingdates.checkin).isEqualTo("2018-01-01");
        assertThat(bookingId.bookingdates.checkout).isEqualTo("2019-01-01");
        assertThat(bookingId.additionalneeds).isEqualTo("Breakfast");

    }
}
