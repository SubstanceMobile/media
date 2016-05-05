#Substance SDK (Pre-Alpha)
Project Managed by the entire Substance team.

[![Version](https://jitpack.io/v/SubstanceMobile/SDK.svg)](https://jitpack.io/#SubstanceMobile/SDK)

Substance SDK is a collection of libraries ddesigned to make developers' lives easier. It is extremely modular; you can go by individual libraries, packages, and even the entire SDK.

##Modules
###Music
The music module contains libraries that help you make a music player.
#####Core
This is the base library. It contains classes you can use to configure the entire music collection of libraries. It also includes the base media objects: `Song`, `Album`, `Playlist`, and `Artist`.
#####Tags
This library is used to edit your media tags. It can also control your playlists.
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
