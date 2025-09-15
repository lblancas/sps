package com.smartpayment.nxt.hierarchy.parameters.repository;
import com.smartpayment.nxt.hierarchy.parameters.dto.GroupedParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ParameterPropertyObjectDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ResponseDataParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.TranslateDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

public interface ParametersRepository {
  GroupedParameterDTO getCatalog(MapSqlParameterSource params,String query);

  ResponseDataParameterDTO getParametersHierarchy(MapSqlParameterSource params, Integer count, Integer totalPaginas,
                                                  Integer size, Integer page,String query);

  Integer getForObjectInt(String query, MapSqlParameterSource pathParams);

  Integer getForInt(String query, MapSqlParameterSource pathParams);

  String replaceParam(String query, MapSqlParameterSource pathParams);

  List<ParameterPropertyObjectDTO> getTemplate(MapSqlParameterSource params);

  Integer createObject(String query, MapSqlParameterSource map);

  Integer updateObject(String query, MapSqlParameterSource pathParams);

  List<TranslateDTO> getTranslate(String category,String language);

  boolean hasAccess(String user, String roleId, String module, String activity);
}
