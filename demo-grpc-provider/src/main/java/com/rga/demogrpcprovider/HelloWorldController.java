package com.rga.demogrpcprovider;

import com.rga.demogrpcjava.stubs.HelloWorldRequest;
import com.rga.demogrpcjava.stubs.HelloWorldResponse;
import com.rga.demogrpcjava.stubs.HelloWorldServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.util.Optional;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class HelloWorldController extends HelloWorldServiceGrpc.HelloWorldServiceImplBase {

  private static final Logger log = LoggerFactory.getLogger(HelloWorldController.class);

  private HelloWorldResponse sayHello2(HelloWorldRequest request) {
    log.info("Received request: {}", request);
    return HelloWorldResponse
        .newBuilder()
        .setGreeting("Hello "
            + Optional
            .of(request.getName())
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .orElse("World")
            + "!"
        )
        .build();
  }

  @Override
  public StreamObserver<HelloWorldRequest> sayHello2(
      StreamObserver<HelloWorldResponse> responseObserver
  ) {
    return StreamObserverUtility.proxyStream(
        responseObserver,
        this::sayHello2
    );
  }
}
