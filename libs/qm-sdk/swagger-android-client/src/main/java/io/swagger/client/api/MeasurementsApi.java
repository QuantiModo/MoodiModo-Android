package io.swagger.client.api;

import io.swagger.client.ApiException;
import io.swagger.client.ApiInvoker;
import io.swagger.client.Pair;

import io.swagger.client.SwaggerClient;
import io.swagger.client.model.*;

import java.util.*;

import io.swagger.client.model.MeasurementSource;
import io.swagger.client.model.Measurement;
import io.swagger.client.model.MeasurementSet;
import io.swagger.client.model.MeasurementDelete;
import io.swagger.client.model.CommonResponse;
import io.swagger.client.model.MeasurementRange;
import io.swagger.client.model.InlineResponse2003;
import io.swagger.client.model.InlineResponse2004;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

public class MeasurementsApi {
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
   * Get measurement sources
   * Returns a list of all the apps from which measurement data is obtained.
   * @return MeasurementSource
   */
  public MeasurementSource  v1MeasurementSourcesGet () throws ApiException {
    Object postBody = null;
    

    // create path and map variables
    String path = "/v1/measurementSources".replaceAll("\\{format\\}","json");

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
        return (MeasurementSource) ApiInvoker.deserialize(response, "", MeasurementSource.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Add a data source
   * Add a life-tracking app or device to the QuantiModo list of data sources.
   * @param body An array of names of data sources you want to add.
   * @param accessToken User&#39;s OAuth2 access token
   * @return void
   */
  public void  v1MeasurementSourcesPost (MeasurementSource body, String accessToken) throws ApiException {
    Object postBody = body;
    
    // verify the required parameter 'body' is set
    if (body == null) {
       throw new ApiException(400, "Missing the required parameter 'body' when calling v1MeasurementSourcesPost");
    }
    

    // create path and map variables
    String path = "/v1/measurementSources".replaceAll("\\{format\\}","json");

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
   * Get measurements for this user
   * Measurements are any value that can be recorded like daily steps, a mood rating, or apples eaten. &lt;br&gt;Supported filter parameters:&lt;br&gt;&lt;ul&gt;&lt;li&gt;&lt;b&gt;value&lt;/b&gt; - Value of measurement&lt;/li&gt;&lt;li&gt;&lt;b&gt;lastUpdated&lt;/b&gt; - The time that this measurement was created or last updated in the UTC format \&quot;YYYY-MM-DDThh:mm:ss\&quot;&lt;/li&gt;&lt;/ul&gt;&lt;br&gt;
   * @param accessToken User&#39;s OAuth2 access token
   * @param variableName Name of the variable you want measurements for
   * @param source Name of the source you want measurements for (supports exact name match only)
   * @param value Value of measurement
   * @param lastUpdated The time that this measurement was created or last updated in the format \&quot;YYYY-MM-DDThh:mm:ss\&quot;
   * @param unit The unit you want the measurements returned in
   * @param startTime The lower limit of measurements returned (Epoch)
   * @param createdAt The time the measurement record was first created in the format YYYY-MM-DDThh:mm:ss. Time zone should be UTC and not local.
   * @param updatedAt The time the measurement record was last changed in the format YYYY-MM-DDThh:mm:ss. Time zone should be UTC and not local.
   * @param endTime The upper limit of measurements returned (Epoch)
   * @param groupingWidth The time (in seconds) over which measurements are grouped together
   * @param groupingTimezone The time (in seconds) over which measurements are grouped together
   * @param limit The LIMIT is used to limit the number of results returned. So if you have 1000 results, but only want to the first 10, you would set this to 10 and offset to 0.
   * @param offset Now suppose you wanted to show results 11-20. You&#39;d set the offset to 10 and the limit to 10.
   * @param sort Sort by given field. If the field is prefixed with `-, it will sort in descending order.
   * @return Measurement
   */
  public Measurement  v1MeasurementsGet (String accessToken, String variableName, String source, String value, String lastUpdated, String unit, String startTime, String createdAt, String updatedAt, String endTime, Integer groupingWidth, String groupingTimezone, Integer limit, Integer offset, Integer sort) throws ApiException {
    Object postBody = null;
    

    // create path and map variables
    String path = "/v1/measurements".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "variableName", variableName));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "source", source));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "value", value));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "lastUpdated", lastUpdated));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "unit", unit));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "startTime", startTime));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "createdAt", createdAt));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "updatedAt", updatedAt));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "endTime", endTime));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "groupingWidth", groupingWidth));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "groupingTimezone", groupingTimezone));
    
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
        return (Measurement) ApiInvoker.deserialize(response, "", Measurement.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Post a new set or update existing measurements to the database
   * You can submit or update multiple measurements in a \&quot;measurements\&quot; sub-array.  If the variable these measurements correspond to does not already exist in the database, it will be automatically added.  The request body should look something like [{\&quot;measurements\&quot;:[{\&quot;startTime\&quot;:1439389320,\&quot;value\&quot;:\&quot;3\&quot;}, {\&quot;startTime\&quot;:1439389319,\&quot;value\&quot;:\&quot;2\&quot;}],\&quot;name\&quot;:\&quot;Acne (out of 5)\&quot;,\&quot;source\&quot;:\&quot;QuantiModo\&quot;,\&quot;category\&quot;:\&quot;Symptoms\&quot;,\&quot;combinationOperation\&quot;:\&quot;MEAN\&quot;,\&quot;unit\&quot;:\&quot;/5\&quot;}]
   * @param body An array of measurements you want to insert.
   * @param accessToken User&#39;s OAuth2 access token
   * @return void
   */
  public void  v1MeasurementsPost (MeasurementSet body, String accessToken) throws ApiException {
    Object postBody = body;
    
    // verify the required parameter 'body' is set
    if (body == null) {
       throw new ApiException(400, "Missing the required parameter 'body' when calling v1MeasurementsPost");
    }
    

    // create path and map variables
    String path = "/v1/measurements".replaceAll("\\{format\\}","json");

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
   * Get daily measurements for this user
   * Measurements are any value that can be recorded like daily steps, a mood rating, or apples eaten. &lt;br&gt;Supported filter parameters:&lt;br&gt;&lt;ul&gt;&lt;li&gt;&lt;b&gt;value&lt;/b&gt; - Value of measurement&lt;/li&gt;&lt;li&gt;&lt;b&gt;lastUpdated&lt;/b&gt; - The time that this measurement was created or last updated in the UTC format \&quot;YYYY-MM-DDThh:mm:ss\&quot;&lt;/li&gt;&lt;/ul&gt;&lt;br&gt;
   * @param variableName Name of the variable you want measurements for
   * @param accessToken User&#39;s OAuth2 access token
   * @param abbreviatedUnitName The unit your want the measurements in
   * @param startTime The lower limit of measurements returned (Iso8601)
   * @param endTime The upper limit of measurements returned (Iso8601)
   * @param groupingWidth The time (in seconds) over which measurements are grouped together
   * @param groupingTimezone The time (in seconds) over which measurements are grouped together
   * @param limit The LIMIT is used to limit the number of results returned. So if you have 1000 results, but only want to the first 10, you would set this to 10 and offset to 0.
   * @param offset Now suppose you wanted to show results 11-20. You&#39;d set the offset to 10 and the limit to 10.
   * @param sort Sort by given field. If the field is prefixed with `-, it will sort in descending order.
   * @return Measurement
   */
  public Measurement  v1MeasurementsDailyGet (String variableName, String accessToken, String abbreviatedUnitName, String startTime, String endTime, Integer groupingWidth, String groupingTimezone, Integer limit, Integer offset, Integer sort) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'variableName' is set
    if (variableName == null) {
       throw new ApiException(400, "Missing the required parameter 'variableName' when calling v1MeasurementsDailyGet");
    }
    

    // create path and map variables
    String path = "/v1/measurements/daily".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "variableName", variableName));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "abbreviatedUnitName", abbreviatedUnitName));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "startTime", startTime));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "endTime", endTime));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "groupingWidth", groupingWidth));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "groupingTimezone", groupingTimezone));
    
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
        return (Measurement) ApiInvoker.deserialize(response, "", Measurement.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Delete a measurement
   * Delete a previously submitted measurement
   * @param body The startTime and variableId of the measurement to be deleted.
   * @return CommonResponse
   */
  public CommonResponse  v1MeasurementsDeletePost (MeasurementDelete body, String accessToken) throws ApiException {
    Object postBody = body;
    
    // verify the required parameter 'body' is set
    if (body == null) {
       throw new ApiException(400, "Missing the required parameter 'body' when calling v1MeasurementsDeletePost");
    }
    

    // create path and map variables
    String path = "/v1/measurements/delete".replaceAll("\\{format\\}","json");

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
   * Get measurements range for this user
   * Get Unix time-stamp (epoch time) of the user&#39;s first and last measurements taken.
   * @param sources Enter source name to limit to specific source (varchar)
   * @param user If not specified, uses currently logged in user (bigint)
   * @return MeasurementRange
   */
  public MeasurementRange  v1MeasurementsRangeGet (String sources, Integer user) throws ApiException {
    Object postBody = null;
    

    // create path and map variables
    String path = "/v1/measurementsRange".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "sources", sources));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "user", user));
    

    

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
        return (MeasurementRange) ApiInvoker.deserialize(response, "", MeasurementRange.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Get Measurement
   * Get Measurement
   * @param id id of Measurement
   * @param accessToken User&#39;s OAuth2 access token
   * @return InlineResponse2003
   */
  public InlineResponse2003  v2MeasurementsIdGet (Integer id, String accessToken) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
       throw new ApiException(400, "Missing the required parameter 'id' when calling v2MeasurementsIdGet");
    }
    

    // create path and map variables
    String path = "/v2/measurements/{id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "id" + "\\}", apiInvoker.escapeString(id.toString()));

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
        return (InlineResponse2003) ApiInvoker.deserialize(response, "", InlineResponse2003.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Update Measurement
   * Update Measurement
   * @param id id of Measurement
   * @param accessToken User&#39;s OAuth2 access token
   * @param body Measurement that should be updated
   * @return InlineResponse2004
   */
  public InlineResponse2004  v2MeasurementsIdPut (Integer id, String accessToken, Measurement body) throws ApiException {
    Object postBody = body;
    
    // verify the required parameter 'id' is set
    if (id == null) {
       throw new ApiException(400, "Missing the required parameter 'id' when calling v2MeasurementsIdPut");
    }
    

    // create path and map variables
    String path = "/v2/measurements/{id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "id" + "\\}", apiInvoker.escapeString(id.toString()));

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
      String response = apiInvoker.invokeAPI(basePath, path, "PUT", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return (InlineResponse2004) ApiInvoker.deserialize(response, "", InlineResponse2004.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Delete Measurement
   * Delete Measurement
   * @param id id of Measurement
   * @param accessToken User&#39;s OAuth2 access token
   * @return InlineResponse2004
   */
  public InlineResponse2004  v2MeasurementsIdDelete (Integer id, String accessToken) throws ApiException {
    Object postBody = null;
    
    // verify the required parameter 'id' is set
    if (id == null) {
       throw new ApiException(400, "Missing the required parameter 'id' when calling v2MeasurementsIdDelete");
    }
    

    // create path and map variables
    String path = "/v2/measurements/{id}".replaceAll("\\{format\\}","json").replaceAll("\\{" + "id" + "\\}", apiInvoker.escapeString(id.toString()));

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
      String response = apiInvoker.invokeAPI(basePath, path, "DELETE", queryParams, postBody, headerParams, formParams, contentType);
      if(response != null){
        return (InlineResponse2004) ApiInvoker.deserialize(response, "", InlineResponse2004.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
}
