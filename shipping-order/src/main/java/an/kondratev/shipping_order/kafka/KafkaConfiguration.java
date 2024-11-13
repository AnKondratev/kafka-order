package an.kondratev.shipping_order.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic newTopic() {
            return new NewTopic("sent_orders", 1, (short) 1);
    }
}
