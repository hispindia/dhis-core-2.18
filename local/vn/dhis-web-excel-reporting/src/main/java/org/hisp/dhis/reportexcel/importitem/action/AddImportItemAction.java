package org.hisp.dhis.reportexcel.importitem.action;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import org.hisp.dhis.reportexcel.importitem.ExcelItem;
import org.hisp.dhis.reportexcel.importitem.ImportItemService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public class AddImportItemAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ImportItemService importItemService;

    // -------------------------------------------------------------------------
    // Inputs
    // -------------------------------------------------------------------------

    private String name;

    private String expression;

    private Integer row;

    private Integer column;

    private Integer sheetNo;

    private Integer importReportId;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setImportItemService( ImportItemService importItemService )
    {
        this.importItemService = importItemService;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    public void setRow( Integer row )
    {
        this.row = row;
    }

    public void setImportReportId( Integer importReportId )
    {
        this.importReportId = importReportId;
    }

    public Integer getImportReportId()
    {
        return importReportId;
    }

    public void setColumn( Integer column )
    {
        this.column = column;
    }

    public void setSheetNo( Integer sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        ExcelItem importItem = new ExcelItem();

        importItem.setName( name );
        importItem.setRow( row );
        importItem.setColumn( column );
        importItem.setExpression( expression );
        importItem.setSheetNo( sheetNo );
        importItem.setExcelItemGroup( importItemService.getImportReport( importReportId ) );

        importItemService.addImportItem( importItem );

        return SUCCESS;
    }

}
