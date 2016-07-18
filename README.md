# Substance SDK (Pre-Alpha)
Project Managed by the entire Substance Mobile development team

[![Version](https://jitpack.io/v/SubstanceMobile/SDK.svg)](https://jitpack.io/#SubstanceMobile/SDK)

Substance SDK is a collection of libraries ddesigned to make developers' lives easier. It is extremely modular; you can go by individual libraries, packages, and even the entire SDK.

## Usage
To use the SDK (or any of it's modules) you will need to do some things. First, add this to your project root:
```
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
Next, make sure you have the "Kotlin" plugin installed in Android Studio. At this point, about a fourth of the SDK is written in Kotlin, so you need the plugin to be able to complile the library. Finally, pick a library and add it to your `build.gradle` file.

To add the entire SDK to your app, just add this line to the `dependencies` of your module's `build.gradle` file:
```
compile('com.github.SubstanceMobile:SDK:-SNAPSHOT'@aar){
    transitive = true
}
``` 

## Module
Click "Read More" on the corresponding section for a description of features, installation instructions, and API tutorials.

## Music
The music package contains the entire playback engine from [GEM Player](https://github.com/SubstanceMobile/GEM). It will help you make an extremely functional music player with very little code. To install, follow the instructions above and then add this to your `build.gradle` file:
```
compile('com.github.SubstanceMobile.SDK:Music:-SNAPSHOT'@aar){
    transitive = true
}
```

This package contains the following libraries:
#### Core
This is the core library. It contains the core media objects: `Song`, `Album`, `Artist`, `Playlist` and `Genre`.
You can also set defaults for the libraries' other three parts in `MusicOptions`
#### Loading
This library is used to load the data from MediaStore into lists of core objects. It's pretty simple to set up.
You initialize it just by calling `Library.init(Context, LibraryConfig);` and start loading items with `Library.build();`

You can register Listeners from everywhere in your code to get notified of load events:

`Library.registerListener(new LibraryListener() {...});`

Or use the item specific ones:

`Library.registerArtistListener(new Loader.TaskListener<Artist>() {...});`

An example usage loading only Songs and Albums where `this` stands for the `Activity` implementing `LibraryListener` would be:

```
// Initialization
Library.init(this, new LibraryConfig()
        .put(MusicType.SONGS)
        .put(MusicType.ALBUMS));
        
// Listener
Library.registerListener(this);

// Loading
Library.build()
```
#### Playback
This library is used to play back the core objects.
The only classes you should care about are `PlaybackRemote`, which wraps a `ServiceConnection`, and `MusicQueue`. These are pretty straightforward.

The ideal way to bind your Activity to `PlaybackRemote` is calling `registerActivity()` in `onStart()` and `unregisterActivity()` in `onStop()`

Some things to note:

- You can have only **one** Activity bound to `PlaybackRemote` at a time.
- You initialize Google Cast by calling `PlaybackRemote.initCast(MenuItem)`  **once**. The passed menu item must have `app:actionProviderClass="android.support.v7.app.MediaRouteActionProvider"` set as xml attribute. Also, make sure you configured your application id in `MusicOptions`.
- You can use your own, custom notification by calling `PlaybackRemote.setNotificationCallback(NotificationCallback)`. This returns a `Callable<Notification>`. You also have the ability to ignore this and the Service will fall back to its own Notification which uses a `NotificationCompat.Builder`
- You are notified of playback events with the `RemoteCallback` you passed in when calling `PlaybackRemote.registerActivity()` which gives you all necessary information. **But**, you can have multiple of them: Call `PlaybackRemote.registerCallback()` to add a new one and `PlaybackRemote.unregisterCallback()` to remove it when you're finished

#### Tags
This library is used to edit your media tags. It can also control your playlists. **COMING SOON**

## Dynamic Colors
A simple library that wraps around `Palette` and has the best possible color extraction system. Straight from GEM Player. [Read More](dynamic-colors/README.md)

## Permissions
A simple permissions helper library. **COMING SOON**

## Theme Engine
This library is used to theme views. Based on `app-theme-engine` by [@afollestad](https://github.com/afollestad). [Read More](theme-engine/README.md)

### More coming in the future!

# Structure
* SDK
    * music
       * music-core
       * loading
       * Playback Library
       * tags
    * Plugin API
       * App Communication
       * Library Communication
       * Remote Views
    * theme-engine
       * core
    * dynamic-colors
    * permissions
