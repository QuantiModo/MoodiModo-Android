package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class ConversionStep  {
  
  public enum OperationEnum {
     MULTIPLY,  ADD, 
  };
  @SerializedName("operation")
  private OperationEnum operation = null;
  @SerializedName("value")
  private Double value = null;

  
  /**
   * ADD or MULTIPLY
   **/
  @ApiModelProperty(required = true, value = "ADD or MULTIPLY")
  public OperationEnum getOperation() {
    return operation;
  }
  public void setOperation(OperationEnum operation) {
    this.operation = operation;
  }

  
  /**
   * This specifies the order of conversion steps starting with 0
   **/
  @ApiModelProperty(required = true, value = "This specifies the order of conversion steps starting with 0")
  public Double getValue() {
    return value;
  }
  public void setValue(Double value) {
    this.value = value;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConversionStep {\n");
    
    sb.append("  operation: ").append(operation).append("\n");
    sb.append("  value: ").append(value).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
