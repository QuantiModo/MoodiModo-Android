package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class PostCorrelation  {
  
  @SerializedName("cause")
  private String cause = null;
  @SerializedName("effect")
  private String effect = null;
  @SerializedName("correlation")
  private Double correlation = null;
  @SerializedName("vote")
  private Integer vote = null;

  
  /**
   * Cause variable name
   **/
  @ApiModelProperty(required = true, value = "Cause variable name")
  public String getCause() {
    return cause;
  }
  public void setCause(String cause) {
    this.cause = cause;
  }

  
  /**
   * Effect variable name
   **/
  @ApiModelProperty(required = true, value = "Effect variable name")
  public String getEffect() {
    return effect;
  }
  public void setEffect(String effect) {
    this.effect = effect;
  }

  
  /**
   * Correlation value
   **/
  @ApiModelProperty(required = true, value = "Correlation value")
  public Double getCorrelation() {
    return correlation;
  }
  public void setCorrelation(Double correlation) {
    this.correlation = correlation;
  }

  
  /**
   * Vote: 0 or 1
   **/
  @ApiModelProperty(value = "Vote: 0 or 1")
  public Integer getVote() {
    return vote;
  }
  public void setVote(Integer vote) {
    this.vote = vote;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class PostCorrelation {\n");
    
    sb.append("  cause: ").append(cause).append("\n");
    sb.append("  effect: ").append(effect).append("\n");
    sb.append("  correlation: ").append(correlation).append("\n");
    sb.append("  vote: ").append(vote).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
