import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

class CardDeliveryTest {

    private String generateDate(int daysFromNow) {
        return LocalDate.now().plusDays(daysFromNow).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Test
    void shouldSubmitFormSuccessfully() {
        open("http://localhost:9999"); // Открыть тестируемую страницу

        $("[data-test-id=city] input").setValue("Москва");
        String planningDate = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a", Keys.BACK_SPACE); // Очистка поля
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79012345678");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(15)); // Убедиться, что уведомление видно
        $("[data-test-id=notification] .notification__title")
                .shouldHave(exactText("Успешно!")); // Проверка заголовка
        $("[data-test-id=notification] .notification__content")
                .shouldHave(text("Встреча успешно забронирована на " + planningDate));
    }

    @Test
    void shouldShowErrorWhenCityIsInvalid() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("InvalidCity");
        String planningDate = generateDate(3);
        $("[data-test-id=date] input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79012345678");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        // Проверка ошибки для поля "Город"
        $("[data-test-id=city].input_invalid .input__sub")
                .shouldBe(visible)
                .shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldShowErrorWhenDateIsInvalid() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=date] input").doubleClick().sendKeys("01.01.2023");
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79012345678");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=date] .input__sub")
                .shouldBe(visible)
                .shouldHave(text("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldShowErrorWhenNameIsInvalid() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("Москва");
        String planningDate = generateDate(3);
        $("[data-test-id=date] input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=name] input").setValue("Ivan Ivanov");
        $("[data-test-id=phone] input").setValue("+79012345678");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=name].input_invalid .input__sub")
                .shouldBe(visible)
                .shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldShowErrorWhenPhoneIsInvalid() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("Москва");
        String planningDate = generateDate(3);
        $("[data-test-id=date] input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("12345"); // Невалидный телефон
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldBe(visible)
                .shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldShowErrorWhenCheckboxIsNotSelected() {
        open("http://localhost:9999");

        $("[data-test-id=city] input").setValue("Москва");
        String planningDate = generateDate(3);
        $("[data-test-id=date] input").doubleClick().sendKeys(planningDate);
        $("[data-test-id=name] input").setValue("Иван Иванов");
        $("[data-test-id=phone] input").setValue("+79012345678");
        $(".button").click(); // Не нажимаем на чекбокс

        $("[data-test-id=agreement].input_invalid")
                .shouldBe(visible);
    }
}
