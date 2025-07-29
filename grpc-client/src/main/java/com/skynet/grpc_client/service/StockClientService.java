package com.skynet.grpc_client.service;

import com.skynet.grpc_server.*;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class StockClientService  {

  // BlockingStub is thought specifically for UNARY
  @GrpcClient("stockService")
  private StockTradingServiceGrpc.StockTradingServiceBlockingStub serviceBlockingStub;

  @GrpcClient("stockService")
  private StockTradingServiceGrpc.StockTradingServiceStub serviceStub;

  public StockResponse getStockPrice(String stockSymbol) {

    StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
    return serviceBlockingStub.getStockPrice(request);
  }

  public void subscribeStockPrice(String stockSymbol) {

    StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
    serviceStub.subscribeStockPrice(request, new StreamObserver<StockResponse>() {
      @Override
      public void onNext(StockResponse stockResponse) {
        // This will be responsible to fetch each response of the server
        System.out.println("New Response receive from Stock Server: " + stockResponse);
      }

      @Override
      public void onError(Throwable throwable) {
        // This will be responsible to fetch the errors
       System.err.println("Error received from Stock Server " + throwable);
      }

      @Override
      public void onCompleted() {
        // This will be called when everything is completed
        System.out.println("Stock Server subScribe rpc completed");
      }
    });

  }

  public void placeBulkOrders(){

    StreamObserver<OrderSummary> responseObserver = new StreamObserver<>() {

      @Override
      public void onNext(OrderSummary orderSummary) {
        // For some reason logs does not work here
        System.out.println("Order Summary Received from Server:");
        System.out.println("Total Orders: " + orderSummary.getTotalOrders());
        System.out.println("Successful Orders: " + orderSummary.getSuccessCount());
        System.out.println("Total Amount: $" + orderSummary.getTotalAmount());
      }

      @Override
      public void onError(Throwable throwable) {
        System.err.println("Error placed Orders");
      }

      @Override
      public void onCompleted() {
        System.out.println("Orders Placed. Ack received");
      }
    };

    StreamObserver<StockOrder> requestStockOrderObserver = serviceStub.bulkStockOrder(responseObserver);

    // Send stream of stock order message/request

    var stockSymbols = List.of("GOOGLE", "APPLE", "TESLA");
    try {

      int count=1;
      for (String stockSymbol : stockSymbols) {
        requestStockOrderObserver.onNext(
                StockOrder.newBuilder()
                        .setOrderId(String.valueOf(count))
                        .setStockSymbol(stockSymbol)
                        .setOrderType(new Random().nextBoolean() ? "SELL" : "BUY")
                        .setPrice(new Random().nextDouble(200))
                        .setQuantity(new Random().nextInt(10))
                        .build());
        count++;
      }

      // Sending ACK
      requestStockOrderObserver.onCompleted();

    } catch (Exception e){
       System.err.println("Error placed Orders " + e);
        requestStockOrderObserver.onError(e);
    }

  }

  public void liveTrading(){

    Map<String, Instant>  liveTradingRequestsTimes = new HashMap<>();

    StreamObserver<StockOrder> requestObserver = serviceStub.liveTrading(new StreamObserver<TradeStatus>() {
      @Override
      public void onNext(TradeStatus tradeStatus) {
        Instant startOrderIdRequest = liveTradingRequestsTimes.get(tradeStatus.getOrderId());
        if (startOrderIdRequest == null) {
          System.err.println("No request with order_id " + tradeStatus.getOrderId() + "sent");
          return;
        }

        Duration duration = Duration.between(startOrderIdRequest, Instant.now());
        System.out.println("Server respond after:" + duration.getSeconds() + " seconds. With the following tradeStatus: " + tradeStatus);

      }

      @Override
      public void onError(Throwable throwable) {
        System.err.println("Error received from server: " + throwable.getMessage());
      }

      @Override
      public void onCompleted() {
        System.out.println("Connection closed by the server successfully");
      }
    });

    List<String> stockSymbols = List.of("GOOGLE", "APPLE", "TESLA");

    // Sending multiple order request from client
    for (int i = 0; i < 20; i++) {

      String stockSymbol = stockSymbols.get(new Random().nextInt(stockSymbols.size() - 1));

      StockOrder stockOrder = StockOrder.newBuilder()
              .setOrderId(String.valueOf(i))
              .setStockSymbol(stockSymbol)
              .setOrderType(new Random().nextBoolean() ? "SELL" : "BUY")
              .setPrice(new Random().nextDouble(200))
              .setQuantity(new Random().nextInt(10))
              .build();

      liveTradingRequestsTimes.put(stockOrder.getOrderId(), Instant.now());

      requestObserver.onNext(stockOrder);
      try {
        TimeUnit.SECONDS.sleep(1);
      }
      catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    System.out.println("20 request done. The connection with Server can be closed");
    requestObserver.onCompleted();


  }




}
