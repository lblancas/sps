package com.smartpayment.nxt.hierarchy.parameters.constant;

public final class ParameterConstants {

  private ParameterConstants() {
  }

  /**
   * Constantes relacionadas con las respuestas de los servicios
   */
  public static final class ResponseCode {
    public static final String CATEGORY = "hierarchy-parameters";
    public static final String PROFILE_CATEGORY = "parameters";
    public static final String PROFILE_CREATE_PROP= "create_prop";
    public static final String PROFILE_DISABLE_PRO = "disable_prop";
    public static final String PROFILE_DELETE_PROP= "delete_prop";
    public static final String PROFILE_DISABLE = "disable";
    public static final String PROFILE_PROPAGATE= "propagate";
    public static final String PROFILE_QUERY = "request";
    public static final String STATUS_ACTIVE = "1";
    public static final Integer STATUS_PROPAGATE =1;
  }








  /**
   * Constantes relacionadas con errores de operaciones
   */
  public static final class ErrorMessages {
    public static final String NXT_MSA_HIERARCHY_PARAMETERS_400 = "nxt-msa-hierarchy-parameters_400";
    public static final String NXT_MSA_HIERARCHY_PARAMETERS_500 = "nxt-msa-hierarchy-parameters_500";
    public static final String BAD_REQUEST_DATA = "Par\u00E1metros inv\u00E1lidos";
    public static final String ACCESS_DENIED = "nxt-msa-hierarchy-parameter_401";
  }

  public static final String ERROR_CREATE_PARAMETER_JSON="nxt-msa-hierarchy-parameter_401_70";
  public static final String ERROR_CREATE_PARAMETER="nxt-msa-hierarchy-parameter_401_71";
  public static final String PARAMETER_INVALIDATE="nxt-msa-hierarchy-parameter_400_60";
  public static final String PAGINATION_PARAMETER="nxt-msa-hierarchy-parameter_400_61";
  public static final String ERROR_UPDATE_STATUS_PARAMETERS_EMPTY="nxt-msa-hierarchy-parameter_400_62";
  public static final String ERROR_SPREAD_PARAMETER="nxt-msa-hierarchy-parameter_400_63";
  public static final String ERROR_SPREAD_PROPERTY_PARAMETER="nxt-msa-hierarchy-parameter_400_64";
  public static final String ERROR_TAKE_PARAMETER="nxt-msa-hierarchy-parameter_400_65";
  public static final String ERROR_TAKE_PROPERTY_PARAMETER="nxt-msa-hierarchy-parameter_400_66";
  public static final String ERROR_DELETE_PARAMETER="nxt-msa-hierarchy-parameter_400_67";
  public static final String ERROR_GROUP_PARAMETER="nxt-msa-hierarchy-parameter_400_68";
  public static final String ERROR_NODE_ENABLED="nxt-msa-hierarchy-parameter_400_78";

  public static final String ID_SYSTEM = "idSystem";
  public static final String ID_PROPERTY = "idProperty";
  public static final String ID_PARAMETER = "idParameter";
  public static final String ATTRIBUTES = "attributes";
  public static final String ID_HIERARCHY = "idHierarchy";
  public static final String PARAM_ID = "id";
  public static final String PAGE = "page";
  public static final String SIZE = "size";
  public static final String ESPANOL = "es";
}
