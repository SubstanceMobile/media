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

/*
 *  This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 *  General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 *  or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 *  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 *  you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jaudiotagger.tag.datatype;

import org.jaudiotagger.tag.id3.framebody.FrameBodyETCO;

/**
 * List of {@link EventTimingCode}s.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 * @version $Id:$
 */
public class EventTimingCodeList extends AbstractDataTypeList<EventTimingCode> {

    /**
     * Mandatory, concretely-typed copy constructor, as required by
     * {@link AbstractDataTypeList#AbstractDataTypeList(AbstractDataTypeList)}.
     *
     * @param copy instance to copy
     */
    public EventTimingCodeList(final EventTimingCodeList copy) {
        super(copy);
    }

    public EventTimingCodeList(final FrameBodyETCO body) {
        super(DataTypes.OBJ_TIMED_EVENT_LIST, body);
    }

    @Override
    protected EventTimingCode createListElement() {
        return new EventTimingCode(DataTypes.OBJ_TIMED_EVENT, frameBody);
    }
}
