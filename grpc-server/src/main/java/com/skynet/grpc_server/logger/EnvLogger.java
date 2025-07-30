package com.skynet.grpc_server.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EnvLogger implements CommandLineRunner {

  @Value("${app.custom-message}")
  private String customMessage;

  @Override
  public void run(String... args) throws Exception {
    log.info("Application Custom message: {}", customMessage);
  }
}
