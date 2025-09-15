package com.smartpayment.nxt.hierarchy.parameters.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = -7069252821322904901L;
  private String name;
  private String description;
  private String nameEN;
  private List<ParameterDTO> parameterDTOS;
}
