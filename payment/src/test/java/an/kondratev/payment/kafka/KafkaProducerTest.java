package an.kondratev.payment.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Nested
@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testSendMessageWithRetry_FailureAfterMaxRetries() {
        String order = "order3";
        when(kafkaTemplate.send(anyString(), anyString())).thenThrow(new RuntimeException("Failure"));

        assertThrows(RuntimeException.class, () -> kafkaProducer.sandMessageWithRetry(order, 2));

        verify(kafkaTemplate, times(2)).send("payed_orders", order);
    }

    @Test
    void testSendMessageWithRetry_InterruptedExceptionHandled() {
        String order = "order4";
        when(kafkaTemplate.send(anyString(), anyString())).thenThrow(new RuntimeException("Failure"));

        Thread.currentThread().interrupt();

        assertThrows(RuntimeException.class, () -> kafkaProducer.sandMessageWithRetry(order, 2));
    }
}