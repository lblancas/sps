package com.smartpayment.nxt.hierarchy.parameters.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TranslateDTO {
  private String codeMessage;
  private String message;
}
