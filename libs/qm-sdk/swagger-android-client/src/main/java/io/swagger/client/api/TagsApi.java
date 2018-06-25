package io.swagger.client.api;

import io.swagger.client.ApiException;
import io.swagger.client.ApiInvoker;
import io.swagger.client.Pair;

import io.swagger.client.SwaggerClient;
import io.swagger.client.model.*;

import java.util.*;

import io.swagger.client.model.CommonResponse;
import io.swagger.client.model.UserTag;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

public class TagsApi {
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
   * Post or update vote
   * This endpoint allows users to tag foods with their ingredients.  This information will then be used to infer the user intake of the different ingredients by just entering the foods. The inferred intake levels will then be used to determine the effects of different nutrients on the user during analysis.
   * @param body Contains the new user tag data
   * @param accessToken User&#39;s OAuth2 access token
   * @return CommonResponse
   */
  public CommonResponse  v1UserTagsPost (UserTag body, String accessToken) throws ApiException {
    Object postBody = body;
    
    // verify the required parameter 'body' is set
    if (body == null) {
       throw new ApiException(400, "Missing the required parameter 'body' when calling v1UserTagsPost");
    }
    

    // create path and map variables
    String path = "/v1/userTags".replaceAll("\\{format\\}","json");

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
        return (CommonResponse) ApiInvoker.deserialize(response, "", CommonResponse.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Delete user tag or ingredient
   * Delete previously created user tags or ingredients.
   * @param taggedVariableId This is the id of the variable being tagged with an ingredient or something.
   * @param tagVariableId This is the id of the ingredient variable whose value is determined based on the value of the tagged variable.
   * @return CommonResponse
   */
  public CommonResponse  v1UserTagsDeletePost (Integer taggedVariableId, Integer tagVariableId) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'taggedVariableId' is set
    if (taggedVariableId == null) {
       throw new ApiException(400, "Missing the required parameter 'taggedVariableId' when calling v1UserTagsDeletePost");
    }
    
    // verify the required parameter 'tagVariableId' is set
    if (tagVariableId == null) {
       throw new ApiException(400, "Missing the required parameter 'tagVariableId' when calling v1UserTagsDeletePost");
    }
    

    // create path and map variables
    String path = "/v1/userTags/delete".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "taggedVariableId", taggedVariableId));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "tagVariableId", tagVariableId));
    

    

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
        return (CommonResponse) ApiInvoker.deserialize(response, "", CommonResponse.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
}
