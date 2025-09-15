package com.smartpayment.nxt.hierarchy.parameters.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartpayment.nxt.hierarchy.parameters.dto.GroupedParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ParameterJSONDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ParameterPropertyObjectDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ResponseDataParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ResponseParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.TranslateDTO;
import com.smartpayment.nxt.hierarchy.parameters.exception.HierarchyException;
import com.smartpayment.nxt.hierarchy.parameters.repository.ParametersRepository;
import com.smartpayment.nxt.hierarchy.parameters.service.ParameterService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ATTRIBUTES;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_CREATE_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_CREATE_PARAMETER_JSON;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_DELETE_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_GROUP_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_NODE_ENABLED;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_SPREAD_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_SPREAD_PROPERTY_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_TAKE_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_TAKE_PROPERTY_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_UPDATE_STATUS_PARAMETERS_EMPTY;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ID_HIERARCHY;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ID_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ID_PROPERTY;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ID_SYSTEM;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.PAGE;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.PAGINATION_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.PARAMETER_INVALIDATE;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.PARAM_ID;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.SIZE;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.ACTIVE;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.CALL_COUNT_PARAMETERS_HIERARCHY;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.CALL_GET_ID_HIERARCHY_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.CALL_GET_PARAMETERS_HIERARCHY;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.COUNT_ELEMENTS_SPREAD;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.DELETE_HIERARCHY_PARAMETERS_CHILD;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.DELETE_HIERARCHY_PARAMETERS_EXTENDS;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.DELETE_ID_HIERARCHY_PARAMETERS;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.GET_GROUPED_PARAMETERS;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.GET_STATUS_HIERARCHY;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.HIERARCHY_PROPERTY_ID_PAGINATION;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.INSERT_HIERARCHY_PARAMETERS_CHILD;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.INSERT_HIERARCHY_PARAMETERS_EXTENDS;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.INSERT_HIERARCHY_PARAMETERS_PROPERTIES_EXTENDS;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.INSERT_HIERARCHY_PROPERTIES_PARAMETERS_CHILD_SYSTEM;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.INSERT_ID_HIERARCHY_PARAMETERS;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.INSERT_ID_HIERARCHY_PARAMETERS_PROPERTIES;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.NO_PROPAGAR;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.PROPAGAR;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.STATUS;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.UPDATE_BT_HIERARCHY_PARAMETER_PROPAGE;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.UPDATE_ID_HIERARCHY_PARAMETERS;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterQueries.UPDATE_STATUS_HIERARCHY_PARAMETERS;
import static com.smartpayment.nxt.hierarchy.parameters.controller.ParameterController.MODULE;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ParameterServiceImpl implements ParameterService {
  private final ParametersRepository parameterRepository;
  private final ObjectMapper mapper;
  private final Integer SPREAD =1;
  private final Integer NOT_SPREAD =2;

  @Override
  @Async
  public CompletableFuture<GroupedParameterDTO> getCatalog(@NotNull Integer id) {
    return CompletableFuture.supplyAsync(() -> {
        log.info("Iniciando getCatalog con id: {}", id);
        validateDataGroup(id);
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue(STATUS, ACTIVE)
            .addValue(ID_HIERARCHY, id);

        log.debug("Obteniendo catálogo con parámetros: {}", params);
        GroupedParameterDTO categories = parameterRepository.getCatalog(params, GET_GROUPED_PARAMETERS);

        log.debug("Verificando spread del catálogo");
        categories.setSpread(verifySpread(categories).join());

        log.info("Catálogo obtenido exitosamente para id: {}", id);
        return categories;
    });
  }
  @Async
  public CompletableFuture<Integer> verifySpread(GroupedParameterDTO groupedParameter) {
    return CompletableFuture.supplyAsync(() -> {
      try{
        log.debug("Verificando spread en groupedParameter");
        if (groupedParameter == null || groupedParameter.getCategories() == null) {
          log.debug("GroupedParameter o sus categorías son nulos, retornando NO_PROPAGAR");
          return NO_PROPAGAR;
        }

        boolean hasSpreadTrue = groupedParameter.getCategories().stream()
            .filter(Objects::nonNull)
            .flatMap(category ->
                (category.getParameterDTOS() == null) ?
                    Stream.empty() :
                    category.getParameterDTOS().stream())
            .filter(param -> param != null && param.getSpread() != null)
            .anyMatch(param -> param.getSpread().equals(PROPAGAR));

        int result = hasSpreadTrue ? PROPAGAR : NO_PROPAGAR;
        log.debug("Resultado de verifySpread: {}", result);
        return result;
      } catch (Exception e) {
        log.error("Error al verificar spread: {}", e.getMessage(), e);
        return NO_PROPAGAR;
      }
    });
  }
  @Async
  @Override
  public CompletableFuture<List<TranslateDTO>> getTranslate(String language) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        log.info("Obteniendo traducciones para language: {}, module: {}", language, MODULE);
        List<TranslateDTO> result = parameterRepository.getTranslate(MODULE, language);
        log.info("Se obtuvieron {} traducciones", result != null ? result.size() : 0);
        return result;
      } catch (Exception e) {
        log.error("Error al obtener traducciones. Language: {}, Module: {}, Error: {}", language, MODULE, e.getMessage(), e);
        return new ArrayList<>();
      }
    });
  }

  private void validateDataGroup(Integer id) throws HierarchyException {
    log.debug("Validando id de grupo: {}", id);
    if (id == null || id <= 0) {
      log.warn("ID de grupo inválido: {}", id);
      throw new HierarchyException(ERROR_GROUP_PARAMETER, "El ID debe ser mayor que cero");
    }
  }
  @Async
  @Override
  public CompletableFuture<List<ParameterPropertyObjectDTO>> getTemplate(Integer hierarchyId, Integer parameterId) {
    return CompletableFuture.supplyAsync(() -> {
        log.info("Obteniendo template para hierarchyId: {}, parameterId: {}", hierarchyId, parameterId);
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue(ID_HIERARCHY, hierarchyId)
            .addValue(STATUS, ACTIVE)
            .addValue(ID_PARAMETER, parameterId);

        List<ParameterPropertyObjectDTO> result = parameterRepository.getTemplate(params);
        log.info("Template obtenido con {} elementos", result != null ? result.size() : 0);
        return result;
    });
  }
  @Async
  @Override
  public CompletableFuture<ResponseDataParameterDTO> getDataHierarchy(Integer idHierarchy, Integer idSystem, Integer idProperty, Integer page, Integer size) {
    validateDataParameter(idHierarchy, idSystem, idProperty, page, size);
    return CompletableFuture.supplyAsync(() -> {
        log.info("Obteniendo datos de jerarquía. idHierarchy: {}, idSystem: {}, idProperty: {}, page: {}, size: {}",
            idHierarchy, idSystem, idProperty, page, size);
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue(ID_HIERARCHY, idHierarchy)
            .addValue(ID_SYSTEM, idSystem)
            .addValue(SIZE, size)
            .addValue(PAGE, page)
            .addValue(ID_PROPERTY, idProperty);

        log.debug("Consultando contador de parámetros");
        int count = parameterRepository.getForInt(CALL_COUNT_PARAMETERS_HIERARCHY, params);
        int totalPaginas = count / size + (count % size > 0 ? 1 : 0);

        log.debug("Encontrados {} registros, total páginas: {}", count, totalPaginas);
        ResponseDataParameterDTO result = parameterRepository.getParametersHierarchy(params, count, totalPaginas, size, page,
            CALL_GET_PARAMETERS_HIERARCHY + " " + HIERARCHY_PROPERTY_ID_PAGINATION);
        result.setSpread(
            result.getData().stream()
            .filter(param->param.getSpreadParameters() != null && param.getSpreadParameters().equals(SPREAD))
            .findFirst()
            .map(p -> SPREAD)
            .orElse(NOT_SPREAD));
        log.info("Datos de jerarquía obtenidos exitosamente");
        return result;
    });
  }

  private void validateDataParameter(Integer hierarchyId, Integer parameterId, Integer idProperty, Integer page, Integer size) throws HierarchyException {
    log.debug("Validando parámetros: hierarchyId={}, parameterId={}, idProperty={}, page={}, size={}",
        hierarchyId, parameterId, idProperty, page, size);

    if (hierarchyId == null || parameterId == null || idProperty == null) {
      log.warn("Parámetros inválidos: hierarchyId={}, parameterId={}, idProperty={}",
          hierarchyId, parameterId, idProperty);
      throw new HierarchyException(PARAMETER_INVALIDATE, "Parametro invalido de entrada");
    }

    if (page < 0 || size <= 0) {
      log.warn("Parámetros de paginación inválidos: page={}, size={}", page, size);
      throw new HierarchyException(PAGINATION_PARAMETER, "Parametro invalido de pagina y numero de elementos");
    }
  }

  @Transactional
  @Async
  @Override
  public CompletableFuture<ResponseParameterDTO> create(ResponseParameterDTO dto) {
    validateNodeDisbaled(dto.getIdHierarchy());
    return CompletableFuture.supplyAsync(() -> {
        log.info("Creando parámetro: {}", dto);
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue(ID_SYSTEM, dto.getIdSystem())
            .addValue(ID_PROPERTY, dto.getIdProperty())
            .addValue(ID_PARAMETER, dto.getIdParameter())
            .addValue(ID_HIERARCHY, dto.getIdHierarchy())
            .addValue(STATUS, dto.getStatus());

        log.debug("Consultando si existe el parámetro en la jerarquía");
        Integer idHierarchyParameters = parameterRepository.getForInt(CALL_GET_ID_HIERARCHY_PARAMETER, params);
        log.info("GET ID DTO PARAMETERS PROPERTIES{}", idHierarchyParameters);

        dto.setIdParameter(idHierarchyParameters);
        params.addValue(ID_PARAMETER, idHierarchyParameters);

        String json;
        try {
          json = mapper.writeValueAsString(new ParameterJSONDTO(dto.getLabelParameter(), dto.getAmountMin(), dto.getAmount(), dto.getAmountMax(), dto.getType()));
          log.debug("DTO convertido a JSON: {}", json);
        } catch (JsonProcessingException e) {
          log.error("Error al convertir DTO a JSON: {}", e.getMessage(), e);
          throw new HierarchyException(ERROR_CREATE_PARAMETER_JSON, "Error al convertir DTO a JSON");
        }

        params.addValue(ATTRIBUTES, json);
        int elementoNuevo = parameterRepository.createObject(INSERT_ID_HIERARCHY_PARAMETERS_PROPERTIES, params);
        log.info("Elemento creado con ID: {}", elementoNuevo);

        updateStatusPropagate(dto.getIdHierarchy(), dto.getIdSystem(), PROPAGAR);

        if (elementoNuevo == 0) {
          log.error("Error al crear el parámetro, no se insertó ningún registro");
          throw new HierarchyException(ERROR_CREATE_PARAMETER, "Error al crear el parámetro");
        }

        dto.setId(elementoNuevo);
        return dto;
    });
  }

  @Transactional
  @Async
  @Override
  public CompletableFuture<Void> update(ResponseParameterDTO dto) {
    // Validación sincrónica (puede lanzar excepción, se atrapará abajo)
    validateNodeDisbaled(dto.getIdHierarchy());
      log.info("Iniciando actualización del parámetro: {}", dto);
      // Lógica principal
      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("id", dto.getId())
          .addValue(STATUS, dto.getStatus());

      int updated = parameterRepository.updateObject(UPDATE_ID_HIERARCHY_PARAMETERS, params);
      log.info("Registros actualizados: {}", updated);

      if (updated != 1) {
        log.warn("Se esperaba actualizar 1 registro, pero se actualizaron: {}", updated);
        throw new HierarchyException(ERROR_UPDATE_STATUS_PARAMETERS_EMPTY, "Error al actualizar el parámetro, registros afectados: " + updated);
      }

      updateStatusPropagate(dto.getIdHierarchy(), dto.getIdSystem(), PROPAGAR);
      log.info("Actualización del parámetro completada exitosamente");

      return CompletableFuture.completedFuture(null);
  }


  @Transactional
  @Async
  @Override
  public CompletableFuture<Integer> spread(ResponseParameterDTO dto) {
    return CompletableFuture.supplyAsync(() -> {
      Integer contElementsToSpread = 0;
      validateNodeDisbaled(dto.getIdHierarchy());
      try {
        log.info("Propagando parámetro: {}", dto);
        MapSqlParameterSource params = getParametersBySpread(dto);

        log.debug("Eliminando parámetros hijos existentes");
        int deletedCount = parameterRepository.updateObject(DELETE_HIERARCHY_PARAMETERS_CHILD, params);
        log.debug("Registros eliminados: {}", deletedCount);

        log.debug("Insertando nuevos parámetros hijos");
        contElementsToSpread = parameterRepository.getForInt(COUNT_ELEMENTS_SPREAD, params);
        if (contElementsToSpread > 0) {
          Integer parameters = parameterRepository.updateObject(INSERT_HIERARCHY_PARAMETERS_CHILD, params);
          log.info("Parámetros insertados: {}", parameters);

          if (parameters != null && parameters > 0) {
            log.debug("Insertando propiedades de parámetros");
            Integer properties = parameterRepository.updateObject(INSERT_HIERARCHY_PROPERTIES_PARAMETERS_CHILD_SYSTEM, params);
            log.info("Propiedades insertadas: {}", properties);

            if (properties == null || properties <= 0) {
              log.error("No se insertaron propiedades al propagar el parámetro");
             // throw new HierarchyException(ERROR_SPREAD_PARAMETER, "Error al propagar el parámetro");
            }
          } else {
            log.error("No se insertaron parámetros al propagar");
            throw new HierarchyException(ERROR_SPREAD_PROPERTY_PARAMETER, "Error al propagar el parámetro-propiedad");
          }
          updateStatusPropagate(dto.getIdHierarchy(), dto.getIdSystem(), NO_PROPAGAR);
          log.info("Parámetro propagado exitosamente");

        } else {
          log.info("No existen Parámetros a propagar");
        }
      } catch (HierarchyException e) {
        log.error("Error de jerarquía al propagar parámetro: {}", e.getMessage());
        throw e;
      } catch (Exception e) {
        log.error("Error inesperado al propagar parámetro. DTO: {}, Error: {}", dto, e.getMessage(), e);
        throw new HierarchyException(ERROR_SPREAD_PARAMETER, "Error al propagar el parámetro: " + e.getMessage());
      }
      return contElementsToSpread;
    });
  }

  @Transactional
  @Async
  @Override
  public CompletableFuture<Void> takeHierarchy(ResponseParameterDTO dto) {

      log.info("Iniciando proceso de tomar jerarquía para parámetro: {}", dto);

      validateNodeDisbaled(dto.getIdHierarchy());

      MapSqlParameterSource params = getParametersBySpread(dto);

      log.debug("Eliminando extensiones de parámetros existentes...");
      int deletedCount = parameterRepository.updateObject(DELETE_HIERARCHY_PARAMETERS_EXTENDS, params);
      log.debug("Registros de extensiones eliminados: {}", deletedCount);

      log.debug("Insertando nuevas extensiones de parámetros...");
      Integer insertedParams = parameterRepository.updateObject(INSERT_HIERARCHY_PARAMETERS_EXTENDS, params);
      log.info("Extensiones de parámetros insertadas: {}", insertedParams);

      if (insertedParams == null || insertedParams <= 0) {
        log.error("No se insertaron parámetros al tomar la jerarquía");
        throw new HierarchyException(ERROR_TAKE_PROPERTY_PARAMETER, "Error al tomar el parámetro-propiedad");
      }

      log.debug("Insertando propiedades de los parámetros extendidos...");
      Integer insertedProperties = parameterRepository.updateObject(INSERT_HIERARCHY_PARAMETERS_PROPERTIES_EXTENDS, params);
      log.info("Propiedades de parámetros insertadas: {}", insertedProperties);

      if (insertedProperties == null || insertedProperties <= 0) {
        log.error("No se insertaron propiedades al tomar la jerarquía");
        throw new HierarchyException(ERROR_TAKE_PARAMETER, "Error al tomar el parámetro");
      }

      log.info("Jerarquía tomada exitosamente para parámetro: {}", dto.getId());
      return CompletableFuture.completedFuture(null);
  }


  private MapSqlParameterSource getParametersBySpread(ResponseParameterDTO dto) {
    try {
      validateDataGroup(dto.getIdHierarchy());
      MapSqlParameterSource map = new MapSqlParameterSource()
          .addValue(STATUS, ACTIVE)
          .addValue(ID_HIERARCHY, dto.getIdHierarchy())
          .addValue(ID_SYSTEM, dto.getIdSystem(), java.sql.Types.INTEGER);
      return map;
    } catch (HierarchyException e) {
      log.error("Error al validar datos para spread: {}", e.getMessage());
      throw e;
    } catch (Exception e) {
      log.error("Error inesperado al obtener parámetros para spread. DTO: {}, Error: {}", dto, e.getMessage(), e);
      throw new HierarchyException(ERROR_GROUP_PARAMETER, "Error al obtener parámetros para spread: " + e.getMessage());
    }
  }

  @Transactional
  @Async
  @Override
  public CompletableFuture<Void> delete(ResponseParameterDTO dto) {
      log.info("Iniciando eliminación del parámetro: {}", dto);

      validateNodeDisbaled(dto.getIdHierarchy());

      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue(PARAM_ID, dto.getId());

      log.debug("Ejecutando eliminación en base de datos...");
      int deleted = parameterRepository.updateObject(DELETE_ID_HIERARCHY_PARAMETERS, params);
      log.info("Registros eliminados: {}", deleted);

      updateStatusPropagate(dto.getIdHierarchy(), dto.getIdSystem(), PROPAGAR);

      if (deleted == 0) {
        log.error("No se eliminó ningún registro para el parámetro con ID: {}", dto.getId());
        throw new HierarchyException(ERROR_DELETE_PARAMETER, "Error al eliminar el parámetro: no se afectaron registros.");
      }

      log.info("Parámetro eliminado exitosamente: ID {}", dto.getId());
      return CompletableFuture.completedFuture(null);

  }


  @Transactional
  @Async
  @Override
  public CompletableFuture<GroupedParameterDTO> disable(ParameterDTO dto) {
    try {
      log.info("Iniciando deshabilitación del parámetro: {}", dto);

      validateNodeDisbaled(dto.getIdHierarchy());

      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue(ID_SYSTEM, dto.getIdSystem())
          .addValue(ID_PROPERTY, dto.getIdProperty())
          .addValue(ID_HIERARCHY, dto.getIdHierarchy())
          .addValue(STATUS, dto.getStatus());

      log.debug("Consultando existencia del parámetro en la jerarquía");
      Integer idHierarchyParameters = parameterRepository.getForObjectInt(CALL_GET_ID_HIERARCHY_PARAMETER, params);

      if (idHierarchyParameters == null || idHierarchyParameters <= 0) {
        log.debug("Parámetro no existe, insertando nuevo");
        parameterRepository.createObject(INSERT_ID_HIERARCHY_PARAMETERS, params);
      } else {
        log.debug("Parámetro existe, actualizando estado");
        params.addValue("id", idHierarchyParameters);
        parameterRepository.updateObject(UPDATE_STATUS_HIERARCHY_PARAMETERS, params);
      }

      // Reestablecer estado para propagación
      params.addValue(STATUS, ACTIVE);
      updateStatusPropagate(dto.getIdHierarchy(), dto.getIdSystem(), PROPAGAR);

      log.debug("Consultando catálogo actualizado");
      GroupedParameterDTO categories = parameterRepository.getCatalog(params, GET_GROUPED_PARAMETERS);

      // Composición asincrónica con verifySpread
      return verifySpread(categories)
          .thenApply(spread -> {
            categories.setSpread(spread);
            log.info("Parámetro deshabilitado exitosamente");
            return categories;
          })
          .exceptionally(ex -> {
            log.error("Error al evaluar spread: {}", ex.getMessage(), ex);
            throw new HierarchyException(ERROR_UPDATE_STATUS_PARAMETERS_EMPTY, "Error al deshabilitar el parámetro: " + ex.getMessage());
          });

    } catch (Exception e) {
      log.error("Error inesperado al deshabilitar parámetro. DTO: {}, Error: {}", dto, e.getMessage(), e);
      return CompletableFuture.failedFuture(new HierarchyException(
          ERROR_UPDATE_STATUS_PARAMETERS_EMPTY,
          "Error inesperado al deshabilitar el parámetro: " + e.getMessage()
      ));
    }
  }


  private void updateStatusPropagate(Integer idHierarchy, Integer idSystem, Integer status) {
    validateNodeDisbaled(idHierarchy);
    try {
      log.debug("Actualizando estado de propagación. idHierarchy: {}, idSystem: {}, status: {}",
          idHierarchy, idSystem, status);

      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue(ID_HIERARCHY, idHierarchy)
          .addValue(ID_SYSTEM, idSystem, java.sql.Types.INTEGER)
          .addValue(STATUS, status);

      int elementos = parameterRepository.updateObject(UPDATE_BT_HIERARCHY_PARAMETER_PROPAGE, params);
      log.info("Elementos actualizados en propagación: {}", elementos);
    } catch (Exception e) {
      log.error("Error al actualizar estado de propagación. idHierarchy: {}, idSystem: {}, status: {}, Error: {}",
          idHierarchy, idSystem, status, e.getMessage(), e);
      throw new HierarchyException(ERROR_UPDATE_STATUS_PARAMETERS_EMPTY, "Error al actualizar estado de propagación: " + e.getMessage());
    }
  }

  private void validateNodeDisbaled(@NotNull Integer idHierarchy) {
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue(ID_HIERARCHY, idHierarchy);
    Integer status = parameterRepository.getForInt(GET_STATUS_HIERARCHY, params);
    log.debug("Consultando status de hiersrchy  {}", status);
    if(!status.equals(ACTIVE))
      throw new HierarchyException(ERROR_NODE_ENABLED, "Error node deshabilitado");
  }
}
