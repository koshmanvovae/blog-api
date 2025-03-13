# 🚀 Blogging Platform API - Microservices Architecture

## 📌 Overview
This project is a fully functional **Blogging Platform API**, designed and implemented using **microservices architecture** in just **2 days**, working only several hours per day! Despite the time constraints, it includes a **robust, scalable, and production-ready backend** that follows modern best practices.

## 🎯 Features
- **🛡️ Authentication Service** - Secure user authentication with JWT.
- **📝 Blog Service** - Create, read, update, and delete blog posts.
- **💬 Comment Service** - Add, edit (within 60 min), and retrieve comments.
- **📈 User Engagement Metrics** - Ranks posts based on engagement (views, likes, comments).
- **⚡ API Gateway** - Centralized request routing for better performance and security.
- **📚 Swagger Documentation** - Self-explanatory API for seamless integration.
- **📦 Dockerized Services** - Easy deployment with **Docker**.
- **📡 Service Discovery** - Using **Eureka** for dynamic microservice registration.
- **🗄️ MySQL & Redis** - Efficient data storage and caching for high performance.
- **📊 Observability** - Integrated **Prometheus, Loki, and Grafana** for monitoring, logging, and visualization.

## 🚀 Why is this cool?
✅ **Built in Just 2 Days** - This architecture usually takes weeks to implement!
✅ **Scalable & Cloud-Ready** - Can handle high traffic with caching and service discovery.
✅ **Best Practices Used** - Clean API design, database indexing, caching, and microservices.
✅ **Monitored & Logged** - No black-box services! Grafana, Loki, and Prometheus provide full visibility.
✅ **API First Approach** - Designed with documentation and testing in mind from day one.
✅ **Modular & Extensible** - Services can be improved or scaled independently.

## 🏗️ Tech Stack
- **Java + Spring Boot** (for microservices)
- **Spring Security + JWT** (for authentication)
- **Spring Cloud Eureka** (for service discovery)
- **Spring Cloud Gateway** (API gateway)
- **MySQL** (for persistent storage)
- **Redis** (for caching frequently accessed data)
- **Prometheus + Loki + Grafana** (for monitoring and logging)
- **Docker** (for containerized deployment)
- **Swagger** (for API documentation)

[//]: # (## 🛠️ How to Run Locally)

[//]: # (```sh)

[//]: # (# Clone the repo)

[//]: # (git clone https://github.com/koshmanvovae/blog-api)

[//]: # (cd blog-api)

[//]: # ()
[//]: # (# Start all services with Docker)

[//]: # (docker-compose up -d)

[//]: # ()
[//]: # (# Access API Docs)

[//]: # (http://localhost:8080/swagger-ui.html)

[//]: # (```)

## 📈 Monitoring & Logging
- **Grafana Dashboard:** [http://localhost:3000](http://localhost:3000)
- **Prometheus Metrics:** [http://localhost:9090](http://localhost:9090)
- **Loki Logs:** Integrated with Grafana

## 🔥 What’s Next?
- Add WebSockets for real-time updates on comments and likes.
- Implement rate-limiting for API security.
- CI/CD pipeline for automated testing and deployment.

---
💡 _This project showcases how much can be done in a short time when leveraging modern frameworks and best practices. It’s efficient, scalable, and production-ready._ 🚀

