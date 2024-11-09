# 🏦 MBank App

MBank App is a Java-based banking application designed to manage user transactions, account information, and provide seamless integration for modern transaction notifications via email. It features robust API documentation, transaction validation, and comprehensive security measures.

---

## 🚀 Features

- 📄 **API Documentation**: Uses Swagger for detailed API documentation.
- 💳 **Unique Card Generation**: Generates unique card numbers with expiration dates.
- 🔒 **Transaction Validation**: Validates transactions for amount limits and security compliance.
- 📧 **Configurable Notifications**: Sends branded generative PDFs and formatted email notifications after each transaction.
- 🔐 **Data Security**: Masks sensitive information such as passwords, SSNs, and card numbers.
- ⚡ **Caching**: Implements caching to improve performance.
- 💾 **Database**: MySQL for storing user and transaction data.

---

## 💻 Technologies

- ☕ **Java**
- 🧩 **Spring Boot** (REST, JPA, Security)
- 💽 **MySQL**
- 📜 **Swagger** (API Documentation)
- 🔄 **MapStruct** (for mapping DTOs)
- ✉️ **Mail API** (for email notifications)
- 🔑 **JWT** (JSON Web Tokens) for authentication

---

## 🛠 Environment Variables

To configure MthreeBank App, set the following environment variables:

### 🔧 Application Settings

- **`SPRING_APPLICATION_NAME`**: Sets the application name for Spring Boot. This helps identify the app in logs and configurations.
  
### 🔐 JWT Authentication

- **`JWT_SECRET`**: Secret key used to sign JWT tokens for securing API endpoints.
- **`JWT_EXPIRATION`**: Expiration time for the JWT tokens (in milliseconds). Adjust based on session requirements.

### 💾 MySQL Database Connection Settings

- **`SPRING_DATASOURCE_URL`**: JDBC URL for the MySQL database connection.
- **`SPRING_DATASOURCE_USERNAME`**: Username for the MySQL database.
- **`SPRING_DATASOURCE_PASSWORD`**: Password for the MySQL database.
- **`SPRING_DATASOURCE_DRIVER_CLASS_NAME`**: Driver class for MySQL (typically `com.mysql.cj.jdbc.Driver`).

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

## 📖 Setup Instructions

### Prerequisites

- 🖥 **JDK** 11 or higher
- 💽 **MySQL database**
- 🧩 **Maven**

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/mthreebank-app.git
   cd mthreebank-app
