package an.kondratev.payment.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@AllArgsConstructor
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sandMessageWithRetry(String order, int maxRetries) throws ExecutionException, InterruptedException {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                kafkaTemplate.send("payed_orders", order).get();
                log.info("Message sent successfully on attempt: {}", attempt + 1);
                return;
            } catch (Exception e) {
                log.error("Error sending order: {}: {}", attempt + 1, e.getMessage());
                if (attempt == maxRetries - 1) {
                    log.error("Error sending order: {}: {}", attempt + 1, e.getMessage());
                    throw e;
                }
                try {
                    log.info("Sleeping for 5 seconds...");
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    log.error("Error sleeping for 5 seconds: {}", e1.getMessage());
                    Thread.currentThread().interrupt();
                    throw e1;
                }
            }
        }
    }
}