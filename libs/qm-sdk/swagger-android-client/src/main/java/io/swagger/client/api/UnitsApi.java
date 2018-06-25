package io.swagger.client.api;

import io.swagger.client.ApiException;
import io.swagger.client.ApiInvoker;
import io.swagger.client.Pair;

import io.swagger.client.SwaggerClient;
import io.swagger.client.model.*;

import java.util.*;

import io.swagger.client.model.UnitCategory;
import io.swagger.client.model.Unit;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.util.Map;
import java.util.HashMap;
import java.io.File;

public class UnitsApi {
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
   * Get unit categories
   * Get a list of the categories of measurement units such as &#39;Distance&#39;, &#39;Duration&#39;, &#39;Energy&#39;, &#39;Frequency&#39;, &#39;Miscellany&#39;, &#39;Pressure&#39;, &#39;Proportion&#39;, &#39;Rating&#39;, &#39;Temperature&#39;, &#39;Volume&#39;, and &#39;Weight&#39;.
   * @return UnitCategory
   */
  public UnitCategory  v1UnitCategoriesGet () throws ApiException {
    Object postBody = null;
    

    // create path and map variables
    String path = "/v1/unitCategories".replaceAll("\\{format\\}","json");

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
        return (UnitCategory) ApiInvoker.deserialize(response, "", UnitCategory.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Get all available units
   * Get all available units
   * @param accessToken User&#39;s OAuth2 access token
   * @param id Unit id
   * @param unitName Unit name
   * @param abbreviatedUnitName Restrict the results to a specific unit by providing the unit abbreviation.
   * @param categoryName Restrict the results to a specific unit category by providing the unit category name.
   * @return List<Unit>
   */
  public List<Unit>  v1UnitsGet (String accessToken, Integer id, String unitName, String abbreviatedUnitName, String categoryName) throws ApiException {
    Object postBody = null;
    

    // create path and map variables
    String path = "/v1/units".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "id", id));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "unitName", unitName));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "abbreviatedUnitName", abbreviatedUnitName));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "categoryName", categoryName));
    

    

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
        return (List<Unit>) ApiInvoker.deserialize(response, "array", Unit.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
  /**
   * Units for Variable
   * Get a list of all possible units to use for a given variable
   * @param accessToken User&#39;s OAuth2 access token
   * @param unitName Name of Unit you want to retrieve
   * @param abbreviatedUnitName Abbreviated Unit Name of the unit you want
   * @param categoryName Name of the category you want units for
   * @param variable Name of the variable you want units for
   * @return List<Unit>
   */
  public List<Unit>  v1UnitsVariableGet (String accessToken, String unitName, String abbreviatedUnitName, String categoryName, String variable) throws ApiException {
    Object postBody = null;
    

    // create path and map variables
    String path = "/v1/unitsVariable".replaceAll("\\{format\\}","json");

    // query params
    List<Pair> queryParams = new ArrayList<Pair>();
    // header params
    Map<String, String> headerParams = new HashMap<String, String>();
    // form params
    Map<String, String> formParams = new HashMap<String, String>();

    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "access_token", accessToken));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "unitName", unitName));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "abbreviatedUnitName", abbreviatedUnitName));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "categoryName", categoryName));
    
    queryParams.addAll(ApiInvoker.parameterToPairs("", "variable", variable));
    

    

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
        return (List<Unit>) ApiInvoker.deserialize(response, "array", Unit.class);
      }
      else {
        return null;
      }
    } catch (ApiException ex) {
      throw ex;
    }
  }
  
}
