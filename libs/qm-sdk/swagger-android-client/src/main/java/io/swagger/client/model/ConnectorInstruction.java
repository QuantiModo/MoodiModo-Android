package io.swagger.client.model;

import java.util.*;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class ConnectorInstruction  {
  
  @SerializedName("url")
  private String url = null;
  @SerializedName("parameters")
  private List<String> parameters = null;
  @SerializedName("usePopup")
  private Boolean usePopup = null;

  
  /**
   * url
   **/
  @ApiModelProperty(value = "url")
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }

  
  /**
   * parameters array
   **/
  @ApiModelProperty(value = "parameters array")
  public List<String> getParameters() {
    return parameters;
  }
  public void setParameters(List<String> parameters) {
    this.parameters = parameters;
  }

  
  /**
   * usePopup
   **/
  @ApiModelProperty(value = "usePopup")
  public Boolean getUsePopup() {
    return usePopup;
  }
  public void setUsePopup(Boolean usePopup) {
    this.usePopup = usePopup;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnectorInstruction {\n");
    
    sb.append("  url: ").append(url).append("\n");
    sb.append("  parameters: ").append(parameters).append("\n");
    sb.append("  usePopup: ").append(usePopup).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
