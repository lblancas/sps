package com.smartpayment.nxt.hierarchy.parameters.controller;

import com.smartpayment.nxt.hierarchy.parameters.dto.TranslateDTO;
import com.smartpayments.nxt.dto.ResponseCodeDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.smartpayment.nxt.hierarchy.parameters.dto.GroupedParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ParameterPropertyObjectDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ResponseDataParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.dto.ResponseParameterDTO;
import com.smartpayment.nxt.hierarchy.parameters.service.ParameterService;
import com.smartpayment.nxt.hierarchy.parameters.util.ParametersTranslate;
import com.smartpayments.nxt.model.ResponseDTO;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;

class ParameterControllerTest {

  @Mock
  private ParameterService parametersService;
  private static final String ESPANOL = "es";
  @Mock
  private ParametersTranslate parametersTranslate;
  private List<TranslateDTO> translations;
  @InjectMocks
  private ParameterController parameterController;

  private List<TranslateDTO> listTranslate=new ArrayList<>();
  @BeforeEach
  void setUp() {
    translations = List.of(
        new TranslateDTO("nxt-msa-hierarchy-parameter_200_04", "Actualización exitosa"),
        new TranslateDTO("nxt-msa-hierarchy-parameter_200_05", "Eliminación exitosa")
    );
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getCatalogShouldCallServiceAndTranslate() throws Exception {
    // Arrange
    Integer id = 5;
    GroupedParameterDTO groupedParameterDTO = new GroupedParameterDTO();
    ResponseDTO expectedResponse = new ResponseDTO();

    // Lista de traducciones simulada
    List<TranslateDTO> fakeTranslations = List.of(
        TranslateDTO.builder()
            .codeMessage("nxt-msa-hierarchy-parameter_200_01")
            .message("ok")
            .build()
    );

    // Mock del resultado de getCatalog
    when(parametersService.getCatalog(id))
        .thenReturn(CompletableFuture.completedFuture(groupedParameterDTO));

    // Mock del cache de traducciones
    when(parametersService.getTranslate(ESPANOL))
        .thenReturn(CompletableFuture.completedFuture(fakeTranslations));

    // Mock del ensamblado final del ResponseDTO
    when(parametersTranslate.getTranslateGroup(
        eq(groupedParameterDTO),
        eq("nxt-msa-hierarchy-parameter_200_01"),
        eq(fakeTranslations)))
        .thenReturn(expectedResponse);

    // Act
    ResponseDTO actualResponse = parameterController.getCatalog(
        id, mock(ServletResponse.class), "es");

    // Assert
    verify(parametersService).getCatalog(id);
    verify(parametersService).getTranslate(ESPANOL);
    verify(parametersTranslate).getTranslateGroup(
        groupedParameterDTO,
        "nxt-msa-hierarchy-parameter_200_01",
        fakeTranslations
    );
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getTemplateShouldCallServiceAndTranslate() throws Exception {
    // Arrange
    Integer hierarchyId = 10;
    Integer parameterId = 20;
    List<ParameterPropertyObjectDTO> mockTemplateList = List.of(new ParameterPropertyObjectDTO());
    ResponseDTO expectedResponse = new ResponseDTO();

    // Traducciones simuladas (el cache)
    List<TranslateDTO> fakeTranslations = List.of(
        TranslateDTO.builder()
            .codeMessage("nxt-msa-hierarchy-parameter_200_02")
            .message("Plantilla obtenida correctamente")
            .build()
    );

    // Mock del resultado del template
    when(parametersService.getTemplate(hierarchyId, parameterId))
        .thenReturn(CompletableFuture.completedFuture(mockTemplateList));

    // Mock del cache de traducciones
    when(parametersService.getTranslate(ESPANOL))
        .thenReturn(CompletableFuture.completedFuture(fakeTranslations));

    // Mock del ensamblado final del ResponseDTO
    when(parametersTranslate.getTranslateGroup(
        eq(mockTemplateList),
        eq("nxt-msa-hierarchy-parameter_200_02"),
        eq(fakeTranslations)))
        .thenReturn(expectedResponse);

    // Act
    ResponseDTO actualResponse = parameterController.getTemplate(
        hierarchyId, parameterId, mock(HttpServletRequest.class), mock(ServletResponse.class), ESPANOL);

    // Assert
    verify(parametersService).getTemplate(hierarchyId, parameterId);
    verify(parametersService).getTranslate(ESPANOL);
    verify(parametersTranslate).getTranslateGroup(
        mockTemplateList,
        "nxt-msa-hierarchy-parameter_200_02",
        fakeTranslations
    );
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getParametersShouldCallServiceAndTranslate() throws Exception {
    // Arrange
    Integer hierarchyId = 1;
    Integer parameterId = 2;
    Integer propertyId = 3;
    Integer page = 0;
    Integer size = 10;

    ResponseDataParameterDTO mockResponseData = new ResponseDataParameterDTO();
    ResponseDataParameterDTO expectedResponse = new ResponseDataParameterDTO();

    // Traducciones simuladas
    List<TranslateDTO> fakeTranslations = List.of(
        TranslateDTO.builder()
            .codeMessage("nxt-msa-hierarchy-parameter_200_03")
            .message("Parámetros obtenidos correctamente")
            .build()
    );

    // Resultado del servicio simulado
    when(parametersService.getDataHierarchy(hierarchyId, parameterId, propertyId, page, size))
        .thenReturn(CompletableFuture.completedFuture(mockResponseData));

    // Traducciones en caché simuladas
    when(parametersService.getTranslate(ESPANOL))
        .thenReturn(CompletableFuture.completedFuture(fakeTranslations));

    // Traducción aplicada al resultado
    when(parametersTranslate.getTranslateResponseDataParameter(
        eq(mockResponseData),
        eq("nxt-msa-hierarchy-parameter_200_03"),
        eq(fakeTranslations)))
        .thenReturn(expectedResponse);

    // Act
    ResponseDataParameterDTO actualResponse = parameterController.getParameters(
        hierarchyId, parameterId, propertyId, page, size,
        mock(HttpServletRequest.class), mock(ServletResponse.class), "es");

    // Assert
    verify(parametersService).getDataHierarchy(hierarchyId, parameterId, propertyId, page, size);
    verify(parametersService).getTranslate(ESPANOL);
    verify(parametersTranslate).getTranslateResponseDataParameter(
        mockResponseData,
        "nxt-msa-hierarchy-parameter_200_03",
        fakeTranslations
    );
    assertEquals(expectedResponse, actualResponse);
  }
  @Test
  void createParameterShouldReturnTranslatedResponse() {
    // Arrange
    ResponseParameterDTO inputDto = ResponseParameterDTO.builder()
        .idHierarchy(1)
        .idParameter(2)
        .idProperty(3)
        .idSystem(12)
        .amount("1")
        .amountMin("1")
        .amountMax("2")
        .labelParameter("ok")
        .build();
    ResponseParameterDTO createdDto = ResponseParameterDTO.builder()
        .idHierarchy(1)
        .idParameter(2)
        .idProperty(3)
        .idSystem(12)
        .amount("1")
        .id(2)
        .amountMin("1")
        .amountMax("2")
        .labelParameter("ok")
        .build();
    String expectedCode = "nxt-msa-hierarchy-parameter_201_01";
    String expectedMessage = "Parámetro creado correctamente";

    List<TranslateDTO> translations = List.of(
        TranslateDTO.builder()
            .codeMessage(expectedCode)
            .message(expectedMessage)
            .build()
    );

    // Simula respuestas del servicio
    when(parametersService.create(inputDto))
        .thenReturn(CompletableFuture.completedFuture(createdDto));
    when(parametersService.getTranslate(ESPANOL))
        .thenReturn(CompletableFuture.completedFuture(translations));

    // Forzamos el cache en el controlador
    ReflectionTestUtils.setField(parameterController, "listTranslate", translations);

    // Simulamos la traducción de respuesta
    when(parametersTranslate.getTranslateResponseDataParameter(any(), eq(expectedCode), anyList()))
        .thenAnswer(invocation -> {
          ResponseDataParameterDTO response = invocation.getArgument(0);
          response.setResponseCode(ResponseCodeDTO.builder()
              .code(expectedCode)
              .message(expectedMessage)
              .htmlMessage(expectedMessage)
              .build());
          return response;
        });

    // Act
    ResponseDataParameterDTO actualResponse = parameterController.createParameter(
        inputDto, mock(HttpServletRequest.class), ESPANOL);

    // Assert
    assertNotNull(actualResponse, "La respuesta no debe ser null");
    assertNotNull(actualResponse.getResponseCode(), "El código de respuesta no debe ser null");
    //assertEquals(expectedCode, actualResponse.getResponseCode().getCode());
    //assertEquals(expectedMessage, actualResponse.getResponseCode().getMessage());
    assertEquals(1, actualResponse.getData().size());
    assertEquals(inputDto.getIdHierarchy(), actualResponse.getData().get(0).getIdHierarchy());

    verify(parametersService).create(inputDto);
  }


  @Test
  void testUpdateParameter_ActiveStatus() throws Exception {
    // Arrange
    ResponseParameterDTO dto = new ResponseParameterDTO();
    dto.setStatus(1); // activo

    // Simula la llamada asincrónica vacía
    when(parametersService.update(dto)).thenReturn(CompletableFuture.completedFuture(null));
    when(parametersService.getTranslate(ESPANOL)).thenReturn(CompletableFuture.completedFuture(translations));

    // Simula traducción
    when(parametersTranslate.getTranslateResponseDataParameter(any(), anyString(), anyList()))
        .thenAnswer(invocation -> {
          ResponseDataParameterDTO response = invocation.getArgument(0);
          String code = invocation.getArgument(1);
          response.setResponseCode(new ResponseCodeDTO(code, "Actualización exitosa", "Actualización exitosa","solution","categoiry"));
          return response;
        });

    // Act
    ResponseDataParameterDTO result = parameterController.updateParameter(dto, null, ESPANOL);

    // Assert
    assertNotNull(result);

    verify(parametersService).update(dto);
  }

  @Test
  void testUpdateParameter_Spread() throws Exception {
    // Arrange
    ResponseParameterDTO dto = new ResponseParameterDTO();
    dto.setStatus(1); // activo
    int elementsToSpread = 3;
    // Simula la llamada asincrónica vacía
    when(parametersService.spread(dto)).thenReturn(CompletableFuture.completedFuture(elementsToSpread));
    ReflectionTestUtils.setField(parameterController, "listTranslate", translations);
    when(parametersService.getTranslate(ESPANOL)).thenReturn(CompletableFuture.completedFuture(translations));

    // Simula traducción
    when(parametersTranslate.getTranslateResponseDataParameter(any(), anyString(), anyList()))
        .thenAnswer(invocation -> {
          ResponseDataParameterDTO response = invocation.getArgument(0);
          String code = invocation.getArgument(1);
          response.setResponseCode(new ResponseCodeDTO(code, "nxt-msa-hierarchy-parameter_200_06","Spread OK","solution","categoiry"));
          return response;
        });

    // Act
    ResponseDataParameterDTO response = parameterController.spreadParameter(dto, mock(HttpServletRequest.class), ESPANOL);

    // Assert
    assertNotNull(response);

    verify(parametersService).spread(dto);
  }




}
