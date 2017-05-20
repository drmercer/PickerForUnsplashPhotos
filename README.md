# Picker for Unsplash Photos
An [MIT-Licensed](#license) Android library that provides a picker UI
for choosing photos from [Unsplash](https://unsplash.com)'s photo
collection. It uses the [Unsplash API](https://api.unsplash.com/) to
show a list of photos and allows the user to choose one.

## Adding to your project
To add Picker for Unsplash Photos to your project in Android Studio,
simply add this line to the `dependencies` block of the `build.gradle`
file in your app's module:

```gradle
    compile 'com.github.drmercer:unsplash:0.0.1'
```

You'll also need to add your Unsplash API app ID to your app's
`AndroidManifest.xml`, as well as the string you'd like to use for the
`utm_source` parameter in your attribution links (see the [Unsplash API
guidelines](https://community.unsplash.com/developersblog/unsplash-api-guidelines)
for more info about that):

```xml
<application>
    <!-- ... -->

    <meta-data
        android:name="net.danmercer.unsplashpicker.unsplash_app_id"
        android:value="<YOUR APP ID HERE>"/>
    <meta-data
        android:name="net.danmercer.unsplashpicker.unsplash_utm_source"
        android:value="<YOUR UTM SOURCE NAME HERE>"/>

</application>
```

### Launching the Image Pick Activity
You can launch the image picker UI from your activity like so:

```java
    ImagePickHelper helper = new ImagePickHelper(this);
    helper.launchPickerActivity();
```

And then in your Activity's `onActivityResult()` method, do something
like this:
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (pickHelper.handleActivityResult(requestCode, resultCode, data)) {

            // Download the chosen image
            pickHelper.download(new ImagePickHelper.OnBitmapDownloadedListener() {
                @Override
                public void onBitmapDownloaded(Bitmap bmp) {
                    // Do something with bitmap here
                }

                @Override
                public void onBitmapDownloadError() {
                    Toast.makeText(MainActivity.this, "Error downloading image. :(", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
```

### Customizing the Image Pick Activity
To customize the look of the ImagePickActivity, add it to your app's
`AndroidManifest.xml` with whatever attributes you want to override. For
example, to change the label and apply a special theme, do something
like this:

```xml
<activity
    android:name="net.danmercer.unsplashpicker.ImagePickActivity"
    android:theme="@style/YourTheme"
    android:label="Your Label Text"
    tools:replace="android:label,android:theme">

</activity>
```

To use a subclass of ImagePickActivity instead, replace the
`android:name` attribute with the name of your subclass activity, and
then call
```java
     helper.setPickerActivityClass(YourSubclassActivity.class);
```
(where `YourSubclassActivty` is your subclass) before calling
`launchPickerActivity()`. Read the
[ImagePickActivity source](library/src/main/java/net/danmercer/unsplashpicker/ImagePickerActivity.java)
to see what methods you can override in your subclass.

# Trello board
Check out the
[Trello board](https://trello.com/b/yrJVWLSB/unsplash-image-picker)
to see if I'm currently working on anything.

# License
The library source code is licensed under [the MIT License](https://opensource.org/licenses/MIT), except
as otherwise noted in certain files (i.e. the `maven-push.gradle` file, which is not a part of the library
itself). The MIT License is a fairly permissive license, so it shouldn't be hard to integrate Picker for
Unsplash Photos into your Android project.
