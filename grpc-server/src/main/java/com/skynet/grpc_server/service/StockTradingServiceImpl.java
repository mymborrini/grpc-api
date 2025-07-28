package com.skynet.grpc_server.service;

import com.skynet.grpc_server.StockRequest;
import com.skynet.grpc_server.StockResponse;
import com.skynet.grpc_server.StockResponseOrBuilder;
import com.skynet.grpc_server.StockTradingServiceGrpc;
import com.skynet.grpc_server.model.Stock;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.util.HashMap;
import java.util.Map;

@GrpcService
public class StockTradingServiceImpl extends StockTradingServiceGrpc.StockTradingServiceImplBase {


  Map<String, Stock> stocks = new HashMap<>();

  // The method returns void. So we need to work with StreamObserver. If you look inside StreamObserver interface
  // it works more or less like a node callback class. It has onNext, onError, onComplete method
  @Override
  public void getStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {

    // Initialize Stocks
    stocks.put("1", new Stock().setPrice(23.3).setTimeStamp("1234565"));

    // In the request you find stockSymbol -> get if from the map response -> return

    String stockSymbol = request.getStockSymbol();
    var stock = stocks.get(stockSymbol);

    StockResponse stockResponse = StockResponse.newBuilder().setStockSymbol(stockSymbol).setPrice(stock.getPrice()).setTimestamp(stock.getTimeStamp()).build();

    responseObserver.onNext(stockResponse);
    responseObserver.onCompleted();

  }
}
