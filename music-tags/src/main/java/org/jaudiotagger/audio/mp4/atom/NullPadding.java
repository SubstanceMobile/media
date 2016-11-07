/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jaudiotagger.audio.mp4.atom;

import java.nio.ByteBuffer;

/**
 * Some mp4s contain null padding at the end of the file, possibly do with gapless playback. This is not really
 * allowable but seeing as seems to cccur in files encoded with iTunes 6 and players such as Winamp and iTunes deal
 * with it we should
 * <p>
 * It isnt actually a box, but it helps to keep as a subclass of this type
 */
public class NullPadding extends Mp4BoxHeader {

    public NullPadding(long startPosition, long fileSize) {
        setFilePos(startPosition);
        length = ((int) (fileSize - startPosition));
    }
}
