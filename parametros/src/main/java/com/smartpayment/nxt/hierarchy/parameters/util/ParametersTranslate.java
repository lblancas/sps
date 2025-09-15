package com.smartpayment.nxt.hierarchy.parameters.util;

import com.smartpayment.nxt.hierarchy.parameters.constant.ParameterConstants;
import com.smartpayment.nxt.hierarchy.parameters.dto.ParameterPropertyObjectDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ResponseDataParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.TranslateDTO;
import com.smartpayments.nxt.dto.ResponseCodeDTO;
import com.smartpayments.nxt.model.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class ParametersTranslate {
  /**
   * Translate the response data parameter
   * @param response
   * @param code
   * @param listTranslate
   * @return
   */

  public ResponseDataParameterDTO getTranslateResponseDataParameter(ResponseDataParameterDTO response, String code, List<TranslateDTO> listTranslate) {
    ResponseCodeDTO codeResponse = ResponseCodeDTO.builder()
        .code(code)
        .htmlMessage(getMessge(code, listTranslate))
        .message(getMessge(code, listTranslate))
        .build();
    response.setResponseCode(codeResponse);
    return response;
  }

  private String getMessge(String code, List<TranslateDTO> listTranslate) {
    Optional<TranslateDTO> dto =
        listTranslate.stream()
            .filter(translate -> code.equals(translate.getCodeMessage()))
            .findFirst();
    if(dto.isPresent())
      return dto.get().getMessage();
    return code;
  }

  /**
   * Translate the response template
   * @param listParaemtersProperty
   * @param code
   * @param listTranslate
   * @return
   */
  public ResponseDTO getTranslateTemplate(List<ParameterPropertyObjectDTO> listParaemtersProperty, String code,List<TranslateDTO> listTranslate) {
    ResponseCodeDTO dto = ResponseCodeDTO.builder().code(code)
        .htmlMessage(getMessge(code, listTranslate))
        .message(getMessge(code, listTranslate))
        .build();
    return ResponseDTO.builder().list(listParaemtersProperty).responseCode(dto).build();
  }
  /**
   * Translate the response group
   * @param groupedParameterDTO
   * @param code
   * @param listTranslate
   * @return
   */
  public ResponseDTO getTranslateGroup(Object groupedParameterDTO, String code, List<TranslateDTO> listTranslate) {
    ResponseCodeDTO dto = ResponseCodeDTO.builder().code(code)
        .htmlMessage(getMessge(code, listTranslate))
        .message(getMessge(code, listTranslate))
        .build();
    return ResponseDTO.builder().clazz(groupedParameterDTO).responseCode(dto).build();
  }

  public static ResponseCodeDTO buildResponseCode(String code, String message) {
    return buildResponseCode(code, message, null, null);
  }

  public static ResponseCodeDTO buildResponseCode(String code, String message, String htmlMessage, String solution) {

    return ResponseCodeDTO.builder()
        .code(code)
        .message(message)
        .htmlMessage(htmlMessage)
        .solution(solution)
        .category(ParameterConstants.ResponseCode.CATEGORY)
        .build();
  }

  public static<E> ResponseDTO<?> buildResponseWithList(List<E> dataList, ResponseCodeDTO responseCode) {
    return ResponseDTO.builder()
        .list(dataList)
        .responseCode(responseCode)
        .build();
  }

  public static<T> ResponseDTO<T> buildResponseWithClass(T data, ResponseCodeDTO responseCode) {
    return ResponseDTO.<T>builder()
        .responseCode(responseCode)
        .clazz(data)
        .build();
  }

}
