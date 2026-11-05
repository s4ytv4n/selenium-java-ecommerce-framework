package io.github.s4ytv4n.models;

/** A store customer, used by both the UI signup form and the account API. */
public record User(
        String title,
        String name,
        String email,
        String password,
        String birthDay,
        String birthMonth,
        String birthYear,
        String firstName,
        String lastName,
        String company,
        String address1,
        String address2,
        String country,
        String state,
        String city,
        String zipcode,
        String mobileNumber
) {
    public User withEmail(String newEmail) {
        return new User(title, name, newEmail, password, birthDay, birthMonth, birthYear, firstName, lastName,
                company, address1, address2, country, state, city, zipcode, mobileNumber);
    }

    public User withCityAndCompany(String newCity, String newCompany) {
        return new User(title, name, email, password, birthDay, birthMonth, birthYear, firstName, lastName,
                newCompany, address1, address2, country, state, newCity, zipcode, mobileNumber);
    }
}
