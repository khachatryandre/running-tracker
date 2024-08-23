# Running Tracker REST API

## Project Description

This project is a REST API for a Running Tracker application. The API allows for user management and run tracking functionalities. The core features include adding, editing, and deleting users, tracking run details such as start and finish locations, calculating the distance covered, and providing user-specific statistics. Authentication is not included in this implementation.

### Features

1. **User Management**
    - Add, edit, and delete users.
    - Retrieve a user by ID.
    - Retrieve a list of all users.
    - Each user contains the following mandatory fields: `id`, `first_name`, `last_name`, `birth_date`, and `sex`.

2. **Run Tracking**
    - Start a run by providing user ID, start latitude/longitude, and start datetime.
    - Finish a run by providing user ID, finish latitude/longitude, finish datetime, and optionally, the distance covered (in meters). If not provided, the distance will be calculated as a straight line between the start and finish points.

3. **Run Retrieval**
    - Get all runs for a specific user, with filtering options for the start date.
    - Calculate the average speed for each run.

4. **User Statistics**
    - Retrieve user-specific statistics, including the number of runs, total distance covered, and average speed, with filtering options for the start date.

## Technologies Used

- **Java**
- **Spring Boot**
- **PostgreSQL**
- **Docker**
- **Docker Compose**

## Testing

- **Unit Tests**: Validating the core business logic of the application.
- **Integration Tests**: Testing the API endpoints and interactions with the database to ensure end-to-end functionality.

## Running Instructions

### Prerequisites

Ensure that **Docker** and **Docker Compose** are installed on your system.

### Step 1: Clone the Repository

```bash
git clone https://github.com/khachatryandre/running-tracker.git
cd running-tracker-api
```

### Step 2: Build and Run the Project
```bash
docker-compose up --build
```

### This will start both the PostgreSQL database and the Spring Boot application. 
### Once everything is up and running, the application will be accessible at http://localhost:8080.

### API Endpoints

- **User Management**
    - `POST /api/users` - Add a new user
    - `GET /api/users/{id}` - Get a user by ID
    - `GET /api/users` - Get a list of all users
    - `PUT /api/users/{id}` - Update an existing user
    - `DELETE /api/users/{id}` - Delete a user

- **Run Tracking**
    - `POST /api/runs/start` - Start a new run
    - `POST /api/runs/finish` - Finish an existing run

- **Run Retrieval**
    - `GET /api/runs/{userId}` - Get all runs for a specific user with optional filtering by start date (`from`, `to`)

- **User Statistics**
    - `GET /api/runs/{userId}/statistics` - Get statistics for a specific user with optional filtering by start date (`from`, `to`)
