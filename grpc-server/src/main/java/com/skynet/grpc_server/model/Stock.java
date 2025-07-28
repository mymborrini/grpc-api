package com.skynet.grpc_server.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Stock {

  String symbol;
  double price;
  String timeStamp;

}
