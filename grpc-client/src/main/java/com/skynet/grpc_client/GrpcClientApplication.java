package com.skynet.grpc_client;

import com.skynet.grpc_client.service.StockClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class GrpcClientApplication implements CommandLineRunner {

	private final StockClientService stockClientService;

	public static void main(String[] args) {
		SpringApplication.run(GrpcClientApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

		for (String stockSymbol : List.of("GOOGLE", "APPLE", "TESLA")){
			log.info("Contact Grpc Server for Stock Symbol: {} => Response: {}", stockSymbol,  stockClientService.getStockPrice(stockSymbol));
		}

	}
}
