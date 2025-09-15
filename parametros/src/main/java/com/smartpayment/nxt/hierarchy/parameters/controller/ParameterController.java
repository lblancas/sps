package com.smartpayment.nxt.hierarchy.parameters.controller;

import com.smartpayment.nxt.hierarchy.parameters.aop.CheckAccess;
import com.smartpayment.nxt.hierarchy.parameters.dto.GroupedParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ParameterPropertyObjectDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ResponseDataParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ResponseParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.TranslateDTO;
import com.smartpayment.nxt.hierarchy.parameters.exception.HierarchyException;
import com.smartpayment.nxt.hierarchy.parameters.service.ParameterService;
import com.smartpayment.nxt.hierarchy.parameters.util.ParametersTranslate;
import com.smartpayments.nxt.model.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_CREATE_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_DELETE_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_GROUP_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_SPREAD_PROPERTY_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_TAKE_PARAMETER;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ERROR_UPDATE_STATUS_PARAMETERS_EMPTY;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ESPANOL;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ResponseCode.PROFILE_CATEGORY;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ResponseCode.PROFILE_CREATE_PROP;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ResponseCode.PROFILE_DELETE_PROP;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ResponseCode.PROFILE_DISABLE;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ResponseCode.PROFILE_DISABLE_PRO;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ResponseCode.PROFILE_PROPAGATE;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ResponseCode.PROFILE_QUERY;
import static com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants.ResponseCode.STATUS_PROPAGATE;

@Slf4j
@RestController
@RequestMapping("/api/v1/hierarchy/parameters")
@RequiredArgsConstructor
public class ParameterController {
  public static final String MODULE = "parameters";
  private final ParameterService parametersService;
  private final ParametersTranslate parametersTranslate;
  public volatile List<TranslateDTO> listTranslate=null;
  @Operation(summary = "Obtener catálogo de parámetros")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Catálogo obtenido exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))})
  @ResponseStatus(HttpStatus.OK)
  @CheckAccess(module = PROFILE_CATEGORY, activity = PROFILE_QUERY)
  @GetMapping(value = "/catalog", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseDTO getCatalog(@RequestParam(value = "id", required = true) Integer id, ServletResponse servletResponse, @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = ESPANOL) String acceptLanguage) {
      try{
        GroupedParameterDTO groupedParameter = parametersService.getCatalog(id).get();
        return parametersTranslate.getTranslateGroup(
            groupedParameter,
            "nxt-msa-hierarchy-parameter_200_01",
            getCachedTranslate(acceptLanguage)
        );
      } catch (InterruptedException | ExecutionException e) {
        throw new HierarchyException(ERROR_GROUP_PARAMETER, "Error al obtener catalogo de jerarquía: " + e.getMessage());
      }

  }

  @Operation(summary = "Obtener plantilla de parámetro")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Plantilla obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))})
  @ResponseStatus(HttpStatus.OK)
  @CheckAccess(module = PROFILE_CATEGORY, activity = PROFILE_QUERY)
  @GetMapping(value = "/template", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseDTO getTemplate(@RequestParam(value = "id", required = true) Integer id, @RequestParam(value = "parameter", required = true) Integer parameter, HttpServletRequest request, ServletResponse servletResponse, @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = ESPANOL) String acceptLanguage) {
    try{
      List<ParameterPropertyObjectDTO> listParameterPropertyObjectDTO = parametersService.getTemplate(id,parameter).get();
      return parametersTranslate.getTranslateGroup(
          listParameterPropertyObjectDTO,
          "nxt-msa-hierarchy-parameter_200_02",getCachedTranslate(acceptLanguage));
    } catch (InterruptedException | ExecutionException e) {
      throw new HierarchyException(ERROR_GROUP_PARAMETER, "Error al obtener template de jerarquía: " + e.getMessage());
    }
  }

  @Operation(summary = "Obtener lista de parámetros")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Parámetros obtenidos exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class)))})
  @ResponseStatus(HttpStatus.OK)
  @CheckAccess(module = PROFILE_CATEGORY, activity = PROFILE_QUERY)
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseDataParameterDTO getParameters(@RequestParam(value = "id", required = true) Integer id, @RequestParam(value = "parameter", required = true) Integer parameter, @RequestParam(value = "property", required = true) Integer property, @RequestParam(value = "page", required = true) Integer page, @RequestParam(value = "size", required = true) Integer size, HttpServletRequest request, ServletResponse servletResponse, @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = ESPANOL) String acceptLanguage) {
    try{
      ResponseDataParameterDTO responseDataParameterDTO = parametersService.getDataHierarchy (id, parameter, property, page, size).get();
      return parametersTranslate.getTranslateResponseDataParameter(
          responseDataParameterDTO,
          "nxt-msa-hierarchy-parameter_200_03",  getCachedTranslate(acceptLanguage));
    } catch (InterruptedException | ExecutionException e) {
      throw new HierarchyException(ERROR_GROUP_PARAMETER, "Error al obtener datos de jerarquía: " + e.getMessage());
    }

  }

  @Operation(summary = "Crear nuevo parámetro")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Parámetro creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class)))})
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  @CheckAccess(module = PROFILE_CATEGORY, activity = PROFILE_CREATE_PROP)
  public ResponseDataParameterDTO createParameter(@Valid @RequestBody ResponseParameterDTO dto, HttpServletRequest request, @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = ESPANOL) String acceptLanguage) {
    try {
      ResponseParameterDTO created = parametersService.create(dto).get();
      return parametersTranslate.getTranslateResponseDataParameter(
          ResponseDataParameterDTO.builder().data(Collections.singletonList(created)).build(),
          "nxt-msa-hierarchy-parameter_201_01",
          getCachedTranslate(acceptLanguage)
      );
    } catch (InterruptedException | ExecutionException e) {
      throw new HierarchyException(ERROR_CREATE_PARAMETER, "Error al crear parámetro: " + e.getMessage());
    }
  }

  @Operation(summary = "Actualizar parámetro existente")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Parámetro actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class)))})
  @ResponseStatus(HttpStatus.OK)
  @CheckAccess(module = PROFILE_CATEGORY, activity = PROFILE_DISABLE_PRO)
  @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseDataParameterDTO updateParameter(@Valid @RequestBody ResponseParameterDTO dto, HttpServletRequest request, @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = ESPANOL) String acceptLanguage) {
    try{
      parametersService.update(dto).get();
      dto.setSpreadParameters(STATUS_PROPAGATE);
      return parametersTranslate.getTranslateResponseDataParameter(
          ResponseDataParameterDTO.builder().data(Arrays.asList(dto)).build(),  dto.getStatus() == 1 ?
              "nxt-msa-hierarchy-parameter_200_04" : "nxt-msa-hierarchy-parameter_200_05", getCachedTranslate(acceptLanguage));
    } catch (InterruptedException | ExecutionException e) {
      throw new HierarchyException(ERROR_UPDATE_STATUS_PARAMETERS_EMPTY, "Error al modificar parámetro: " + e.getMessage());
    }
  }

  @Operation(summary = "Propagar parámetro")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Parámetro propagado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class)))})
  @ResponseStatus(HttpStatus.OK)
  @CheckAccess(module = PROFILE_CATEGORY, activity = PROFILE_PROPAGATE)
  @PutMapping(value = "/spread", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseDataParameterDTO spreadParameter(@Valid @RequestBody ResponseParameterDTO dto, HttpServletRequest request, @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = ESPANOL) String acceptLanguage) {
    try{
        Integer elementsToSpread= parametersService.spread(dto).get();
        String codeMessage="nxt-msa-hierarchy-parameter_200_11";
        if(elementsToSpread>0){
          codeMessage="nxt-msa-hierarchy-parameter_200_06";
        }
        return parametersTranslate.getTranslateResponseDataParameter(
            ResponseDataParameterDTO.builder().data(Arrays.asList(dto)).build(),  codeMessage,  getCachedTranslate(acceptLanguage));
    } catch (InterruptedException | ExecutionException e) {
      throw new HierarchyException(ERROR_SPREAD_PROPERTY_PARAMETER, "Error al modificar parámetro: " + e.getMessage());
    }
  }

  @Operation(summary = "Deshabilitar parámetro")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Parámetro deshabilitado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))})
  @ResponseStatus(HttpStatus.OK)
  @CheckAccess(module = PROFILE_CATEGORY, activity = PROFILE_DISABLE)
  @PutMapping(value = "/enable", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseDTO disableParameter( @Valid @RequestBody ParameterDTO dto, HttpServletRequest request, @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = ESPANOL) String acceptLanguage) {
    try{
      GroupedParameterDTO  dato =parametersService.disable(dto).get();
        return parametersTranslate.getTranslateGroup(dato, (dto.getStatus() == 1 ?
            "nxt-msa-hierarchy-parameter_200_07" : "nxt-msa-hierarchy-parameter_200_08"),getCachedTranslate(acceptLanguage));
    } catch (InterruptedException | ExecutionException e) {
      throw new HierarchyException(ERROR_UPDATE_STATUS_PARAMETERS_EMPTY, "Error al modificar parámetro: " + e.getMessage());
    }
  }

  @Operation(summary = "Eliminar parámetro")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Parámetro eliminado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class)))})
  @ResponseStatus(HttpStatus.OK)
  @CheckAccess(module = PROFILE_CATEGORY, activity = PROFILE_DELETE_PROP)
  @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseDataParameterDTO deleteParameter( @Valid @RequestBody ResponseParameterDTO dto, HttpServletRequest request, @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = ESPANOL) String acceptLanguage) {
    try{
      parametersService.delete(dto).get();
      dto.setDeleteParameter(1);
      return parametersTranslate.getTranslateResponseDataParameter(
          ResponseDataParameterDTO.builder().data(Arrays.asList(dto)).build(),
          "nxt-msa-hierarchy-parameter_200_09", getCachedTranslate(acceptLanguage));
    } catch (InterruptedException | ExecutionException e) {//
      throw new HierarchyException(ERROR_DELETE_PARAMETER, "Error al modificar parámetro: " + e.getMessage());
    }
  }
  @Operation(summary = "Toma parámetros de Parent")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Parámetros heredados con éxito", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "403", description = "Prohibido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class))), @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDataParameterDTO.class)))})
  @ResponseStatus(HttpStatus.OK)
  @PutMapping(value = "/take", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseDataParameterDTO takeParameter(@Valid @RequestBody ResponseParameterDTO dto, HttpServletRequest request, @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = ESPANOL) String acceptLanguage) {
    try{
      parametersService.takeHierarchy(dto).get();
      return parametersTranslate.getTranslateResponseDataParameter(
          ResponseDataParameterDTO.builder().data(Arrays.asList(dto)).build(),
          "nxt-msa-hierarchy-parameter_200_10",  getCachedTranslate(acceptLanguage));
    } catch (InterruptedException | ExecutionException e) {//
      throw new HierarchyException(ERROR_TAKE_PARAMETER, "Error al modificar parámetro: " + e.getMessage());
    }
  }
  private List<TranslateDTO> getCachedTranslate(String language) {
    if (listTranslate == null || listTranslate.isEmpty()) {
      synchronized (this) {
        if (listTranslate == null || listTranslate.isEmpty()) {
          listTranslate = parametersService.getTranslate(language).join(); // o .get() con manejo de excepciones
        }
      }
    }
    return listTranslate;
  }
}
