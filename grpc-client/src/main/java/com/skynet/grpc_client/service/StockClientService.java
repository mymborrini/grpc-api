package com.skynet.grpc_client.service;

import com.skynet.grpc_server.StockRequest;
import com.skynet.grpc_server.StockResponse;
import com.skynet.grpc_server.StockTradingServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class StockClientService  {

  // BlockingStub is thought specifically for UNARY
  @GrpcClient("stockService")
  private StockTradingServiceGrpc.StockTradingServiceBlockingStub serviceBlockingStub;

  public StockResponse getStockPrice(String stockSymbol) {

    StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
    return serviceBlockingStub.getStockPrice(request);
  }



}
