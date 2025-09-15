package com.smartpayment.nxt.hierarchy.parameters.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseParameterDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = -2096843637720784487L;
  private Integer id;
  @JsonProperty("AMOUNT")
  private String amount;
  private Integer status;

  @NotNull
  private Integer idSystem;

  @NotNull
  private Integer idProperty;

  @NotNull
  private Integer idHierarchy;

  @NotNull
  private Integer idParameter;

  @JsonProperty("TYPE")
  private String type;
  @JsonProperty("LABEL_PARAMETER")
  private String labelParameter;
  @JsonProperty("AMOUNT_MIN")
  private String amountMin;
  @JsonProperty("AMOUNT_MAX")
  private String amountMax;
  @JsonProperty("delete")
  private Integer deleteParameter;
  @JsonProperty("spread")
  private Integer spreadParameters;
}
