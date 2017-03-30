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

package mobile.substance.media.core.music.objects

/**
 * Created by Adrian on 12/21/2015.
 */
class SearchResult(var heading: String, var results: List<*>) {

    val isEmpty: Boolean
        get() = results.isEmpty()

    fun addIfNotEmpty(list: MutableList<SearchResult>) {
        if (!isEmpty) list.add(this)
    }
}
