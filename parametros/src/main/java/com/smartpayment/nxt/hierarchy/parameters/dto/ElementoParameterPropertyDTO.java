package com.smartpayment.nxt.hierarchy.parameters.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElementoParameterPropertyDTO  implements Serializable {
  @Serial
  private static final long serialVersionUID = 4895047728520760729L;
  private String label;
  private String length;
  @SerializedName("data_type")
  private String dataType;
  @SerializedName("field_name")
  private String fieldName;
  @SerializedName("default_value")
  private String defaultValue;
  @SerializedName("place_holder")
  private String placeHolder;
  @SerializedName("required")
  private Boolean required;
}
