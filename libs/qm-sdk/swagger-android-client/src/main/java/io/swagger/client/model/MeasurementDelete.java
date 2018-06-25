package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class MeasurementDelete  {
  
  @SerializedName("variableName")
  private String variableName = null;
  @SerializedName("startTime")
  private Integer startTime = null;

  
  /**
   * Variable name of the measurement to be deleted
   **/
  @ApiModelProperty(required = true, value = "Variable variableName of the measurement to be deleted")
  public String getVariableName() {
    return variableName;
  }
  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }

  
  /**
   * Start time of the measurement to be deleted
   **/
  @ApiModelProperty(required = true, value = "Start time of the measurement to be deleted")
  public Integer getStartTime() {
    return startTime;
  }
  public void setStartTime(Integer startTime) {
    this.startTime = startTime;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class MeasurementDelete {\n");
    
    sb.append("  variableName: ").append(variableName).append("\n");
    sb.append("  startTime: ").append(startTime).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
