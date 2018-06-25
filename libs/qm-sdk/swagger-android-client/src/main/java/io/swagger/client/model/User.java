package io.swagger.client.model;


import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class User  {
  
  @SerializedName("id")
  private Integer id = null;
  @SerializedName("wpId")
  private Integer wpId = null;
  @SerializedName("displayName")
  private String displayName = null;
  @SerializedName("loginName")
  private String loginName = null;
  @SerializedName("email")
  private String email = null;
  @SerializedName("token")
  private String token = null;
  @SerializedName("administrator")
  private Boolean administrator = null;

  
  /**
   * User id
   **/
  @ApiModelProperty(required = true, value = "User id")
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  
  /**
   * Wordpress user id
   **/
  @ApiModelProperty(required = true, value = "Wordpress user id")
  public Integer getWpId() {
    return wpId;
  }
  public void setWpId(Integer wpId) {
    this.wpId = wpId;
  }

  
  /**
   * User display name
   **/
  @ApiModelProperty(required = true, value = "User display name")
  public String getDisplayName() {
    return displayName;
  }
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  
  /**
   * User login name
   **/
  @ApiModelProperty(required = true, value = "User login name")
  public String getLoginName() {
    return loginName;
  }
  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  
  /**
   * User email
   **/
  @ApiModelProperty(required = true, value = "User email")
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  
  /**
   * User token
   **/
  @ApiModelProperty(required = true, value = "User token")
  public String getToken() {
    return token;
  }
  public void setToken(String token) {
    this.token = token;
  }

  
  /**
   * Is user administrator
   **/
  @ApiModelProperty(required = true, value = "Is user administrator")
  public Boolean getAdministrator() {
    return administrator;
  }
  public void setAdministrator(Boolean administrator) {
    this.administrator = administrator;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class User {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  wpId: ").append(wpId).append("\n");
    sb.append("  displayName: ").append(displayName).append("\n");
    sb.append("  loginName: ").append(loginName).append("\n");
    sb.append("  email: ").append(email).append("\n");
    sb.append("  token: ").append(token).append("\n");
    sb.append("  administrator: ").append(administrator).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
