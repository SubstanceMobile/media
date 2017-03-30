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

package org.jaudiotagger.logging;

/**
 * For parsing the exact cause of a file exception, because variations not handled well by Java
 */
public enum FileSystemMessage {
    ACCESS_IS_DENIED("Access is denied"),;
    String msg;

    FileSystemMessage(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
