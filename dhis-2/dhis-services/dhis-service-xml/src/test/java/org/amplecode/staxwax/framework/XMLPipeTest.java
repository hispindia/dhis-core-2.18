package org.amplecode.staxwax.framework;


import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import junit.framework.TestCase;
import org.amplecode.staxwax.factory.XMLFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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
/**
 *
 * @author bobj
 * @version created 15-Jan-2010
 */
public class XMLPipeTest extends TestCase
{

    private InputStream inputStreamB;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        inputStreamB = classLoader.getResourceAsStream( "dataB.xml" );
    }

    public synchronized void testReadWrite() throws XMLStreamException
    {
        XMLPipe pipe = new XMLPipe();
        XMLEventWriter pipeinput = pipe.getInput();
        XMLEventReader pipeoutput = pipe.getOutput();

        XMLEventReader reader = XMLFactory.getXMLEventReader( inputStreamB ).getXmlEventReader();

        int events1 = 0;
        while ( reader.hasNext() )
        {
            events1++;
            XMLEvent ev = reader.nextEvent();
            // System.out.println( "Got an event " + ev.toString() );
            pipeinput.add( ev );
        }

        // XMLOutputFactory2 factory = (XMLOutputFactory2) XMLOutputFactory.newInstance();
        // XMLEventWriter stdoutWriter = factory.createXMLEventWriter( System.out );

        // read the other end of the pipe
        int events2 = 0;
        while ( pipeoutput.hasNext() )
        {
            events2++;
            XMLEvent ev = pipeoutput.nextEvent();
            // stdoutWriter.add( ev );
        }

        assertEquals( "Number of events in and out of pipe", events1, events2 );
        System.out.println( "Number of events : " + events1 );

    }

    public synchronized void testMTReadWrite() throws XMLStreamException
    {

        // Create a thread to write one end of the pipe
        class Writer implements Runnable
        {

            protected XMLPipe pipe;

            public void setPipe( XMLPipe pipe )
            {
                this.pipe = pipe;
            }

            @Override
            public void run()
            {

                try
                {
                    XMLEventReader reader = XMLFactory.getXMLEventReader( inputStreamB ).getXmlEventReader();

                    int events1 = 0;
                    while ( reader.hasNext() )
                    {
                        events1++;
                        XMLEvent ev = reader.nextEvent();
                        pipe.getInput().add( ev );
                    }
                } catch ( Exception ex )
                {
                    assertTrue( ex.getMessage(), false );
                }

            }
        }


        class Reader implements Runnable
        {

            protected XMLPipe pipe;

            public void setPipe( XMLPipe pipe )
            {
                this.pipe = pipe;
            }

            @Override
            public void run()
            {

                try
                {
//                    XMLOutputFactory2 factory = (XMLOutputFactory2) XMLOutputFactory.newInstance();
//                    XMLEventWriter stdoutWriter = factory.createXMLEventWriter( System.out );


                    int events1 = 0;
                    XMLEvent ev = null;

                    do
                    {
                        events1++;
                        ev = pipe.output.nextEvent();
//                        stdoutWriter.add( ev );
                    }
                    while ( !ev.isEndDocument() );

                    assertEquals( "Number of events in and out of pipe", 80, events1 );

                } catch ( Exception ex )
                {
                    assertTrue( ex.getMessage(), false );
                }

            }
        }

        Writer w = new Writer();
        Reader r = new Reader();
        XMLPipe pipe = new XMLPipe();
        w.setPipe( pipe );

        // take a nap ...
        try
        {
            Thread.sleep( 1000 );
        } catch ( InterruptedException ex )
        {
            Logger.getLogger( XMLPipeTest.class.getName() ).log( Level.SEVERE, null, ex );
        }

        r.setPipe( pipe );

        SimpleAsyncTaskExecutor ex = new SimpleAsyncTaskExecutor();
        // set off the writer thread
        ex.execute( w );

        // read the pipe from this thread
        r.run();
    }
}
