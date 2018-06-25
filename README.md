![moodimodo-chrome-extension-promotional-image-1400x560-1024x409](https://cloud.githubusercontent.com/assets/2808553/3848851/aeda3f7e-1e73-11e4-83be-eb403a8b3fbe.jpg)

[MoodiModo](https://play.google.com/store/apps/details?id=com.moodimodo&hl=en) is an open source project for Android aimed at gathering the data necessary to eradicate mental illness and maximize gross national happiness.

#### How MoodiModo Can Help
* Identify Negative Mood Triggers - Integrate mood data with other life-tracking data at [QuantiModo](https://quantimo.do/)
* Quantify the Effectiveness of Treatments at [QuantiModo](https://quantimo.do/)
* Unique Pop-Up Interface - Effortlessly track your mood on a regular basis
* Unintrusive Status Bar Notifications - Rate mood in a fraction of a second in the notification menu
* High-Frequency Tracking - Necessary to apply causal predictive analysis to figure out what is affecting your mood
* High Precision Mood Ratings - Submit a more detailed and precise measurement of your mood when time allows. This is done by answering questions related to the 20 different feelings and emotions defined by the Positive and Negative Affect Schedule (PANAS). 
* Reminder Frequency Setting
* Cloud Backup at [QuantiModo](https://quantimo.do/)
* Analyze Your Mood Over Time - Review their mood trends retrospectively to try to find causal relationships
* Social Sharing - Easily share graphs of their mood on various social media websites such as Facebook, Google+, Twitter, Tumblr, and Pinterest

### Contributing
**How You Can Help Abolish Suffering**
- Please follow the Git-Flow model when contributing code to the repository.  This means you should create a new branch for any features or hotfixes you do.  Then you make a request to merge to the development branch when your addition is complete. See this great [tutorial](https://www.atlassian.com/git/workflows#!workflow-gitflow) to find out how.
- Use MoodiMood as a [template](https://github.com/mikepsinn/MoodiModo-Android/wiki/Guide-for-New-Developers) for your own app to gather data on other symptoms of chronic illness.  
- Help us close some of these [issues](https://github.com/mikepsinn/MoodiModo-Android/issues). There's still a lot of documentation that needs to be done so please ask as many questions as you can in the comments of the issues. 
- Improve [Wiki](https://github.com/Abolitionist-Project/MoodiModo-Android/wiki) Documentation
- Follow [Code Style Guidelines for Contributors](https://source.android.com/source/code-style.html) with a modified 180 character per line limit 

### Requirement: Install QuantiModo for Android
The [QuantiModo for Android app](https://play.google.com/store/apps/details?id=com.quantimodo.android&hl=en) must also be installed for your users to upload life-tracking data to our free cloud storage system.  At QuantiModo, data from a variety of applications can be integrated and analyzed to help you discover hidden correlations between your mood, aspects of health, productivity and any of the countless variables that can affect them. 

##### [The Costs of Mental Illness](https://quantimo.do/cost-of-mental-illness/)
Mental health is the greatest unmet need of our time. About half of the people you know will develop a mental illness at some point during their lifetime. Over 50 million Americans currently suffer from some form of mental illness. MoodiModo is intended to help those who suffer from mood disorders such as major depressive disorder, dysthymic disorder, and bipolar disorder.

##### How MoodiModo Can Help
You can’t manage what you don’t measure. Hence, it is crucial that mood is continually tracked and quantified in order to assess the effectiveness of various treatments. Those suffering from depression are characteristically non-compliant towards most avenues of treatment, including mood tracking.

When the user first installs the app, they get to set this reminder frequency and indicate their preferred means of notification, as well as reporting their first mood, engaging the user from the moment they start using MoodiModo.

##### Social Sharing

The social sharing features of MoodiModo would facilitate social support that is a critical element to recovery from these disorders. MoodiModo allows the user to easily share graphs of their mood on various social media websites such as Facebook, Google+, Twitter, Tumblr, and Pinterest. This will inform friends and family when someone is suffering.  Since emotional states are often invisible, visual depiction of emotion can ensure that no one ever needs to suffer alone in silence.

##### Figure Out What Affects Your Mood

Physical and environmental factors influence how you feel, just as emotions often cause changes in your physical system. There are 100 billion neurons in the human brain, and each of them is busy most of the time constantly sending tiny electrical impulses from one brain cell to another.  This signaling is modulated by chemicals called neurotransmitters. This activity is the biochemical basis of feelings.

In turn, brain chemistry is influenced by a number of factors including the degree of social interaction, sleep, diet, medication, and physical activity. Mood data from MoodiModo can also be automatically uploaded to the QuantiModo website.  At QuantiModo, this data can be combined with data from other applications, devices, and electronic health records.  Since the human mind is not powerful enough to retain all of the necessary information, this data integration feature is essential to identifying correlations and causal relationships.

##### Crowdsourcing Cures

Users will also have the option to anonymously donate their data to the Mind First Foundation, the Personal Genome Project, and other researchers in order to help facilitate the crowdsourced observational research which will eventually lead to the eradication of mental illness.

##### Try MoodiModo!

MoodiModo is currently available as a beta on [Google Play](https://play.google.com/store/apps/details?id=com.moodimodo&hl=en).

![screenshot_-moodimodo20](http://i.imgur.com/paDw2O8.png)

## Developing with MoodiModo
To work on MoodiModo you have to get the project, and configure the SDK that's included on the project as a submodule, to do that open a terminal and follow these instructions:
- Get the project: 

```$ git clone git@github.com:Abolitionist-Project/MoodiModo-Android.git```

- Stand at MoodiModo project:

```$ cd MoodiModo-Android```

- Set up the submodule:

```
$ git submodule init
$ git submodule sync
$ git submodule update
```
When opening the project the gradle settings have to be setted to default, to do this:
- Open Android Studio Settings
- Then go to Build, Execution, Deployment -> Build Tools -> Gradle.
- "Use default gradle wrapper (recommended)" has to marked.
- Apply settings.

Then you have to make sure to include these properties files needed to connect the app with the API (these files are also used to run tests on the app):
- **quantimodo.properties** file under `MoodiModo-Android/app` folder.
- **quantimodo.properties** file under `qm-sdk/quantimodo-sdk-tools` folder. (This is if you are using Quantimodo SDK project)

Each one of the properties files have to include these values:
- quantimodoClient=CLIENT_ID
- quantimodoSecret=CLIENT_PASSWORD
- quantimodoApiUrl=BASE_API_URL
- quantimodoApiAuthUrl=API_AUTH_URL

### Create your own flavor
To creater your Flavor the first thing is register: https://admin.quantimo.do/register. Then you need to get the client ID and Password, you can enter at https://admin.quantimo.do/login to get the credentials.

Then you need to create your .properties file, following the format described before.

You can take quantimodo.properties.example (on app/ folder) as a reference for the files

On gradle you have to add the flavor doing the following:

```
productFlavors {
        myFlavor { //flavor name
            applicationId = "com.company.myflavor" //your namespace

            resValue "bool", "show_shopping_card", "false"
            def qmPropertiesFile = file('myFlavor.properties') //here is where you set the properties file to get the credentials
            if (qmPropertiesFile.exists()) {
                def Properties props = new Properties()
                props.load(new FileInputStream(qmPropertiesFile))
                resValue "string", "quantimodo_client", props['quantimodoClient']
                resValue "string", "quantimodo_secret", props['quantimodoSecret']
                buildConfigField 'String', 'API_HOST', '"'+props['quantimodoApiUrl']+'"'
                buildConfigField 'String', 'AUTH_SOCIAL_URL', '"'+props['quantimodoApiAuthUrl']+'"'
            } else {
                logger.warn("Couldn't find myFlavor.properties, trying to get from env properties, communication with QuantiModo may not work")
                resValue "string", "quantimodo_client", System.getenv("quantimodoClient") == null ? "NONE" : System.getenv("quantimodoClient")
                resValue "string", "quantimodo_secret", System.getenv("quantimodoSecret") == null ? "NONE" : System.getenv("quantimodoSecret")
                buildConfigField 'String', 'API_HOST' , System.getenv("quantimodoApiUrl") == null ? 'null' : System.getenv("quantimodoApiUrl")
                buildConfigField 'String', 'AUTH_SOCIAL_URL' , System.getenv("quantimodoApiAuthUrl") == null ? 'null' : System.getenv("quantimodoApiAuthUrl")
            }
        }
```

Then you need to create a folder under src/ with the name of your flavor. ie: src/myFlavor/, and include these files:
- **icon.png under src/myFlavor/drawable/ (96px x 96px)
- **ic_action_appicon.png** under `src/myFlavor/drawable-hdpi/` (72px x 72px)
- **ic_action_appicon.png** under `src/myFlavor/drawable-mdpi/` (48px x 48px)
- **ic_action_appicon.png** under `src/myFlavor/drawable-xhdpi/` (96px x 96px)
- **ic_sync_notification.png** under `src/myFlavor/drawable-hdpi/` (24px x 38px)
- **ic_sync_notification.png** under `src/myFlavor/drawable-mdpi/` (16px x 25px)
- **ic_sync_notification.png** under `src/myFlavor/drawable-xhdpi/` (32px x 50px)
- **strings.xml** file under `src/myFlavor/res/` like this:

```
<resources>
    <string name="app_name">My Flavor!</string>
    <string name="welcome_moodimodo"><![CDATA[Welcome to <font color=\"#48BDD1\">My Flavor!</font>!]]></string>
</resources>
```

## Testing
See a test here: https://github.com/Abolitionist-Project/QM-Android/blob/develop/app/src/androidTest/java/com.quantimodo.android.tests/MainActivityTest.java

For instructions and guidance to create tests: http://developer.android.com/intl/es/training/testing/ui-testing/espresso-testing.html

To run tests open a terminal, stand at the root project folder and execute: `gradlew cAT`

To run the tests on Android Studio, you have to :
- Create testing.properties file with these parameters:
   - quantimodoClient={client_id obtained from admin.quantimo.do}
   - quantimodoSecret={client_secret obtained from admin.quantimo.do}
   - quantimodoApiUrl=https://staging.quantimo.do/
   - quantimodoApiAuthUrl=https://staging.quantimo.do/api/v2/auth/social/authorizeToken
- To get client secret and password send an email to mike@quantimo.do to get those paramemters
- Select the `quantimodoTestFlavorDebug` from the Build Variants (View -> Tool Windows -> Build Variants, or a Build Variants button at bottom left)
- Then run a test right clicking on the method, class, or folder with the implementation of the test(s) and then click on `Run 'xyz'` where xyz is the selected one(s). 

Before testing make sure you have a reliable internet connection. For better performance and fewer chances of failures, go to Settings -> Developer options on the testing device and update these:
- Window animation scale -> off
- Transition animation scale -> off
- Animator duration scale -> off

Also be sure to include the 3 .properties files described on the above section.


## Creating new flavor

### First step
First step is to create flavor in **build.gradle**

For that you need to create new flavor:
```
energy { //name of flavor
            applicationId = "com.quantimodo.energytracker" //flavor package, that would be used
            buildConfigField 'String', 'OUTCOME_VARIABLE', '"Overall Mood"' //Outcome variable
            buildConfigField 'String', 'OUTCOME_CATEGORY', '"Emotions"' //Outcome variable category, must match real category of variable
            buildConfigField 'String', 'APPLICATION_SOURCE', '"EnergyModo"' //Would be shown as source of measurements

            def qmPropertiesFile = file('energy.properties') //Path to properties file with credentials
            if (qmPropertiesFile.exists()) {
                def Properties props = new Properties()
                props.load(new FileInputStream(qmPropertiesFile))
                resValue "string", "quantimodo_client", props['quantimodoClient']
                resValue "string", "quantimodo_secret", props['quantimodoSecret']
                buildConfigField 'String', 'API_HOST', '"'+props['quantimodoApiUrl']+'"'
                buildConfigField 'String', 'AUTH_SOCIAL_URL', '"'+props['quantimodoApiAuthUrl']+'"'
            } else {
                logger.warn("Couldn't find quantimodo.properties, trying to get from env properties, communication with QuantiModo may not work")
                resValue "string", "quantimodo_client", System.getenv("quantimodoClient") == null ? "NONE" : System.getenv("quantimodoClient")
                resValue "string", "quantimodo_secret", System.getenv("quantimodoSecret") == null ? "NONE" : System.getenv("quantimodoSecret")
                buildConfigField 'String', 'API_HOST' , System.getenv("quantimodoApiUrl") == null ? 'null' : System.getenv("quantimodoApiUrl")
            }
        }
```

### Second step

Create properties file:
```
quantimodoClient=CLIENT_ID
quantimodoSecret=CLIENT_SECRET
quantimodoApiUrl=https://app.quantimo.do/
```

### Third step

Customize resources:
- Strings
- Images

Easiest way is to copy  `src/energy` to `src/<flavor name>` , than edit strings in :
- `src/<flavor name>/values/strings.xml`
- `src/<flavor name>/values/flavor_strings.xml`

And replace images in `src/<flavor name>/drawable`

After that your new flavor ready for use!

