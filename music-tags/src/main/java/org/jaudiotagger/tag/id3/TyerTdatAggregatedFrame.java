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

package org.jaudiotagger.tag.id3;

import java.util.Iterator;

/**
 * For use in ID3 for mapping YEAR field to TYER and TDAT Frames
 */
public class TyerTdatAggregatedFrame extends AggregatedFrame {
    public static final String ID_TYER_TDAT = ID3v23Frames.FRAME_ID_V3_TYER + ID3v23Frames.FRAME_ID_V3_TDAT;

    public String getContent() {
        StringBuilder sb = new StringBuilder();

        Iterator<AbstractID3v2Frame> i = frames.iterator();
        AbstractID3v2Frame tyer = i.next();
        sb.append(tyer.getContent());
        AbstractID3v2Frame tdat = i.next();
        sb.append("-");
        sb.append(tdat.getContent().substring(2, 4));
        sb.append("-");
        sb.append(tdat.getContent().substring(0, 2));
        return sb.toString();
    }

}
