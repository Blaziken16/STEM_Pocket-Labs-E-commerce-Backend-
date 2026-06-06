# STEM Pocket Labs E-Commerce Backend
A robust, secure, and highly responsive e-commerce backend and landing page server built entirely from scratch to power STEM Pocket Labs.

🟢 **Live Status:** Deployed and running on Render.com
## Table of Contents
>About the Project

>Key Features

>Tech Stack

>Getting Started

>Project Structure

>About the Developer

## About the Project
This repository contains the backend infrastructure for the STEM Pocket Labs e-commerce platform. Building this from scratch allowed for complete control over the architecture, ensuring high performance, top-tier security, and a flexible foundation. By utilizing Ktor, the application remains exceptionally lightweight, and maintaining Kotlin on the backend creates a seamless bridge for future mobile application integrations.

## Key Features
- Secure User Authentication: Implements JSON Web Tokens (JWT) for stateless, secure user sessions.

* Robust Database Management: Fully integrated with PostgreSQL for reliable, relational data storage of products, users, and orders.

+ Cross-Origin Support: Pre-configured CORS to easily accept requests from the frontend.

- Automated Content Negotiation: Uses kotlinx.serialization to automatically serialize and deserialize JSON payloads.


- Graceful Error Handling: Centralized exception handling via Ktor Status Pages to ensure API clients receive clean, readable error messages.


- Cryptographic Password Hashing: Ensures user credentials are securely salted and hashed before entering the PostgreSQL database, protecting against plaintext data breaches.


- Environment Security & Hidden Keys: All sensitive API keys, JWT secrets, and database credentials are fully abstracted using environment variables. No secrets are ever exposed in the source code.

# Tech Stack
**Language:** Kotlin

**Framework:** Ktor

**Database:** PostgreSQL

**Authentication:** JWT (JSON Web Tokens)

**Serialization**: kotlinx.serialization

## Getting Started
- Prerequisites
JDK 11 or higher

- PostgreSQL running locally or remotely

- Installation & Execution
Clone the repository:

```Bash
git clone https://github.com/Blaziken16/STEM_Pocket-Labs-E-commerce-Backend-.git
```
Navigate to the project directory:

```Bash
cd STEM_Pocket-Labs-E-commerce-Backend-
```
Build the project using Gradle:

```Bash
./gradlew build
````
Run the server:
```Bash
./gradlew run
```
If the server starts successfully, it will respond at https://stem-pocket-labs-e-commerce-backend.onrender.com.

## 📂 **Project Structure**
```Plaintext
├── src/                # Core application source code (routing, models, plugins)
├── gradle/             # Gradle wrapper scripts
├── build.gradle.kts    # Project dependencies and configurations
├── Dockerfile          # Containerization instructions
└── README.md           # You are here!
```
# About the Developer
Built by Rishabh Upadhyay. As a 2nd-year B.Tech student at VIT-AP, I am highly focused on mastering scalable software architecture. Building backend systems like this provides a strong foundation that pairs perfectly with my interests in Android development and integrating AI/ML capabilities into modern applications.