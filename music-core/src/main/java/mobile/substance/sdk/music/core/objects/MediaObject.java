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

package mobile.substance.sdk.music.core.objects;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import java.util.HashMap;

import mobile.substance.sdk.music.core.CoreUtil;


public class MediaObject {
    static final MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
    MediaMetadataCompat data;
    HashMap<String, Object> extraVars;
    private long id, TIME_LOADED = 0;
    private boolean isLocked, isAnimated = false;
    ///////////////////////////////////////////////////////////////////////////
    // Uri
    ///////////////////////////////////////////////////////////////////////////
    private int posInList;

    ///////////////////////////////////////////////////////////////////////////
    //Title
    ///////////////////////////////////////////////////////////////////////////
    private Context context;

    protected Uri getBaseUri() {
        return null;
    }

    public Uri getUri() {
        return ContentUris.withAppendedId(getBaseUri(), getID());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Handles time data
    ///////////////////////////////////////////////////////////////////////////

    public String getFilePath() {
        return CoreUtil.getFilePath(context, getUri());
    }

    public long getID() {
        return id;
    }

    public MediaObject setID(long id) {
        this.id = id;
        return this;
    }

    public MediaObject lock() {
        TIME_LOADED = System.currentTimeMillis();
        isLocked = true;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Context
    ///////////////////////////////////////////////////////////////////////////

    public MediaObject unlock() {
        TIME_LOADED = 0;
        isLocked = false;
        return this;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public long getTimeLoaded() {
        return TIME_LOADED;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public void setAnimated(boolean isAnimated) {
        this.isAnimated = isAnimated;
    }

    protected void onContextSet(Context context) {
        //Override if you want to do something when the context is set
    }

    protected void putLong(String key, long value) {
        if (isLocked()) throw new Error("Object locked. Cannot edit");
        builder.putLong(key, value);
        data = builder.build();
    }

    protected void putString(String key, String value) {
        if (isLocked()) throw new Error("Object locked. Cannot edit");
        builder.putString(key, value);
        data = builder.build();
    }

    protected void putBitmap(String key, Bitmap value) {
        if (isLocked()) throw new Error("Object locked. Cannot edit");
        builder.putBitmap(key, value);
        data = builder.build();
    }

    protected void putInteger(String key, int value) {
        if (isLocked()) throw new Error("Object locked. Cannot edit");
        builder.putLong(key, value);
        data = builder.build();
    }

    protected boolean isContextRequired() {
        //Override to change
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Position in list
    ///////////////////////////////////////////////////////////////////////////

    @Nullable
    public Context getContext() {
        return context.getApplicationContext();
    }

    public MediaObject setContext(Context context) {
        if (isContextRequired()) {
            this.context = context;
            onContextSet(context);
        } else Log.d(getClass().getSimpleName(), "Context was not requested. Ignoring");
        return this;
    }

    public MediaMetadataCompat getMetadataCompat() {
        return getMetadataCompat();
    }

    public int getPosInList() {
        return posInList;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Extra Data Storage
    ///////////////////////////////////////////////////////////////////////////

    public MediaObject setPosInList(int posInList) {
        this.posInList = posInList;
        return this;
    }

    public void putData(String key, Object data) {
        if (extraVars == null) extraVars = new HashMap<>();
        extraVars.put(key, data);
    }

    public Object getData(String key) {
        if (extraVars == null || !extraVars.containsKey(key)) return null;
        return extraVars.get(key);
    }

    public boolean removeData(String key) {
        return !(extraVars == null || !extraVars.containsKey(key)) && extraVars.remove(key) != null;
    }

}
