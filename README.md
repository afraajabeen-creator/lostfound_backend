# 🏫 Campus Lost and Found Management System - Backend

Welcome to the backend of the **Campus Lost & Found Management System**.
This is a production-ready, clean, textbook RESTful backend developed with **Java 21**, **Spring Boot 3**, **Spring Data JPA (Hibernate)**, **Spring Security 6 (JWT)**, and **MySQL**.

---

## 🚀 Quick Start (Running on Your Laptop)

### Step 1: Database Setup
1. Make sure your local MySQL server is running on `localhost:3306`.
2. Ensure the database `lostfound` exists:
   ```sql
   CREATE DATABASE IF NOT EXISTS lostfound;
   ```
   *(Hibernate will automatically create all tables upon the first run thanks to `spring.jpa.hibernate.ddl-auto=update`)*.

### Step 2: Set Your Local MySQL Password
Open the file:
```
src/main/resources/application-local.properties
```
Replace the placeholder with your local MySQL root password:
```properties
spring.datasource.password=YOUR_REAL_MYSQL_PASSWORD
```
> **Security Note:** `application-local.properties` is registered in `.gitignore` and is **never** committed or pushed to GitHub. On cloud hosting (e.g. Render), credentials are provided securely via environment variables (`DB_URL`, `DB_USER`, `DB_PASS`).

### Step 3: Run the Application
In your terminal (Command Prompt or PowerShell) inside this `lostfound_backend` directory, run:

- **On Windows:**
  ```cmd
  mvnw.cmd spring-boot:run
  ```
  *(or `mvn spring-boot:run`)*

- **On macOS / Linux:**
  ```bash
  ./mvnw spring-boot:run
  ```

The server starts on port **8080**:
👉 `http://localhost:8080`

---

## 🔑 Default Seeded Accounts

On the very first application startup, the system automatically seeds a default administrator account:

| Role | Email | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin@campus.com` | `Admin@123` | Audit all items, manage users, block/unblock, approve/reject claims, verify pickup OTP |
| **Student** | *(Register via `/api/auth/register` or the React UI)* | User-chosen | Report lost/found items, search & filter, submit claim with proof, view personal OTP |

---

## 🌟 Key Architecture & Viva Highlights

### 1. Smart Match Engine
- **What it does:** Whenever a student reports a `LOST` item or a `FOUND` item, the system automatically compares it against open listings of the opposite type.
- **How it scores:**
  - **Category Match (35%)**: Exact category comparison.
  - **Location Proximity (25%)**: Exact match or campus building/room token overlap.
  - **Date Proximity (20%)**: Days difference between loss date and found date ($\le 1$ day = 20 pts, $\le 3$ days = 15 pts, $\le 7$ days = 10 pts, $\le 14$ days = 5 pts).
  - **Keyword Similarity (20%)**: Jaccard word-similarity coefficient computed across item title and description.
- **Outcome:** Pairs scoring $\ge 50\%$ are saved as "Possible Matches", and automated in-app notifications are sent to both reporters.

### 2. Secure Two-Step OTP Handover Protocol
- **What it does:** Guarantees that items are safely handed over only to the rightful owner.
- **Workflow:**
  1. Student submits proof details (e.g., serial number, hidden markings).
  2. Administrator audits the claim:
     - If rejected: Admin enters a mandatory reason, and claimant is notified.
     - If approved: The backend generates a secure 6-digit random code (`handover_otp`) and sets claim status to `VERIFIED`. The item status updates to `CLAIM_PENDING`.
  3. **Privacy:** The OTP is strictly delivered **only** to the claimant on their dashboard. It is never exposed publicly or to other users.
  4. **Pickup:** At the campus lost & found desk, the student presents their 6-digit OTP. The Admin enters this code into the Admin Dashboard.
  5. Upon OTP validation, the claim and item status permanently become `RECOVERED`.

---

## 📡 REST API Reference

All responses return a uniform JSON format:
```json
{
  "success": true,
  "message": "Operation description",
  "data": { ... },
  "timestamp": "2026-10-11T20:45:00"
}
```

### Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Register new student account |
| `POST` | `/api/auth/login` | Public | Login and receive Bearer JWT token |
| `GET` | `/api/auth/me` | Authenticated | Retrieve current user profile summary |

### Items (`/api/items`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/items` | Public | Search items by `type`, `category`, `location`, `keyword`, `startDate`, `endDate` |
| `GET` | `/api/items/{id}` | Public | Retrieve item details by ID |
| `POST` | `/api/items` | Authenticated | Report a new LOST or FOUND item (JSON or multipart) |
| `POST` | `/api/items/upload-image` | Authenticated | Upload item photo and receive image URL |
| `GET` | `/api/items/my-items` | Authenticated | Get all items reported by current user |
| `PUT` | `/api/items/{id}` | Owner / Admin | Update reported item |
| `DELETE` | `/api/items/{id}` | Owner / Admin | Delete reported item |

### Claims & Handover (`/api/claims`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/claims` | Student | Submit ownership claim with proof description |
| `GET` | `/api/claims/my-claims` | Student | Get user's claims (**includes Handover OTP when approved**) |
| `GET` | `/api/claims/item/{itemId}` | Owner / Admin | View claims submitted for a specific item |
| `GET` | `/api/claims/all` | Admin Only | View all claims across campus |
| `PUT` | `/api/claims/{id}/status` | Admin Only | Approve (`VERIFIED` -> triggers OTP) or Reject (`REJECTED` + reason) |
| `POST` | `/api/claims/{id}/verify-otp`| Admin Only | Enter student's 6-digit OTP at pickup to mark `RECOVERED` |

### Smart Matches (`/api/matches`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/matches/item/{itemId}` | Authenticated | Get match suggestions for a specific item |
| `GET` | `/api/matches/my-matches` | Authenticated | Get match suggestions for all items reported by logged-in user |

### Notifications (`/api/notifications`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/notifications` | Authenticated | Get user notifications list |
| `GET` | `/api/notifications/unread-count` | Authenticated | Get unread notification badge count |
| `PUT` | `/api/notifications/{id}/read` | Authenticated | Mark a notification as read |
| `PUT` | `/api/notifications/read-all` | Authenticated | Mark all notifications as read |

### Admin User Management (`/api/users`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/users` | Admin Only | List all campus users |
| `GET` | `/api/users/{id}` | Admin Only | Get user details |
| `PUT` | `/api/users/{id}` | Admin Only | Update user profile/role |
| `PUT` | `/api/users/{id}/toggle-block` | Admin Only | Block or unblock a user |

---

## 🧪 Testing with Postman

1. Open Postman.
2. Click **Import** and select the file `lostfound.postman_collection.json` located in the root of this folder.
3. The collection is pre-configured with the environment variables `baseUrl` (`http://localhost:8080`), `jwtToken`, and `adminToken`.
