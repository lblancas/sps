package com.smartpayment.nxt.hierarchy.parameters.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParameterDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = 581207156844546968L;
  private int idSystem;
  private int idParameter;
  private int idProperty;
  private int idHierarchy;
  private String name;
  private String description;
  private String nameEN;
  private int status;
  private int maxElements;
  private Integer spread;
}
