package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class JsonErrorResponse  {
  
  @SerializedName("status")
  private String status = null;
  @SerializedName("message")
  private String message = null;

  
  /**
   * Status: \"ok\" or \"error\"
   **/
  @ApiModelProperty(required = true, value = "Status: \"ok\" or \"error\"")
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }

  
  /**
   * Error message
   **/
  @ApiModelProperty(value = "Error message")
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class JsonErrorResponse {\n");
    
    sb.append("  status: ").append(status).append("\n");
    sb.append("  message: ").append(message).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
