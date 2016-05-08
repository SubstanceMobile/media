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
```
compile 'com.github.SubstanceMobile:SDK:-SNAPSHOT'
``` 

## Module
Click "Read More" on the corresponding section for a description of features, installation instructions, and API tutorials.
###Music
The music package contains the entire playback engine from [GEM Player](https://github.com/SubstanceMobile/GEM). It will help you make an extremely functional music player with very little code. To install, follow the instructions above and then add this to your `build.gradle` file:
```
compile('com.github.SubstanceMobile.SDK:Music:-SNAPSHOT'@aar){
    transitive = true
}
```
This package contains the following libraries:
######Core
This is the base library. It contains classes you can use to configure the entire music package. It also includes the base media objects: `Song`, `Album`, `Playlist`, and `Artist`. **COMING SOON**
######Loading
This library is used to load the data from your mediastore into lists of the core objects. **COMING SOON**
######Playback
This library is used to play back the core objects. **COMING SOON**
######Tags
This library is used to edit your media tags. It can also control your playlists. **COMING SOON**

###Dynamic Colors
A simple library that wraps around `Palette` and has the best possible color extraction system. Straight from GEM Player. [Read More](dynamic-colors/README.md)

###Permissions
A simple permissions helper library. **COMING SOON**

### Theme Engine
This library is used to theme views. Based on `app-theme-engine` by [@afollestad](https://github.com/afollestad). [Read More](theme-engine/README.md)

###More coming in the future!


##Structure
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