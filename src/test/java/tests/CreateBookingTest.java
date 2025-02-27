package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.qameta.allure.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Работа с данными")
@Feature("Создание нового бронирования")
public class CreateBookingTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking; //Храним созданное бронирование
    private NewBooking newBooking; //Новый объект для создания бронирования

    @Description("Подготовка тела запроса")
    @BeforeEach
    public void setUp() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        newBooking = new NewBooking();
        newBooking.setFirstname("Jim");
        newBooking.setLastname("Jones");
        newBooking.setTotalprice(1200);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2025-06-12", "2025-06-17"));
        newBooking.setAdditionalneeds("Launch");
    }

    @Story("Создание нового бронирования")
    @Description("Создание бронирования с подготовленными данными")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void testCreateBooking() throws Exception {
        //Выполняем запрос к эндпойнту /booking через APIClient
        objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response response = apiClient.createBooking(requestBody);

        //Проверяем, что статус-код ответа 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //Десериализуем тело ответа в объект Booking
        createdBooking = objectMapper.readValue(response.getBody().asString(), CreatedBooking.class);

        //Проверяем что тело ответа содержит объект нового бронирования
        assertThat(createdBooking).isNotNull();
        assertEquals(newBooking.getFirstname(), createdBooking.getBooking().getFirstname());
        assertEquals(newBooking.getLastname(), createdBooking.getBooking().getLastname());
        assertEquals(newBooking.getTotalprice(), createdBooking.getBooking().getTotalprice());
        assertEquals(newBooking.getBookingdates().getCheckin(), createdBooking.getBooking().getBookingdates().getCheckin());
        assertEquals(newBooking.getBookingdates().getCheckout(), createdBooking.getBooking().getBookingdates().getCheckout());
        assertEquals(newBooking.getAdditionalneeds(), createdBooking.getBooking().getAdditionalneeds());
    }

    @Description("Удаление ранее созданного бронирования")
    @AfterEach
    public void tearDown() {
        //Удаляем созданное бронирование
        apiClient.createToken("admin", "password123");
        apiClient.deleteBookingById(createdBooking.getBookingid());

        //Проверяем, что бронирование успешно удалено
        assertThat(apiClient.getBookingById(createdBooking.getBookingid()).getStatusCode()).isEqualTo(404);
    }
}
