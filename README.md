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

To add the entire SDK to your app, just add this line to the `dependencies` of your module's `build.gradle` file:
```compile 'com.github.SubstanceMobile:SDK:-SNAPSHOT'```

##Modules
###Music
The music package contains the entire playback engine from GEM Player. It will help you make an extremely functional music player with very little code. [Read More](music/README.md)
######Core
This is the base library. It contains classes you can use to configure the entire music package. It also includes the base media objects: `Song`, `Album`, `Playlist`, and `Artist`. [Read More](music-core/README.md)
#####Loading
This library is used to load the data from your mediastore into lists of the core objects. [Read More](music-loading/README.md)
#####Tags
This library is used to edit your media tags. It can also control your playlists. [Read More](music-tags/README.md)
###Dynamic Colors
A simple library that wraps around `Palette` and has the best possible color extraction system. Straight from GEM Player. [Read More](dynamic-colors/README.md)
###Permissions
A simple permissions helper library. [Read More](permissions/README.md)
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
