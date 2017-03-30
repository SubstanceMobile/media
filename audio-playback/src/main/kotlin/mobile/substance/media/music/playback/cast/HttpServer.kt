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

package mobile.substance.media.music.playback.cast


import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.*

/**
 * A simple, tiny, nicely embeddable HTTP 1.0 server in Java Modified from
 * <a href="http://elonen.iki.fi/code/nanohttpd/" />
 */
class HttpServer(private val tcpPort: Int) {

    companion object {
        val HTTP_OK = "200 OK"
        val HTTP_PARTIALCONTENT = "206 Partial Content"
        val HTTP_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable"
        val HTTP_REDIRECT = "301 Moved Permanently"
        val HTTP_FORBIDDEN = "403 Forbidden"
        val HTTP_NOTFOUND = "404 Not Found"
        val HTTP_BADREQUEST = "400 Bad Request"
        val HTTP_INTERNALERROR = "500 Internal Server Error"
        val HTTP_NOTIMPLEMENTED = "501 Not Implemented"

        val MIME_PLAINTEXT = "text/plain"
        val MIME_HTML = "text/html"
        val MIME_DEFAULT_BINARY = "application/octet-stream"
        val MIME_DEFAULT_JPG = "image/jpg"
        val MIME_XML = "text/xml"

        protected var mimeTypes = CaseInsensitiveMap<String>().apply {
            put("MP3", "audio/mpeg")
            put("MPGA", "audio/mpeg")
            put("M4A", "audio/mp4")
            put("WAV", "audio/x-wav")
            put("AMR", "audio/amr")
            put("AWB", "audio/amr-wb")
            put("WMA", "audio/x-ms-wma")
            put("OGG", "audio/ogg")
            put("OGG", "application/ogg")
            put("OGA", "application/ogg")
            put("AAC", "audio/aac")
            put("AAC", "audio/aac-adts")
            put("MKA", "audio/x-matroska")
            put("MID", "audio/midi")
            put("MIDI", "audio/midi")
            put("XMF", "audio/midi")
            put("RTTTL", "audio/midi")
            put("SMF", "audio/sp-midi")
            put("IMY", "audio/imelody")
            put("RTX", "audio/midi")
            put("OTA", "audio/midi")
            put("MXMF", "audio/midi")
            put("FLAC", "audio/flac")
            put("ALAC", "audio/alac")
        }

        private var gmtFormat: java.text.SimpleDateFormat? = null

        init {
            gmtFormat = java.text.SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US)
            gmtFormat!!.timeZone = TimeZone.getTimeZone("GMT")
        }
    }

    /**
     * This delegates to serveFile() and allows directory listing.
     *
     * @param uri  Percent-decoded URI without parameters, for example "/index.cgi"
     * @param method  "GET", "POST" etc.
     * @param parameters  Parsed, percent decoded parameters from URI and, in case of POST, data.
     * @param header  Header entries, percent decoded
     * @return HTTP response, see [Response] for details
     */
    fun serve(uri: String, method: String, header: Properties, parameters: Properties, files: Properties): Response? {
        println("$method '$uri' ")

        var e = header.propertyNames()
        while (e.hasMoreElements()) {
            val value = e.nextElement() as String
            println("  HDR: '" + value + "' = '" + header.getProperty(value) + "'")
        }
        e = parameters.propertyNames()
        while (e.hasMoreElements()) {
            val value = e.nextElement() as String
            println("  PRM: '" + value + "' = '" + parameters.getProperty(value) + "'")
        }
        e = files.propertyNames()
        while (e.hasMoreElements()) {
            val value = e.nextElement() as String
            println("  UPLOADED: '" + value + "' = '" + files.getProperty(value) + "'")
        }
        return serveFile(uri, header, File("/"))
    }

    /**
     * HTTP response. Return one of these from serve().
     */
    class Response {
        var status: String
        var mimeType: String? = null
        var data: InputStream? = null
        var header = Properties()

        /**
         * Default constructor: response = HTTP_OK, data = mime = 'null'
         */
        constructor() {
            this.status = HTTP_OK
        }

        /**
         * Basic constructor.
         */
        constructor(status: String, mimeType: String, data: InputStream) {
            this.status = status
            this.mimeType = mimeType
            this.data = data
        }

        /**
         * Convenience method that makes an InputStream out of given text.
         */
        constructor(status: String, mimeType: String, txt: String) {
            this.status = status
            this.mimeType = mimeType
            try {
                this.data = ByteArrayInputStream(txt.toByteArray(charset("UTF-8")))
            } catch (uee: java.io.UnsupportedEncodingException) {
                uee.printStackTrace()
            }

        }

        /**
         * Adds given line to the header.
         */
        fun addHeader(name: String, value: String) {
            header.put(name, value)
        }
    }

    private var myServerSocket: ServerSocket? = null
    private var myThread: Thread? = null

    /**
     * Starts the server.
     */
    fun start() {
        stop()
        myServerSocket = ServerSocket(tcpPort)
        myThread = Thread(Runnable {
            try {
                while (true)
                    HTTPSession(myServerSocket!!.accept())
            } catch (ioe: IOException) {
            }
        })
        myThread?.isDaemon = true
        myThread?.start()
    }

    /**
     * Stops the server.
     */
    fun stop() {
        try {
            myServerSocket?.close()
            myThread?.join()
        } catch (e: Throwable) {
        }

    }

    /**
     * Handles one session, i.e. parses the HTTP request and returns the
     * response.
     */
    private inner class HTTPSession(private val mySocket: Socket) : Runnable {
        init {
            val t = Thread(this)
            t.isDaemon = true
            t.start()
        }

        override fun run() {
            try {
                val `is` = mySocket.getInputStream() ?: return

                // Read the first 8192 bytes.
                // The full header should fit in here.
                // Apache's default header limit is 8KB.
                val bufsize = 8192
                var buf = ByteArray(bufsize)
                var rlen = `is`.read(buf, 0, bufsize)
                if (rlen <= 0)
                    return

                // Create a BufferedReader for parsing the header.
                val hbis = ByteArrayInputStream(buf, 0, rlen)
                val hin = BufferedReader(InputStreamReader(hbis))
                val pre = Properties()
                val parms = Properties()
                val header = Properties()
                val files = Properties()

                // Decode the header into parms and header java properties
                decodeHeader(hin, pre, parms, header)
                val method = pre.getProperty("method")
                val uri = pre.getProperty("uri")

                var size = 0x7FFFFFFFFFFFFFFFL
                val contentLength = header.getProperty("content-length")
                if (contentLength != null) {
                    try {
                        size = Integer.parseInt(contentLength).toLong()
                    } catch (ex: NumberFormatException) {
                    }

                }

                // We are looking for the byte separating header from body.
                // It must be the last byte of the first two sequential new
                // lines.
                var splitbyte = 0
                var sbfound = false
                while (splitbyte < rlen) {
                    if (buf[splitbyte].toChar() == '\r' && buf[++splitbyte].toChar() == '\n' && buf[++splitbyte].toChar() == '\r' && buf[++splitbyte].toChar() == '\n') {
                        sbfound = true
                        break
                    }
                    splitbyte++
                }
                splitbyte++

                // Write the part of body already read to ByteArrayOutputStream
                // f
                val f = ByteArrayOutputStream()
                if (splitbyte < rlen)
                    f.write(buf, splitbyte, rlen - splitbyte)

                // While Firefox sends on the first read all the data fitting
                // our buffer, Chrome and Opera sends only the headers even if
                // there is data for the body. So we do some magic here to find
                // out whether we have already consumed part of body, if we
                // have reached the end of the data to be sent or we should
                // expect the first byte of the body at the next read.
                if (splitbyte < rlen)
                    size -= (rlen - splitbyte + 1).toLong()
                else if (!sbfound || size == 0x7FFFFFFFFFFFFFFFL)
                    size = 0

                // Now read all the body and write it to f
                buf = ByteArray(512)
                while (rlen >= 0 && size > 0) {
                    rlen = `is`.read(buf, 0, 512)
                    size -= rlen.toLong()
                    if (rlen > 0)
                        f.write(buf, 0, rlen)
                }

                // Get the raw body as a byte []
                val fbuf = f.toByteArray()

                // Create a BufferedReader for easily reading it as string.
                val bin = ByteArrayInputStream(fbuf)
                val `in` = BufferedReader(InputStreamReader(bin))

                // If the method is POST, there may be parameters
                // in data section, too, read it:
                if (method.equals("POST", ignoreCase = true)) {
                    var contentType = ""
                    val contentTypeHeader = header.getProperty("content-type")
                    var st = StringTokenizer(contentTypeHeader, "; ")
                    if (st.hasMoreTokens()) {
                        contentType = st.nextToken()
                    }

                    if (contentType.equals("multipart/form-data", ignoreCase = true)) {
                        // Handle multipart/form-data
                        if (!st.hasMoreTokens())
                            sendError(HTTP_BADREQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html")
                        val boundaryExp = st.nextToken()
                        st = StringTokenizer(boundaryExp, "=")
                        if (st.countTokens() != 2)
                            sendError(HTTP_BADREQUEST,
                                    "BAD REQUEST: Content type is multipart/form-data but boundary syntax error. Usage: GET /example/file.html")
                        st.nextToken()
                        val boundary = st.nextToken()

                        decodeMultipartData(boundary, fbuf, `in`, parms, files)
                    } else {
                        // Handle application/x-www-form-urlencoded
                        var postLine = ""
                        val pbuf = CharArray(512)
                        var read = `in`.read(pbuf)
                        while (read >= 0 && !postLine.endsWith("\r\n")) {
                            postLine += String(pbuf, 0, read)
                            read = `in`.read(pbuf)
                        }
                        postLine = postLine.trim { it <= ' ' }
                        decodeParms(postLine, parms)
                    }
                }

                // Ok, now do the serve()
                val r = serve(uri, method, header, parms, files)
                if (r == null)
                    sendError(HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.")
                else
                    sendResponse(r.status, r.mimeType, r.header, r.data)

                `in`.close()
                `is`.close()
            } catch (ioe: IOException) {
                try {
                    sendError(HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.message)
                } catch (t: Throwable) {
                }

            } catch (ie: InterruptedException) {
                // Thrown by sendError, ignore and exit the thread.
            } catch (e: Throwable) {
            }

        }

        /**
         * Decodes the sent headers and loads the data into java Properties' key
         * - value pairs
         */
        @Throws(InterruptedException::class)
        private fun decodeHeader(`in`: BufferedReader, pre: Properties, parms: Properties, header: Properties) {
            try {
                // Read the request line
                val inLine = `in`.readLine() ?: return
                val st = StringTokenizer(inLine)
                if (!st.hasMoreTokens())
                    sendError(HTTP_BADREQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html")

                val method = st.nextToken()
                pre.put("method", method)

                if (!st.hasMoreTokens())
                    sendError(HTTP_BADREQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html")

                var uri = st.nextToken()

                // Decode parameters from the URI
                val qmi = uri.indexOf('?')
                if (qmi >= 0) {
                    decodeParms(uri.substring(qmi + 1), parms)
                    uri = decodePercent(uri.substring(0, qmi))
                } else
                    uri = decodePercent(uri)

                // If there's another token, it's protocol version,
                // followed by HTTP headers. Ignore version but parse headers.
                // NOTE: this now forces header names lowercase since they are
                // case insensitive and vary by client.
                if (st.hasMoreTokens()) {
                    var line: String? = `in`.readLine()
                    while (line != null && line.trim { it <= ' ' }.isNotEmpty()) {
                        val p = line.indexOf(':')
                        if (p >= 0)
                            header.put(line.substring(0, p).trim { it <= ' ' }.toLowerCase(), line.substring(p + 1).trim { it <= ' ' })
                        line = `in`.readLine()
                    }
                }

                pre.put("uri", uri)
            } catch (ioe: IOException) {
                sendError(HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.message)
            }

        }

        /**
         * Decodes the Multipart Body data and put it into java Properties' key
         * - value pairs.
         */
        @Throws(InterruptedException::class)
        private fun decodeMultipartData(boundary: String, fbuf: ByteArray, `in`: BufferedReader, parms: Properties, files: Properties) {
            try {
                val bpositions = getBoundaryPositions(fbuf, boundary.toByteArray())
                var boundarycount = 1
                var mpline: String? = `in`.readLine()
                while (mpline != null) {
                    if (mpline.indexOf(boundary) == -1)
                        sendError(HTTP_BADREQUEST,
                                "BAD REQUEST: Content type is multipart/form-data but next chunk does not start with boundary. Usage: GET /example/file.html")
                    boundarycount++
                    val item = Properties()
                    mpline = `in`.readLine()
                    while (mpline != null && mpline.trim { it <= ' ' }.isNotEmpty()) {
                        val p = mpline.indexOf(':')
                        if (p != -1)
                            item.put(mpline.substring(0, p).trim { it <= ' ' }.toLowerCase(), mpline.substring(p + 1).trim { it <= ' ' })
                        mpline = `in`.readLine()
                    }
                    if (mpline != null) {
                        val contentDisposition = item.getProperty("content-disposition")
                        if (contentDisposition == null) {
                            sendError(HTTP_BADREQUEST,
                                    "BAD REQUEST: Content type is multipart/form-data but no content-disposition info found. Usage: GET /example/file.html")
                        }
                        val st = StringTokenizer(contentDisposition, "; ")
                        val disposition = Properties()
                        while (st.hasMoreTokens()) {
                            val token = st.nextToken()
                            val p = token.indexOf('=')
                            if (p != -1)
                                disposition.put(token.substring(0, p).trim { it <= ' ' }.toLowerCase(), token.substring(p + 1).trim { it <= ' ' })
                        }
                        var pname = disposition.getProperty("name")
                        pname = pname.substring(1, pname.length - 1)

                        var value = ""
                        if (item.getProperty("content-type") == null) {
                            while (mpline != null && mpline.indexOf(boundary) == -1) {
                                mpline = `in`.readLine()
                                if (mpline != null) {
                                    val d = mpline.indexOf(boundary)
                                    if (d == -1)
                                        value += mpline
                                    else
                                        value += mpline.substring(0, d - 2)
                                }
                            }
                        } else {
                            if (boundarycount > bpositions.size)
                                sendError(HTTP_INTERNALERROR, "Error processing request")
                            val offset = stripMultipartHeaders(fbuf, bpositions[boundarycount - 2])
                            val path = saveTmpFile(fbuf, offset, bpositions[boundarycount - 1] - offset - 4)
                            files.put(pname, path)
                            value = disposition.getProperty("filename")
                            value = value.substring(1, value.length - 1)
                            do {
                                mpline = `in`.readLine()
                            } while (mpline != null && mpline.indexOf(boundary) == -1)
                        }
                        parms.put(pname, value)
                    }
                }
            } catch (ioe: IOException) {
                sendError(HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.message)
            }

        }

        /**
         * Find the byte positions where multipart boundaries start.
         */
        fun getBoundaryPositions(b: ByteArray, boundary: ByteArray): IntArray {
            var matchcount = 0
            var matchbyte = -1
            val matchbytes = Vector<Int>()
            run {
                var i = 0
                while (i < b.size) {
                    if (b[i] == boundary[matchcount]) {
                        if (matchcount == 0)
                            matchbyte = i
                        matchcount++
                        if (matchcount == boundary.size) {
                            matchbytes.addElement(Integer.valueOf(matchbyte))
                            matchcount = 0
                            matchbyte = -1
                        }
                    } else {
                        i -= matchcount
                        matchcount = 0
                        matchbyte = -1
                    }
                    i++
                }
            }
            val ret = IntArray(matchbytes.size)
            for (i in ret.indices) {
                ret[i] = (matchbytes.elementAt(i) as Int).toInt()
            }
            return ret
        }

        /**
         * Retrieves the content of a sent file and saves it to a temporary
         * file. The full path to the saved file is returned.
         */
        private fun saveTmpFile(b: ByteArray, offset: Int, len: Int): String {
            var path = ""
            if (len > 0) {
                val tmpdir = System.getProperty("java.io.tmpdir")
                try {
                    val temp = File.createTempFile("NanoHTTPD", "", File(tmpdir))
                    val fstream = FileOutputStream(temp)
                    fstream.write(b, offset, len)
                    fstream.close()
                    path = temp.absolutePath
                } catch (e: Exception) { // Catch exception if any
                    System.err.println("Error: " + e.message)
                }

            }
            return path
        }

        /**
         * It returns the offset separating multipart file headers from the
         * file's data.
         */
        private fun stripMultipartHeaders(b: ByteArray, offset: Int): Int {
            var i = offset
            while (i < b.size) {
                if (b[i].toChar() == '\r' && b[++i].toChar() == '\n' && b[++i].toChar() == '\r' && b[++i].toChar() == '\n')
                    break
                i++
            }
            return i + 1
        }

        /**
         * Decodes the percent encoding scheme. <br></br>
         * For example: "an+example%20string" -> "an example string"
         */
        @Throws(InterruptedException::class)
        private fun decodePercent(str: String): String? {
            try {
                val sb = StringBuffer()
                var i = 0
                while (i < str.length) {
                    val c = str[i]
                    when (c) {
                        '+' -> sb.append(' ')
                        '%' -> {
                            sb.append(Integer.parseInt(str.substring(i + 1, i + 3), 16).toChar())
                            i += 2
                        }
                        else -> sb.append(c)
                    }
                    i++
                }
                return sb.toString()
            } catch (e: Exception) {
                sendError(HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding.")
                return null
            }

        }

        /**
         * Decodes parameters in percent-encoded URI-format ( e.g.
         * "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them to given
         * Properties. NOTE: this doesn't support multiple identical keys due to
         * the simplicity of Properties -- if you need multiples, you might want
         * to replace the Properties with a Hashtable of Vectors or such.
         */
        @Throws(InterruptedException::class)
        private fun decodeParms(parms: String?, p: Properties) {
            if (parms == null)
                return

            val st = StringTokenizer(parms, "&")
            while (st.hasMoreTokens()) {
                val e = st.nextToken()
                val sep = e.indexOf('=')
                if (sep >= 0) {
                    p.put(decodePercent(e.substring(0, sep))!!.trim { it <= ' ' }, decodePercent(e.substring(sep + 1)))
                }
            }
        }

        /**
         * Returns an error message as a HTTP response and throws
         * InterruptedException to stop further request processing.
         */
        @Throws(InterruptedException::class)
        private fun sendError(status: String, msg: String) {
            sendResponse(status, MIME_PLAINTEXT, null, ByteArrayInputStream(msg.toByteArray()))
            throw InterruptedException()
        }

        /**
         * Sends given response to the socket.
         */
        private fun sendResponse(status: String?, mime: String?, header: Properties?, data: InputStream?) {
            try {
                if (status == null)
                    throw Error("sendResponse(): Status can't be null.")

                val out = mySocket.getOutputStream()
                val pw = PrintWriter(out)
                pw.print("HTTP/1.0 $status \r\n")

                if (mime != null)
                    pw.print("Content-Type: " + mime + "\r\n")

                if (header == null || header.getProperty("Date") == null)
                    pw.print("Date: " + gmtFormat!!.format(Date()) + "\r\n")

                if (header != null) {
                    val e = header.keys()
                    while (e.hasMoreElements()) {
                        val key = e.nextElement() as String
                        val value = header.getProperty(key)
                        pw.print(key + ": " + value + "\r\n")
                    }
                }

                pw.print("\r\n")
                pw.flush()

                if (data != null) {
                    var pending = data.available() // This is to support
                    // partial sends, see
                    // serveFile()
                    val buff = ByteArray(2048)
                    while (pending > 0) {
                        val read = data.read(buff, 0, if (pending > 2048) 2048 else pending)
                        if (read <= 0)
                            break
                        out.write(buff, 0, read)
                        pending -= read
                    }
                }
                out.flush()
                out.close()
                data?.close()
            } catch (ioe: IOException) {
                // Couldn't write? No can do.
                try {
                    mySocket.close()
                } catch (t: Throwable) {
                }

            }

        }
    }

    /**
     * Serves file from homeDir and its' subdirectories (only). Uses only URI,
     * ignores all headers and HTTP parameters.
     */
    fun serveFile(uri: String, header: Properties, homeDir: File): Response {
        var res: Response? = null

        // Make sure we won't die of an exception later
        if (!homeDir.isDirectory)
            res = Response(HTTP_INTERNALERROR, MIME_PLAINTEXT, "INTERNAL ERRROR: serveFile(): given homeDir is not a directory.")

        val requestedFile = File(homeDir, uri)
        println(requestedFile.path)
        if (res == null && !requestedFile.exists())
            res = Response(HTTP_NOTFOUND, MIME_PLAINTEXT, "Error 404, file not found.")

        try {
            if (res == null) {
                // Get MIME type from file name extension, if possible

                println("Retrieving the mime type...")

                val mime: String

                val lastDotIndex = requestedFile.canonicalPath.lastIndexOf('.')
                if (lastDotIndex >= 0 && requestedFile.canonicalPath.lastIndexOf("/") <= lastDotIndex) {
                    val retrievedMime = requestedFile.canonicalPath.substring(lastDotIndex + 1).toLowerCase()
                    mime = mimeTypes[retrievedMime] as String
                } else mime = MIME_DEFAULT_JPG

                // Support (simple) skipping:
                var startFrom: Long = 0
                var endAt: Long = -1
                var range: String? = header.getProperty("range")
                if (range != null) {
                    if (range.startsWith("bytes=")) {
                        range = range.substring("bytes=".length)
                        val minus = range.indexOf('-')
                        try {
                            if (minus > 0) {
                                startFrom = java.lang.Long.parseLong(range.substring(0, minus))
                                endAt = java.lang.Long.parseLong(range.substring(minus + 1))
                            }
                        } catch (ignored: NumberFormatException) {
                        }
                    }
                }

                // Change return code and add Content-Range header when skipping
                // is requested
                val fileLength = requestedFile.length()
                if (range != null && startFrom >= 0) {
                    if (startFrom >= fileLength) {
                        println("The requested range is not satisfiable")
                        res = Response(HTTP_RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, "")
                        res.addHeader("Content-Range", "bytes 0-0/" + fileLength)
                    } else {
                        if (endAt < 0)
                            endAt = fileLength - 1
                        var requestedLength = endAt - startFrom + 1
                        if (requestedLength < 0)
                            requestedLength = 0

                        val inputStream = object : FileInputStream(requestedFile) {
                            @Throws(IOException::class)
                            override fun available(): Int {
                                return requestedLength.toInt()
                            }
                        }
                        inputStream.skip(startFrom)

                        res = Response(HTTP_PARTIALCONTENT, mime, inputStream)
                        res.addHeader("Content-Length", "" + requestedLength)
                        res.addHeader("Content-Range", "bytes $startFrom-$endAt/$fileLength")
                    }
                } else {
                    res = Response(HTTP_OK, mime, FileInputStream(requestedFile))
                    res.addHeader("Content-Length", "" + fileLength)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            res = Response(HTTP_FORBIDDEN, MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.")
        }

        res!!.addHeader("Accept-Ranges", "bytes") // Announce that the file
        // server accepts partial
        // content requestes
        return res
    }
}