package com.quantimodo.android.sdk;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.quantimodo.android.sdk.model.HistoryMeasurement;
import com.quantimodo.android.sdk.model.MeasurementSet;
import com.quantimodo.android.sdk.model.QuantimodoUser;
import com.quantimodo.android.sdk.model.Unit;
import com.quantimodo.android.sdk.model.Variable;
import com.quantimodo.android.sdk.model.VariableCategory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.quantimodo.android.sdk.SdkDefs.*;

@Deprecated
public class QuantimodoApi {

    public static final String QUANTIMODO_ADDRESS = SdkDefs.QUANTIMODO_ADDRESS;
    public static final int AUTH_QUANTIMODO = 0;
    public static final int AUTH_TOKEN = 1;


    public interface OnAuthenticationDoneListener {
        public void onSuccess(String authenticationToken);

        public void onFailed(String reason);
    }

    public static QuantimodoApi getInstance() {
        return new QuantimodoApi();
    }

    public String getAccessTokenSynchronous(Context context, Account account, String clientId, String clientSecret, String scopes) throws AuthenticatorException, OperationCanceledException, IOException {
        Bundle bundle = new Bundle();
        bundle.putString("clientId", clientId);
        bundle.putString("clientSecret", clientSecret);
        Bundle result = AccountManager.get(context).getAuthToken(account, scopes, bundle, true, null, null).getResult();
        Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);

        //TODO check intent for NULL, if NULL show notification to let the user sign in again (if scopes == "cookie") or reauthorize.

        return result.getString(AccountManager.KEY_AUTHTOKEN);
    }

    public void getAccessToken(final Activity activity, Account account, String clientId, String clientSecret, String scopes, final OnAuthenticationDoneListener listener) {
        if (account == null) {
            listener.onFailed("Account is null");
            return;
        }

        AccountManager manager = AccountManager.get(activity);
        Bundle bundle = new Bundle();
        bundle.putString("clientId", clientId);
        bundle.putString("clientSecret", clientSecret);
        if (manager != null) {
            manager.getAuthToken(account, scopes, bundle, activity, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> bundleAccountManagerFuture) {
                    try {
                        // If this doesn't throw an exception we'll have a token
                        Bundle result = bundleAccountManagerFuture.getResult();
                        listener.onSuccess(result.getString(AccountManager.KEY_AUTHTOKEN));
                    } catch (OperationCanceledException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (AuthenticatorException e) {
                        e.printStackTrace();
                        listener.onFailed(e.getMessage());
                    }
                }
            }, null);
        }
    }

//    @Deprecated
//    public ArrayList<HistoryMeasurement> getMeasurmentHistory(Context context, String token, Date startTime, Date endTime, String variableName, String source, String toUnitName) {
//        setupIon(context);
//
//        try {
//            Uri.Builder uriBuilder = Uri.parse(QUANTIMODO_ADDRESS + "api/measurements/history").buildUpon();
//            if (startTime != null) {
//                uriBuilder.appendQueryParameter("startTime", "" + startTime.getTime() / 1000);
//            }
//            if (endTime != null) {
//                uriBuilder.appendQueryParameter("endTime", "" + endTime.getTime() / 1000);
//            }
//            if (variableName != null) {
//                uriBuilder.appendQueryParameter("variableName", variableName);
//            }
//            if (source != null) {
//                uriBuilder.appendQueryParameter("source", source);
//            }
//            if (toUnitName != null) {
//                uriBuilder.appendQueryParameter("unit", toUnitName);
//            }
//
//            String response = Ion.with(context)
//                    .load(uriBuilder.build().toString())
//                    .setHeader("Authorization", "Bearer " + token)
//                    .asString()
//                    .get();
//
//            Type measurementType = new TypeToken<Collection<HistoryMeasurement>>() {
//            }.getType();
//            return Ion.getDefault(context).configure().getGson().fromJson(response, measurementType);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    public int putMeasurementsSynchronous(Context context, String token, List<MeasurementSet> measurementSets) {
        setupIon(context);

        try {
            Log.i("QMSDK", "Sending " + measurementSets.size() + " measurement sets");
            String response = Ion.with(context)
                    .load(QUANTIMODO_ADDRESS + "api/measurements/v2")
                    .setTimeout(10000 * measurementSets.size())
                    .setHeader("Authorization", "Bearer " + token)
                    .setJsonPojoBody(measurementSets)
                    .asString()
                    .get();
            try {
                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = (JsonObject) parser.parse(response);

                if (jsonResponse.get("success").getAsBoolean()) {
                    Log.i("QMSDK", "Done sending measurement sets");
                    return 1;
                } else {
                    Log.e("QMSDK", "Server returned an error " + jsonResponse.get("status").getAsInt());
                    Log.e("QMSDK", response);
                    return -1;  //TODO return proper error code
                }
            } catch (Exception e) {
                Log.e("QMSDK", "Failed to parse json, server returned:");
                Log.e("QMSDK", response);
                return -1;      //TODO return proper error code (500?)
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public ArrayList<Variable> searchVariables(Context context, String token, String search, int limit, int offset, String source, String category) {
        setupIon(context);

        if (search == null) {
            throw new IllegalArgumentException("\"search\" cannot be null");
        }

        try {
            search = URLEncoder.encode(search, "utf-8");
            search = "*" + search + "*";

            Uri.Builder uriBuilder = Uri.parse(QUANTIMODO_ADDRESS + "api/variables/search/" + search).buildUpon();
            uriBuilder.appendQueryParameter("limit", "" + limit);
            uriBuilder.appendQueryParameter("offset", "" + offset);

            if (source != null) {
                uriBuilder.appendQueryParameter("source", source);
            }
            if (category != null) {
                uriBuilder.appendQueryParameter("categoryName", category);
            }


            String response = Ion.with(context)
                    .load(uriBuilder.build().toString())
                    .setHeader("Authorization", "Bearer " + token)
                    .asString()
                    .get();

            Type collectionType = new TypeToken<ArrayList<Variable>>() {
            }.getType();
            return Ion.getDefault(context).configure().getGson().fromJson(response, collectionType);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Unit> getUnits(Context context, String token) {
        setupIon(context);

        try {
            String response = Ion.with(context)
                    .load(QUANTIMODO_ADDRESS + "api/units")
                    .setHeader("Authorization", "Bearer " + token)
                    .asString()
                    .get();

            Type collectionType = new TypeToken<ArrayList<Unit>>() {
            }.getType();
            return Ion.getDefault(context).configure().getGson().fromJson(response, collectionType);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<VariableCategory> getCategories(Context context, String token) {
        setupIon(context);

        try {
            String response = Ion.with(context)
                    .load(QUANTIMODO_ADDRESS + "api/variableCategories")
                    .setHeader("Authorization", "Bearer " + token)
                    .asString()
                    .get();

            Type collectionType = new TypeToken<ArrayList<VariableCategory>>() {
            }.getType();
            return Ion.getDefault(context).configure().getGson().fromJson(response, collectionType);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Variable> getVariables(Context context, String token) {
        setupIon(context);

        try {
            String response = Ion.with(context)
                    .load(QUANTIMODO_ADDRESS + "api/variables")
                    .setHeader("Authorization", "Bearer " + token)
                    .asString()
                    .get();

            Type collectionType = new TypeToken<ArrayList<Variable>>() {
            }.getType();
            return Ion.getDefault(context).configure().getGson().fromJson(response, collectionType);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public QuantimodoUser getUserSynchronous(Context context, String token) {
        setupIon(context);

        try {
            String response = Ion.with(context)
                    .load(QUANTIMODO_ADDRESS + "api/user")
                    .setHeader("Authorization", "Bearer " + token)
                    .asString()
                    .get();
            return Ion.getDefault(context).configure().getGson().fromJson(response, QuantimodoUser.class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setupIon(Context context) {
        Ion defaultIon = Ion.getDefault(context);
        defaultIon.configure().setLogging("QMSDK", Log.DEBUG);
        defaultIon.getCookieMiddleware().getCookieStore().removeAll();
        defaultIon.getCookieMiddleware().clear();

        GsonBuilder builder = new GsonBuilder();
        // Convert epoch returned by API to date
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong() * 1000);
            }
        });
        Gson gson = builder.create();
        defaultIon.configure().setGson(gson);
    }
}
