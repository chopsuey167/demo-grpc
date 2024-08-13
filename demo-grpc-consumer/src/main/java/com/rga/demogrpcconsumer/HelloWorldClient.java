package com.rga.demogrpcconsumer;

import com.rga.demogrpcjava.stubs.HelloWorldRequest;
import com.rga.demogrpcjava.stubs.HelloWorldResponse;
import com.rga.demogrpcjava.stubs.HelloWorldServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.util.stream.Stream;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldClient {
  private static final Logger log = LoggerFactory.getLogger(HelloWorldClient.class);

  @GrpcClient("hello")
  HelloWorldServiceGrpc.HelloWorldServiceStub stub;


  @EventListener(ContextRefreshedEvent.class)
  void sendRequests() {
    // prepare the response callback
    final StreamObserver<HelloWorldResponse> responseObserver = new StreamObserver<HelloWorldResponse>() {
      @Override
      public void onNext(HelloWorldResponse helloWorldResponse) {
        log.info("Hello World Response: {}", helloWorldResponse.getGreeting());
      }

      @Override
      public void onError(Throwable throwable) {
        log.error("Error occured", throwable);
      }

      @Override
      public void onCompleted() {
        log.info("Hello World request finished");
      }
    };
    // connect to the server
    final StreamObserver<HelloWorldRequest> request = stub.sayHello(responseObserver);
    // send multiple requests (streaming)
    Stream.of("Tom", "Julia", "Baeldung", "", "Ralf")
        .map(HelloWorldRequest.newBuilder()::setName)
        .map(HelloWorldRequest.Builder::build)
        .forEach(request::onNext);
    request.onCompleted();
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
