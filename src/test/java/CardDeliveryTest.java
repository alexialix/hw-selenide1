
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;


import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

class CardDeliveryTest {

    private String generateDate(int addDays) {
        return LocalDate.now().plusDays(addDays).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Test
    void shouldSubmitFormSuccessfully() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("Москва");
        String planningDate = generateDate(3);
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79012345678");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__title")
                .shouldHave(exactText("Успешно!"));
        $("[data-test-id=notification] .notification__content")
                .shouldHave(text("Встреча успешно забронирована на " + planningDate));
    }

    @Test
    void shouldShowErrorWhenCityIsInvalid() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("InvalidCity");
        String planningDate = generateDate(3);
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79012345678");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=city].input_invalid .input__sub")
                .shouldBe(visible)
                .shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldShowErrorWhenDateIsInvalid() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue("01.01.2023");
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79012345678");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=date] .input__sub")
                .shouldBe(visible)
                .shouldHave(text("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldShowErrorWhenNameIsInvalid() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("Москва");
        String planningDate = generateDate(3);
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Ivan Ivanov");
        $("[data-test-id=phone] input").setValue("+79012345678");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=name].input_invalid .input__sub")
                .shouldBe(visible)
                .shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldShowErrorWhenPhoneIsInvalid() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("Москва");
        String planningDate = generateDate(3);
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("12345");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldBe(visible)
                .shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldShowErrorWhenCheckboxIsNotSelected() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("Москва");
        String planningDate = generateDate(3);
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79012345678");
        $("button.button").click();

        $("[data-test-id=agreement].input_invalid")
                .shouldBe(visible);
    }

    // Задание 2
    @Test
    void shouldSubmitFormWithCityAndDate() {
        open("http://localhost:9999");
//Город
        SelenideElement cityInput = $("[data-test-id='city'] input");
        cityInput.setValue("Мо");
        $$("[class*='menu-item']")
                .findBy(text("Москва"))
                .click();
//Календарь
        SelenideElement calendarButton = $x("//button[contains(@class, 'icon-button') and .//span[contains(@class, 'icon_name_calendar')]]");

        calendarButton.click();
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(7);
        int targetMonthValue = targetDate.getMonthValue();
        int targetDay = targetDate.getDayOfMonth();

        HashMap<String, Integer> months = new HashMap<>() {{
            put("январь", 1);
            put("февраль", 2);
            put("март", 3);
            put("апрель", 4);
            put("май", 5);
            put("июнь", 6);
            put("июль", 7);
            put("август", 8);
            put("сентябрь", 9);
            put("октябрь", 10);
            put("ноябрь", 11);
            put("декабрь", 12);
        }};

        SelenideElement calendarHeader = $(".calendar__name");

        while (true) {
            String currentMonth = calendarHeader.getText().split(" ")[0].toLowerCase();
            if (months.get(currentMonth) == targetMonthValue) {
                break;
            }
            $("div.calendar__arrow[data-step='1']").click();

        }

        SelenideElement targetDayElement = $$("td.calendar__day")
                .find(text(String.valueOf(targetDay)));
        targetDayElement.click();

        String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id='date'] input").shouldHave(value(formattedDate));
    }
}