package io.swagger.client.api;

import io.swagger.client.ApiException;
import io.swagger.client.ApiInvoker;
import io.swagger.client.Pair;

import io.swagger.client.SwaggerClient;
import io.swagger.client.model.*;

import java.util.*;

import io.swagger.client.model.Connector;
import io.swagger.client.model.ConnectorInstruction;
import io.swagger.client.model.ConnectorInfo;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

public class ConnectorsApi {
  String basePath = SwaggerClient.getInstance().getAppBasePath();
  ApiInvoker apiInvoker = ApiInvoker.getInstance();

  public void addHeader(String key, String value) {
    getInvoker().addDefaultHeader(key, value);
  }

  public ApiInvoker getInvoker() {
    return apiInvoker;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getBasePath() {
    return basePath;
  }

  
  /**
   * Get embeddable connect javascript
   * Get embeddable connect javascript. Usage:\n\n  - Embedding in applications with popups for 3rd-party authentication\nwindows.\n\n    Use `qmSetupInPopup` function after connecting `connect.js`.\n\n  - Embedding in applications with popups for 3rd-party authentication\nwindows.\n\n    Requires a selector to block. It will be embedded in this block.\n\n    Use `qmSetupOnPage` function after connecting `connect.js`.\n\n  - Embedding in mobile applications without popups for 3rd-party\nauthentication.\n\n    Use `qmSetupOnMobile` function after connecting `connect.js`.\n\n    if using the MoodiModo Clones, Use `qmSetupOnIonic` function after connecting `connect.js`.
   * @param accessToken User&#39;s OAuth2 access token
   * @return void
   */
  public void  v1ConnectJsGet (String accessToken) throws ApiException {
    Object postBody = null;
    

    // create path and map variables
    String path = "/v1/connect.js".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    

    

    String[] contentTypes = {
      "application/json"
    };
    String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

    if (contentType.startsWith("multipart/form-data")) {
      // file uploading
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      

      HttpEntity httpEntity = builder.build();
      postBody = httpEntity;
    } else {
      // normal form params
      
    }

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return ;
      }
      else {
        return ;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Mobile connect page
   * This page is designed to be opened in a webview.  Instead of using popup authentication boxes, it uses redirection. You can include the user&#39;s access_token as a URL parameter like https://app.quantimo.do/api/v1/connect/mobile?access_token=123
   * @param accessToken User OAuth access token
   * @return void
   */
  public void  v1ConnectMobileGet (String accessToken) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'accessToken' is set
    if (accessToken == null) {
       throw new ApiException(400, "Missing the required parameter 'accessToken' when calling v1ConnectMobileGet");
    }
    

    // create path and map variables
    String path = "/v1/connect/mobile".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    

    

    String[] contentTypes = {
      "application/json"
    };
    String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

    if (contentType.startsWith("multipart/form-data")) {
      // file uploading
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      

      HttpEntity httpEntity = builder.build();
      postBody = httpEntity;
    } else {
      // normal form params
      
    }

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return ;
      }
      else {
        return ;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * List of Connectors
   * A connector pulls data from other data providers using their API or a screenscraper. Returns a list of all available connectors and information about them such as their id, name, whether the user has provided access, logo url, connection instructions, and the update history.
   * @return List<Connector>
   */
  public List<Connector>  v1ConnectorsListGet () throws ApiException {
    Object postBody = null;
    

    // create path and map variables
    String path = "/v1/connectors/list".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    

    

    String[] contentTypes = {
      "application/json"
    };
    String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

    if (contentType.startsWith("multipart/form-data")) {
      // file uploading
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      

      HttpEntity httpEntity = builder.build();
      postBody = httpEntity;
    } else {
      // normal form params
      
    }

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return (List<Connector>) ApiInvoker.deserialize(response, "array", Connector.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Obtain a token from 3rd party data source
   * Attempt to obtain a token from the data provider, store it in the database. With this, the connector to continue to obtain new user data until the token is revoked.
   * @param connector Lowercase system name of the source application or device. Get a list of available connectors from the /connectors/list endpoint.
   * @param accessToken User&#39;s OAuth2 access token
   * @return void
   */
  public void  v1ConnectorsConnectorConnectGet (String connector, String accessToken) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'connector' is set
    if (connector == null) {
       throw new ApiException(400, "Missing the required parameter 'connector' when calling v1ConnectorsConnectorConnectGet");
    }
    

    // create path and map variables
    String path = "/v1/connectors/{connector}/connect".replaceAll("\\{format\\}","json").replaceAll("\\{" + "connector" + "\\}", apiInvoker.escapeString(connector.toString()));

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    

    

    String[] contentTypes = {
      "application/json"
    };
    String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

    if (contentType.startsWith("multipart/form-data")) {
      // file uploading
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      

      HttpEntity httpEntity = builder.build();
      postBody = httpEntity;
    } else {
      // normal form params
      
    }

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return ;
      }
      else {
        return ;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Connection Instructions
   * Returns instructions that describe what parameters and endpoint to use to connect to the given data provider.
   * @param connector Lowercase system name of the source application or device. Get a list of available connectors from the /connectors/list endpoint.
   * @param parameters JSON Array of Parameters for the request to enable connector.
   * @param url URL which should be used to enable the connector.
   * @param usePopup Should use popup when enabling connector
   * @param accessToken User&#39;s OAuth2 access token
   * @return void
   */
  public void  v1ConnectorsConnectorConnectInstructionsGet (String connector, String parameters, String url, Boolean usePopup, String accessToken) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'connector' is set
    if (connector == null) {
       throw new ApiException(400, "Missing the required parameter 'connector' when calling v1ConnectorsConnectorConnectInstructionsGet");
    }
    
    // verify the required parameter 'parameters' is set
    if (parameters == null) {
       throw new ApiException(400, "Missing the required parameter 'parameters' when calling v1ConnectorsConnectorConnectInstructionsGet");
    }
    
    // verify the required parameter 'url' is set
    if (url == null) {
       throw new ApiException(400, "Missing the required parameter 'url' when calling v1ConnectorsConnectorConnectInstructionsGet");
    }
    
    // verify the required parameter 'usePopup' is set
    if (usePopup == null) {
       throw new ApiException(400, "Missing the required parameter 'usePopup' when calling v1ConnectorsConnectorConnectInstructionsGet");
    }
    

    // create path and map variables
    String path = "/v1/connectors/{connector}/connectInstructions".replaceAll("\\{format\\}","json").replaceAll("\\{" + "connector" + "\\}", apiInvoker.escapeString(connector.toString()));

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "parameters", parameters));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "url", url));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "usePopup", usePopup));
    

    

    String[] contentTypes = {
      "application/json"
    };
    String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

    if (contentType.startsWith("multipart/form-data")) {
      // file uploading
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      

      HttpEntity httpEntity = builder.build();
      postBody = httpEntity;
    } else {
      // normal form params
      
    }

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return ;
      }
      else {
        return ;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Connect Parameter
   * Returns instructions that describe what parameters and endpoint to use to connect to the given data provider.
   * @param connector Lowercase system name of the source application or device. Get a list of available connectors from the /connectors/list endpoint.
   * @param displayName Name of the parameter that is user visible in the form
   * @param key Name of the property that the user has to enter such as username or password Connector (used in HTTP request)
   * @param placeholder Placeholder hint value for the parameter input tag.
   * @param type Type of input field such as those found here http://www.w3schools.com/tags/tag_input.asp
   * @param usePopup Should use popup when enabling connector
   * @param accessToken User&#39;s OAuth2 access token
   * @param defaultValue Default parameter value
   * @return ConnectorInstruction
   */
  public ConnectorInstruction  v1ConnectorsConnectorConnectParameterGet (String connector, String displayName, String key, String placeholder, String type, Boolean usePopup, String accessToken, String defaultValue) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'connector' is set
    if (connector == null) {
       throw new ApiException(400, "Missing the required parameter 'connector' when calling v1ConnectorsConnectorConnectParameterGet");
    }
    
    // verify the required parameter 'displayName' is set
    if (displayName == null) {
       throw new ApiException(400, "Missing the required parameter 'displayName' when calling v1ConnectorsConnectorConnectParameterGet");
    }
    
    // verify the required parameter 'key' is set
    if (key == null) {
       throw new ApiException(400, "Missing the required parameter 'key' when calling v1ConnectorsConnectorConnectParameterGet");
    }
    
    // verify the required parameter 'placeholder' is set
    if (placeholder == null) {
       throw new ApiException(400, "Missing the required parameter 'placeholder' when calling v1ConnectorsConnectorConnectParameterGet");
    }
    
    // verify the required parameter 'type' is set
    if (type == null) {
       throw new ApiException(400, "Missing the required parameter 'type' when calling v1ConnectorsConnectorConnectParameterGet");
    }
    
    // verify the required parameter 'usePopup' is set
    if (usePopup == null) {
       throw new ApiException(400, "Missing the required parameter 'usePopup' when calling v1ConnectorsConnectorConnectParameterGet");
    }
    

    // create path and map variables
    String path = "/v1/connectors/{connector}/connectParameter".replaceAll("\\{format\\}","json").replaceAll("\\{" + "connector" + "\\}", apiInvoker.escapeString(connector.toString()));

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "defaultValue", defaultValue));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "displayName", displayName));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "key", key));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "placeholder", placeholder));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "type", type));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "usePopup", usePopup));
    

    

    String[] contentTypes = {
      "application/json"
    };
    String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

    if (contentType.startsWith("multipart/form-data")) {
      // file uploading
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      

      HttpEntity httpEntity = builder.build();
      postBody = httpEntity;
    } else {
      // normal form params
      
    }

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return (ConnectorInstruction) ApiInvoker.deserialize(response, "", ConnectorInstruction.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Delete stored connection info
   * The disconnect method deletes any stored tokens or connection information from the connectors database.
   * @param connector Lowercase system name of the source application or device. Get a list of available connectors from the /connectors/list endpoint.
   * @return void
   */
  public void  v1ConnectorsConnectorDisconnectGet (String connector) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'connector' is set
    if (connector == null) {
       throw new ApiException(400, "Missing the required parameter 'connector' when calling v1ConnectorsConnectorDisconnectGet");
    }
    

    // create path and map variables
    String path = "/v1/connectors/{connector}/disconnect".replaceAll("\\{format\\}","json").replaceAll("\\{" + "connector" + "\\}", apiInvoker.escapeString(connector.toString()));

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    

    

    String[] contentTypes = {
      "application/json"
    };
    String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

    if (contentType.startsWith("multipart/form-data")) {
      // file uploading
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      

      HttpEntity httpEntity = builder.build();
      postBody = httpEntity;
    } else {
      // normal form params
      
    }

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return ;
      }
      else {
        return ;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Get connector info for user
   * Returns information about the connector such as the connector id, whether or not is connected for this user (i.e. we have a token or credentials), and its update history for the user.
   * @param connector Lowercase system name of the source application or device. Get a list of available connectors from the /connectors/list endpoint.
   * @param accessToken User&#39;s OAuth2 access token
   * @return ConnectorInfo
   */
  public ConnectorInfo  v1ConnectorsConnectorInfoGet (String connector, String accessToken) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'connector' is set
    if (connector == null) {
       throw new ApiException(400, "Missing the required parameter 'connector' when calling v1ConnectorsConnectorInfoGet");
    }
    

    // create path and map variables
    String path = "/v1/connectors/{connector}/info".replaceAll("\\{format\\}","json").replaceAll("\\{" + "connector" + "\\}", apiInvoker.escapeString(connector.toString()));

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    

    

    String[] contentTypes = {
      "application/json"
    };
    String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

    if (contentType.startsWith("multipart/form-data")) {
      // file uploading
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      

      HttpEntity httpEntity = builder.build();
      postBody = httpEntity;
    } else {
      // normal form params
      
    }

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return (ConnectorInfo) ApiInvoker.deserialize(response, "", ConnectorInfo.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Sync with data source
   * The update method tells the QM Connector Framework to check with the data provider (such as Fitbit or MyFitnessPal) and retrieve any new measurements available.
   * @param connector Lowercase system name of the source application or device
   * @param accessToken User&#39;s OAuth2 access token
   * @return void
   */
  public void  v1ConnectorsConnectorUpdateGet (String connector, String accessToken) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'connector' is set
    if (connector == null) {
       throw new ApiException(400, "Missing the required parameter 'connector' when calling v1ConnectorsConnectorUpdateGet");
    }
    

    // create path and map variables
    String path = "/v1/connectors/{connector}/update".replaceAll("\\{format\\}","json").replaceAll("\\{" + "connector" + "\\}", apiInvoker.escapeString(connector.toString()));

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    

    

    String[] contentTypes = {
      "application/json"
    };
    String contentType = contentTypes.length > 0 ? contentTypes[0] : "application/json";

    if (contentType.startsWith("multipart/form-data")) {
      // file uploading
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      

      HttpEntity httpEntity = builder.build();
      postBody = httpEntity;
    } else {
      // normal form params
      
    }

    try {
      String response = apiInvoker.invokeAPI(basePath, path, "GET", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return ;
      }
      else {
        return ;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
}
