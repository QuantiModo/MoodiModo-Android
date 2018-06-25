QuantiModo-SDK-Android
======================

[![Codacy Badge](https://api.codacy.com/project/badge/grade/2602cca77a94422284880f649a43f8c4)](https://www.codacy.com/app/m_3/QuantiModo-SDK-Android)

This is the software development kit for implementing enabling Android Applications to store or access user Quantified Self data in the QuantiModo database.

# Getting Started

### Step 1: Create Your Developer Account
Create your free developer account and app at [https://admin.quantimo.do/register](https://admin.quantimo.do/register).

### Step 2: Create Your App
Create your app on [https://admin.quantimo.do/apps](https://admin.quantimo.do/apps) and get your client_id and client_secret from it, save them to set up your project later.

### Step 3: Add the QM SDK module to your app

The SDK module contains the model classes and API client for QuantiModo API web-service.

Add the QM SDK Module Using Maven:
```
<dependency>
  <groupId>com.quantimodo.android</groupId>
  <artifactId>sdk</artifactId>
  <version>2.2.4</version>
  <type>aar</type>
</dependency>
```

Or using Gradle:
```
compile 'com.quantimodo.android:sdk:2.2.4'
```



### Step 4. Enable your user to connect to the QM API

There are two ways to implement Quantimodo login on Android:

- LoginButton class - Which provides a button you can add to your UI. It follows the current access token and can log people in and out.
- LoginManager class - To initiate login without using a UI element.

### Prerequisites
Before you implement Quantimodo Login you need:

- Create your Quantimodo App. See Step 2
- Get the Client Id and Client Secret 
- Add the QuantimodoSDK to your project. See step 3
- Add AuthenticatorActivity to your AndroidManifest.xml:s

```
<activity android:name="com.quantimodo.android.sdk.login.AuthenticatorActivity"
    android:configChanges="orientation|keyboardHidden|screenSize"
    >
</activity>
```

### Add Quantimodo Login
    
The simplest way to add Quantimodo Login to your app is to add LoginButton from the SDK. 

There are other classes you use to add login to your app. The SDK includes:

- LoginManager: Initiates the login process.
- LoginButton: This UI element wraps functionality available in the LoginManager. So when someone clicks on the button, the login is initiated.
- QuantimodoSDKHelper: This class holds the auth token after successfully signed in, which can be used to perform request to the API
- ToolsPrefs: Needed class to setup the constants used by the SDK

### Add the Login Button
    
Add the button to your layout XML file with the full class name, com.quantimodo.android.sdk.login.widget.LoginButton:

```
<com.quantimodo.android.sdk.login.widget.LoginButton
    android:id="@+id/login_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal"
    android:layout_marginTop="30dp"
    android:layout_marginBottom="30dp" />
```

As a second option you can directly call `LoginManager.getInstance().performLogin`, you have to pass the current Activity 
or Fragment as parameter to performLogin:

```
LoginManager.getInstance().performLogin(MainActivity.this);
```

### Implementing the code

To use the authenticator follow this full example:
```
public class MainActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToolsPrefs.getInstance().initialize();
        QuantimodoSDKHelper.getInstance().initialize(
                this, //current context
                "myAppName", //your app name
                "my_client_id", //your private client id
                "my_client_secret" //your private client secret or password
        );
    }
}
```


If login succeeds, the `QuantimodoSDKHelper` object has the new auth token value, you can get it implementing 
onActivityResult on your Activity or Fragment

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == LoginManager.LOGIN_REQUEST_CODE){
        if(resultCode == RESULT_OK){ 
            Toast.makeText(this, "login result ok", Toast.LENGTH_LONG).show();
            try {
                String authToken = QuantimodoSDKHelper.getInstance().getAuthTokenWithRefresh();
            } catch (NoNetworkConnection noNetworkConnection) {
                noNetworkConnection.printStackTrace();
            }
        }
        else if(resultCode == RESULT_CANCELED) Toast.makeText(this, "login result canceled", Toast.LENGTH_LONG).show();
        else Toast.makeText(this, "login error", Toast.LENGTH_LONG).show();
    }
}
```

### Step 5. Send user data to the QM API
Here is an example of how to submit a measurement to QuantiModo service for variable "Overall Mood"

```
//Variable Category : Mood
//Variable : Overall Mood
//Unit : /5 ( from 1 to 5 )
//Source name : Sample application
MeasurementSet sets = new MeasurementSet("Overall Mood", null, "Mood", "/5", MeasurementSet.COMBINE_MEAN, "Sample application");

//Measurement with Overall Mood, with 5 out of 5 rating, that submitted right now
Measurement measurement = new Measurement(System.currentTimeMillis() / 1000, 5.0d);

//Adding measurement into set
sets.getMeasurements().add(measurement);

String token = "oauth_token";
QuantimodoApiV2 api = QuantimodoApiV2.getInstance(null);

SdkResponse<Integer> result = api.putMeasurements(ctx,token,sets);
if (result.isSuccessful() && ((Integer) 1).equals(result.getData())){
  Log.i("sample","sent");
}
```

For more info, please check the [JavaDocs](http://quantimodo.github.io/QuantiModo-SDK-Android/javadoc/sdk/).
You can find examples of usage in our [tests](https://github.com/QuantiModo/QuantiModo-SDK-Android/tree/develop/sdk/src/androidTest/).

### Step 6. Get your user's data from the QM API
You can get a QuantimodoUser object like this:

```
QuantimodoApiV2 api = QuantimodoApiV2.getInstance(null);

SdkResponse<QuantimodoUser> response = api.getUser(context, token);
```

### Step 7. Add QTools to your application

The Qtools module is a set of components that make it easy to create an application that will take full advantage of the QuantiModo platform. Qtools handles:
- Auth and token management
- Data synchronization with the QuantiModo we service
- Network interaction using [RoboSpice](https://github.com/stephanenicolas/robospice) 

Using Maven:
```
<dependency>
  <groupId>com.quantimodo.android</groupId>
  <artifactId>sdk-tools</artifactId>
  <version>1.0</version>
  <type>aar</type>
</dependency>
```

Or using Gradle:
```
compile 'com.quantimodo.android:sdk-tools:1.0'
```

#### Configure QTools

Dependency injection (DI) is used to initialize components. Several components need to be initialized to use QTools:

1. [Dagger](http://square.github.io/dagger/#using) facilitates dependency injection
2. ToolsPrefs contains info about the endpoint such as the base URL, application source, permissions
3. SpiceService is used to configure caching
4. SyncService is used to sync data between the app and QuantiModo
5. [QApplication](http://quantimodo.github.io/QuantiModo-SDK-Android/javadoc/qm-tools/index.html?com/quantimodo/tools/QApplication.html) interface, which provides dependencies to other components.

First, you need to implement the [QApplication](http://quantimodo.github.io/QuantiModo-SDK-Android/javadoc/qm-tools/index.html?com/quantimodo/tools/QApplication.html) interface,
or you can extend the [QBaseApplication](http://quantimodo.github.io/QuantiModo-SDK-Android/javadoc/qm-tools/index.html?com/quantimodo/tools/QApplication.html).

To do that, you need to create a Module that would provide dependency injection. An example can be found in [tests sources](https://github.com/QuantiModo/QuantiModo-SDK-Android/blob/master/quantimodo-sdk-tools/src/androidTest/java/com/quantimodo/tools/testhelpers/TestModule.java)

The configuration is stored in ToolsPrefs class instance, which should be created
```
ToolsPrefs prefs = new ToolsPrefs("https://app.quantimo.do/","readmeasurements writemeasurements","QuantimodoTest");
```
Then you need to create AuthHelper component:
```
mAuthHelper = new AuthHelper(applicationContext,prefs);
```
Next you have to set an action application icon, used to customize several notifications implemented on Qtools:
```
QTools.setAppActionIcon(R.drawable.ic_action_appicon);
```
Refer to [test sources](https://github.com/QuantiModo/QuantiModo-SDK-Android/blob/master/quantimodo-sdk-tools/src/androidTest/java/com/quantimodo/tools/testhelpers/) to see how to configure the application.

#### Useful QTools Components

- [AuthHelper](http://quantimodo.github.io/QuantiModo-SDK-Android/javadoc/qm-tools/index.html?com/quantimodo/tools/sdk/AuthHelper.html) provides access and refresh tokens needed to access the API.
- [TrackingFragment](http://quantimodo.github.io/QuantiModo-SDK-Android/javadoc/qm-tools/index.html?com/quantimodo/tools/fragments/TrackingFragment.html), would help you to submit tokens, could be configurated to show/submit to one category or to any
- [FactorsFragment](http://quantimodo.github.io/QuantiModo-SDK-Android/javadoc/qm-tools/index.html?com/quantimodo/tools/fragments/FactorsFragment.html) lists the strongest predictors for a specified outcome such as Overall Mood or Back Pain.
- [ImportWebFragment](http://quantimodo.github.io/QuantiModo-SDK-Android/javadoc/qm-tools/index.html?com/quantimodo/tools/fragments/ImportWebFragment.html) creates a view where your users can import their data from 3rd-party services like Fitbit, Withings, etc.
- [SyncHelper](http://quantimodo.github.io/QuantiModo-SDK-Android/javadoc/qm-tools/index.html?com/quantimodo/tools/sync/SyncHelper.html) helps to configure data synchronization between the mobile device and the web service.


### Step 8. Import user data from 3rd party sources
Qtools provides an authenticator to easily manage your users.

To use the authenticator, start `QuantimodoLoginActivity`. This allows the user to sign in with Facebook, Google, and Quantimodo directly.
 
The QuantimodoLoginActivity can take two optional parameters:

- `KEY_SHOW_LOGIN_AGAIN` (boolean): if true a toast will pop up right after the Activity starts saying: "You need to log in again"
- `KEY_APP_NAME` (string): is used to customize the Quantimodo log in button. 
If you provide it, the button text will be "Sign in with KEY_APP_NAME", if not, the text will just be: "Sign in".

Example:

```
Intent auth = new Intent(context, QuantimodoLoginActivity.class);
auth.putExtra(QuantimodoLoginActivity.KEY_APP_NAME, getString(R.string.app_name));
auth.putExtra(QuantimodoLoginActivity.KEY_SHOW_LOGIN_AGAIN, true);
startActivity(auth)
```

Of course, authentication uses internet connection, so make sure to include `android.permission.INTERNET` on your AndroidManifest.xml file:

```
<!-- Add it as a child of <manifest> tag -->
<uses-permission android:name="android.permission.INTERNET" />
```

## Running tests

Connect your device and run in the project root:

```
./gradlew cAT
```

You can also run them in [Android Studio](http://developer.android.com/training/testing/unit-testing/local-unit-tests.html).

## Developing the SDK itself
To work on the SDK you can add it manually as a git submodule folder inside your project. You have to do the following:

1. Create a folder in your project called `libs/qm-sdk`.
2. Create a file called `.gitmodules` in the root of your project and include the following code:

```
[submodule "libs/qm-sdk"]
	path = libs/qm-sdk
	url = git@github.com:QuantiModo/QuantiModo-SDK-Android.git
```

3. Then execute the following commands in the terminal (at the root of the project):

```
git submodule init
git submodule sync
git submodule update
```

**Use the SDK as a submodule**

Create a folder, for example, libs/ and inside it execute:
```
$ git submodule add git@github.com:QuantiModo/QuantiModo-SDK-Android.git
```
After that, the folder 'Quantimodo-SDK-Android' will be created containing the SDK as a submodule.

### Deploy and Release Updates to the QM-Android-SDK

#### Releasing a new version of the SDK and QTools

- See detailed instructions [here](https://github.com/Abolitionist-Project/OLD-QuantiModo-SDK-Android/wiki/Android-SDK-Distribution)
- Run `gradle upload artifact` in the `/quantimodo-sdk-tools` and in `/sdk`.
- Credentials can be found [here](https://docs.google.com/document/d/1kOqPrefgqHAoOylIdCuvcremAJ7dUWVSVK9QiN_uJrQ/edit)

The SDK can be found in the [Sonatype Central Repository](https://oss.sonatype.org/#nexus-search;quick~quantimodo) for open-source software. See http://central.sonatype.org/pages/ossrh-guide.html

[Guide To Publish An Aar To Maven Using Gradle](http://www.survivingwithandroid.com/2014/05/android-guide-to-publish-aar-to-maven-gradle.html)
