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

import java.util.logging.Logger;

/**
 * This is the abstract base class for all ID3 tags.
 *
 * @author : Eric Farng
 * @author : Paul Taylor
 */
public abstract class AbstractID3Tag extends AbstractTag {
    //Logger
    public static Logger logger = Logger.getLogger("org.jaudiotagger.tag.id3");

    public AbstractID3Tag() {
    }

    protected static final String TAG_RELEASE = "ID3v";

    //The purpose of this is to provide the filename that should be used when writing debug messages
    //when problems occur reading or writing to file, otherwise it is difficult to track down the error
    //when processing many files
    private String loggingFilename = "";

    /**
     * Get full version
     */
    public String getIdentifier() {
        return TAG_RELEASE + getRelease() + "." + getMajorVersion() + "." + getRevision();
    }

    /**
     * Retrieve the Release
     *
     * @return
     */
    public abstract byte getRelease();


    /**
     * Retrieve the Major Version
     *
     * @return
     */
    public abstract byte getMajorVersion();

    /**
     * Retrieve the Revision
     *
     * @return
     */
    public abstract byte getRevision();


    public AbstractID3Tag(AbstractID3Tag copyObject) {
        super(copyObject);
    }


    /**
     * Retrieve the logging filename to be used in debugging
     *
     * @return logging filename to be used in debugging
     */
    protected String getLoggingFilename() {
        return loggingFilename;
    }

    /**
     * Set logging filename when construct tag for read from file
     *
     * @param loggingFilename
     */
    protected void setLoggingFilename(String loggingFilename) {
        this.loggingFilename = loggingFilename;
    }
}
