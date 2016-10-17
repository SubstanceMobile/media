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

package mobile.substance.sdk.music.playback.cast


import fi.iki.elonen.NanoHTTPD
import mobile.substance.sdk.music.playback.MusicPlaybackUtil
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


class LocalServer : NanoHTTPD(MusicPlaybackUtil.SERVER_PORT) {

    var paths: Pair<String?, String?>? = null

    override fun serve(session: IHTTPSession): Response {
        val headers = session.headers
        val method = session.method
        val files = HashMap<String, String>()

        var path = if (session.uri.contains("audio")) paths!!.first else if (session.uri.contains("artwork")) paths!!.second else ""

        if (Method.POST == method || Method.PUT == method) {
            try {
                session.parseBody(files)
            } catch (e: IOException) {
                return getResponse("Internal Error IO Exception: ${e.message}")
            } catch (e: ResponseException) {
                return newFixedLengthResponse(e.status, MIME_PLAINTEXT, e.message)
            }

        }

        path = path!!.trim { it <= ' ' }.replace(File.separatorChar, '/')
        if (path!!.indexOf('?') >= 0) {
            path = path!!.substring(0, path!!.indexOf('?'))
        }

        val f = File(path)
        return serveFile(path!!, headers, f)
    }

    private fun serveFile(path: String, header: Map<String, String>, file: File): Response {
        var response: Response?
        val mime = getMimeTypeForFile(path)
        try {
            val eTag = Integer.toHexString((file.absolutePath + file.lastModified() + "" + file.length()).hashCode())

            var start: Long = 0
            var end: Long = -1
            var range = header["range"]
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length)
                    val minus = range.indexOf('-')
                    try {
                        if (minus > 0) {
                            start = java.lang.Long.parseLong(range.substring(0, minus))
                            end = java.lang.Long.parseLong(range.substring(minus + 1))
                        }
                    } catch (ignored: NumberFormatException) {
                    }
                }
            }

            val length = file.length()
            if (range != null && start >= 0) {
                if (start >= length) {
                    response = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, "")
                    response.addHeader("Content-Range", "bytes 0-0/" + length)
                    response.addHeader("ETag", eTag)
                } else {
                    if (end < 0) {
                        end = length - 1
                    }
                    var newLength = end - start + 1
                    if (newLength < 0) {
                        newLength = 0
                    }

                    val dataLen = newLength
                    val inputStream = object : FileInputStream(file) {
                        @Throws(IOException::class)
                        override fun available(): Int {
                            return dataLen.toInt()
                        }
                    }
                    inputStream.skip(start)

                    response = createResponse(Response.Status.PARTIAL_CONTENT, mime, inputStream)
                    response.addHeader("Content-Length", "" + dataLen)
                    response.addHeader("Content-Range", "bytes " + start + "-" +
                            end + "/" + length)
                    response.addHeader("ETag", eTag)
                }
            } else {
                if (eTag == header["if-none-match"])
                    response = createResponse(Response.Status.NOT_MODIFIED, mime, "")
                else {
                    response = createResponse(Response.Status.OK, mime, FileInputStream(file))
                    response.addHeader("Content-Length", "" + length)
                    response.addHeader("ETag", eTag)
                }
            }
        } catch (ioe: IOException) {
            response = getResponse("Forbidden: Reading file failed")
        }
        return if (response == null) getResponse("Error 404: File not found") else response
    }

    private fun createResponse(status: Response.Status, mimeType: String, message: InputStream): Response {
        val res = newChunkedResponse(status, mimeType, message)
        res.addHeader("Accept-Ranges", "bytes")
        return res
    }

    private fun createResponse(status: Response.Status, mimeType: String, message: String): Response {
        val res = newFixedLengthResponse(status, mimeType, message)
        res.addHeader("Accept-Ranges", "bytes")
        return res
    }

    private fun getResponse(message: String): Response {
        return createResponse(Response.Status.OK, "text/plain", message)
    }

}