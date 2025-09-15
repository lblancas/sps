package com.smartpayment.nxt.hierarchy.parameters.service;
import com.smartpayment.nxt.hierarchy.parameters.dto.GroupedParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ParameterPropertyObjectDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ResponseDataParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ResponseParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.TranslateDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ParameterService {

  /**
   * Retrieves a catalog of parameters based on the provided hierarchy ID.
   *
   * @param id the hierarchy ID
   * @return a GroupedParameterDTO containing the catalog of parameters
   */
  CompletableFuture<GroupedParameterDTO> getCatalog(Integer id);

  /**
   * Retrieves a list of parameters based on the provided hierarchy ID.
   *
   * @param groupedParameter
   * @return a list of ParameterDTO containing the parameters
   */
  CompletableFuture<Integer> verifySpread(GroupedParameterDTO groupedParameter);
  /**
   * Busca los translate de los mensajes de parametros
   *
   */
  CompletableFuture<List<TranslateDTO>> getTranslate(String language);

  /**
   * Retrieves a list of teamplate - parameters
   *
   * @param hierarchyId the hierarchy ID
   * @param parameterId the parameter ID
   * @return a list of ParameterPropertyObjectDTO containing the parameters
   */
  CompletableFuture<List<ParameterPropertyObjectDTO>> getTemplate(Integer hierarchyId, Integer parameterId);


  /**
   * Retrieves a list of parameters
   *
   * @param idHierarchy the hierarchy ID
   * @param idSystem    the system ID
   * @param idProperty  the property ID
   * @param page        the page number
   * @param size        the page size
   * @return a ResponseDataParameterDTO containing the parameters
   */
  CompletableFuture<ResponseDataParameterDTO> getDataHierarchy(Integer idHierarchy, Integer idSystem, Integer idProperty, Integer page, Integer size);

  /**
   * Create new Parameter
   *
   * @param dto
   * @return a ResponseDataParameterDTO containing the parameters
   */
  CompletableFuture<ResponseParameterDTO> create(ResponseParameterDTO dto);

  /**
   * Update new Parameter
   *
   * @param dto
   * @return a ResponseDataParameterDTO containing the parameters
   */
  CompletableFuture<Void> update(ResponseParameterDTO dto);
  /**
   * Spread new Parameter
   *
   * @param dto
   * @return a ResponseDataParameterDTO containing the parameters
   */
  CompletableFuture<Integer> spread(ResponseParameterDTO dto);

  /**
   * Take new Parameter
   * @param dto
   */
  CompletableFuture<Void> takeHierarchy(ResponseParameterDTO dto);
  /**
   * Delete3 new Parameter
   *
   * @param dto
   * @return a ResponseDataParameterDTO containing the parameters
   */
  CompletableFuture<Void> delete(ResponseParameterDTO dto);

  /**
   * Disblae Group Parameter
   *
   * @param dto
   * @return a ResponseDataParameterDTO containing the parameters
   */
  CompletableFuture<GroupedParameterDTO> disable(ParameterDTO dto);

}
