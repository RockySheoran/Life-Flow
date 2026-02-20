# Life-Flow Backend

This is the backend for the Life-Flow application. It is a microservices-based architecture with the following services:

*   **API Gateway**: The single entry point for all client requests.
*   **Discovery Service**: For service registration and discovery.
*   **User Service**: Handles user authentication and management.
*   **Donor Service**: Manages blood donors.
*   **Request Service**: Manages blood requests.
*   **Inventory Service**: Manages blood inventory.
*   **Geolocation Service**: Handles geolocation functionalities.

## Getting Started

### Prerequisites

*   Java
*   Maven
*   Docker

### Running the services

To run the services, you can use the `docker-compose.yml` file in the root of the project.

```bash
docker-compose up -d
```

Alternatively, you can run each service individually.

#### Discovery Service

```bash
cd discovery-service
mvn spring-boot:run
```

#### API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

#### User Service

```bash
cd user_service
mvn spring-boot:run
```

#### Donor Service

```bash
cd donor_service
mvn spring-boot:run
```

#### Request Service

```bash
cd request_service
mvn spring-boot:run
```

#### Inventory Service

```bash
cd inventory_service
mvn spring-boot:run
```

#### Geolocation Service

```bash
cd geolocation_service
mvn spring-boot:run
```

## System Design

The Life-Flow backend is a microservices-based system designed for scalability and resilience. Each service is responsible for a specific domain, and they communicate with each other through REST APIs.

### Architecture

*   **API Gateway**: All incoming requests are routed through the API Gateway, which acts as a single entry point to the system. It handles concerns like authentication, rate limiting, and routing to the appropriate microservice.
*   **Discovery Service**: This service allows microservices to register themselves and discover the locations of other services. This enables dynamic scaling and resilience, as services can be added or removed without requiring manual configuration changes.
*   **Microservices**: Each service is a self-contained application with its own database, providing loose coupling and independent deployment.

### Communication

*   **Synchronous Communication**: Services communicate with each other using REST APIs for synchronous requests.
*   **Asynchronous Communication**: For tasks that can be performed asynchronously, such as sending notifications or generating reports, a message queue (e.g., RabbitMQ) can be used to decouple services and improve performance.

## Services

### API Gateway

The API Gateway is the single entry point for all client requests. It provides a unified interface to the various microservices, handling routing, authentication, and other cross-cutting concerns.

### Discovery Service

The Discovery Service, using Netflix Eureka, allows services to register themselves and discover other services dynamically. This is crucial for a microservices architecture, as it enables services to find each other without hardcoded URLs.





### User Service

The User Service handles all user-related functionality, including authentication, profile management, and roles.

#### API Endpoints

##### Authentication

*   **POST /auth/signup**
    *   **Description**: Registers a new user.
    *   **Request Body**: `SignupDto`
    *   **Response**: `ResponseStringDto` with a success message.

*   **POST /auth/login**
    *   **Description**: Logs in a user and returns an access token. A refresh token is set as an HTTP-only cookie.
    *   **Request Body**: `LogInDto`
    *   **Response**: `LoginResponseDto` with the access token.

*   **GET /auth/google**
    *   **Description**: Redirects to Google for OAuth2 authentication.

*   **GET /auth/verify**
    *   **Description**: Verifies a user's email with a token.
    *   **Query Parameters**: `token`, `email`
    *   **Response**: `ResponseStringDto` with a success message.

*   **POST /auth/logout**
    *   **Description**: Logs out the user by clearing the refresh token cookie.
    *   **Response**: `ResponseStringDto` with a success message.

*   **POST /auth/refresh-token**
    *   **Description**: Refreshes the access token using the refresh token from the cookie.
    *   **Response**: `LoginResponseDto` with the new access token.

*   **POST /auth/password-update**
    *   **Description**: Updates the user's password after a Google login.
    *   **Request Body**: `passwordUpdateAfterGoogleLoginDto`
    *   **Response**: `ResponseStringDto` with a success message.

*   **POST /auth/forget-password**
    *   **Description**: Sends a password reset link to the user's email.
    *   **Request Body**: `ForgetPasswordRequestDto`
    *   **Response**: `ResponseStringDto` with a success message.

*   **POST /auth/reset-password**
    *   **Description**: Resets the user's password with a valid token.
    *   **Request Body**: `ResetPasswordDto`
    *   **Response**: `ResponseStringDto` with a success message.

##### Profile

*   **GET /profile/get-me**
    *   **Description**: Retrieves the profile of the currently authenticated user.
    *   **Response**: `UserDto` with the user's profile information.

##### Donor Profile

*   **POST /profile/donors**
    *   **Description**: Creates a donor profile for the authenticated user.
    *   **Request Body**: `DonorProfileCreateDto`
    *   **Response**: `DonorProfileResponseDto`

*   **GET /profile/donors/get-profile**
    *   **Description**: Retrieves the donor profile of the authenticated user.
    *   **Response**: `DonorProfileResponseDto`

*   **GET /profile/donors/{userId}**
    *   **Description**: Retrieves the donor profile of a specific user.
    *   **Path Variable**: `userId`
    *   **Response**: `DonorProfileResponseDto`

*   **PUT /profile/donors/update**
    *   **Description**: Updates the donor profile of the authenticated user.
    *   **Request Body**: `DonorProfileUpdateDto`
    *   **Response**: `DonorProfileResponseDto`

*   **PATCH /profile/donors/{userId}/verify**
    *   **Description**: Verifies a donor's profile.
    *   **Path Variable**: `userId`
    *   **Request Body**: `DonorVerificationDto`
    *   **Response**: `DonorProfileResponseDto`

*   **DELETE /profile/donors/delete-profile**
    *   **Description**: Deletes the donor profile of the authenticated user.
    *   **Response**: `204 No Content`

*   **GET /profile/donors/search**
    *   **Description**: Searches for donors based on criteria.
    *   **Query Parameters**: `bloodType`, `city`, `minWeight`, `eligibilityStatus`, `page`, `limit`
    *   **Response**: `Page<DonorProfileResponseDto>`

*   **PUT /profile/donors/{userId}/location**
    *   **Description**: Updates the location of a donor.
    *   **Path Variable**: `userId`
    *   **Request Body**: `PointDTO`
    *   **Response**: `200 OK`

*   **GET /profile/donors/nearby**
    *   **Description**: Finds nearby donors.
    *   **Query Parameters**: `latitude`, `longitude`, `radiusKm`, `bloodType`
    *   **Response**: `List<DonorProfileResponseDto>`

*   **GET /profile/donors/nearby-less-data**
    *   **Description**: Finds nearby donors with less response data.
    *   **Query Parameters**: `latitude`, `longitude`, `radiusKm`, `bloodType`
    *   **Response**: `List<DonorProfileResponseLessDto>`

##### Hospital Profile

*   **POST /profile/hospitals/register**
    *   **Description**: Creates a hospital profile for the authenticated user.
    *   **Request Body**: `HospitalProfileCreateDto`
    *   **Response**: `HospitalProfileResponseDto`

*   **GET /profile/hospitals/{userId}**
    *   **Description**: Retrieves the hospital profile of a specific user.
    *   **Path Variable**: `userId`
    *   **Response**: `HospitalProfileResponseDto`

*   **GET /profile/hospitals/get-profile**
    *   **Description**: Retrieves the hospital profile of the authenticated user.
    *   **Response**: `HospitalProfileResponseDto`

*   **PUT /profile/hospitals/update**
    *   **Description**: Updates the hospital profile of the authenticated user.
    *   **Request Body**: `HospitalProfileUpdateDto`
    *   **Response**: `HospitalProfileResponseDto`

*   **DELETE /profile/hospitals/delete-profile**
    *   **Description**: Deletes the hospital profile of the authenticated user.
    *   **Response**: `204 No Content`

*   **PUT /profile/hospitals/update-verification-status/{hospitalId}**
    *   **Description**: Updates the verification status of a hospital.
    *   **Path Variable**: `hospitalId`
    *   **Response**: `String` with a success message.

*   **PUT /profile/hospitals/update-verification-status**
    *   **Description**: Updates the verification status of the authenticated hospital.
    *   **Response**: `String` with a success message.

*   **GET /profile/hospitals/verification-status**
    *   **Description**: Retrieves the verification status of the authenticated hospital.
    *   **Response**: `HospitalStatusDto`

*   **GET /profile/hospitals/search**
    *   **Description**: Searches for hospitals based on city.
    *   **Query Parameters**: `city`
    *   **Response**: `List<HospitalProfileResponseDto>`

*   **GET /profile/hospitals/nearby**
    *   **Description**: Finds nearby hospitals.
    *   **Query Parameters**: `latitude`, `longitude`, `radiusKm`
    *   **Response**: `List<HospitalProfileResponseDto>`

### Donor Service

The Donor Service handles all donor-related functionality, including donor profiles, donation history, and gamification.

#### API Endpoints

##### Donor

*   **GET /donor/profile/me**
    *   **Description**: Retrieves the profile of the currently authenticated donor.
    *   **Response**: `DonorProfileResponse`

*   **POST /donor/profile/create**
    *   **Description**: Creates a donor profile for the authenticated user.
    *   **Request Body**: `DonorProfileRequestDto`
    *   **Response**: `DonorProfileResponse`

*   **GET /donor/{donorId}**
    *   **Description**: Retrieves the profile of a specific donor.
    *   **Path Variable**: `donorId`
    *   **Response**: `DonorProfileResponse`

*   **GET /donor/me/eligibility**
    *   **Description**: Checks the eligibility of the currently authenticated donor.
    *   **Response**: `EligibilityCheckResponse`

*   **GET /donor/{id}/eligibility**
    *   **Description**: Checks the eligibility of a specific donor.
    *   **Path Variable**: `id`
    *   **Response**: `EligibilityCheckResponse`

##### Gamification

*   **GET /donor/{donor_id}/gamification**
    *   **Description**: Retrieves the gamification profile of a donor.
    *   **Path Variable**: `donor_id`
    *   **Response**: `GamificationProfileDto`

*   **POST /donor/{donor_id}/points/add**
    *   **Description**: Adds points to a donor's profile.
    *   **Path Variable**: `donor_id`
    *   **Request Body**: `AddPointsRequestDto`
    *   **Response**: `AddPointsResponseDto`

*   **POST /donor/{donor_id}/badge/check**
    *   **Description**: Checks and updates a donor's badge.
    *   **Path Variable**: `donor_id`
    *   **Response**: `BadgeUpdateDto`

*   **GET /donor/{donor_id}/achievements**
    *   **Description**: Retrieves the achievements of a donor.
    *   **Path Variable**: `donor_id`
    *   **Response**: `List<AchievementDto>`

*   **POST /donor/{donor_id}/achievements/award**
    *   **Description**: Awards an achievement to a donor.
    *   **Path Variable**: `donor_id`
    *   **Request Body**: `AwardAchievementRequestDto`
    *   **Response**: `AwardAchievementResponseDto`

*   **GET /donor/leaderboard**
    *   **Description**: Retrieves the leaderboard.
    *   **Query Parameters**: `timeframe`, `center_id`, `limit`
    *   **Response**: `List<LeaderboardEntryDto>`

*   **POST /donor/{donor_id}/referral**
    *   **Description**: Creates a referral for a donor.
    *   **Path Variable**: `donor_id`
    *   **Response**: `ReferralDto`

*   **POST /donor/referral/use**
    *   **Description**: Uses a referral code.
    *   **Request Body**: `UseReferralRequestDto`
    *   **Response**: `UseReferralResponseDto`

*   **GET /donor/{donor_id}/streak**
    *   **Description**: Retrieves the donation streak of a donor.
    *   **Path Variable**: `donor_id`
    *   **Response**: `DonationStreakDto`

##### Donation History

*   **GET /donor/{donor_id}/donations**
    *   **Description**: Retrieves the donation history of a donor.
    *   **Path Variable**: `donor_id`
    *   **Query Parameters**: `limit`, `offset`, `date_from`, `date_to`
    *   **Response**: `List<DonationRecordDto>`

*   **GET /donor/{donor_id}/donations/{donation_id}**
    *   **Description**: Retrieves the details of a specific donation.
    *   **Path Variables**: `donor_id`, `donation_id`
    *   **Response**: `DonationDetailsDto`

*   **POST /donor/donations/record**
    *   **Description**: Records a new donation.
    *   **Request Body**: `RecordDonationRequestDto`
    *   **Response**: `RecordDonationResponseDto`

*   **PUT /donor/donations/{donation_id}**
    *   **Description**: Updates a donation record.
    *   **Path Variable**: `donation_id`
    *   **Request Body**: `UpdateDonationRecordDto`
    *   **Response**: `200 OK`

*   **GET /donor/{donor_id}/donations/stats**
    *   **Description**: Retrieves the donation statistics of a donor.
    *   **Path Variable**: `donor_id`
    *   **Response**: `DonationStatsDto`

*   **GET /donor/{donor_id}/donations/next-eligible**
    *   **Description**: Retrieves the next eligible donation date for a donor.
    *   **Path Variable**: `donor_id`
    *   **Response**: `NextEligibleDateDto`

##### Donor Availability

*   **PUT /donor/{donor_id}/availability**
    *   **Description**: Updates the availability of a donor.
    *   **Path Variable**: `donor_id`
    *   **Request Body**: `DonorAvailabilityDto`
    *   **Response**: `UpdateAvailabilityResponseDto`

*   **GET /donor/{donor_id}/availability**
    *   **Description**: Retrieves the availability of a donor.
    *   **Path Variable**: `donor_id`
    *   **Response**: `List<DonorAvailabilityDto>`

*   **GET /donor/{donor_id}/availability/current**
    *   **Description**: Retrieves the current availability of a donor.
    *   **Path Variable**: `donor_id`
    *   **Response**: `CurrentAvailabilityDto`

*   **POST /donor/{donor_id}/availability/bulk**
    *   **Description**: Bulk updates the availability of a donor.
    *   **Path Variable**: `donor_id`
    *   **Request Body**: `BulkAvailabilityUpdateRequestDto`
    *   **Response**: `BulkAvailabilityUpdateResponseDto`

*   **PUT /donor/{donor_id}/availability/emergency**
    *   **Description**: Updates the emergency availability of a donor.
    *   **Path Variable**: `donor_id`
    *   **Request Body**: `EmergencyAvailabilityDto`
    *   **Response**: `200 OK`

*   **DELETE /donor/{donor_id}/availability/{day}**
    *   **Description**: Deletes the availability of a donor for a specific day.
    *   **Path Variables**: `donor_id`, `day`
    *   **Response**: `204 No Content`

### Request Service

The Request Service handles blood requests, appointments, and donor matching.

#### API Endpoints

##### Reminder

*   **POST /reminders/schedule**
    *   **Description**: Schedules a reminder.
    *   **Request Body**: `Map<String, Object>`
    *   **Response**: `200 OK`

*   **POST /reminders/send-now**
    *   **Description**: Sends a reminder immediately.
    *   **Request Body**: `Map<String, Object>`
    *   **Response**: `200 OK`

##### Donor Matching

*   **POST /matching/find-donors**
    *   **Description**: Finds suitable donors for a blood request.
    *   **Request Body**: `Map<String, Object>`
    *   **Response**: `List<DonorMatchResultDto>`

*   **POST /matching/notify-donors**
    *   **Description**: Notifies matched donors about a blood request.
    *   **Request Body**: `Map<String, Object>`
    *   **Response**: `200 OK`

##### Appointment Slot

*   **POST /requests/{request_id}/slots/generate**
    *   **Description**: Generates appointment slots for a blood request.
    *   **Path Variable**: `request_id`
    *   **Request Body**: `SlotGenerationRequestDto`
    *   **Response**: `200 OK`

*   **GET /requests/{request_id}/slots**
    *   **Description**: Retrieves the appointment slots for a blood request.
    *   **Path Variable**: `request_id`
    *   **Response**: `List<AppointmentSlotDto>`

*   **PUT /slots/{slot_id}/status**
    *   **Description**: Updates the status of an appointment slot.
    *   **Path Variable**: `slot_id`
    *   **Request Body**: `AppointmentSlotDto`
    *   **Response**: `200 OK`

*   **POST /slots/bulk/update**
    *   **Description**: Bulk updates the status of appointment slots.
    *   **Request Body**: `List<AppointmentSlotDto>`
    *   **Response**: `200 OK`

##### Emergency Request

*   **POST /requests/emergency**
    *   **Description**: Creates an emergency blood request.
    *   **Request Body**: `EmergencyRequestDto`
    *   **Response**: `RequestSummaryDto`

*   **GET /requests/{request_id}**
    *   **Description**: Retrieves a blood request by its ID.
    *   **Path Variable**: `request_id`
    *   **Response**: `RequestSummaryDto`

*   **GET /requests**
    *   **Description**: Retrieves a list of blood requests.
    *   **Query Parameters**: `hospitalId`, `status`, `urgency`, `dateFrom`
    *   **Response**: `List<RequestSummaryDto>`

*   **PUT /requests/{request_id}/cancel**
    *   **Description**: Cancels a blood request.
    *   **Path Variable**: `request_id`
    *   **Request Body**: `RequestUpdateDto`
    *   **Response**: `200 OK`

*   **POST /requests/{request_id}/extend**
    *   **Description**: Extends the deadline of a blood request.
    *   **Path Variable**: `request_id`
    *   **Request Body**: `RequestUpdateDto`
    *   **Response**: `200 OK`

*   **POST /requests/bulk**
    *   **Description**: Creates multiple blood requests at once.
    *   **Request Body**: `List<EmergencyRequestDto>`
    *   **Response**: `200 OK`

##### Appointment Booking

*   **POST /appointments/book**
    *   **Description**: Books an appointment for a blood donation.
    *   **Request Body**: `BookAppointmentRequestDto`
    *   **Response**: `Map<String, String>` with a confirmation message.

*   **POST /appointments/offer**
    *   **Description**: Offers an appointment to a donor.
    *   **Request Body**: `Map<String, Object>`
    *   **Response**: `200 OK`

*   **POST /appointments/{id}/checkin**
    *   **Description**: Checks in a donor for their appointment.
    *   **Path Variable**: `id`
    *   **Request Body**: `CheckInRequestDto`
    *   **Response**: `200 OK`

*   **POST /appointments/{id}/checkout**
    *   **Description**: Checks out a donor after their appointment.
    *   **Path Variable**: `id`
    *   **Request Body**: `CheckOutRequestDto`
    *   **Response**: `200 OK`

### Inventory Service

The Inventory Service manages blood inventory, stock levels, and collection centers.

#### API Endpoints

##### Stock

*   **GET /inventory/stock/{bloodType}**
    *   **Description**: Retrieves the stock summary for a specific blood type and collection center.
    *   **Path Variable**: `bloodType`
    *   **Query Parameters**: `centerId`, `status`
    *   **Response**: `StockResponseDto`

*   **GET /inventory/stock/hospital/{hospitalId}**
    *   **Description**: Retrieves the stock for a specific hospital.
    *   **Path Variable**: `hospitalId`
    *   **Response**: `StockResponseDto`

*   **GET /inventory/stock/blood-type/{bloodType}/all**
    *   **Description**: Retrieves the stock for a specific blood type across all centers.
    *   **Path Variable**: `bloodType`
    *   **Response**: `StockResponseDto`

*   **POST /inventory/stock/update**
    *   **Description**: Updates the stock information.
    *   **Request Body**: `StockUpdateDto`
    *   **Response**: `StockResponseDto`

*   **GET /inventory/stock/needs-reorder**
    *   **Description**: Retrieves a list of stock that needs to be reordered.
    *   **Response**: `StockResponseDto`

*   **POST /inventory/stock/alert-threshold**
    *   **Description**: Sets the low stock alert threshold.
    *   **Request Body**: `StockUpdateDto`
    *   **Response**: `StockResponseDto`

*   **POST /inventory/stock/initialize**
    *   **Description**: Initializes the stock for a collection center.
    *   **Request Body**: `StockUpdateDto`
    *   **Response**: `StockResponseDto`

##### Blood Inventory

*   **POST /core/blood-bag**
    *   **Description**: Creates a new blood bag in the inventory.
    *   **Request Body**: `BloodBagDto`
    *   **Response**: `BloodBagDto`

*   **GET /core/blood-bag/{bagId}**
    *   **Description**: Retrieves a blood bag by its ID.
    *   **Path Variable**: `bagId`
    *   **Response**: `BagResponseDto`

*   **PUT /core/blood-bag/{bagId}/status**
    *   **Description**: Updates the status of a blood bag.
    *   **Path Variable**: `bagId`
    *   **Request Body**: `BagUpdateDto`
    *   **Response**: `StringResponseDto`

*   **GET /core/search**
    *   **Description**: Searches for blood bags based on criteria.
    *   **Query Parameters**: `bloodType`, `status`, `centerId`
    *   **Response**: `List<BagResponseDto>`

*   **GET /core/trace/{bagId}**
    *   **Description**: Traces the history of a blood bag.
    *   **Path Variable**: `bagId`
    *   **Response**: `BagResponseDto`

*   **PUT /core/blood-bag/{id}/release**
    *   **Description**: Releases a blood bag from the inventory.
    *   **Path Variable**: `id`
    *   **Response**: `StringResponseDto`

*   **PUT /core/blood-bag/{id}/update/quality_check**
    *   **Description**: Updates the quality check status of a blood bag.
    *   **Path Variable**: `id`
    *   **Request Body**: `UpdateQualityDto`
    *   **Response**: `StringResponseDto`

##### Collection Center

*   **POST /centers**
    *   **Description**: Creates a new collection center.
    *   **Request Body**: `CollectionCenterDto`
    *   **Response**: `CollectionCenterDto`

*   **PUT /centers/{centerId}**
    *   **Description**: Updates a collection center.
    *   **Path Variable**: `centerId`
    *   **Request Body**: `CollectionCenterDto`
    *   **Response**: `CollectionCenterDto`

*   **GET /centers/{centerId}**
    *   **Description**: Retrieves a collection center by its ID.
    *   **Path Variable**: `centerId`
    *   **Response**: `CollectionCenterDto`

*   **GET /centers**
    *   **Description**: Retrieves all collection centers.
    *   **Response**: `List<CollectionCenterDto>`

*   **GET /centers/hospital/{hospitalId}**
    *   **Description**: Retrieves all collection centers for a specific hospital.
    *   **Path Variable**: `hospitalId`
    *   **Response**: `List<CollectionCenterDto>`

*   **GET /centers/{centerId}/capacity**
    *   **Description**: Checks the capacity of a collection center.
    *   **Path Variable**: `centerId`
    *   **Response**: `Object` (placeholder)

*   **POST /centers/{centerId}/staff**
    *   **Description**: Adds staff to a collection center.
    *   **Path Variable**: `centerId`
    *   **Response**: `Object` (placeholder)

*   **DELETE /centers/{centerId}**
    *   **Description**: Deletes a collection center.
    *   **Path Variable**: `centerId`
    *   **Response**: `204 No Content`

##### Expiry Management

*   **GET /expiry/expiring-soon**
    *   **Description**: Retrieves a list of blood bags that are expiring soon.
    *   **Query Parameters**: `daysThreshold`, `bloodType`, `centerId`
    *   **Response**: `List<ExpiryManagementTableResponseDto>`

*   **GET /expiry/expiry-alert**
    *   **Description**: Retrieves a list of blood bags based on their alert level.
    *   **Query Parameters**: `alertLevel`
    *   **Response**: `List<ExpiryManagementTableResponseDto>`

*   **GET /expiry/UpdateTableJob**
    *   **Description**: Manually triggers the job to update the expiry management table.
    *   **Response**: `String` with a success message.

*   **GET /expiry/all**
    *   **Description**: Retrieves all data from the expiry management table.
    *   **Response**: `List<ExpiryManagementTableResponseDto>`

*   **GET /expiry/dashboard**
    *   **Description**: Retrieves inventory dashboard data.
    *   **Query Parameters**: `centerId`, `timePeriod`
    *   **Response**: `Object` (placeholder)

*   **GET /expiry/admin/reports**
    *   **Description**: Retrieves admin reports.
    *   **Response**: `Object` (placeholder)

*   **POST /expiry/admin/audit**
    *   **Description**: Creates an audit.
    *   **Response**: `Object` (placeholder)

##### Inventory Transaction

*   **POST /inventory/transfer**
    *   **Description**: Transfers blood units between collection centers.
    *   **Request Body**: `TransactionRequestDto`
    *   **Response**: `TransactionResponseDto`

*   **POST /inventory/request-fulfillment**
    *   **Description**: Fulfills a blood request.
    *   **Request Body**: `TransactionRequestDto`
    *   **Response**: `TransactionResponseDto`

*   **GET /inventory/transactions/{transactionId}**
    *   **Description**: Retrieves a transaction by its ID.
    *   **Path Variable**: `transactionId`
    *   **Response**: `TransactionResponseDto`

*   **GET /inventory/transactions/bag/{bagId}**
    *   **Description**: Retrieves all transactions for a specific blood bag.
    *   **Path Variable**: `bagId`
    *   **Response**: `List<TransactionResponseDto>`

*   **GET /inventory/transactions**
    *   **Description**: Retrieves transactions by type for the authenticated hospital.
    *   **Query Parameters**: `type`
    *   **Response**: `List<TransactionResponseDto>`

*   **GET /inventory/transactions/request/{requestId}**
    *   **Description**: Retrieves all transactions for a specific blood request.
    *   **Path Variable**: `requestId`
    *   **Response**: `List<TransactionResponseDto>`

### Geolocation Service

The Geolocation Service provides functionalities related to location-based services.

#### API Endpoints

*   **GET /geo/donors/nearby**
    *   **Description**: Finds nearby donors based on location and other criteria.
    *   **Query Parameters**: `latitude`, `longitude`, `radius_km`, `bloodType`, `urgency_level`, `required_units`, `time_slot_start`, `time_slot_end`
    *   **Response**: `NearbyDonorsResponse`

*   **POST /geo/appointments/calculate-slots**
    *   **Description**: Calculates optimal appointment slots based on donor and center locations.
    *   **Request Body**: `CalculateSlotsRequest`
    *   **Response**: `CalculateSlotsResponse`

*   **GET /geo/donors/{donor_id}/travel-time**
    *   **Description**: Calculates the travel time for a donor to a collection center.
    *   **Path Variable**: `donor_id`
    *   **Query Parameters**: `center_id`, `departure_time`, `travel_mode`
    *   **Response**: `TravelTimeResponse`

*   **GET /geo/centers/nearby**
    *   **Description**: Finds nearby collection centers.
    *   **Query Parameters**: `latitude`, `longitude`, `radius_km`, `blood_type_needed`, `urgency`, `capacity_required`
    *   **Response**: `NearbyCentersResponse`

*   **GET /geo/requests/{request_id}/coverage**
    *   **Description**: Retrieves the coverage of a blood request.
    *   **Path Variable**: `request_id`
    *   **Query Parameters**: `include_donors`
    *   **Response**: `RequestCoverageResponse`

*   **POST /geo/optimize/routes**
    *   **Description**: Optimizes routes for blood transportation.
    *   **Request Body**: `OptimizeRoutesRequest`
    *   **Response**: `OptimizeRoutesResponse`

*   **GET /geo/stats/response-times**
    *   **Description**: Retrieves statistics on response times.
    *   **Query Parameters**: `center_id`, `time_period`, `radius_km`, `blood_type`
    *   **Response**: `ResponseTimeStatsResponse`

*   **POST /geo/predict/shortage**
    *   **Description**: Predicts blood shortages.
    *   **Request Body**: `ShortagePredictionRequest`
    *   **Response**: `ShortagePredictionResponse`

*   **GET /geo/hotspots**
    *   **Description**: Identifies hotspots for blood demand.
    *   **Query Parameters**: `city`, `time_period`, `blood_type`
    *   **Response**: `HotspotsResponse`

*   **POST /geo/simulate/emergency**
    *   **Description**: Simulates an emergency scenario.
    *   **Request Body**: `EmergencySimulationRequest`
    *   **Response**: `EmergencySimulationResponse`

*   **GET /geo/admin/coverage-report**
    *   **Description**: Generates a coverage report.
    *   **Query Parameters**: `region`, `report_type`, `format`
    *   **Response**: `CoverageReportResponse`
