package com.example.demo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication(scanBasePackages="com.example.demo")
@EnableIntegration
@RestController
@ImportResource("classpath:integration-context.xml")
public class DemoApplication {

	@Autowired
	@Qualifier("channel1")
	private MessageChannel channel1;
	@Autowired
	@Qualifier("channel2")
	private MessageChannel channel2;
	
	@Autowired
	@Qualifier("httpChannel")
	private MessageChannel httpChannel;

	private AtomicInteger atomicId = new AtomicInteger();

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@GetMapping("channel1/{MSG}")
	public String channel1(@PathVariable("MSG") String msg) {
		channel1.send(new GenericMessage<>(msg));
		return "Chibana's integration\n";
	}
	@GetMapping("channel2/{MSG}")
	public String channel2(@PathVariable("MSG") String msg) {
		channel2.send(new GenericMessage<>(msg));
		return "Chibana's integration\n";
	}

	@GetMapping("start/{MSG}")
	public ResponseEntity<Serializable> start(@PathVariable("MSG") String msg) {
		httpChannel.send(this.createMsg(msg));
		return ResponseEntity.ok(msg);
	}

	@PostMapping("httpservice")
	public ResponseEntity<Serializable> channel3(@RequestBody String message) {
        HelloModel model = new HelloModel("OK",atomicId.getAndIncrement());
		return  ResponseEntity.ok(model);
	}

    private Message<Serializable> createMsg(String msg) {
        Map<String, Object> map = new HashMap<>();
        final int id = atomicId.getAndIncrement();
        map.put("ID", id);
        MessageHeaders messageHeaders = new MessageHeaders(map);
        HelloModel model = new HelloModel(msg, 10);
        String json = null;
        try {
			json = new ObjectMapper().writeValueAsString(model);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
        return MessageBuilder.createMessage(json, messageHeaders);
    }
}
