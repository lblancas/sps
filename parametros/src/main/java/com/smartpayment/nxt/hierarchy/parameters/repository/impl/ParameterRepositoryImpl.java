package com.smartpayment.nxt.hierarchy.parameters.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smartpayment.nxt.hierarchy.parameters.dto.*;
import com.smartpayment.nxt.hierarchy.parameters.repository.ParametersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ResponseCode.STATUS_ACTIVE;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ParameterRepositoryImpl implements ParametersRepository {
  private final ObjectMapper mapper;
  private final NamedParameterJdbcTemplate primaryJdbcTemplate;
  private final NamedParameterJdbcTemplate readOnlyJdbcTemplate;


  @Override
  public GroupedParameterDTO getCatalog(MapSqlParameterSource params, String query) {
    GroupedParameterDTO result = new GroupedParameterDTO();
    result.setCategories(new ArrayList<>());
    try {
      readOnlyJdbcTemplate.query(query, params, (rs) -> {
        String categoryName = rs.getString("category_name");
        CategoryDTO category = getCategory(rs, result, categoryName);
        ParameterDTO parameter = getGroupParameter(rs);
        category.getParameterDTOS().add(parameter);
      });
    } catch (DataAccessException e) {
      log.error("Error al obtener el catálogo de parámetros. Query: {}, Params: {}, Error: {}", query, params, e.getMessage(), e);
      throw new RuntimeException("Error al obtener el catálogo de parámetros", e);
    }
    return result;
  }

  @Override
  public List<TranslateDTO> getTranslate(String category, String language) {
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("language", language);
    params.addValue("category", category);
    try {
      return readOnlyJdbcTemplate.query(
          GET_HIERARCHY_TRANSLATE_PARAMETERS,
          params,
          (rs, rowNum) -> TranslateDTO.builder()
              .codeMessage(rs.getString("code_message"))
              .message(rs.getString("message"))
              .build()
      );
    } catch (DataAccessException e) {
      log.error("Error al obtener traducciones. Category: {}, Language: {}, Error: {}", category, language, e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  private static CategoryDTO getCategory(ResultSet rs, GroupedParameterDTO result, String categoryName) {
    CategoryDTO category = null;
    try {
      category = result.getCategories().stream()
          .filter(c -> c.getName().equals(categoryName))
          .findFirst()
          .orElseGet(() -> {
            CategoryDTO newCategory;
            try {
              newCategory = CategoryDTO.builder()
                  .name(categoryName)
                  .nameEN(categoryName)
                  .description(rs.getString("category_description"))
                  .build();
            } catch (SQLException e) {
              log.error("No se tiene datos para categoria descripcion. Category: {}, Error: {}", categoryName, e.getMessage(), e);
              newCategory = new CategoryDTO();
            }
            result.getCategories().add(newCategory);
            newCategory.setParameterDTOS(new ArrayList<>());
            return newCategory;
          });
    } catch (Exception e) {
      log.error("Error al obtener la categoría. Category: {}, Error: {}", categoryName, e.getMessage(), e);
      throw new RuntimeException("Error al obtener la categoría", e);
    }
    return category;
  }

  private static ParameterDTO getGroupParameter(ResultSet rs) throws SQLException {
    try {
      return ParameterDTO.builder()
          .idSystem(rs.getInt("idsystem"))
          .idParameter(rs.getInt("idparameter"))
          .idProperty(rs.getInt("idproperty"))
          .idHierarchy(rs.getInt("idhierarchy"))
          .status(rs.getInt(STATUS))
          .maxElements(rs.getInt("max_properties"))
          .name(rs.getString("parameter_name"))
          .nameEN(rs.getString("parameter_name"))
          .spread(rs.getInt("spread"))
          .description(rs.getString("parameter_description"))
          .build();
    } catch (SQLException e) {
      log.error("Error al mapear parámetro del grupo. Error: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public ResponseDataParameterDTO getParametersHierarchy(MapSqlParameterSource params, Integer count, Integer totalPaginas,
                                                         Integer size, Integer page, String query) {
    ResponseDataParameterDTO response = new ResponseDataParameterDTO();
    try {
      List<ResponseParameterDTO> lista = readOnlyJdbcTemplate.query(query, params, (rs, rowNum) -> {
        ResponseParameterDTO dto = new ResponseParameterDTO();
        String attributes = rs.getString("hierarchy_attributes");
        if (attributes != null) {
          try {
            dto = mapper.readValue(attributes, ResponseParameterDTO.class);
          } catch (JsonProcessingException e) {
            log.error("No tiene datos en atributos. Attributes: {}, Error: {}", attributes, e.getMessage(), e);
          }
        }
        dto.setIdSystem(rs.getInt("idSystem"));
        dto.setIdProperty(rs.getInt("idProperty"));
        dto.setIdParameter(rs.getInt("idParamater"));
        dto.setIdHierarchy(rs.getInt("idHierarchy"));
        dto.setId(rs.getInt("id"));
        dto.setStatus(rs.getInt(STATUS));
        dto.setDeleteParameter(2);
        dto.setSpreadParameters(rs.getInt("spread"));
        return dto;
      });
      response.setData(lista);
      response.setPage(page);
      response.setSize(size);
      response.setTotalElements(count);
      response.setTotalPages(totalPaginas);
    } catch (DataAccessException e) {
      log.error("Error al obtener la jerarquía de parámetros. Query: {}, Params: {}, Error: {}", query, params, e.getMessage(), e);
      throw new RuntimeException("Error al obtener la jerarquía de parámetros", e);
    }
    return response;
  }

  @Override
  public Integer getForObjectInt(String query, MapSqlParameterSource pathParams) {
    try {
      return readOnlyJdbcTemplate.queryForObject(query, pathParams, (rs, rowNum) ->
          rs.getObject(1, Integer.class));
    } catch (DataAccessException e) {
      log.error("Error en getForObjectInt. Query: {}, Params: {}, Error: {}", query, pathParams, e.getMessage(), e);
      return null;
    }
  }

  @Override
  public Integer getForInt(String query, MapSqlParameterSource pathParams) {
    try {
      return readOnlyJdbcTemplate.queryForObject(query, pathParams, (rs, rowNum) ->
          rs.getInt(1));
    } catch (DataAccessException e) {
      log.error("Error en getForInt. Query: {}, Params: {}, Error: {}", query, pathParams, e.getMessage(), e);
      return null;
    }
  }

  @Override
  public String replaceParam(String query, MapSqlParameterSource pathParams) {
    try {
      for (String key : pathParams.getValues().keySet()) {
        Object value = pathParams.getValue(key);
        query = query.replace(":" + key, value != null ? ((value instanceof String) ? "'" + value + "'" : value.toString()) : "-");
      }
      return query;
    } catch (Exception e) {
      log.error("Error en replaceParam. Query: {}, Params: {}, Error: {}", query, pathParams, e.getMessage(), e);
      throw new RuntimeException("Error al reemplazar parámetros en la query", e);
    }
  }

  @Override
  public List<ParameterPropertyObjectDTO> getTemplate(MapSqlParameterSource params) {
    try {
      return readOnlyJdbcTemplate.query(CALL_GET_PARAMETERS_TEMPLATE, params, (rs, rowNum) -> {
        ParameterPropertyObjectDTO property = new ParameterPropertyObjectDTO();
        try {
          property.setIdSystem(rs.getInt("idSystem"));
          property.setIdParameter(rs.getInt("idParameter"));
          property.setIdHierarchy(rs.getInt("idHierarchy"));
          property.setStatus(rs.getInt(STATUS));
          String jsonAttributes = rs.getString("property_attributes");
          if (jsonAttributes != null) {
            property.setElements(converterJsonToParameterProperty(jsonAttributes));
          }
        } catch (SQLException e) {
          log.error("Error al mapear property en getTemplate. Error: {}", e.getMessage(), e);
        }
        return property;
      });
    } catch (DataAccessException e) {
      log.error("Error en getTemplate. Params: {}, Error: {}", params, e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  private List<ElementoParameterPropertyDTO> converterJsonToParameterProperty(String json) {
    try {
      Gson gson = new Gson();
      return gson.fromJson(json, new TypeToken<List<ElementoParameterPropertyDTO>>() {}.getType());
    } catch (Exception e) {
      log.error("Error al convertir JSON a ElementoParameterPropertyDTO. JSON: {}, Error: {}", json, e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  @Override
  public Integer createObject(String query, MapSqlParameterSource map) {
    try {
      return primaryJdbcTemplate.queryForObject(query, map, Integer.class);
    } catch (DataAccessException e) {
      log.error("Error en createObject. Query: {}, Params: {}, Error: {}", query, map, e.getMessage(), e);
      return null;
    }
  }

  @Override
  public Integer updateObject(String query, MapSqlParameterSource pathParams) {
    try {
      return primaryJdbcTemplate.update(query, pathParams);
    } catch (DataAccessException e) {
      log.error("Error en updateObject. Query: {}, Params: {}, Error: {}", query, pathParams, e.getMessage(), e);
      return null;
    }
  }
  @Override
  public boolean hasAccess(String user, String roleId, String module, String activity) {
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("roleId", roleId,java.sql.Types.VARCHAR)
        .addValue("moduleId", module,java.sql.Types.VARCHAR)
        .addValue("userId", user,java.sql.Types.VARCHAR)
        .addValue("statusProfileId", STATUS_ACTIVE, Types.INTEGER)
        .addValue("activityId", activity,java.sql.Types.VARCHAR);
    Integer status = getForInt(GET_ACCESS_CONTROLLER, params);
    return status >0;
  }
}
