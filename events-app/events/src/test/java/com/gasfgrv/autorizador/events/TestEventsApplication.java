package com.gasfgrv.autorizador.events;

import org.springframework.boot.SpringApplication;

public class TestEventsApplication {

	public static void main(String[] args) {
		SpringApplication.from(EventsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
