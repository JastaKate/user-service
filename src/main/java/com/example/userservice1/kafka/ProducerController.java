//package com.example.userservice1.kafka;
//
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class ProducerController {
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    public ProducerController(KafkaTemplate<String, String> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    @GetMapping("/send")
//    public void send() {
//        kafkaTemplate.send("test", "test");
//    }
//}
