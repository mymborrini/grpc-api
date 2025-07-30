package com.skynet.grpc_client;

import com.skynet.grpc_client.service.StockClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class GrpcClientApplication implements CommandLineRunner {

	private final StockClientService stockClientService;

  public GrpcClientApplication(StockClientService stockClientService) {
    this.stockClientService = stockClientService;
  }

  public static void main(String[] args) {
		SpringApplication.run(GrpcClientApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

 		var stockSymbolList = List.of("GOOGLE", "APPLE", "TESLA");

		for (String stockSymbol : stockSymbolList){
			System.out.println("Contact Grpc Server getStockPrice for Stock Symbol: " + stockSymbol + " => Response: " +  stockClientService.getStockPrice(stockSymbol));
		}


		System.out.println("Unary Method completed waiting 70 seconds...");
		TimeUnit.SECONDS.sleep(70);
		System.out.println("Invoke the subscribe Stock Method");

		for (String stockSymbol : stockSymbolList) {
			stockClientService.subscribeStockPrice(stockSymbol);
		}

		System.out.println("Server streaming Method completed waiting 70 seconds...");
		TimeUnit.SECONDS.sleep(70);
		System.out.println("Invoke the place bulk orders Method");

		stockClientService.placeBulkOrders();

		System.out.println("Client streaming Method completed waiting 70 seconds...");
		TimeUnit.SECONDS.sleep(70);
		System.out.println("Invoke the live trading Method");
		stockClientService.liveTrading();

		System.out.println("Bidirectional Method completed");
	}
}
