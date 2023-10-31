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

### Environment Variables

Before running the application, make sure to set the following environment variables:

- `EXCHANGE_API_LIVE_URL`: The URL for the live exchange rate API. His value should be `http://api.exchangerate.host/live`
- `EXCHANGE_API_ACCESS_KEY`: The access key to authenticate requests to the exchange rate API. You should take it from your created account on `https://exchangerate.host`. For testing purposes you can use the following for a limited time `37298218449d3b5907ee77be300dfe94`.
You can set them directly as JVM arguments, see step 3.

### Steps

1. **Clone and open the Repository**

git clone https://github.com/EduardHantig/bvc-challenge.git

cd bvc-challenge

2. **Build the Project**

mvn clean install

3. **Run the Application**

mvn spring-boot:run -Dspring-boot.run.jvmArguments='-DEXCHANGE_API_LIVE_URL=http://api.exchangerate.host/live -DEXCHANGE_API_ACCESS_KEY=your_access_key'

OR

mvn spring-boot:run -Dspring-boot.run.jvmArguments='-DEXCHANGE_API_LIVE_URL=http://api.exchangerate.host/live -DEXCHANGE_API_ACCESS_KEY=37298218449d3b5907ee77be300dfe94'

## API Documentation

After starting the application, you can visit the Swagger UI for detailed API documentation and testing:

http://localhost:8080/swagger-ui.html

## Contributing

If you'd like to contribute, please fork the repository and use a feature branch. Pull requests are warmly welcome.

## Licensing

The code in this project is licensed under BCV license.
