# audio-Loading
### Install
This library is used to load ```audio-core``` objects from MediaStore.
```
compile('com.github.SubstanceMobile.SDK:audio-loading:-SNAPSHOT'@aar){
    transitive = true
}
```

### API
The basic use for this library is this:
```
Library.init(Context, LibraryConfig() // Initialization
        .put(AUDIO_TYPE_SONGS)
        .hookPlayback()
        .hookTags())
Library.registerListener(LibraryListener) // Get notified of all loading events
Library.registerSonglistener(Library.TaskListener<Song>) // Or individual ones

Library.build() // Finally, start loading!
```