
package com.smartpayment.nxt.hierarchy.parameters.util;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.Map;

public final class ParamUtils {

    private ParamUtils() {
        // Constructor privado para evitar la instanciación
    }

    /**
     * Calcula el número total de páginas basado en los valores de total de registros y tamaño de las páginas.
     * @param totalRecords Número total de registros
     * @param size Tamaño de la página
     * @return Número total de páginas
     */
    public static int calculateTotalPages(int totalRecords, int size) {
        if (totalRecords == 0) {
            return 0;
        }
        int division = totalRecords / size;
        return (division * size < totalRecords) ? division + 1 : division;
    }

    /**
     * Crea los parámetros para la consulta de `getTipAll`.
     * @param pHierarchyParameterName ID de jerarquía padre
     * @param pHierarchyId ID de jerarquía
     * @param pHierarchySize Tamaño de la página
     * @param pHierarchyPage Página actual
     * @param pHierarchyCode Total de registros
     * @param ptotal Total de páginas
     * @param pagestotales Mensaje a incluir
     * @param pMessage Mensaje a incluir
     * @return Parámetros como `MapSqlParameterSource`
     */


    public static MapSqlParameterSource createParamsForGetTipAll(
            String pHierarchyParameterName, Integer pHierarchyId, Integer pHierarchySize,
            int pHierarchyPage, int pHierarchyCode, Integer ptotal, Integer pagestotales,String pMessage) {
        return new MapSqlParameterSource()
                .addValue("pHierarchyParameterName", pHierarchyParameterName)
                .addValue("pHierarchyId", pHierarchyId)
                .addValue("pHierarchySize", pHierarchySize)
                .addValue("pHierarchyPage", pHierarchyPage)
                .addValue("pHierarchyCode", pHierarchyCode)
                .addValue("ptotal", ptotal)
                .addValue("pMessage", pMessage)
                .addValue("pagestotales", pagestotales);

    }

    /**
     * Crea los parámetros para la consulta específica de `getTip`.
     * @param parameterId ID del parámetro
     * @param parentHierarchyId ID de la jerarquía padre
     * @param hierarchyId ID de jerarquía
     * @param counter Contador específico del parámetro
     * @param message Mensaje a incluir
     * @return Parámetros como `MapSqlParameterSource`
     */
    public static MapSqlParameterSource createParamsForGetTip(Integer parameterId, Integer parentHierarchyId, Integer hierarchyId, Integer counter, String message) {
        return new MapSqlParameterSource()
                .addValue("pHierarchyIdParameter", parameterId)
                .addValue("pHierarchyIdDad", parentHierarchyId)
                .addValue("pHierarchyId", hierarchyId)
                .addValue("pContador", counter)
                .addValue("pMessage", message);
    }

    /**
     * Crea los parámetros para la consulta de inserción de un tip.
     * @param tipDTO Objeto del tipo TipDTO con datos para el parámetro
     * @param parameterId ID asignado al parámetro
     * @return Parámetros como `MapSqlParameterSource`
     *
    public static MapSqlParameterSource createParamsForInsertTip(DataDto tipDTO, int parameterId) {
    return new MapSqlParameterSource()
    .addValue("pIdHierarchy", tipDTO.getHierarchyId())
    .addValue("pIdParameter", parameterId)
    .addValue("pParameterName", tipDTO.getParameterId())
    .addValue("pParameterValue", tipDTO.getParameterValue());
    }*/

    /**
     * Crea los parámetros para la consulta de actualización de un tip.
     * @param tipDTO Objeto del tipo TipDTO con datos actualizados
     * @param parameterId ID del parámetro
     * @return Parámetros como `MapSqlParameterSource`
     *
    public static MapSqlParameterSource createParamsForUpdateTip(TipDTO tipDTO, int parameterId) {
    return new MapSqlParameterSource()
    .addValue("pIdHierarchy", tipDTO.getHierarchyId())
    .addValue("pIdParameter", parameterId)
    .addValue("pParameterName", tipDTO.getParameterId())
    .addValue("pParameterValue", tipDTO.getParameterValue())
    .addValue("pIdHierarchyParameter", tipDTO.getIdHierarchyParameter());
    }*/

    /**
     * Crea los parámetros para la consulta de eliminación de un tip.
     * @param tipDTO Objeto del tipo TipDTO
     * @return Parámetros como `MapSqlParameterSource`
     *
    public static MapSqlParameterSource createParamsForDeleteTip(TipDTO tipDTO) {
    return new MapSqlParameterSource()
    .addValue("pIdHierarchy", tipDTO.getHierarchyId())
    .addValue("pHierarchyParameter", tipDTO.getIdHierarchyParameter());
    }
     */
    /**
     * Reemplaza dinámicamente los parámetros en la consulta SQL para propósitos de logging.
     * @param query Consulta SQL como plantilla
     * @param params Parámetros a ser reemplazados
     * @return Query con los valores de los parámetros reemplazados
     */
    public static String replaceLogParameters(String query, MapSqlParameterSource params) {
        String result = query;
        for (Map.Entry<String, Object> entry : params.getValues().entrySet()) {
            String key = ":" + entry.getKey();
            String value = entry.getValue() != null ? "'" + entry.getValue().toString() + "'" : "NULL";
            result = result.replace(key, value);
        }
        return result;
    }
}
