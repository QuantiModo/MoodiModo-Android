package io.swagger.client.api;

import io.swagger.client.ApiException;
import io.swagger.client.ApiInvoker;
import io.swagger.client.Pair;

import io.swagger.client.SwaggerClient;
import io.swagger.client.model.*;

import java.util.*;


import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

public class OauthApi {
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
   * Authorize
   * Ask the user if they want to allow a client&#39;s application to submit or obtain data from their QM account. It will redirect the user to the url provided by the client application with the code as a query parameter or error in case of an error.
   * @param clientId This is the unique ID that QuantiModo uses to identify your application. Obtain a client id by emailing info@quantimo.do.
   * @param clientSecret This is the secret for your obtained clientId. QuantiModo uses this to validate that only your application uses the clientId.
   * @param responseType If the value is code, launches a Basic flow, requiring a POST to the token endpoint to obtain the tokens. If the value is token id_token or id_token token, launches an Implicit flow, requiring the use of Javascript at the redirect URI to retrieve tokens from the URI #fragment.
   * @param scope Scopes include basic, readmeasurements, and writemeasurements. The \&quot;basic\&quot; scope allows you to read user info (displayname, email, etc). The \&quot;readmeasurements\&quot; scope allows one to read a user&#39;s data. The \&quot;writemeasurements\&quot; scope allows you to write user data. Separate multiple scopes by a space.
   * @param redirectUri The redirect URI is the URL within your client application that will receive the OAuth2 credentials.
   * @param state An opaque string that is round-tripped in the protocol; that is to say, it is returned as a URI parameter in the Basic flow, and in the URI
   * @return void
   */
  public void  v1Oauth2AuthorizeGet (String clientId, String clientSecret, String responseType, String scope, String redirectUri, String state) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'clientId' is set
    if (clientId == null) {
       throw new ApiException(400, "Missing the required parameter 'clientId' when calling v1Oauth2AuthorizeGet");
    }
    
    // verify the required parameter 'clientSecret' is set
    if (clientSecret == null) {
       throw new ApiException(400, "Missing the required parameter 'clientSecret' when calling v1Oauth2AuthorizeGet");
    }
    
    // verify the required parameter 'responseType' is set
    if (responseType == null) {
       throw new ApiException(400, "Missing the required parameter 'responseType' when calling v1Oauth2AuthorizeGet");
    }
    
    // verify the required parameter 'scope' is set
    if (scope == null) {
       throw new ApiException(400, "Missing the required parameter 'scope' when calling v1Oauth2AuthorizeGet");
    }
    

    // create path and map variables
    String path = "/v1/oauth2/authorize".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "clientId", clientId));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "client_secret", clientSecret));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "response_type", responseType));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "scope", scope));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "redirect_uri", redirectUri));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "state", state));
    

    

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
   * Access Token
   * Client provides authorization token obtained from /api/v1/oauth2/authorize to this endpoint and receives an access token. Access token can then be used to query different API endpoints of QuantiModo.
   * @param clientId This is the unique ID that QuantiModo uses to identify your application. Obtain a client id by emailing info@quantimo.do.
   * @param clientSecret This is the secret for your obtained clientId. QuantiModo uses this to validate that only your application uses the clientId.
   * @param grantType Grant Type can be &#39;authorization_code&#39; or &#39;refresh_token&#39;
   * @param responseType If the value is code, launches a Basic flow, requiring a POST to the token endpoint to obtain the tokens. If the value is token id_token or id_token token, launches an Implicit flow, requiring the use of Javascript at the redirect URI to retrieve tokens from the URI #fragment.
   * @param scope Scopes include basic, readmeasurements, and writemeasurements. The \&quot;basic\&quot; scope allows you to read user info (displayname, email, etc). The \&quot;readmeasurements\&quot; scope allows one to read a user&#39;s data. The \&quot;writemeasurements\&quot; scope allows you to write user data. Separate multiple scopes by a space.
   * @param redirectUri The redirect URI is the URL within your client application that will receive the OAuth2 credentials.
   * @param state An opaque string that is round-tripped in the protocol; that is to say, it is returned as a URI parameter in the Basic flow, and in the URI
   * @return void
   */
  public void  v1Oauth2TokenGet (String clientId, String clientSecret, String grantType, String responseType, String scope, String redirectUri, String state) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'clientId' is set
    if (clientId == null) {
       throw new ApiException(400, "Missing the required parameter 'clientId' when calling v1Oauth2TokenGet");
    }
    
    // verify the required parameter 'clientSecret' is set
    if (clientSecret == null) {
       throw new ApiException(400, "Missing the required parameter 'clientSecret' when calling v1Oauth2TokenGet");
    }
    
    // verify the required parameter 'grantType' is set
    if (grantType == null) {
       throw new ApiException(400, "Missing the required parameter 'grantType' when calling v1Oauth2TokenGet");
    }
    

    // create path and map variables
    String path = "/v1/oauth2/token".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "clientId", clientId));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "client_secret", clientSecret));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "grant_type", grantType));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "response_type", responseType));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "scope", scope));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "redirect_uri", redirectUri));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "state", state));
    

    

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
