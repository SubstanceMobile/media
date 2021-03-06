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

import java.util.HashMap;

/**
 * Abstract class that provides structure to use for displaying a files metadata content
 */
public abstract class AbstractTagDisplayFormatter {
    protected int level;

    private static HashMap<String, String> hexBinaryMap = new HashMap<String, String>();

    public abstract void openHeadingElement(String type, String value);

    public abstract void openHeadingElement(String type, boolean value);

    public abstract void openHeadingElement(String type, int value);


    public abstract void closeHeadingElement(String type);

    public abstract void addElement(String type, String value);

    public abstract void addElement(String type, int value);

    public abstract void addElement(String type, boolean value);

    public abstract String toString();

    /**
     * Use to display headers as their binary representation
     * @param buffer
     * @return
     */
    public static String displayAsBinary(byte buffer) {
        //Convert buffer to hex representation
        String hexValue = Integer.toHexString(buffer);
        String char1 = "";
        String char2 = "";
        try {
            if (hexValue.length() == 8) {
                char1 = hexValue.substring(6, 7);
                char2 = hexValue.substring(7, 8);
            } else if (hexValue.length() == 2) {
                char1 = hexValue.substring(0, 1);
                char2 = hexValue.substring(1, 2);
            } else if (hexValue.length() == 1) {
                char1 = "0";
                char2 = hexValue.substring(0, 1);
            }
        } catch (StringIndexOutOfBoundsException se) {
            return "";
        }
        return hexBinaryMap.get(char1) + hexBinaryMap.get(char2);
    }

    static {
        hexBinaryMap.put("0", "0000");
        hexBinaryMap.put("1", "0001");
        hexBinaryMap.put("2", "0010");
        hexBinaryMap.put("3", "0011");
        hexBinaryMap.put("4", "0100");
        hexBinaryMap.put("5", "0101");
        hexBinaryMap.put("6", "0110");
        hexBinaryMap.put("7", "0111");
        hexBinaryMap.put("8", "1000");
        hexBinaryMap.put("9", "1001");
        hexBinaryMap.put("a", "1010");
        hexBinaryMap.put("b", "1011");
        hexBinaryMap.put("c", "1100");
        hexBinaryMap.put("d", "1101");
        hexBinaryMap.put("e", "1110");
        hexBinaryMap.put("f", "1111");
    }
}
