<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"/>
  <img src="https://img.shields.io/badge/Architecture-MVVM-blue?style=for-the-badge" alt="Architecture"/>
  <img src="https://img.shields.io/badge/Status-Production--Deployed-success?style=for-the-badge" alt="Status"/>
  <img src="https://img.shields.io/badge/License-Proprietary-red?style=for-the-badge" alt="License"/>
</p>

<h1 align="center">🏭 Industrial Motor Temperature Monitoring System 🚀</h1>

<p align="center">
  <strong>An enterprise-grade Android application built with Kotlin for real‑time telemetry, safety alerting, and MySQL data logging of physical motors and thermal zones in industrial plant environments.</strong>
</p>

---

> [!IMPORTANT]  
> **Commercial Deployment & Confidentiality Notice**  
> * This project was successfully developed, acquired, and is actively deployed locally within a private industrial manufacturing facility.
> * Due to active commercial usage, corporate security policies, and non-disclosure agreements (NDAs), the **complete source code, connection strings, and live plant screenshots are strictly confidential and withheld**.
> * This repository serves as a **portfolio showcase** demonstrating the system design, architectural choices, and technical capabilities behind this industrial IoT solution.
> * 💬 **Inquiries & Custom Work:** If you are interested in a technical walkthrough, architecture discussion, or want to build a similar solution for your business, please feel free to **DM me** or contact me directly via the details in the [Contact Section](#-lets-collaborate).

---

## 📊 System Architecture & Telemetry Pipeline

The application functions as a high-reliability telemetry visualizer and alert hub, communicating directly with local industrial databases and sensors. Below is the conceptual architectural flow:

```mermaid
graph TD
    subgraph Physical Plant Floor
        S1[Motor A Sensor] -->|Modbus / TCP| GW[Edge IoT Gateway]
        S2[Motor B Sensor] -->|Modbus / TCP| GW
        S3[Room Temp Sensor] -->|Modbus / TCP| GW
    end

    subgraph Data & Storage Layer (On-Premise)
        GW -->|Continuous Telemetry Insertion| DB[(MySQL Server)]
    end

    subgraph Monitoring Client (Android App)
        App[Kotlin Android App] -->|Secure JDBC / Local APIs| DB
        App -->|MVVM LiveData / StateFlow| UI[Real-Time Dashboard]
        App -->|Background worker / Services| Alert[Local Push Notification System]
    end

    style S1 fill:#f9d5e5,stroke:#333,stroke-width:1px
    style S2 fill:#f9d5e5,stroke:#333,stroke-width:1px
    style S3 fill:#f9d5e5,stroke:#333,stroke-width:1px
    style DB fill:#eeeeee,stroke:#333,stroke-width:2px
    style App fill:#d5f9de,stroke:#333,stroke-width:2px
    style Alert fill:#ffcccc,stroke:#ff3333,stroke-width:2px
```

---

## 🌟 Key Engineering Features

* 🌡️ **Real‑Time Industrial Telemetry** – Continuous, low-overhead data fetching from multiple motors and room environments.
* 🚨 **Fail-Safe Alarm System** – Instant system-level alerts triggered when any thermal threshold is breached, utilizing custom notification channels with high-priority status for noisy plant environments.
* 📡 **On-Premises Local Sync** – Tailored for corporate intranets and air-gapped environments, ensuring data sovereignty and network security.
* 💾 **MySQL Time-Series Logging** – Direct database interaction designed for structured tracking, analysis, and report generation.
* 📱 **Operator-Centric UX** – Highly-readable visual design optimized for floor workers, featuring dynamic status indicators (Green/Orange/Red) corresponding to thermal thresholds.

---

## 🛠️ Technical Implementation & Skills Demonstrated

As a developer, this project showcases clean-code practices, design patterns, and platform-specific performance optimizations:

### 1. Architectural & Asynchronous Patterns
* **MVVM Architecture:** Strict separation of concerns between UI layers, ViewModels, and Data Repositories for maximum maintainability and testability.
* **Kotlin Coroutines & Flow:** Asynchronous database calls executed safely outside the Main UI thread, ensuring zero frame drops on the dashboard.
* **State Management:** Utilizing `StateFlow` and `LiveData` to propagate real-time temperature updates seamlessly from the repository to the UI.

### 2. Database & Data Management
* **Optimized MySQL Connectivity:** Configured secure, thread-safe database connections capable of handling frequent query polling intervals.
* **Data Serialization:** Built robust helper utilities to map raw relational database rows into Kotlin Data Classes dynamically.

### 3. Industrial Robustness & Reliability
* **Intranet Resiliency:** Implemented network state monitoring (via Android's `ConnectivityManager`) to gracefully queue retry attempts and notify operators when local Wi-Fi drops.
* **Custom Foreground Services / WorkManager:** Background logic configured to check thresholds and query data in a battery-efficient manner, complying with modern Android power-saving restrictions.

---

## ⚙️ Concept & Tech Stack Reference

| Technology | Implementation Scope |
|------------|------|
| **Kotlin** | Clean, object-oriented Android app logic, coroutines, and structured concurrency. |
| **Android SDK** | Notifications, connectivity management, custom background workers, and life-cycle awareness. |
| **MySQL** | Time-series sensor logging database. |
| **Material Design 3** | High-contrast, industrial UI components built for active floor operations. |

---

## 📱 Conceptual Interface Structure

Due to confidentiality agreements, live dashboard images showing sensitive machinery identifiers are redacted. Below is the functional structure of the UI:

```
+-------------------------------------------------------+
|  [🏭] Industrial Monitor v1.2               [Status: OK]  |
+-------------------------------------------------------+
|  ROOM A: 24.5 °C  [ SAFE ]                            |
|  MOTOR 1: 78.2 °C [ WARNING ]  ||||||||||||||.....    |
|  MOTOR 2: 96.1 °C [ CRITICAL! ] ||||||||||||||||||||!  |
+-------------------------------------------------------+
|  -> Active Alerts Log:                                |
|  [09:42] Alert: Motor 2 exceeded 90.0 °C threshold    |
|  [09:15] Warning: Motor 1 entered warning state       |
+-------------------------------------------------------+
|  [ Dashboard ]        [ Thresholds ]       [ Logs ]   |
+-------------------------------------------------------+
```

---

## 🚀 Concept Validation Setup

For developers interested in how the original concept was set up and tested in sandbox environments:

### Local Sandbox Requirements
* **Android Studio Koala+** (with JDK 17)
* A local **MySQL Server** (sandbox instance) running the schema below:

```sql
CREATE DATABASE industrial_telemetry;
USE industrial_telemetry;

CREATE TABLE motor_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    motor_name VARCHAR(50),
    temperature DOUBLE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Sandbox Configuration File
In a sandbox environment, edit the connection profile:
```kotlin
object DatabaseConfig {
    const val DB_HOST = "192.168.1.100" // Local server IP
    const val DB_PORT = "3306"
    const val DB_NAME = "industrial_telemetry"
    const val DB_USER = "sandbox_user"
    const val DB_PASSWORD = "secure_password"
}
```

---

## 💬 Let's Collaborate!

Are you looking for an experienced Kotlin Android developer to build custom IoT visualizers, industrial dashboard systems, or local network utility applications?

* 📬 **Direct Message:** Reach out on [GitHub](https://github.com/Mejri-Mehdi) or message me directly through my profile links.
* 📧 **Email:** [mehdi.mejri@esprit.tn](mailto:mehdi.mejri@esprit.tn)
* 💼 **LinkedIn:** Let's discuss your next project!

---
<p align="center"><sub>Made with ❤️ by <a href="https://github.com/Mejri-Mehdi">Mejri Mehdi</a></sub></p>
