package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class TrackingReminderPendingSkip  {
  
  @SerializedName("id")
  private Integer id = null;

  
  /**
   * Id of the pending reminder to be skipped
   **/
  @ApiModelProperty(required = true, value = "Id of the pending reminder to be skipped")
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class TrackingReminderPendingSkip {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
