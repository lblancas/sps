package com.smartpayment.nxt.hierarchy.parameters.service.impl;

public interface AccessService {
  boolean hasAccess(String user,String roleId, String module, String activity);
}
