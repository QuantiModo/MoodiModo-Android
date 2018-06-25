package io.swagger.client.model;

import io.swagger.client.model.ConnectorInfoHistoryItem;
import java.util.*;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


@ApiModel(description = "")
public class ConnectorInfo  {
  
  @SerializedName("id")
  private Integer id = null;
  @SerializedName("connected")
  private Boolean connected = null;
  @SerializedName("error")
  private String error = null;
  @SerializedName("history")
  private List<ConnectorInfoHistoryItem> history = null;

  
  /**
   * Connector ID number
   **/
  @ApiModelProperty(required = true, value = "Connector ID number")
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  
  /**
   * True if the authenticated user has this connector enabled
   **/
  @ApiModelProperty(required = true, value = "True if the authenticated user has this connector enabled")
  public Boolean getConnected() {
    return connected;
  }
  public void setConnected(Boolean connected) {
    this.connected = connected;
  }

  
  /**
   * Error message. Empty if connected.
   **/
  @ApiModelProperty(required = true, value = "Error message. Empty if connected.")
  public String getError() {
    return error;
  }
  public void setError(String error) {
    this.error = error;
  }

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  public List<ConnectorInfoHistoryItem> getHistory() {
    return history;
  }
  public void setHistory(List<ConnectorInfoHistoryItem> history) {
    this.history = history;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnectorInfo {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  connected: ").append(connected).append("\n");
    sb.append("  error: ").append(error).append("\n");
    sb.append("  history: ").append(history).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
