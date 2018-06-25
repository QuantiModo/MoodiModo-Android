package io.swagger.client.model;

import java.math.BigDecimal;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class UserTag  {
  
  @SerializedName("taggedVariableId")
  private Integer taggedVariableId = null;
  @SerializedName("tagVariableId")
  private Integer tagVariableId = null;
  @SerializedName("conversionFactor")
  private BigDecimal conversionFactor = null;

  
  /**
   * This is the id of the variable being tagged with an ingredient or something.
   **/
  @ApiModelProperty(required = true, value = "This is the id of the variable being tagged with an ingredient or something.")
  public Integer getTaggedVariableId() {
    return taggedVariableId;
  }
  public void setTaggedVariableId(Integer taggedVariableId) {
    this.taggedVariableId = taggedVariableId;
  }

  
  /**
   * This is the id of the ingredient variable whose value is determined based on the value of the tagged variable.
   **/
  @ApiModelProperty(required = true, value = "This is the id of the ingredient variable whose value is determined based on the value of the tagged variable.")
  public Integer getTagVariableId() {
    return tagVariableId;
  }
  public void setTagVariableId(Integer tagVariableId) {
    this.tagVariableId = tagVariableId;
  }

  
  /**
   * Number by which we multiply the tagged variable value to obtain the tag variable (ingredient) value
   **/
  @ApiModelProperty(required = true, value = "Number by which we multiply the tagged variable value to obtain the tag variable (ingredient) value")
  public BigDecimal getConversionFactor() {
    return conversionFactor;
  }
  public void setConversionFactor(BigDecimal conversionFactor) {
    this.conversionFactor = conversionFactor;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserTag {\n");
    
    sb.append("  taggedVariableId: ").append(taggedVariableId).append("\n");
    sb.append("  tagVariableId: ").append(tagVariableId).append("\n");
    sb.append("  conversionFactor: ").append(conversionFactor).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
