package com.smartpayment.nxt.hierarchy.parameters.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParameterJSONDTO implements java.io.Serializable {
  @JsonProperty("LABEL_PARAMETER")
  private String labelParameter;
  @JsonProperty("AMOUNT_MIN")
  private String amountMin;
  @JsonProperty("AMOUNT")
  private String amount;
  @JsonProperty("AMOUNT_MAX")
  private String amountMax;
  @JsonProperty("TYPE")
  private String type;
}
