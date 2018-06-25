package io.swagger.client.model;

import io.swagger.client.model.UserTokenRequestInnerUserField;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class UserTokenRequest  {
  
  @SerializedName("user")
  private UserTokenRequestInnerUserField user = null;
  @SerializedName("organizationAccessToken")
  private String organizationAccessToken = null;

  
  /**
   **/
  @ApiModelProperty(value = "")
  public UserTokenRequestInnerUserField getUser() {
    return user;
  }
  public void setUser(UserTokenRequestInnerUserField user) {
    this.user = user;
  }

  
  /**
   * Organization Access token
   **/
  @ApiModelProperty(required = true, value = "Organization Access token")
  public String getOrganizationAccessToken() {
    return organizationAccessToken;
  }
  public void setOrganizationAccessToken(String organizationAccessToken) {
    this.organizationAccessToken = organizationAccessToken;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserTokenRequest {\n");
    
    sb.append("  user: ").append(user).append("\n");
    sb.append("  organizationAccessToken: ").append(organizationAccessToken).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
