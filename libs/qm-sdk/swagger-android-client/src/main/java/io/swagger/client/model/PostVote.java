package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class PostVote  {
  
  @SerializedName("cause")
  private String cause = null;
  @SerializedName("effect")
  private String effect = null;
  @SerializedName("vote")
  private Boolean vote = null;

  
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
   * Vote: 0 (for implausible) or 1 (for plausible)
   **/
  @ApiModelProperty(required = true, value = "Vote: 0 (for implausible) or 1 (for plausible)")
  public Boolean getVote() {
    return vote;
  }
  public void setVote(Boolean vote) {
    this.vote = vote;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class PostVote {\n");
    
    sb.append("  cause: ").append(cause).append("\n");
    sb.append("  effect: ").append(effect).append("\n");
    sb.append("  vote: ").append(vote).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
