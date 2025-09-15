package com.smartpayment.nxt.hierarchy.parameters.exception.handler;

import com.smartpayment.nxt.exception.handler.GenericExceptionHandler;
import com.smartpayment.nxt.hierarchy.parameters.exception.HierarchyException;
import com.smartpayment.nxt.messaging.ResponseMessageComponent;
import com.smartpayments.nxt.model.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler extends GenericExceptionHandler {

  private final ResponseMessageComponent responseMessageComponent;

  @Override
  public ResponseMessageComponent getResponseMessage() {
    return responseMessageComponent;
  }

  @Override
  public String getNxtMsaName() {
    return "";
  }

  @ExceptionHandler({HierarchyException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseDTO<String> handleHierarchyException(HierarchyException e) {
    Object[] args = Stream.concat(Stream.of(e.getCode()), e.getMessageArgs() != null ? Arrays.stream(e.getMessageArgs()) : Stream.of("null")).toArray();
    log.error(MessageFormat.format("Hierarchy  Exception with code {0} and args {1}", args), e);
    return responseMessageComponent.buildResponse(e.getCode(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessageArgs());
  }


  @ExceptionHandler({CompletionException.class})
  public ResponseEntity<ResponseDTO<String>> handleCompletionException(CompletionException ec) {
    if (ec.getCause() instanceof HierarchyException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(handleHierarchyException(e));
    }else{
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(handleException(ec));
    }

  }
}
