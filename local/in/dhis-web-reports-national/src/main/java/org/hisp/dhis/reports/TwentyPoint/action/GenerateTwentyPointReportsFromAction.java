/**
 * 
 */
package org.hisp.dhis.reports.TwentyPoint.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.util.ReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.ActionSupport;


/**
 * @author Brajesh Murari
 *
 */
public class GenerateTwentyPointReportsFromAction extends ActionSupport
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
 // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /*
    private final int ALL = 0;

    public int getALL()
    {
        return ALL;
    }
*/    
    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------
	 
    //private static final long serialVersionUID = 1L;
    private List<Report_in> reportList;
    
    public List<Report_in> getReportList()
    {
        return reportList;
    }
    
    
    private List<Period> periods = new ArrayList<Period>();

    public List<Period> getPeriods()
    {
        return periods;
    }

    
    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }
    
    private String raFolderName;
    
  
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
       
        //reportList = new ArrayList<Report>();
        
        raFolderName = reportService.getRAFolderName();
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        PeriodType monthlyPeriodType = periodService.getPeriodTypeByName( "Monthly" );

        periods.addAll( periodService.getPeriodsByPeriodType( monthlyPeriodType ) );
        Iterator<Period> periodIterator = periods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove( );
            }
            
        }
        Collections.sort( periods, new PeriodComparator() );
        
        getSelectedReportList();

        return SUCCESS;
    }

    public void getSelectedReportList()
    {
        String fileName = "TwentyPointsReportsList.xml";
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
            + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + File.separator + raFolderName + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }

        String reportId = "";
        String reportName = "";
        String reportType = "";
        String reportLevel = "";
        String reportModel = "";
        String reportFileName = "";        

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at user home" );
                return;
            }

            NodeList listOfReports = doc.getElementsByTagName( "report" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;
                    reportId = reportElement.getAttribute( "id" );

                    NodeList reportNameList = reportElement.getElementsByTagName( "name" );
                    Element reportNameElement = (Element) reportNameList.item( 0 );
                    NodeList textreportNameList = reportNameElement.getChildNodes();
                    reportName = ((Node) textreportNameList.item( 0 )).getNodeValue().trim();

                    NodeList reportTypeList = reportElement.getElementsByTagName( "type" );
                    Element reportTypeElement = (Element) reportTypeList.item( 0 );
                    NodeList textreportTypeList = reportTypeElement.getChildNodes();
                    reportType = ((Node) textreportTypeList.item( 0 )).getNodeValue().trim();

                    NodeList reportModelList = reportElement.getElementsByTagName( "model" );
                    Element reportModelElement = (Element) reportModelList.item( 0 );
                    NodeList textreportModelList = reportModelElement.getChildNodes();
                    reportModel = ((Node) textreportModelList.item( 0 )).getNodeValue().trim();

                    NodeList reportFileNameList = reportElement.getElementsByTagName( "filename" );
                    Element reportFileNameElement = (Element) reportFileNameList.item( 0 );
                    NodeList textreportFileNameList = reportFileNameElement.getChildNodes();
                    reportFileName = ((Node) textreportFileNameList.item( 0 )).getNodeValue().trim();

                    NodeList reportLevelList = reportElement.getElementsByTagName( "level" );
                    Element reportLevelElement = (Element) reportLevelList.item( 0 );
                    NodeList textreportLevelList = reportLevelElement.getChildNodes();
                    reportLevel = ((Node) textreportLevelList.item( 0 )).getNodeValue().trim();
                    
                    //Report reportObj = new Report(reportId, reportName, reportType, reportModel, reportFileName, reportLevel);
                    
                    System.out.println(reportId + " : " +  reportFileName);
                    
                 //  reportList.add( reportObj );                   
                }
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }

    }// getReportList end

}

