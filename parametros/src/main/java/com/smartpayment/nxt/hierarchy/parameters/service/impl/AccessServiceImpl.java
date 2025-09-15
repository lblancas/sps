package com.smartpayment.nxt.hierarchy.parameters.service.impl;

import com.smartpayment.nxt.hierarchy.parameters.repository.ParametersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AccessServiceImpl implements AccessService{
  private final ParametersRepository repositoryHierarchy;
  public boolean hasAccess(String user,String roleId, String module, String activity) {
    return repositoryHierarchy.hasAccess(user, roleId,  module,  activity);
  }
}
