package com.smartpayment.nxt.hierarchy.parameters.dto;

import com.smartpayments.nxt.dto.ResponseCodeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDataParameterDTO implements java.io.Serializable {
  @Serial
  private static final long serialVersionUID = -5725510959052978398L;
  private List<ResponseParameterDTO> data= new ArrayList<>();
  private Integer page;
  private Integer size;
  private Integer spread;
  private Integer totalPages;
  private Integer totalElements;
  private ResponseCodeDTO responseCode;
}
