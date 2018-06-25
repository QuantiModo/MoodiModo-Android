package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class TrackingReminderPendingSnooze  {
  
  @SerializedName("id")
  private Integer id = null;

  
  /**
   * Id of the pending reminder to be snoozed
   **/
  @ApiModelProperty(required = true, value = "Id of the pending reminder to be snoozed")
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class TrackingReminderPendingSnooze {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
