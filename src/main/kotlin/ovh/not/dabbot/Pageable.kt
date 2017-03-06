/*
 * Copyright (c) 2003-2006, Simon Brown
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *   - Neither the name of Pebble nor the names of its contributors may
 *     be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ovh.not.dabbot

/**
 * Helper class that implements paging over a collection.
 *
 * @author Simon Brown (original in java)
 * @author Joe Burnard (ported to kotlin)
 */
class Pageable<T>(val list: List<T>) {
    private val defaultPageSize = 10
    private val pageWindow = 10
    private var pageSize = defaultPageSize
    private var page = 1
    private var startingIndex = 0
    private var endingIndex = 0
    private var maxPages = 1

    fun calculatePages() {
        if (pageSize > 0) {
            if (list.size % pageSize == 0) {
                maxPages = list.size / pageSize
            } else {
                maxPages = (list.size / pageSize) + 1
            }
        }
    }

    fun getListForPage(): List<T> {
        return list.subList(startingIndex, endingIndex)
    }

    fun getPageSize(): Int {
        return pageSize
    }

    fun setPageSize(pageSize: Int) {
        this.pageSize = pageSize
        calculatePages()
    }

    fun getPage(): Int {
        return page
    }

    fun setPage(p: Int) {
        if (p >= maxPages) {
            this.page = maxPages
        } else if (p <= 1) {
            this.page = 1
        } else {
            this.page = p
        }
        startingIndex = pageSize * (page - 1)
        if (startingIndex < 0) {
            startingIndex = 0
        }
        endingIndex = startingIndex + pageSize
        if (endingIndex > list.size) {
            endingIndex = list.size
        }
    }

    fun getMaxPages(): Int {
        return maxPages
    }

    fun getPreviousPage(): Int {
        if (page > 1) {
            return page - 1
        } else {
            return 0
        }
    }

    fun getNextPage(): Int {
        if (page < maxPages) {
            return page + 1
        } else {
            return 0
        }
    }

    fun getMinPageRange(): Int {
        if (getPage() > pageWindow) {
            return getPage() - pageWindow
        } else {
            return 1
        }
    }

    fun getMaxPageRange(): Int {
        if (getPage() < (getMaxPages() - pageWindow)) {
            return getPage() + pageWindow
        } else {
            return getMaxPages()
        }
    }
}