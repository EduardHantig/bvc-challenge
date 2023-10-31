# Exchange Service API

The Exchange Service API provides functionalities to fetch exchange rates from a third-party provider and offers operations to perform currency conversions.

## Features

- Fetch exchange rate for a given base currency to a target currency.
- Retrieve all exchange rates for a specified base currency.
- Convert a specific amount from a base currency to a target currency.
- Convert a specific amount from a base currency to a list of target currencies.

## Setup & Installation

### Prerequisites

- Java 17 or newer.
- Maven.

### Steps

1. **Clone the Repository**

git clone https://github.com/EduardHantig/bvc-challenge.git

2. **Build the Project**

mvn clean install

3. **Run the Application**

mvn spring-boot:run

The application should now be running on `http://localhost:8080/`.

## API Documentation

After starting the application, you can visit the Swagger UI for detailed API documentation and testing:

http://localhost:8080/swagger-ui.html

## Contributing

If you'd like to contribute, please fork the repository and use a feature branch. Pull requests are warmly welcome.

## Licensing

The code in this project is licensed under BCV license.
