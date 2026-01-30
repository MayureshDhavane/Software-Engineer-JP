package com.jpmc.midascore;
import com.jpmc.midascore.UserPopulator;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaskFourTests {
    static final Logger logger = LoggerFactory.getLogger(TaskFourTests.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserRepository userRepository;

    @Test
    void task_four_verifier() throws InterruptedException {
        String[] transactionLines = fileLoader.loadStrings("/test_data/lkjhgfdsa.hjkl");
        
        userPopulator.populate();
        
        for (String transactionLine : transactionLines) {
            kafkaProducer.send("transactions", transactionLine);
        }

        // Wait for processing
        Thread.sleep(2000);

        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("use your debugger to find out what wilbur's balance is after all transactions are processed");
        logger.info("kill this test once you find the answer");

        while (true) {
            Thread.sleep(5000); 
            logger.info("...");
            
            logger.info("🔥🔥🔥 DUMPING ALL BALANCES 🔥🔥🔥");
            for (UserRecord user : userRepository.findAll()) {
                if (user.getName().equals("wilbur")) {
                    logger.info("FOUND HIM! User: " + user.getName() + " | Balance: " + user.getBalance());
                }
            }
            logger.info("----------------------------------");
        }
    }
}