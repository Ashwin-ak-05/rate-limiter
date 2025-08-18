# Prerequisites

- **Run Config Server**: Ensure the config server is running before starting the rate limiter server.
- **RabbitMQ**: Install RabbitMQ for GitHub webhook integration.
- **Hookdeck**: Install Hookdeck and listen on port `8090`.
- **Prometheus**: Run Prometheus using the `docker-compose` file in the `observability` folder.
- **Kafka**: Install Zookeeper and Kafka using the `docker-compose` file in the `events` folder.
- ---
- 
### Rate Limiter Configuration

The rate limiter configuration is fetched dynamically from the following repository:  
[Rate Limiter Config](https://github.com/Ashwin-ak-05/rate-limiter-config/blob/main/testserver.yml)

**Example configuration:**

```yaml
ratelimiter:
  global:
    limit: 200
    duration: 1m
    keyType: USER

  apis:
    global-api:
      strategy: TOKEN_BUCKET
      keyType: USER
      limit: 5
      duration: 10
    test-api:
      strategy: FIXED_WINDOW
      keyType: USER
      limit: 4
      burstlimit: 3
      duration: 10
```
The configuration can be changed dynamically for each API and globally.  
You can dynamically update:

- **strategy**
- **keyType**
- **limit**
- **duration**
---

## Dynamic Configuration Updates

### API for Live Config Updates
```bash
POST http://localhost:8080/actuator/refresh
```
## GitHub Webhook Integration

- Requires **RabbitMQ** to be running:
```bash
docker run -d --hostname my-rabbit --name some-rabbit \
  -p 5672:5672 -p 15672:15672 rabbitmq:management
```
- Use **Hookdeck** to send events to `localhost:8090`.
- Add the **Hookdeck URL** to GitHub webhook settings.
---
# Testing

## Test Controller

The `TestController` demonstrates the usage of the `@RateLimit` annotation.

```java
@CrossOrigin(origins = "*")
@RateLimit(api = "global-api")
@RestController
public class TestController {

    @RateLimit(api = "test-api")
    @GetMapping("/test")
    public String test(@RequestHeader(value = "X-USER-ID", required = false) String userId) {
        return "Request successful";
    }
}
```
The `@RateLimit` annotation can be applied at both:
- **Controller level** – applies to all endpoints in the controller.
- **Method level** – applies only to the specific endpoint.
---

# Testing with HTML and JS

A testing script is deployed on **GitHub Pages**:  
[https://ashwin-ak-05.github.io/rate-limiter/](https://ashwin-ak-05.github.io/rate-limiter/)
---

## Monitoring and Observability

### Prometheus and Micrometer
- **Prometheus** is integrated for monitoring.
- To run Prometheus, use the `docker-compose` file in the `observability` folder.
- Prometheus runs on port `9090`.

### Kafka for Throttle Events
- **Kafka** is used to consume throttle events.
- Install **Zookeeper** and **Kafka** using the `docker-compose` file in the `events` folder.
- Create the kafka Topic
  ```bash
  kafka-topics.sh --create \
  --topic ratelimiter-logs \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1
  ```
---
## How to Run

1. Start the **Config Server**.  
2. Run the **Rate Limiter Server**.  
3. For live configuration updates, you can:
   - Use the API:  
     ```bash
     POST http://localhost:8080/actuator/refresh
     ```
   - Or configure a **GitHub webhook** with **RabbitMQ** and **Hookdeck**.  
4. Monitor metrics using **Prometheus** on port `9090`.  
5. Run the **Kafka Consumer** to receive throttle events using **Kafka**.  

