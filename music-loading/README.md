# Music-Loading
### Install
This library is used to load ```music-core``` objects from MediaStore.
```
compile('com.github.SubstanceMobile.SDK:music-loading:-SNAPSHOT'@aar){
    transitive = true
}
```

### API
The basic use for this library is this:
```
Library.init(Context, LibraryConfig() // Initialization
        .put(MusicType.SONGS)
        .hookPlayback()
        .hookTags())
Library.registerListener(LibraryListener) // Get notified of all loading events
Library.registerSonglistener(Library.TaskListener<Song>) // Or individual ones

Library.build() // Finally, start loading!
```