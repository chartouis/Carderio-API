# Carderio-API

Backend service for the Carderio web flashcard application.

## Features
- User authentication and management
- Flashcard creation, editing, and deletion
- Deck organization
- Progress tracking

## Technologies Used
- **Spring Boot** - Backend framework
- **PostgreSQL** - Database
- **Docker** - Containerization
- **Gradle** - Build tool
- **Java 23** - Programming language

## Configuration
Configuration settings such as database credentials and application ports can be adjusted in the `docker-compose.yml` and `application.properties` files.

## API Endpoints
- `GET /cards` - Retrieve all cards
- `PATCH /cards` - Update multiple cards
- `POST /cards` - Create a new card
- `DELETE /cards/{id}` - Delete a card by ID
- `GET /cards/{id}` - Retrieve a card by ID
- `POST /cards/m` - Create multiple cards
- `PATCH /cards/request` - Update card requests
- `POST /cards/request` - Create a card request
- `POST /cards/request/progress` - Update card progress
- `POST /login` - User login
- `POST /register` - Register a new user

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

