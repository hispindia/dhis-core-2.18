package org.hisp.dhis.importexport.dhis14.xml.converter.xsd;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportParams;

/**
 * @author Jason P. Pickering
 */

public class OrganisationUnitXSDConverter
    extends AbstractXSDConverter
{
    public OrganisationUnitXSDConverter()
    {
    }

    @Override
    public void write( XMLWriter writer, ExportParams params )
    {

        writer.openElement( "xsd:element", "name", "OrgUnit" );

        writeAnnotation( writer );

        writer.openElement( "xsd:complexType" );

        writer.openElement( "xsd:sequence" );

        writeInteger( writer, "OrgUnitID", 1, true );

        writeText( writer, "UID", 0, false, 11 );

        writeText( writer, "OrgUnitCode", 0, false, 15 );

        writeInteger( writer, "OrgUnitLevel", 1, true );

        writeText( writer, "OrgUnitName", 1, true, 230 );

        writeText( writer, "OrgUnitShort", 1, true, 25 );

        writeLongInteger( writer, "ValidFrom", 1, true );

        writeLongInteger( writer, "ValidTo", 1, true );

        writeInteger( writer, "Active", 1, true );

        writeMemo( writer, "Comment", 0, false, 536870910 );

        writeDouble( writer, "Latitude", 1, true );

        writeDouble( writer, "Longitude", 1, true );

        writeInteger( writer, "Selected", 1, true );

        writeInteger( writer, "LastUserID", 1, true );

        writeDateTime( writer, "LastUpdated", 1, true );

        writeText( writer, "OrgUnitNameOld", 0, false, 230 );

        writeText( writer, "OrgUnitShortOld", 0, false, 25 );

        writeText( writer, "OrgUnitGUIDAlt1", 0, false, 50 );

        writeText( writer, "OrgUnitCodeAlt1", 0, false, 18 );

        writeText( writer, "OrgUnitNameAlt1", 0, false, 230 );

        writeText( writer, "OrgUnitShortAlt1", 0, false, 25 );

        writeText( writer, "OrgUnitGUIDAlt2", 0, false, 50 );

        writeText( writer, "OrgUnitCodeAlt2", 0, false, 18 );

        writeText( writer, "OrgUnitNameAlt2", 0, false, 230 );

        writeText( writer, "OrgUnitShortAlt2", 0, false, 25 );

        writeText( writer, "OrgUnitGUIDAlt3", 0, false, 50 );

        writeText( writer, "OrgUnitCodeAlt3", 0, false, 18 );

        writeText( writer, "OrgUnitNameAlt3", 0, false, 230 );

        writeText( writer, "OrgUnitShortAlt3", 0, false, 25 );

        writer.closeElement();

        writer.closeElement();

        writer.closeElement();

    }

    @Override
    public void read( XMLReader reader, ImportParams params )
    {
        // Not implemented
    }

}
