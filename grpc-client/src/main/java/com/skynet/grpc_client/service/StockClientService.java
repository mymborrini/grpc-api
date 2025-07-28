package com.skynet.grpc_client.service;

import com.skynet.grpc_server.StockRequest;
import com.skynet.grpc_server.StockResponse;
import com.skynet.grpc_server.StockTradingServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

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






}
