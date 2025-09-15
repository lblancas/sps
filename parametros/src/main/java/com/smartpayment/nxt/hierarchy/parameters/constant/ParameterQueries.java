package com.smartpayment.nxt.hierarchy.parameters.constant;

public class ParameterQueries {

  /**
   * Constructor privado para evitar la instanciación de la clase.
   */
  private ParameterQueries() {
  }

  /**
   * Obtiene el total de registros de la tabla bt_hierarchy_parameters_properties
   */
  public static final int ACTIVE = 1;
  public static final String STATUS = "status";
  public static final Integer PROPAGAR= 1;
  public static final Integer NO_PROPAGAR= 2;

  public static final String GET_STATUS_HIERARCHY = """
              select h.hierarchy_status status
                  from
                  hierarchy.bt_hierarchy h
                  where  h.hierarchy_id = :idHierarchy
      """;
  public static final String CALL_COUNT_PARAMETERS_HIERARCHY = """
              select count(1)  as total
                  from
                  hierarchy.bt_hierarchy_parameters dt
                  inner join hierarchy.bt_hierarchy_parameters_properties prop
                  on dt.hierarchy_parameter_id = prop.hierarchy_parameter_id
                  where  dt.hierarchy_id = :idHierarchy
                  and dt.parameter_id = :idSystem
                  and dt.property_id = :idProperty
      """;
  /**
   * Obtiene la lista de registros de la tabla bt_hierarchy_parameters_properties
   * paginados
   */
  public static final String CALL_GET_PARAMETERS_HIERARCHY = """
              select dt.hierarchy_id idHierarchy,
                  dt.parameter_id idSystem,
                  dt.property_id idProperty,
                  hierarchy_property_id id,
                  hierarchy_attributes,
                  prop.hierarchy_parameter_id idParamater,
                  prop.status,
                  coalesce(dt.propagate_to_children,2) spread
                  from
                  hierarchy.bt_hierarchy_parameters dt
                  inner join hierarchy.bt_hierarchy_parameters_properties prop
                  on dt.hierarchy_parameter_id = prop.hierarchy_parameter_id
                  where  dt.hierarchy_id = :idHierarchy
                  and dt.parameter_id = :idSystem
                  and dt.property_id = :idProperty
      """;
  public static final String HIERARCHY_PROPERTY_ID_PAGINATION= """
                  ORDER BY hierarchy_property_id  LIMIT :size OFFSET (:size*(:page-1));
      """;
  /**
   * Obtiene la informacion del template
   */
  public static final String CALL_GET_PARAMETERS_TEMPLATE = """
              select
                    par.parameter_id idSystem,
                    btpp.property_id idParameter,
                    bh.hierarchy_id  idHierarchy,
                    bh.hierarchy_status status,
                    btpp.property_attributes ::text
                    FROM  hierarchy.bt_system_parameters par
                    inner join hierarchy.bt_hierarchy bh on bh.node_type_id = ANY(par.applicable_to_node_type)
                    inner join hierarchy.bt_parameter_properties btpp on   btpp.parameter_id = par.parameter_id
                    where bh.hierarchy_id  = :idHierarchy
                    and par.parameter_id  = :idParameter
      """;
  /**
   * Obtiene la relacion con hierarchy parameter
   */
  public static final String CALL_GET_ID_HIERARCHY_PARAMETER = """
              select   dt.hierarchy_parameter_id
              from hierarchy.bt_parameter_properties pp
              left join hierarchy.bt_hierarchy_parameters dt on dt.parameter_id  = pp.parameter_id
              and dt.property_id = pp.property_id
              and dt.hierarchy_id = :idHierarchy
              where pp.property_id = :idProperty
              and pp.parameter_id = :idSystem
      """;
  /**
   * insert hierarchy parameters
   */
  public static final String INSERT_ID_HIERARCHY_PARAMETERS = """
              INSERT INTO hierarchy.bt_hierarchy_parameters
                  (hierarchy_parameter_id, hierarchy_id, parameter_id, property_id, created_at, updated_at, status,propagate_to_children)
                  VALUES(nextval('hierarchy.bt_hierarchy_parameters_hierarchy_parameter_id_seq'::regclass),
                  :idHierarchy, :idSystem, :idProperty,  now(), now(), :status,1)  RETURNING hierarchy_parameter_id;
      """;
  /**
   * Actualiza status de parametros
   */
  public static final String UPDATE_ID_HIERARCHY_PARAMETERS = """
              UPDATE hierarchy.bt_hierarchy_parameters_properties set status = :status where hierarchy_property_id=:id;
      """;
  /**
   * Habilita / deshabilita status de parametros glob al
   */
  public static final String UPDATE_STATUS_HIERARCHY_PARAMETERS = """
              UPDATE hierarchy.bt_hierarchy_parameters
                  SET status=:status
                  WHERE   hierarchy_parameter_id = :id
                  and hierarchy_id = :idHierarchy
      """;
  /**
   * Borrado de parametros
   */
  public static final String DELETE_ID_HIERARCHY_PARAMETERS = """
              DELETE FROM hierarchy.bt_hierarchy_parameters_properties   where hierarchy_property_id=:id;
      """;
    /**
     * Modifica de propagate_to_children a un estatus
     */
    public static final String UPDATE_BT_HIERARCHY_PARAMETER_PROPAGE = """
        update  hierarchy.bt_hierarchy_parameters
            set propagate_to_children=:status, updated_at=now()
            where hierarchy_id= :idHierarchy
            and (:idSystem IS NULL OR parameter_id= :idSystem)
        """;
  /**
   * Inserta los prpiedad del parametro
   */
  public static final String INSERT_ID_HIERARCHY_PARAMETERS_PROPERTIES = """
      INSERT INTO hierarchy.bt_hierarchy_parameters_properties
      (hierarchy_parameter_id, hierarchy_attributes, status, created_at, updated_at)
      VALUES(:idParameter, :attributes::json, :status, now(), now())
            RETURNING hierarchy_property_id
      """;
  public static final String GET_HIERARCHY_TRANSLATE_PARAMETERS  = """
      select  c.code_message  , c.message
      from catalogs.lkp_response_messages m
      inner join  catalogs.lkp_response_message_translations c
      on c.code_message= m.code_message
      where m.category  = :category
      and language_code= :language
      """;

  public static final String GET_GROUPED_PARAMETERS = """
      SELECT
          li.category_name,
          par.parameter_name,
          par.parameter_id idSystem,
          par.parameter_description,
          li.category_description,
          bh.hierarchy_id idhierarchy,
          btpp.property_id idproperty,
          coalesce(bthp.status,2) status,
          coalesce(bthp.hierarchy_parameter_id,0) idParameter,
          max_properties,
          coalesce(bthp.propagate_to_children,2) spread
          FROM  hierarchy.bt_system_parameters par
          inner join hierarchy.lkp_parameter_categories  li on li.category_id=par.category_id
          inner join hierarchy.bt_hierarchy bh on bh.node_type_id = ANY(par.applicable_to_node_type)
          inner join hierarchy.bt_parameter_properties btpp on   btpp.parameter_id = par.parameter_id
          left  join hierarchy.bt_hierarchy_parameters bthp
          on bthp.parameter_id =  par.parameter_id
          and bthp.property_id  =  btpp.property_id
          and bthp.hierarchy_id =  bh.hierarchy_id
          where bh.hierarchy_id  = :idHierarchy
          and   par.status =:status
          order by  li.category_name,
          par.parameter_name
      """;

  public static final String COUNT_ELEMENTS_SPREAD= """
      select count(1) total
      from hierarchy.bt_hierarchy bh
      left join hierarchy.bt_hierarchy bh_child
        on bh.hierarchy_path @> bh_child.hierarchy_path
      inner join hierarchy.bt_system_parameters par
        on bh_child.node_type_id  = ANY(par.applicable_to_node_type)
      inner join hierarchy.bt_parameter_properties btpp
        on btpp.parameter_id = par.parameter_id
      left  join hierarchy.bt_hierarchy_parameters bthp
        on bthp.parameter_id =  par.parameter_id
        and bthp.hierarchy_id  = bh.hierarchy_id
      and bthp.property_id =  btpp.property_id
      inner join hierarchy.bt_hierarchy_parameters prParent
      on  bh.hierarchy_id = prParent.hierarchy_id
      and prParent.parameter_id  = par.parameter_id
      where bh.hierarchy_id  = :idHierarchy
      and  bh_child.hierarchy_id  not  in(bh.hierarchy_id)
      and bthp.propagate_to_children=1
      and   par.status =:status
      and (:idSystem IS NULL OR par.parameter_id= :idSystem)
      """;
  public static final String INSERT_HIERARCHY_PARAMETERS_CHILD= """
      insert into hierarchy.bt_hierarchy_parameters
      ( hierarchy_id, parameter_id, property_id, created_at,  status,  propagate_to_children)
      select
      bh_child.hierarchy_id,
      par.parameter_id ,
      btpp.property_id,
      now()created_at,bthp.status, 2 propagate_to_children
      from hierarchy.bt_hierarchy bh
      left join hierarchy.bt_hierarchy bh_child
        on bh.hierarchy_path @> bh_child.hierarchy_path
      inner join hierarchy.bt_system_parameters par
        on bh_child.node_type_id  = ANY(par.applicable_to_node_type)
      inner join hierarchy.bt_parameter_properties btpp
        on btpp.parameter_id = par.parameter_id
      left  join hierarchy.bt_hierarchy_parameters bthp
        on bthp.parameter_id =  par.parameter_id
        and bthp.hierarchy_id  = bh.hierarchy_id
      and bthp.property_id =  btpp.property_id
      inner join hierarchy.bt_hierarchy_parameters prParent
      on  bh.hierarchy_id = prParent.hierarchy_id
      and prParent.parameter_id  = par.parameter_id
      where bh.hierarchy_id  = :idHierarchy
      and  bh_child.hierarchy_id  not  in(bh.hierarchy_id)
      and bthp.propagate_to_children=1
      and   par.status =:status
      and (:idSystem IS NULL OR par.parameter_id= :idSystem)
""";

  public static final String INSERT_HIERARCHY_PROPERTIES_PARAMETERS_CHILD_SYSTEM="""
    insert into hierarchy.bt_hierarchy_parameters_properties
    (hierarchy_parameter_id,hierarchy_attributes, created_at,status, property_code)
    select
    bthpper.hierarchy_parameter_id ,
    btpppr.hierarchy_attributes,
    now()created_at,btpppr.status,btpppr.property_code
    from hierarchy.bt_hierarchy bh
    left join hierarchy.bt_hierarchy bh_child
      on bh.hierarchy_path @> bh_child.hierarchy_path
      and  bh_child.hierarchy_id  not  in(bh.hierarchy_id)
    inner join hierarchy.bt_system_parameters par
      on bh_child.node_type_id  = ANY(par.applicable_to_node_type)
    inner  join hierarchy.bt_hierarchy_parameters bthp
      on bthp.parameter_id =  par.parameter_id
      and bthp.hierarchy_id  = bh.hierarchy_id
    left join hierarchy.bt_hierarchy_parameters_properties btpppr
      on bthp.hierarchy_parameter_id  = btpppr.hierarchy_parameter_id
    inner  join hierarchy.bt_hierarchy_parameters bthpper
      on bthpper.parameter_id =  par.parameter_id
      and bthpper.hierarchy_id  = bh_child.hierarchy_id
    inner join hierarchy.bt_hierarchy_parameters prParent
      on  bh.hierarchy_id = prParent.hierarchy_id
      and prParent.parameter_id  = par.parameter_id
    where bh.hierarchy_id  = :idHierarchy
    and bthp.propagate_to_children=1
    and   par.status =:status
    and   btpppr.hierarchy_attributes is not  null
    and   btpppr.status is not null
    and (:idSystem IS NULL OR par.parameter_id= :idSystem)
""";

public static final String DELETE_HIERARCHY_PARAMETERS_CHILD="""
  WITH parameters_to_delete AS (
          SELECT   bthp.hierarchy_parameter_id, bh_child.hierarchy_id,par.parameter_id,  prParent.parameter_id
                            FROM hierarchy.bt_hierarchy bh
                            LEFT JOIN hierarchy.bt_hierarchy bh_child
                            ON bh.hierarchy_path @> bh_child.hierarchy_path
                            AND bh_child.hierarchy_id != bh.hierarchy_id
                            INNER JOIN hierarchy.bt_system_parameters par
                            ON bh_child.node_type_id = ANY(par.applicable_to_node_type)
                            inner join hierarchy.bt_hierarchy_parameters prParent
                            on  bh.hierarchy_id = prParent.hierarchy_id
                            and prParent.parameter_id  = par.parameter_id
                    INNER JOIN hierarchy.bt_hierarchy_parameters bthp
                    ON bthp.parameter_id = par.parameter_id
                    AND bthp.hierarchy_id = bh_child.hierarchy_id
                    WHERE bh.hierarchy_id = :idHierarchy
                    and (:idSystem IS NULL OR par.parameter_id= :idSystem)
                    and prParent.propagate_to_children=1
                    AND par.status = :status
    ),
      delete_properties AS (
          DELETE FROM hierarchy.bt_hierarchy_parameters_properties
              WHERE hierarchy_parameter_id IN (SELECT hierarchy_parameter_id FROM parameters_to_delete)
    )
      DELETE FROM hierarchy.bt_hierarchy_parameters
      WHERE hierarchy_parameter_id IN (SELECT hierarchy_parameter_id FROM parameters_to_delete);
""";

public static final String DELETE_HIERARCHY_PARAMETERS_EXTENDS="""
WITH parameters_to_delete AS (
    SELECT DISTINCT bthp.hierarchy_parameter_id
    FROM hierarchy.bt_hierarchy bh
    INNER JOIN hierarchy.bt_hierarchy_parameters bthp
    ON  bthp.hierarchy_id = 	bh.hierarchy_id
    WHERE bh.hierarchy_id = :idHierarchy
    ),
    delete_properties AS (
    DELETE FROM hierarchy.bt_hierarchy_parameters_properties
    WHERE hierarchy_parameter_id IN (SELECT hierarchy_parameter_id FROM parameters_to_delete)
    )
    DELETE FROM hierarchy.bt_hierarchy_parameters
    WHERE hierarchy_parameter_id IN (SELECT hierarchy_parameter_id FROM parameters_to_delete);
""";

public static final String INSERT_HIERARCHY_PARAMETERS_EXTENDS="""
  insert into hierarchy.bt_hierarchy_parameters
      ( hierarchy_id, parameter_id, property_id, created_at,  status,  propagate_to_children)
  select
  bh.hierarchy_id,
  par.parameter_id ,
  btpp.property_id,
  now()created_at,1 , 2
  from hierarchy.bt_hierarchy bh
  left join hierarchy.bt_hierarchy bh_parent
  on bh.hierarchy_parent_id = bh_parent.hierarchy_id
  inner join hierarchy.bt_system_parameters par
  on bh_parent.node_type_id  = ANY(par.applicable_to_node_type)
  inner join hierarchy.bt_parameter_properties btpp
  on btpp.parameter_id = par.parameter_id
  left  join hierarchy.bt_hierarchy_parameters bthp
  on bthp.parameter_id =  par.parameter_id
  and bthp.hierarchy_id  = bh_parent.hierarchy_id
  and bthp.property_id =  btpp.property_id
  where bh.hierarchy_id  = :idHierarchy
  and  bh_parent.hierarchy_id  not  in(bh.hierarchy_id)
  and   par.status =:status;
""";


  public static final String INSERT_HIERARCHY_PARAMETERS_PROPERTIES_EXTENDS="""
    insert into hierarchy.bt_hierarchy_parameters_properties
    (hierarchy_parameter_id,hierarchy_attributes, created_at,status, property_code)
    select
    bthpper.hierarchy_parameter_id ,
    btpppr.hierarchy_attributes,
    now() ,btpppr.status,btpppr.property_code
     from hierarchy.bt_hierarchy bh
    left join hierarchy.bt_hierarchy bh_parent
      on bh.hierarchy_parent_id = bh_parent.hierarchy_id
    inner join hierarchy.bt_system_parameters par
      on bh.node_type_id  = ANY(par.applicable_to_node_type)
    inner  join hierarchy.bt_hierarchy_parameters bthp
      on bthp.parameter_id =  par.parameter_id
      and bthp.hierarchy_id  = bh_parent.hierarchy_id
    left join hierarchy.bt_hierarchy_parameters_properties btpppr
      on bthp.hierarchy_parameter_id  = btpppr.hierarchy_parameter_id
    inner  join hierarchy.bt_hierarchy_parameters bthpper
      on bthpper.parameter_id =  par.parameter_id
      and bthpper.hierarchy_id  = bh.hierarchy_id
    where bh.hierarchy_id  = :idHierarchy
    and   par.status =:status;
""";

  public static final String GET_ACCESS_CONTROLLER = """
      SELECT  count(1) as total
          FROM login.lkp_policies lp
          INNER JOIN login.role_policies rp ON lp.policy_id = rp.policy_id
          INNER JOIN login.lkp_role lr ON rp.role_id = lr.role_id
          INNER JOIN login.profile_role pr ON pr.role_id = lr.role_id
          inner join login.user_role ur  on ur.role_id = pr.role_id and pr.role_id = lr.role_id
          inner join login.lkp_users lu on ur.user_id = lu.user_id
          inner join login.lkp_modules lm on lm.module_id = lp.module_id
          WHERE  lr.role_id = :roleId
            and ur.user_id = :userId
            and lm.name= :moduleId
            and lp.type = :activityId
            and lp.status = :statusProfileId
  """;
}

