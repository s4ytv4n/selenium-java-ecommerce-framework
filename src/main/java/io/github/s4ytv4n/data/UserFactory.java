package io.github.s4ytv4n.data;

import io.github.s4ytv4n.models.User;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public final class UserFactory {

    /** Countries available in the store's signup dropdown. */
    private static final List<String> COUNTRIES = List.of(
            "India", "United States", "Canada", "Australia", "Israel", "New Zealand", "Singapore");

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private UserFactory() {
    }

    /** A unique, valid user. Timestamp + random suffix keeps emails unique across parallel threads and CI reruns. */
    public static User create() {
        String firstName = FAKER.name().firstName();
        String lastName = FAKER.name().lastName();
        LocalDate birth = LocalDate.now()
                .minusYears(FAKER.number().numberBetween(18, 70))
                .minusDays(FAKER.number().numberBetween(0, 365));

        return new User(
                FAKER.options().option("Mr", "Mrs"),
                firstName + " " + lastName,
                "qa.%d.%s@example.com".formatted(System.currentTimeMillis(), FAKER.regexify("[a-z0-9]{6}")),
                "Qa1!" + FAKER.regexify("[A-Za-z0-9]{10}"),
                String.valueOf(birth.getDayOfMonth()),
                String.valueOf(birth.getMonthValue()),
                String.valueOf(birth.getYear()),
                firstName,
                lastName,
                FAKER.company().name(),
                FAKER.address().streetAddress(),
                FAKER.address().secondaryAddress(),
                FAKER.options().nextElement(COUNTRIES),
                FAKER.address().state(),
                FAKER.address().city(),
                FAKER.regexify("[0-9]{5}"),
                FAKER.regexify("[0-9]{10}"));
    }
}
