package org.hisp.dhis.calendar;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javax.validation.constraints.NotNull;

/**
 * Class representing a date interval.
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 * @see DateUnit
 * @see Calendar
 */
public class DateInterval
{
    /**
     * Start of interval. Required.
     */
    @NotNull
    private final DateUnit from;

    /**
     * End of interval. Required.
     */
    @NotNull
    private final DateUnit to;

    /**
     * Interval type this interval represents.
     */
    private DateIntervalType type;

    public DateInterval( DateUnit from, DateUnit to )
    {
        this.from = from;
        this.to = to;
    }

    public DateInterval( DateUnit from, DateUnit to, DateIntervalType type )
    {
        this.from = from;
        this.to = to;
        this.type = type;
    }

    public DateUnit getFrom()
    {
        return from;
    }

    public DateUnit getTo()
    {
        return to;
    }

    public DateIntervalType getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return "DateInterval{" +
            "from=" + from +
            ", to=" + to +
            ", type=" + type +
            '}';
    }
}