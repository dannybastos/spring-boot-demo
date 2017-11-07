package com.example.demo;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class MarioService {

	Logger log = LoggerFactory.getLogger(getClass());
	@ServiceActivator(inputChannel="pubChannel")
	public void start(Message<Serializable> message) {
		String msg = null;
		if (message.getPayload() instanceof HelloModel) {
			msg = ((HelloModel) message.getPayload()).toString();
		} else {
			msg = (String) message.getPayload();
		}		
		log.info(String.format("PUB => ID=%s, PAYLODAD:%s", message.getHeaders().get("ID"),msg));
	}
	
}