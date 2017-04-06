# Music-Playback
### Install
This library is used to play back the ```music-core``` objects.
```
compile('com.github.SubstanceMobile.SDK:music-playback:-SNAPSHOT'@aar){
    transitive = true
}
```

### API
The basic use for this library is this:
```
PlaybackRemote.withActivity(Context) // Initialize the singleton
PlaybackRemote.registerRemoteCallback(RemoteCallback) // Get notified of playback events
PlaybackRemote.play(Song, true) // Starts playback and replaces the active queue
```
All the heavy lifting is handled behind the scenes. But of course, this library provides some additional features to make this perfectly suit your usecase.

### Features

1. [Custom Notifications](https://github.com/SubstanceMobile/SDK/music-playback#custom-notifications)
2. [Gapless local playback](https://github.com/SubstanceMobile/SDK/music-playback#custom-notifications)
3. [Google Cast](https://github.com/SubstanceMobile/SDK/music-playback#custom-notifications)
4. [Custom Playback Engines](https://github.com/SubstanceMobile/SDK/music-playback#custom-notifications)

#### Custom Notifications

_COMING SOON_

#### Gapless local playback

_COMING SOON_

#### Google Cast

This library supports Google Cast **Out of the box**.

The first step is to add the Cast Button. You can use the following snippet from [here](https://developers.google.com/cast/docs/android_sender_integrate#add_a_cast_button):

```
// Add the following snippet to add a Cast button.
// menu.xml
<item
    android:id="@+id/media_route_menu_item"
    android:title="@string/media_route_menu_title"
    app:actionProviderClass="android.support.v7.app.MediaRouteActionProvider"
    app:showAsAction="always" />
// Then override the onCreateOptionMenu() for each of your activities.
// MyActivity.java
@Override public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.main, menu);
    CastButtonFactory.setUpMediaRouteButton(menu, R.id.media_route_menu_item);
    return true;
}
// If your Activity inherits from FragmentActivity, you can also add a snippet
// like the following to your layout.
// menu.xml
<android.support.v7.app.MediaRouteButton
    android:id="@+id/media_route_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:mediaRouteTypes="user"
    android:visibility="gone" />
// MyActivity.javax
@Override public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.main, menu);
    mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
    CastButtonFactory.setUpMediaRouteButton(mMediaRouteButton);
    return true;
}
```

Then, set the receiver application id in ``MusicPlaybackOptions```:

```MusicPlaybackOptions.applicationId = APPLICATION_ID // It's that simple!```


And the last step, enable it:

```MusicPlaybackOptions.isCastEnabled = true```

#### Custom Playback Engines

_COMING SOON_