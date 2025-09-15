package com.smartpayment.nxt.hierarchy.parameters.exception;

import com.smartpayments.nxt.dto.ResponseCodeDTO;
import com.smartpayments.nxt.model.ResponseDTO;
import lombok.Getter;

import java.io.Serial;

/**
 * {@code HierarchyException} excepci&oacute;n para arrojar rechazos por validaciones
 * de reglas de negocio.
 */
@Getter
public class HierarchyException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 982638303869342723L;

  private final String code;

  private Object[] messageArgs;


  /**
   * @param  code   el código de error. El código de error es almacenado para
   *         su posterior obtención por el método {@link #getCode()}.
   * @param  message el mensaje detallado (el cual es almacenado para su
   *         posterior obtención por el método {@link #getMessage()}).
   */
  public HierarchyException (String code, String message) {
    super(message);
    this.code = code;
  }
  /**
   * @param  code   el código de error. El código de error es almacenado para
   *         su posterior obtención por el método {@link #getCode()}.
   * @param  message el mensaje detallado (el cual es almacenado para su
   *         posterior obtención por el método {@link #getMessage()}).
   * @param args un arreglo de argumentos que serán utilizados en los
   * parámetros del mensaje (los parámetros se muestran como "{0}", "{1,date}"
   * , "{2,time}" de un mensaje), o {@code null} si no requiere
   */
  public HierarchyException (String code, String message, Object... args) {
    super(message);
    this.code = code;
    this.messageArgs = args;
  }

  /**
   * @param  code   el código de error. El código de error es almacenado para
   *         su posterior obtención por el método {@link #getCode()}.
   * @param  message el mensaje detallado (el cual es almacenado para su
   *         posterior obtención por el método {@link #getMessage()}).
   * @param  cause el motivo (el cual es almacenado para su posterior obtención
   *         por el método {@link #getCause()}).  (Un valor <tt>null</tt>
   *         es permitido, e indica que no existe una causa o no es conocida)
   * @see RuntimeException#RuntimeException(String message, Throwable cause)
   */
  public HierarchyException (String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  /**
   * @param  code   el código de error. El código de error es almacenado para
   *         su posterior obtención por el método {@link #getCode()}.
   * @param  cause el motivo (el cual es almacenado para su posterior obtención
   *         por el método {@link #getCause()}).  (Un valor <tt>null</tt>
   *         es permitido, e indica que no existe una causa o no es conocida)
   * @see RuntimeException#RuntimeException(Throwable cause)
   */
  public HierarchyException (String code, Throwable cause) {
    super(cause);
    this.code = code;
  }

}
