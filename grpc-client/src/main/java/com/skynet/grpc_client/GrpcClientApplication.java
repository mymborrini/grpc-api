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

		/*log.info("Invoke the subscribe Stock Method");

		for (String stockSymbol : stockSymbolList) {
			stockClientService.subscribeStockPrice(stockSymbol);
		} */

		// stockClientService.placeBulkOrders();


		//stockClientService.liveTrading();

	}
}
