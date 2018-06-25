package io.swagger.client.api;

import io.swagger.client.ApiException;
import io.swagger.client.ApiInvoker;
import io.swagger.client.Pair;

import io.swagger.client.SwaggerClient;
import io.swagger.client.model.*;

import java.util.*;

import io.swagger.client.model.Correlation;
import io.swagger.client.model.JsonErrorResponse;
import io.swagger.client.model.PostCorrelation;
import io.swagger.client.model.CommonResponse;
import io.swagger.client.model.PostVote;
import io.swagger.client.model.VoteDelete;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

public class CorrelationsApi {
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
   * Get aggregated correlations
   * Get correlations based on the anonymized aggregate data from all QuantiModo users.
   * @param accessToken User&#39;s OAuth2 access token
   * @param effect ORIGINAL variable name of the effect variable for which the user desires correlations
   * @param cause ORIGINAL variable name of the cause variable for which the user desires correlations
   * @param correlationCoefficient Pearson correlation coefficient between cause and effect after lagging by onset delay and grouping by duration of action
   * @param onsetDelay The number of seconds which pass following a cause measurement before an effect would likely be observed.
   * @param durationOfAction The time in seconds over which the cause would be expected to exert a measurable effect. We have selected a default value for each variable. This default value may be replaced by a user specified by adjusting their variable user settings.
   * @param lastUpdated The time that this measurement was last updated in the UTC format \&quot;YYYY-MM-DDThh:mm:ss\&quot;
   * @param limit The LIMIT is used to limit the number of results returned. So if you have 1000 results, but only want to the first 10, you would set this to 10 and offset to 0.
   * @param offset Now suppose you wanted to show results 11-20. You&#39;d set the offset to 10 and the limit to 10.
   * @param sort Sort by given field. If the field is prefixed with `-, it will sort in descending order.
   * @return List<Correlation>
   */
  public List<Correlation>  v1AggregatedCorrelationsGet (String accessToken, String effect, String cause, String correlationCoefficient, String onsetDelay, String durationOfAction, String lastUpdated, Integer limit, Integer offset, Integer sort) throws ApiException {
    Object postBody = null;
    

    // create path and map variables
    String path = "/v1/aggregatedCorrelations".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "effect", effect));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "cause", cause));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "correlationCoefficient", correlationCoefficient));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "onsetDelay", onsetDelay));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "durationOfAction", durationOfAction));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "lastUpdated", lastUpdated));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "limit", limit));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "offset", offset));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "sort", sort));
    

    

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
        return (List<Correlation>) ApiInvoker.deserialize(response, "array", Correlation.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Store or Update a Correlation
   * Add correlation
   * @param body Provides correlation data
   * @param accessToken User&#39;s OAuth2 access token
   * @return void
   */
  public void  v1AggregatedCorrelationsPost (PostCorrelation body, String accessToken) throws ApiException {
    Object postBody = body;
    
    // verify the required parameter 'body' is set
    if (body == null) {
       throw new ApiException(400, "Missing the required parameter 'body' when calling v1AggregatedCorrelationsPost");
    }
    

    // create path and map variables
    String path = "/v1/aggregatedCorrelations".replaceAll("\\{format\\}","json");

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
   * Get correlations
   * Get correlations.&lt;br&gt;Supported filter parameters:&lt;br&gt;&lt;ul&gt;&lt;li&gt;&lt;b&gt;correlationCoefficient&lt;/b&gt; - Pearson correlation coefficient between cause and effect after lagging by onset delay and grouping by duration of action&lt;/li&gt;&lt;li&gt;&lt;b&gt;onsetDelay&lt;/b&gt; - The number of seconds which pass following a cause measurement before an effect would likely be observed.&lt;/li&gt;&lt;li&gt;&lt;b&gt;durationOfAction&lt;/b&gt; - The time in seconds over which the cause would be expected to exert a measurable effect. We have selected a default value for each variable. This default value may be replaced by a user specified by adjusting their variable user settings.&lt;/li&gt;&lt;li&gt;&lt;b&gt;lastUpdated&lt;/b&gt; - The time that this measurement was last updated in the UTC format \&quot;YYYY-MM-DDThh:mm:ss\&quot;&lt;/li&gt;&lt;/ul&gt;&lt;br&gt;
   * @param accessToken User&#39;s OAuth2 access token
   * @param effect ORIGINAL variable name of the effect variable for which the user desires correlations
   * @param cause ORIGINAL variable name of the cause variable for which the user desires correlations
   * @param correlationCoefficient Pearson correlation coefficient between cause and effect after lagging by onset delay and grouping by duration of action
   * @param onsetDelay The number of seconds which pass following a cause measurement before an effect would likely be observed.
   * @param durationOfAction The time in seconds over which the cause would be expected to exert a measurable effect. We have selected a default value for each variable. This default value may be replaced by a user specified by adjusting their variable user settings.
   * @param lastUpdated The time that this measurement was last updated in the UTC format \&quot;YYYY-MM-DDThh:mm:ss\&quot;
   * @param limit The LIMIT is used to limit the number of results returned. So if you have 1000 results, but only want to the first 10, you would set this to 10 and offset to 0.
   * @param offset Now suppose you wanted to show results 11-20. You&#39;d set the offset to 10 and the limit to 10.
   * @param sort Sort by given field. If the field is prefixed with `-, it will sort in descending order.
   * @return List<Correlation>
   */
  public List<Correlation>  v1CorrelationsGet (String accessToken, String effect, String cause, String correlationCoefficient, String onsetDelay, String durationOfAction, String lastUpdated, Integer limit, Integer offset, Integer sort) throws ApiException {
    Object postBody = null;
    

    // create path and map variables
    String path = "/v1/correlations".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "effect", effect));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "cause", cause));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "correlationCoefficient", correlationCoefficient));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "onsetDelay", onsetDelay));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "durationOfAction", durationOfAction));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "lastUpdated", lastUpdated));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "limit", limit));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "offset", offset));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "sort", sort));
    

    

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
        return (List<Correlation>) ApiInvoker.deserialize(response, "array", Correlation.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Search user correlations for a given cause
   * Returns average of all correlations and votes for all user cause variables for a given cause. If parameter \&quot;include_public\&quot; is used, it also returns public correlations. User correlation overwrites or supersedes public correlation.
   * @param organizationId Organization ID
   * @param userId User id
   * @param variableName Effect variable name
   * @param organizationToken Organization access token
   * @param accessToken User&#39;s OAuth2 access token
   * @param includePublic Include public correlations, Can be \&quot;1\&quot; or empty.
   * @return List<Correlation>
   */
  public List<Correlation>  v1OrganizationsOrganizationIdUsersUserIdVariablesVariableNameCausesGet (Integer organizationId, Integer userId, String variableName, String organizationToken, String accessToken, String includePublic) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'organizationId' is set
    if (organizationId == null) {
       throw new ApiException(400, "Missing the required parameter 'organizationId' when calling v1OrganizationsOrganizationIdUsersUserIdVariablesVariableNameCausesGet");
    }
    
    // verify the required parameter 'userId' is set
    if (userId == null) {
       throw new ApiException(400, "Missing the required parameter 'userId' when calling v1OrganizationsOrganizationIdUsersUserIdVariablesVariableNameCausesGet");
    }
    
    // verify the required parameter 'variableName' is set
    if (variableName == null) {
       throw new ApiException(400, "Missing the required parameter 'variableName' when calling v1OrganizationsOrganizationIdUsersUserIdVariablesVariableNameCausesGet");
    }
    
    // verify the required parameter 'organizationToken' is set
    if (organizationToken == null) {
       throw new ApiException(400, "Missing the required parameter 'organizationToken' when calling v1OrganizationsOrganizationIdUsersUserIdVariablesVariableNameCausesGet");
    }
    

    // create path and map variables
    String path = "/v1/organizations/{organizationId}/users/{userId}/variables/{variableName}/causes".replaceAll("\\{format\\}","json").replaceAll("\\{" + "organizationId" + "\\}", apiInvoker.escapeString(organizationId.toString())).replaceAll("\\{" + "userId" + "\\}", apiInvoker.escapeString(userId.toString())).replaceAll("\\{" + "variableName" + "\\}", apiInvoker.escapeString(variableName.toString()));

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "organization_token", organizationToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "includePublic", includePublic));
    

    

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
        return (List<Correlation>) ApiInvoker.deserialize(response, "array", Correlation.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Search user correlations for a given cause
   * Returns average of all correlations and votes for all user cause variables for a given effect. If parameter \&quot;include_public\&quot; is used, it also returns public correlations. User correlation overwrites or supersedes public correlation.
   * @param organizationId Organization ID
   * @param userId User id
   * @param variableName Cause variable name
   * @param organizationToken Organization access token
   * @param accessToken User&#39;s OAuth2 access token
   * @param includePublic Include public correlations, Can be \&quot;1\&quot; or empty.
   * @return List<CommonResponse>
   */
  public List<CommonResponse>  v1OrganizationsOrganizationIdUsersUserIdVariablesVariableNameEffectsGet (Integer organizationId, Integer userId, String variableName, String organizationToken, String accessToken, String includePublic) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'organizationId' is set
    if (organizationId == null) {
       throw new ApiException(400, "Missing the required parameter 'organizationId' when calling v1OrganizationsOrganizationIdUsersUserIdVariablesVariableNameEffectsGet");
    }
    
    // verify the required parameter 'userId' is set
    if (userId == null) {
       throw new ApiException(400, "Missing the required parameter 'userId' when calling v1OrganizationsOrganizationIdUsersUserIdVariablesVariableNameEffectsGet");
    }
    
    // verify the required parameter 'variableName' is set
    if (variableName == null) {
       throw new ApiException(400, "Missing the required parameter 'variableName' when calling v1OrganizationsOrganizationIdUsersUserIdVariablesVariableNameEffectsGet");
    }
    
    // verify the required parameter 'organizationToken' is set
    if (organizationToken == null) {
       throw new ApiException(400, "Missing the required parameter 'organizationToken' when calling v1OrganizationsOrganizationIdUsersUserIdVariablesVariableNameEffectsGet");
    }
    

    // create path and map variables
    String path = "/v1/organizations/{organizationId}/users/{userId}/variables/{variableName}/effects".replaceAll("\\{format\\}","json").replaceAll("\\{" + "organizationId" + "\\}", apiInvoker.escapeString(organizationId.toString())).replaceAll("\\{" + "userId" + "\\}", apiInvoker.escapeString(userId.toString())).replaceAll("\\{" + "variableName" + "\\}", apiInvoker.escapeString(variableName.toString()));

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "organization_token", organizationToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "include_public", includePublic));
    

    

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
        return (List<CommonResponse>) ApiInvoker.deserialize(response, "array", CommonResponse.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Get average correlations for variables containing search term
   * Returns the average correlations from all users for all public variables that contain the characters in the search query. Returns average of all users public variable correlations with a specified cause or effect.
   * @param search Name of the variable that you want to know the causes or effects of.
   * @param effectOrCause Setting this to effect indicates that the searched variable is the effect and that the causes of this variable should be returned.  cause indicates that the searched variable is the cause and the effects should be returned.
   * @param accessToken User&#39;s OAuth2 access token
   * @return List<Correlation>
   */
  public List<Correlation>  v1PublicCorrelationsSearchSearchGet (String search, String effectOrCause, String accessToken) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'search' is set
    if (search == null) {
       throw new ApiException(400, "Missing the required parameter 'search' when calling v1PublicCorrelationsSearchSearchGet");
    }
    
    // verify the required parameter 'effectOrCause' is set
    if (effectOrCause == null) {
       throw new ApiException(400, "Missing the required parameter 'effectOrCause' when calling v1PublicCorrelationsSearchSearchGet");
    }
    

    // create path and map variables
    String path = "/v1/public/correlations/search/{search}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "search" + "\\}", apiInvoker.escapeString(search.toString()));

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "effectOrCause", effectOrCause));
    

    

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
        return (List<Correlation>) ApiInvoker.deserialize(response, "array", Correlation.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Search user correlations for a given effect
   * Returns average of all correlations and votes for all user cause variables for a given effect
   * @param variableName Effect variable name
   * @return List<Correlation>
   */
  public List<Correlation>  v1VariablesVariableNameCausesGet (String variableName) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'variableName' is set
    if (variableName == null) {
       throw new ApiException(400, "Missing the required parameter 'variableName' when calling v1VariablesVariableNameCausesGet");
    }
    

    // create path and map variables
    String path = "/v1/variables/{variableName}/causes".replaceAll("\\{format\\}","json").replaceAll("\\{" + "variableName" + "\\}", apiInvoker.escapeString(variableName.toString()));

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
        return (List<Correlation>) ApiInvoker.deserialize(response, "array", Correlation.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Search user correlations for a given cause
   * Returns average of all correlations and votes for all user effect variables for a given cause
   * @param variableName Cause variable name
   * @param accessToken User&#39;s OAuth2 access token
   * @return List<Correlation>
   */
  public List<Correlation>  v1VariablesVariableNameEffectsGet (String variableName, String accessToken) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'variableName' is set
    if (variableName == null) {
       throw new ApiException(400, "Missing the required parameter 'variableName' when calling v1VariablesVariableNameEffectsGet");
    }
    

    // create path and map variables
    String path = "/v1/variables/{variableName}/effects".replaceAll("\\{format\\}","json").replaceAll("\\{" + "variableName" + "\\}", apiInvoker.escapeString(variableName.toString()));

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
        return (List<Correlation>) ApiInvoker.deserialize(response, "array", Correlation.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Search public correlations for a given effect
   * Returns average of all correlations and votes for all public cause variables for a given effect
   * @param variableName Effect variable name
   * @param accessToken User&#39;s OAuth2 access token
   * @return List<Correlation>
   */
  public List<Correlation>  v1VariablesVariableNamePublicCausesGet (String variableName, String accessToken) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'variableName' is set
    if (variableName == null) {
       throw new ApiException(400, "Missing the required parameter 'variableName' when calling v1VariablesVariableNamePublicCausesGet");
    }
    

    // create path and map variables
    String path = "/v1/variables/{variableName}/public/causes".replaceAll("\\{format\\}","json").replaceAll("\\{" + "variableName" + "\\}", apiInvoker.escapeString(variableName.toString()));

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
        return (List<Correlation>) ApiInvoker.deserialize(response, "array", Correlation.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Search public correlations for a given cause
   * Returns average of all correlations and votes for all public cause variables for a given cause
   * @param variableName Cause variable name
   * @param accessToken User&#39;s OAuth2 access token
   * @return List<Correlation>
   */
  public List<Correlation>  v1VariablesVariableNamePublicEffectsGet (String variableName, String accessToken) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'variableName' is set
    if (variableName == null) {
       throw new ApiException(400, "Missing the required parameter 'variableName' when calling v1VariablesVariableNamePublicEffectsGet");
    }
    

    // create path and map variables
    String path = "/v1/variables/{variableName}/public/effects".replaceAll("\\{format\\}","json").replaceAll("\\{" + "variableName" + "\\}", apiInvoker.escapeString(variableName.toString()));

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
        return (List<Correlation>) ApiInvoker.deserialize(response, "array", Correlation.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Post or update vote
   * This is to enable users to indicate their opinion on the plausibility of a causal relationship between a treatment and outcome. QuantiModo incorporates crowd-sourced plausibility estimations into their algorithm. This is done allowing user to indicate their view of the plausibility of each relationship with thumbs up/down buttons placed next to each prediction.
   * @param body Contains the cause variable, effect variable, and vote value.
   * @param accessToken User&#39;s OAuth2 access token
   * @return CommonResponse
   */
  public CommonResponse  v1VotesPost (PostVote body, String accessToken) throws ApiException {
    Object postBody = body;
    
    // verify the required parameter 'body' is set
    if (body == null) {
       throw new ApiException(400, "Missing the required parameter 'body' when calling v1VotesPost");
    }
    

    // create path and map variables
    String path = "/v1/votes".replaceAll("\\{format\\}","json");

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
   * Delete vote
   * Delete previously posted vote
   * @param body The cause and effect variable names for the predictor vote to be deleted.
   * @return CommonResponse
   */
  public CommonResponse  v1VotesDeletePost (VoteDelete body, String accessToken) throws ApiException {
    Object postBody = body;
    
    // verify the required parameter 'body' is set
    if (body == null) {
       throw new ApiException(400, "Missing the required parameter 'body' when calling v1VotesDeletePost");
    }
    

    // create path and map variables
    String path = "/v1/votes/delete".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
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
