#Substance SDK (Pre-Alpha)
Project Managed by the entire Substance team.

[![Version](https://jitpack.io/v/SubstanceMobile/SDK.svg)](https://jitpack.io/#SubstanceMobile/SDK)

Substance SDK is a collection of libraries ddesigned to make developers' lives easier. It is extremely modular; you can go by individual libraries, packages, and even the entire SDK.

##Using
To use the SDK (or any of it's modules) you will need to do some things. First, add this to your project root:
```
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
Next, make sure you have the "Kotlin" plugin installed in Android Studio. Finally, pick a library and add it to your `build.gradle`.

To add the entire SDK to your app, just add this line to the `dependencies` of your module's `build.gradle`:

`compile 'com.github.SubstanceMobile:SDK:-SNAPSHOT'`

##Modules
###Music
The music package contains the entire playback engine from GEM Player. It will help you make an extremely functional music player with very little code.
######Core
This is the base library. It contains classes you can use to configure the entire music package. It also includes the base media objects: `Song`, `Album`, `Playlist`, and `Artist`.
######Loading
THis library is used to load the data from your mediastore into lists of the core objects.
######Tags
This library is used to edit your media tags. It can also control your playlists.
###Dynamic Colors
A simple library that wraps around `Palette` and has the best possible color extraction system. Straight from GEM Player.
###Permissions
A simple permissions helper library.
###More coming in the future!


##Structure
* SDK
    * music
       * music-core
       * Data Loading Library
       * Playback Library
       * tags
    * Plugin API
       * App Communication
       * Library Communication
       * Remote Views
    * theme-engine (fork of app-theme-engine by @afollestad)
       * core
    * dynamic-colors
    * permissions
