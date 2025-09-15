package com.smartpayment.nxt.hierarchy.parameters.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterPropertyObjectDTO implements Serializable {


  @Serial
  private static final long serialVersionUID = 8664647985884072555L;
  @NotNull
  private Integer idSystem;
  @NotNull
  private Integer idParameter;
  @NotNull
  private Integer idHierarchy;
  @NotNull
  private Integer status;
  @Builder.Default
  private List<ElementoParameterPropertyDTO> elements = new ArrayList<>();

}
