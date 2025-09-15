package com.smartpayment.nxt.hierarchy.parameters.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseConfigDTO implements Serializable {
  private String username;
  private String password;
  private String engine;
  private String host;
  private String port;
  private String dbname;
  private String dbInstanceIdentifier;
}
