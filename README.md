# 🏥 Hospital Management System

A backend REST API built with Java and Spring Boot for managing hospital operations including patients, doctors, departments, appointments, medical records, billing, and user authentication.

## 🌐 Live Demo

🚀 **Live API:**  
https://hospital-management-system-dap5.onrender.com

📂 **GitHub Repository:**  
https://github.com/Ajett/hospital-management-system

### Deployment

- **Application:** Render
- **Database:** Aiven MySQL
- **Containerization:** Docker
- **Authentication:** JWT + Google OAuth2
- **CI/CD:** GitHub → Render Auto Deploy

## 🚀 Features

- User registration and login
- JWT-based authentication
- Refresh token authentication
- Google OAuth2 login
- Role-based authorization
- Patient management
- Doctor management
- Department management
- Appointment management
- Medical record management
- Bill and payment management
- Appointment status management
- Payment status management
- Global exception handling
- Request validation
- RESTful APIs
- Unit and controller testing

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming |
| Spring Boot | Application framework |
| Spring Security | Authentication and authorization |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| MySQL | Database |
| JWT | Token-based authentication |
| Google OAuth2 | Social login |
| Maven | Build and dependency management |
| JUnit 5 | Testing |
| Mockito | Mocking and unit testing |
| MockMvc | Controller/API testing |

## 🏗️ Architecture

The application follows a layered architecture:

```text
Client
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
MySQL Database
```
### Main Layers

- **Controller** – Handles HTTP requests and responses.
- **Service** – Contains business logic.
- **Repository** – Handles database operations using Spring Data JPA.
- **Entity** – Represents database tables.
- **DTO** – Handles request and response data.
- **Exception** – Handles application-specific exceptions and global error responses.
- **Security** – Handles JWT authentication and authorization.

## 🔐 Authentication & Security

The application supports JWT authentication and Google OAuth2 login.

### JWT Authentication

Users can authenticate using username/password and receive a JWT token.

The token is sent with protected API requests:

```text
Authorization: Bearer <JWT_TOKEN>
```

### Google OAuth2

The application also supports Google OAuth2 login.

OAuth2 credentials are provided through environment variables so that sensitive information is not stored in the source code.

## 📋 Main Modules

### 🔑 Authentication

- User registration
- User login
- JWT authentication
- Refresh access token
- Google OAuth2 login

### 👤 Patient Management

- Create patient
- Get all patients
- Get patient by ID
- Update patient
- Delete patient

### 👨‍⚕️ Doctor Management

- Create doctor
- Get all doctors
- Get doctor by ID
- Update doctor
- Delete doctor

### 🏢 Department Management

- Create department
- Get departments
- Update department
- Delete department

### 📅 Appointment Management

- Create appointment
- Get all appointments
- Get appointment by ID
- Update appointment
- Delete appointment
- Update appointment status

### Appointment Status Flow

```text
SCHEDULED
    ↓
CONFIRMED
    ↓
COMPLETED

SCHEDULED
    ↓
CANCELLED
```

### 🩺 Medical Records

- Create medical record
- Get medical records
- Update medical record
- Delete medical record

### 💳 Billing & Payments

- Create bill
- Get bill
- Update bill
- Delete bill
- Update payment status

## 🗄️ Database

The application uses **MySQL** with **Spring Data JPA** and **Hibernate** for database management and ORM.

Database credentials are provided through environment variables.

### Required Environment Variables

```text
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
JWT_SECRET=your_jwt_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

> ⚠️ Never commit real passwords, JWT secrets, or Google OAuth credentials to GitHub.

The application uses environment-variable placeholders in the configuration:

```properties
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

## 🧪 Testing

The project contains **253 automated tests**.

### Test Result

```text
Tests run: 253
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

### Testing Tools

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc

### Test Coverage

The tests cover:

- Controller endpoints
- Service-layer logic
- Authentication
- JWT functionality
- JWT authentication filter
- Refresh token functionality
- Request validation
- Exception handling
- Application context loading

### Run Tests

To run all tests:

```bash
mvn clean test
```

## 📁 Project Structure

```text
hospital-management-system/
│
├── .gitignore
├── .gitattributes
├── pom.xml
├── mvnw
├── mvnw.cmd
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ajeet/hospital/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       └── service/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       ├── java/
│       └── resources/
│
└── README.md
```

## ⚙️ Running the Project Locally

### 1. Clone the Repository

```bash
git clone https://github.com/Ajett/hospital-management-system.git
```

### 2. Navigate to the Project

```bash
cd hospital-management-system
```

### 3. Configure Environment Variables

Set the following environment variables before starting the application:

```text
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
JWT_SECRET=your_jwt_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

### 4. Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

On Windows, you can also use the Maven wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

The application runs on:

```text
http://localhost:8081
```

## 🔨 Build the Project

To build the project and create the executable Spring Boot JAR:

```bash
mvn clean package
```

The executable JAR will be generated inside:

```text
target/
```

Example:

```text
target/hospital-management-system-0.0.1-SNAPSHOT.jar
```

### Run the JAR

After building the project:

```bash
java -jar target/hospital-management-system-0.0.1-SNAPSHOT.jar
```

## 📌 API Base URL

When running the application locally:

```text
http://localhost:8081
```

### Main API Endpoints

| Module | Endpoint |
|---|---|
| Authentication | `/api/auth` |
| Patients | `/api/patients` |
| Doctors | `/api/doctors` |
| Appointments | `/api/appointments` |
| Bills | `/api/bills` |
| Departments | `/api/departments` |
| Medical Records | `/api/medical-records` |
| Users | `/api/users` |

### Authentication Header

Protected endpoints require a JWT token:

```text
Authorization: Bearer <JWT_TOKEN>
```

## 🔒 Security

Sensitive information is handled using environment variables.

The following credentials should never be committed to GitHub:

- Database username and password
- JWT secret
- Google OAuth client ID and client secret
- Other private credentials

The application configuration uses environment-variable placeholders:

```properties
DB_USERNAME=${DB_USERNAME}
DB_PASSWORD=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

For production deployment, configure these values through the hosting platform's environment variables.

## 👨‍💻 Author

**Ajeet Kumar**

- GitHub: https://github.com/Ajett
- LinkedIn: https://www.linkedin.com/in/ajeet-kumar-it/

## 📊 Project Status

```text
✅ REST APIs implemented
✅ JWT authentication implemented
✅ Refresh token authentication implemented
✅ Google OAuth2 login implemented
✅ Role-based authorization implemented
✅ Global exception handling implemented
✅ Request validation implemented
✅ Automated tests implemented
✅ 253 tests passing
🚀 Ready for deployment
```

## 📄 License

This project is created for **learning, portfolio, and demonstration purposes**.

---

⭐ If you find this project useful, feel free to explore the repository and give it a star.

