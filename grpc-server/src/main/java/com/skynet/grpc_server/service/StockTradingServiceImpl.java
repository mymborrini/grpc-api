package com.skynet.grpc_server.service;

import com.skynet.grpc_server.StockRequest;
import com.skynet.grpc_server.StockResponse;
import com.skynet.grpc_server.StockResponseOrBuilder;
import com.skynet.grpc_server.StockTradingServiceGrpc;
import com.skynet.grpc_server.model.Stock;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@GrpcService
@Slf4j
public class StockTradingServiceImpl extends StockTradingServiceGrpc.StockTradingServiceImplBase {


  Map<String, Stock> stocks = Map.of(
          "GOOGLE", new Stock().setPrice(24.1).setTimeStamp("12345635"),
          "APPLE", new Stock().setPrice(33.3).setTimeStamp("987654332"),
          "TESLA", new Stock().setPrice(15.6).setTimeStamp("123987045")
  );

  // The method returns void. So we need to work with StreamObserver. If you look inside StreamObserver interface
  // it works more or less like a node callback class. It has onNext, onError, onComplete method
  @Override
  public void getStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {

    // In the request you find stockSymbol -> get if from the map response -> return

    String stockSymbol = request.getStockSymbol();
    var stock = stocks.get(stockSymbol);

    StockResponse stockResponse = StockResponse.newBuilder().setStockSymbol(stockSymbol).setPrice(stock.getPrice()).setTimestamp(stock.getTimeStamp()).build();

    responseObserver.onNext(stockResponse);
    responseObserver.onCompleted();

  }

  @Override
  public void subscribeStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
    String stockSymbol = request.getStockSymbol();

    try {
      for (int i = 0; i < 10; i++) {
        StockResponse stockResponse = StockResponse.newBuilder()
                .setStockSymbol(stockSymbol)
                .setPrice(new Random().nextDouble(200))
                .setTimestamp(Instant.now().toString())
                .build();
        responseObserver.onNext(stockResponse);

        // This is used to give the effect of a server response
        TimeUnit.SECONDS.sleep(1);
      }
    } catch (InterruptedException e) {
      log.error(e.getMessage());
      responseObserver.onError(e);
    }


  }
}
