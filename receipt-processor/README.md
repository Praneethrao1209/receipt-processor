# Receipt Processor

This is a Spring Boot application that processes receipts and calculates points based on predefined rules.

## Running the Application Locally

### Prerequisites
- Java 17 or higher
- Maven
- Docker (if running via Docker)

### Steps to Run Locally
1. Clone the repository:
   ```sh
   git clone <repository-url>
   cd receipt-processor
   ```
2. Build the project using Maven:
   ```sh
   mvn clean install
   ```
3. Run the application:
   ```sh
   mvn spring-boot:run
   ```
4. The application will start on `http://localhost:8080`

## Running the Application with Docker

### Prerequisites
- Install Docker

### Steps to Build and Run
1. Build the Docker image:
   ```sh
   docker build -t receipt-processor .
   ```
2. Run the Docker container:
   ```sh
   docker run -p 8080:8080 receipt-processor
   ```
3. The application will be accessible at `http://localhost:8080`

## API Endpoints

### Submit a Receipt
- **Endpoint:** `POST /receipts/process`
- **Description:** Submits a receipt for processing and returns a receipt ID.
- **Response:**
  ```json
  { "id": "<generated-uuid>" }
  ```

### Get Points for a Receipt
- **Endpoint:** `GET /receipts/{id}/points`
- **Description:** Retrieves the points awarded for a receipt.
- **Response:**
  ```json
  { "points": 100 }
  ```

## Notes:
- A generic error message `"The receipt is invalid. Please verify input."` is returned for invalid requests.
- The condition for awarding **5 points if the total is greater than 10.00** has been excluded from the points calculation logic.

## Running Tests
Run the test cases using:
```sh
mvn test
```

## License
This project is for assessment purposes only.

