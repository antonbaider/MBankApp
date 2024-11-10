# 🏦 MBank App API
[![Maven CI with Dependency Submission](https://github.com/antonbaider/MBankApp/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/antonbaider/MBankApp/actions/workflows/maven.yml)
[![Java Docker Image CI](https://github.com/antonbaider/MBankApp/actions/workflows/docker-image.yml/badge.svg?branch=main)](https://github.com/antonbaider/MBankApp/actions/workflows/docker-image.yml)

[![SonarCloud](https://github.com/antonbaider/MBankApp/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/antonbaider/MBankApp/actions/workflows/build.yml)

MBank App is a Java-based banking application designed to manage user transactions, account information, and provide seamless integration for modern transaction notifications via email. It features robust API documentation, transaction validation, and comprehensive security measures.

🌐 FrontEnd - https://github.com/antonbaider/uMBank.git

---

# 🔍 Overview

The MBank API offers:
- **User Management**: Register, login, view profile, and update information.
- **Account Management**: Open, close, and list accounts.
- **Transaction Handling**: Transfer funds between accounts and cards.
- **Admin Operations**: Elevated access for admin functionalities.

=======

## 🚀 Features

- 📄 **API Documentation**: Uses Swagger for detailed API documentation.
- 💳 **Unique Card Generation**: Generates unique card numbers with expiration dates.
- 🔒 **Transaction Validation**: Validates transactions for amount limits and security compliance.
- 📧 **Configurable Notifications**: Sends branded generative PDFs and formatted email notifications after each transaction.
- 🔐 **Data Security**: Masks sensitive information such as passwords, SSNs, and card numbers.
- ⚡ **Caching**: Implements caching to improve performance.
- 💾 **Database**: MariaDB for storing user and transaction data.

---

## 💻 Technologies

- ☕ **Java**
- 🧩 **Spring Boot** (REST, JPA, Security, Cache)
- 💽 **MariaDB**
- 📜 **Swagger** (API Documentation)
- 🔄 **MapStruct** (for mapping DTOs)
- ✉️ **Mail API** (for email notifications)
- 🔑 **JWT** (JSON Web Tokens) for authentication

---

## 🛠 Environment Variables

To configure MBank App, set the following environment variables:

### 🔧 Application Settings

- **`SPRING_APPLICATION_NAME`**: Sets the application name for Spring Boot. This helps identify the app in logs and configurations.

### 🔐 JWT Authentication

- **`JWT_SECRET`**: Secret key used to sign JWT tokens for securing API endpoints.
- **`JWT_EXPIRATION`**: Expiration time for the JWT tokens (in milliseconds). Adjust based on session requirements.

### 💾 MariaDB Database Connection Settings

- **`SPRING_DATASOURCE_URL`**: JDBC URL for the MariaDB database connection.
- **`SPRING_DATASOURCE_USERNAME`**: Username for the MariaDB database.
- **`SPRING_DATASOURCE_PASSWORD`**: Password for the MariaDB database.
- **`SPRING_DATASOURCE_DRIVER_CLASS_NAME`**: Driver class for MariaDB (typically `org.mariadb.jdbc.Driver`).

### 🔄 Hibernate Configuration

- **`SPRING_JPA_HIBERNATE_DDL_AUTO`**: Controls the database schema generation strategy (e.g., `update`, `create`, `validate`).

### 💳 Bank Account and Card Generation

- **`CARD_NUMBER_MAX_ATTEMPTS`**: Maximum number of attempts to generate a unique card number. Adjust based on expected uniqueness.

### 📧 Gmail SMTP Server Settings for Email Notifications

- **`SPRING_MAIL_HOST`**: SMTP server host for sending emails (e.g., `smtp.gmail.com` for Gmail).
- **`SPRING_MAIL_PORT`**: SMTP server port (e.g., `587` for Gmail).
- **`SPRING_MAIL_USERNAME`**: SMTP username for email (usually the email address).
- **`SPRING_MAIL_PASSWORD`**: SMTP password for email.
- **`SPRING_MAIL_PROPERTIES_MAIL_SMTP_SSL_TRUST`**: Specifies SSL trust for SMTP (e.g., `smtp.gmail.com` for Gmail).
- **`SPRING_MAIL_PROPERTIES_MAIL_SMTP_SSL`**: Enables or disables STARTTLS encryption for email security.

### 🖼 Email and PDF Settings

- **`PDF_LOGO_PATH`**: Path to the logo image used in generated PDF documents.
- **`PDF_FOOTER_IMAGE_PATH`**: Path to the footer image used in generated PDF documents.
- **`RECIPIENT_EMAIL`**: Default recipient email address for notifications.
- **`EMAIL_LOGO_PATH`**: Path to the logo image used in email notifications.

---

## 🌐 Base URL

All endpoints use this base URL:
```
{{base_url}} = http://localhost:8080/api
```

---

## 🔐 Authentication

Authentication is required for most endpoints and is provided through a Bearer token, obtained upon successful login.

---

## 🗂️ Endpoints

### 👤 User Endpoints

#### **Register**
- **Endpoint**: `POST {{base_url}}/auth/register`
- **Description**: Register a new user.
- **Body**:
  ```json
  {
    "firstName": "John",
    "lastName": "Doe",
    "username": "johndoe",
    "password": "SecurePassword123!",
    "email": "johndoe@example.com",
    "phone": "1234567890",
    "ssn": "111-22-3333"
  }
  ```

#### **Login**
- **Endpoint**: `POST {{base_url}}/auth/login`
- **Description**: Login with credentials to receive an authentication token.
- **Body**:
  ```json
  {
    "username": "johndoe",
    "password": "SecurePassword123!"
  }
  ```

#### **My Profile**
- **Endpoint**: `GET {{base_url}}/users/profile`
- **Description**: Retrieve the current user's profile details.

#### **Update Info**
- **Endpoint**: `PUT {{base_url}}/users/update`
- **Description**: Update the current user's information.
- **Body**:
  ```json
  {
    "firstName": "UpdatedFirstName",
    "lastName": "UpdatedLastName",
    "email": "newemail@example.com",
    "phone": "0987654321",
    "password": "NewSecurePassword123!"
  }
  ```

### 🏦 Account Endpoints

#### **My Accounts**
- **Endpoint**: `GET {{base_url}}/accounts`
- **Description**: List all accounts associated with the user.

#### **Open Account**
- **Endpoint**: `POST {{base_url}}/accounts`
- **Description**: Open a new account with a specified currency.
- **Body**:
  ```json
  {
    "currency": "USD"
  }
  ```

#### **Close Account**
- **Endpoint**: `DELETE {{base_url}}/accounts/close`
- **Description**: Close an existing account.
- **Body**:
  ```json
  {
    "cardNumber": "1234567812345678"
  }
  ```

### 💸 Transaction Endpoints

#### **Pay by Account**
- **Endpoint**: `POST {{base_url}}/transactions/transfer`
- **Description**: Transfer funds between two accounts.
- **Body**:
  ```json
  {
    "senderAccountId": 1,
    "receiverAccountId": 2,
    "amount": 100
  }
  ```

#### **Pay by Card**
- **Endpoint**: `POST {{base_url}}/transactions/transferByCard`
- **Description**: Transfer funds between two cards.
- **Body**:
  ```json
  {
    "senderCardNumber": "4000123456789010",
    "receiverCardNumber": "4000987654321234",
    "amount": 50
  }
  ```

#### **Transaction History**
- **Endpoint**: `GET {{base_url}}/transactions/history`
- **Description**: Retrieve the transaction history for the user.

### 🔑 Admin Endpoint

#### **Admin Transfer**
- **Endpoint**: `POST {{base_url}}/admin/adminTransfer`
- **Description**: Allows an admin to transfer funds between any accounts.
- **Body**:
  ```json
  {
    "senderAccountId": 3,
    "receiverAccountId": 4,
    "amount": 500
  }
  ```

---

## 🧰 Postman Collection

This API collection can be imported into [Postman](https://www.postman.com/) for easier testing and management. Download the collection file from the repository or create one manually using the endpoints listed above.


## 📖 Setup Instructions

### Prerequisites

- 🖥 **JDK** 11 or higher
- 💽 **MariaDB database**
- 🧩 **Maven**

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/antonbaider/MBankApp.git
   cd MBankApp
   ```

2. **Build the project with Maven**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   java -jar target/MBankApp-0.0.1-SNAPSHOT.jar
   ```

---

## 🐳 Using Docker

The project includes a Docker setup to run the application and MariaDB as services.

### Docker Compose Setup

1. **Ensure Docker and Docker Compose are installed.**

2. **Run the following command** in the root directory where `docker-compose.yml` is located:
   ```bash
   docker-compose up -d
   ```

This command will start the MBank App and a MariaDB instance. The app will be accessible at `http://localhost:8080`, and MariaDB will be running on `localhost:3307`.

3. **Shut down the containers**:
   ```bash
   docker-compose down
   ```

---

## 📚 API Documentation

The application uses Swagger for API documentation. Access it at:
```
http://localhost:8080/swagger-ui.html
```

---

## 📝 License

This project is licensed under the MIT License.
