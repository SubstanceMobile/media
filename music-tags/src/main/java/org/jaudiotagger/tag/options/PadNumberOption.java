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

package org.jaudiotagger.tag.options;

/**
 * Number of padding zeroes digits 1- 9, numbers larger than nine will be padded accordingly based on the value.
 * <p>
 * i.e
 * If set to PAD_ONE_ZERO,   9 -> 09 , 99 -> 99 , 999 ->999
 * If set to PAD_TWO_ZERO,   9 -> 009 , 99 -> 099 , 999 ->999
 * If set to PAD_THREE_ZERO, 9 -> 0009 , 99 -> 0099 , 999 ->0999
 */
public enum PadNumberOption {
    PAD_ONE_ZERO,
    PAD_TWO_ZERO,
    PAD_THREE_ZERO,

}
