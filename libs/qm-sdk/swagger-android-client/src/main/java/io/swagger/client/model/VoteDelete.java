package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class VoteDelete  {
  
  @SerializedName("cause")
  private String cause = null;
  @SerializedName("effect")
  private String effect = null;

  
  /**
   * Cause variable name for the correlation to which the vote pertains
   **/
  @ApiModelProperty(required = true, value = "Cause variable name for the correlation to which the vote pertains")
  public String getCause() {
    return cause;
  }
  public void setCause(String cause) {
    this.cause = cause;
  }

  
  /**
   * Effect variable name for the correlation to which the vote pertains
   **/
  @ApiModelProperty(required = true, value = "Effect variable name for the correlation to which the vote pertains")
  public String getEffect() {
    return effect;
  }
  public void setEffect(String effect) {
    this.effect = effect;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class VoteDelete {\n");
    
    sb.append("  cause: ").append(cause).append("\n");
    sb.append("  effect: ").append(effect).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
