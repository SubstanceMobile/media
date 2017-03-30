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


/*
 * For Formatting metadata contents of a file as simple text
*/
public class PlainTextTagDisplayFormatter extends AbstractTagDisplayFormatter {
    private static PlainTextTagDisplayFormatter formatter;

    StringBuffer sb = new StringBuffer();
    StringBuffer indent = new StringBuffer();

    public PlainTextTagDisplayFormatter() {

    }

    public void openHeadingElement(String type, String value) {
        addElement(type, value);
        increaseLevel();
    }

    public void openHeadingElement(String type, boolean value) {
        openHeadingElement(type, String.valueOf(value));
    }

    public void openHeadingElement(String type, int value) {
        openHeadingElement(type, String.valueOf(value));
    }

    public void closeHeadingElement(String type) {
        decreaseLevel();
    }

    public void increaseLevel() {
        level++;
        indent.append("  ");
    }

    public void decreaseLevel() {
        level--;
        indent = new StringBuffer(indent.substring(0, indent.length() - 2));
    }

    public void addElement(String type, String value) {
        sb.append(indent).append(type).append(":").append(value).append('\n');
    }

    public void addElement(String type, int value) {
        addElement(type, String.valueOf(value));
    }

    public void addElement(String type, boolean value) {
        addElement(type, String.valueOf(value));
    }

    public String toString() {
        return sb.toString();
    }

    public static AbstractTagDisplayFormatter getInstanceOf() {
        if (formatter == null) {
            formatter = new PlainTextTagDisplayFormatter();
        }
        return formatter;
    }
}
