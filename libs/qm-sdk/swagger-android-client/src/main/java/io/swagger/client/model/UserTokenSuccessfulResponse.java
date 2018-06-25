package io.swagger.client.model;

import io.swagger.client.model.UserTokenSuccessfulResponseInnerUserField;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class UserTokenSuccessfulResponse  {
  
  @SerializedName("code")
  private Integer code = null;
  @SerializedName("message")
  private String message = null;
  @SerializedName("user")
  private UserTokenSuccessfulResponseInnerUserField user = null;

  
  /**
   * Status code
   **/
  @ApiModelProperty(required = true, value = "Status code")
  public Integer getCode() {
    return code;
  }
  public void setCode(Integer code) {
    this.code = code;
  }

  
  /**
   * Message
   **/
  @ApiModelProperty(required = true, value = "Message")
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  public UserTokenSuccessfulResponseInnerUserField getUser() {
    return user;
  }
  public void setUser(UserTokenSuccessfulResponseInnerUserField user) {
    this.user = user;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserTokenSuccessfulResponse {\n");
    
    sb.append("  code: ").append(code).append("\n");
    sb.append("  message: ").append(message).append("\n");
    sb.append("  user: ").append(user).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
