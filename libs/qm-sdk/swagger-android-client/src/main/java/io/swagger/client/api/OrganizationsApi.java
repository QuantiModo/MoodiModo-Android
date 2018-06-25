package io.swagger.client.api;

import io.swagger.client.ApiException;
import io.swagger.client.ApiInvoker;
import io.swagger.client.Pair;

import io.swagger.client.SwaggerClient;
import io.swagger.client.model.*;

import java.util.*;

import io.swagger.client.model.UserTokenRequest;
import io.swagger.client.model.UserTokenSuccessfulResponse;
import io.swagger.client.model.UserTokenFailedResponse;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

public class OrganizationsApi {
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
   * Get user tokens for existing users, create new users
   * Get user tokens for existing users, create new users
   * @param organizationId Organization ID
   * @param body Provides organization token and user ID
   * @param accessToken User&#39;s OAuth2 access token
   * @return UserTokenSuccessfulResponse
   */
  public UserTokenSuccessfulResponse  v1OrganizationsOrganizationIdUsersPost (Integer organizationId, UserTokenRequest body, String accessToken) throws ApiException {
    Object postBody = body;
    
    // verify the required parameter 'organizationId' is set
    if (organizationId == null) {
       throw new ApiException(400, "Missing the required parameter 'organizationId' when calling v1OrganizationsOrganizationIdUsersPost");
    }
    
    // verify the required parameter 'body' is set
    if (body == null) {
       throw new ApiException(400, "Missing the required parameter 'body' when calling v1OrganizationsOrganizationIdUsersPost");
    }
    

    // create path and map variables
    String path = "/v1/organizations/{organizationId}/users".replaceAll("\\{format\\}","json").replaceAll("\\{" + "organizationId" + "\\}", apiInvoker.escapeString(organizationId.toString()));

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
      String response = apiInvoker.invokeAPI(basePath, path, "POST", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return (UserTokenSuccessfulResponse) ApiInvoker.deserialize(response, "", UserTokenSuccessfulResponse.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
}
