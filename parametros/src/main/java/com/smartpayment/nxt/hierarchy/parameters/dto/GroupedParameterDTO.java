package com.smartpayment.nxt.hierarchy.parameters.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupedParameterDTO  implements Serializable{
  @Serial
  private static final long serialVersionUID = -1506066512485067115L;
  private List<CategoryDTO> categories =new ArrayList<>();
  private Integer spread;
  public void append(CategoryDTO categoryDTO) {
    if (categoryDTO == null) {
      return;
    }

    boolean categoryExists = this.categories.stream()
        .anyMatch(m -> m.getName().equals(categoryDTO.getName()));

    if (!categoryExists) {
      this.categories.add(categoryDTO);
    }
  }

}
