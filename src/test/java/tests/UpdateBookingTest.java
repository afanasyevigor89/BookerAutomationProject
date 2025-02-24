package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.*;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Работа с данными")
@Feature("Обновление бронирования")
public class UpdateBookingTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking; //Храним созданное бронирование
    private NewBooking newBooking;
    private NewUpdateBooking newUpdateBooking;
    private UpdateBooking updateBooking;
    private NewUpdatedFieldBooking newUpdateFieldBooking;
    private NewFieldBooking  newFieldBooking;
    private int createdId;

    @Description("Подготовка тела запроса для создания бронирования")
    @BeforeEach
    public void setUp() throws JsonProcessingException {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin","password123");

        newBooking = new NewBooking();
        newBooking.setFirstname("Jim");
        newBooking.setLastname("Jones");
        newBooking.setTotalprice(1200);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2025-06-12", "2025-06-17"));
        newBooking.setAdditionalneeds("Launch");

        updateBooking = new UpdateBooking();
        updateBooking.setFirstname("James");
        updateBooking.setLastname("LeBron");
        updateBooking.setTotalprice(1450);
        updateBooking.setDepositpaid(true);
        updateBooking.setBookingdates(new BookingDates("2025-06-06", "2025-06-12"));
        updateBooking.setAdditionalneeds("Launch");

        //Выполняем запрос к эндпойнту /booking через APIClient
        String requestBody = objectMapper.writeValueAsString(newBooking);
        Response response = apiClient.createBooking(requestBody);

        //Проверяем, что статус-код ответа 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //Десериализуем тело ответа в объект Booking
        createdBooking = objectMapper.readValue(response.getBody().asString(), CreatedBooking.class);

        createdId = createdBooking.getBookingid();
    }

    @Story("Обновление данных ранее созданного бронирования")
    @Description("Обновление данных бронирования методом PUT")
    @Test
    public void testUpdateBooking() throws Exception {
        objectMapper = new ObjectMapper();

        String requestBody = objectMapper.writeValueAsString(updateBooking);
        Response response = apiClient.updateBooking(createdId, requestBody);

        //Проверяем, что статус-код ответа 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //Десериализуем тело ответа в объект Booking
        newUpdateBooking = objectMapper.readValue(response.getBody().asString(), NewUpdateBooking.class);

        //Проверяем что тело ответа содержит объект нового бронирования
        assertThat(newUpdateBooking).isNotNull();
        assertEquals(updateBooking.getFirstname(), newUpdateBooking.getFirstname());
        assertEquals(updateBooking.getLastname(), newUpdateBooking.getLastname());
        assertEquals(updateBooking.getTotalprice(), newUpdateBooking.getTotalprice());
        assertEquals(updateBooking.getBookingdates().getCheckin(), newUpdateBooking.getBookingdates().getCheckin());
        assertEquals(updateBooking.getBookingdates().getCheckout(), newUpdateBooking.getBookingdates().getCheckout());
        assertEquals(updateBooking.getAdditionalneeds(), newUpdateBooking.getAdditionalneeds());
    }


    @Story("Частичное обновление данных ранее созданного бронирования")
    @Description("Обновление данных бронирования методом PATCH")
    @Test
    public void testUpdateFieldBooking() throws Exception {
        objectMapper = new ObjectMapper();
        newFieldBooking = new NewFieldBooking();
        newFieldBooking.setFirstname("Tim");
        newFieldBooking.setLastname("Dankon");

        String requestBody = objectMapper.writeValueAsString(newFieldBooking);
        Response response = apiClient.updateFieldBooking(createdId, requestBody);

        //Проверяем, что статус-код ответа 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        //Десериализуем тело ответа в объект NewUpdatedFieldBooking
        newUpdateFieldBooking = objectMapper.readValue(response.getBody().asString(), NewUpdatedFieldBooking.class);

        //Проверяем что тело ответа содержит объект нового бронирования
        assertThat(newUpdateFieldBooking).isNotNull();
        assertEquals(newFieldBooking.getFirstname(), newUpdateFieldBooking.getFirstname());
        assertEquals(newFieldBooking.getLastname(), newUpdateFieldBooking.getLastname());
        assertEquals(newBooking.getTotalprice(), createdBooking.getBooking().getTotalprice());
        assertEquals(newBooking.getBookingdates().getCheckin(), createdBooking.getBooking().getBookingdates().getCheckin());
        assertEquals(newBooking.getBookingdates().getCheckout(), createdBooking.getBooking().getBookingdates().getCheckout());
        assertEquals(newBooking.getAdditionalneeds(), createdBooking.getBooking().getAdditionalneeds());

    }

    @Description("Удаление ранее созданного бронирования")
    @AfterEach
    public void tearDown() {
        Response response = apiClient.deleteBookingById(createdId);

        //Проверяем что статус код ответа равен 404
        assertThat(response.getStatusCode()).isEqualTo(201);
    }
}
