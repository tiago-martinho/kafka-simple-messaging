package pt.tiago.kafka_producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

		MessageProducer producer = context.getBean(MessageProducer.class);

		producer.sendMessage("message from spring app");

		context.close();

	}

	@Bean
	public MessageProducer messageProducer() {
		return new MessageProducer();
	}

	public static class MessageProducer {

		Logger logger = Logger.getGlobal();

		@Autowired
		private KafkaTemplate<String, String> kafkaTemplate;

		@Value(value = "${message.topic.name}")
		private String topicName;

		public void sendMessage(String message) {

			ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);

			future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
				@Override
				public void onFailure(Throwable throwable) {
					logger.log(Level.WARNING, "Unable to send message=[" + message + "] due to : " + throwable.getMessage());
				}

				@Override
				public void onSuccess(SendResult<String, String> result) {
					logger.log(Level.INFO, "Sent message=[" + message + "] " +
							"with offset=[" + result.getRecordMetadata().offset() + "]");
				}
			});
		}
	}
	
}
