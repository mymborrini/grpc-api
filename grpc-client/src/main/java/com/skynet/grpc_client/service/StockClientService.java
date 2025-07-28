package com.skynet.grpc_client.service;

import com.skynet.grpc_server.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
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
        log.info("New Response receive from Stock Server: {}", stockResponse);
      }

      @Override
      public void onError(Throwable throwable) {
        // This will be responsible to fetch the errors
        log.error("Error received from Stock Server", throwable);
      }

      @Override
      public void onCompleted() {
        // This will be called when everything is completed
        log.info("Stock Server subScribe rpc completed");
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
        log.error("Error placed Orders", e);
        requestStockOrderObserver.onError(e);
    }

  }






}
