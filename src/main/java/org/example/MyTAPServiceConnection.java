package org.example;

import adql.db.FunctionDef;
import adql.parser.ParseException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import tap.TAPFactory;
import tap.TAPException;
import tap.ServiceConnection;

import tap.db.DBConnection;
import tap.db.DBException;
import tap.formatter.OutputFormat;
import tap.formatter.VOTableFormat;
import tap.formatter.SVFormat;
import tap.formatter.TextFormat;
import tap.formatter.JSONFormat;
import tap.formatter.FITSFormat;
import tap.formatter.HTMLFormat;
import tap.log.TAPLog;
import tap.log.DefaultTAPLog;
import tap.metadata.TAPMetadata;

import uk.ac.starlink.votable.DataFormat;

import uws.UWSException;
import uws.service.file.UWSFileManager;
import uws.service.file.LocalUWSFileManager;
import uws.service.UserIdentifier;

public class MyTAPServiceConnection implements ServiceConnection {

    private boolean available = false;	// => service disabled by default!
    private String availability = "Service not yet ready! Initialization in progress...";

    private final UWSFileManager fileManager;

    private final TAPLog logger;

    private final TAPFactory factory;

    private final TAPMetadata metadata;

    private final List<OutputFormat> formats;

    /* LIMITS */
    // Fetch size:
    private final int[] fetchSize             = null;
    // Asynchronous Jobs:
    private final int maxAsync                = 30;
    // Execution Duration:
    private final int[] executionDuration     = new int[]{600,3600};
    // Job Destruction:
    private final int[] retentionPeriod       = new int[]{3600,86400};
    // Output:
    private final int[] outputLimit           = new int[]      {          1000,          10000};
    private final LimitUnit[] outputLimitType = new LimitUnit[]{LimitUnit.rows, LimitUnit.rows};
    // Upload:
    private final boolean uploadEnabled       = true;
    private final long maxUploadSize           = 1000000L;
    private final long[] uploadLimit           = new long[]    {         1000L,         2000L};
    private final LimitUnit[] uploadLimitType = new LimitUnit[]{LimitUnit.rows,LimitUnit.rows};

    // List of allowed coordinate systems:
    private final List<String> coordinateSystems;

    // List of all user defined functions:
    private final List<FunctionDef> allowedUdfs;

    // Provider information:
    private final String PROVIDER = "TAPTuto";
    private final String PROVIDER_DESCRIPTION = null;

    public MyTAPServiceConnection() throws TAPException, DBException, UWSException {
        // 1. Build the file manager:
        fileManager = new LocalUWSFileManager(new File("ServiceFiles"));

        // 2. Get the logger to use in the whole TAP service:
        logger = new DefaultTAPLog(fileManager);

        // 3. Build the factory:
        factory = new MyTAPFactory(this);

        // 4. Get the metadata:
        DBConnection dbConn = factory.getConnection("MetaConn");
        metadata = dbConn.getTAPSchema();
        getFactory().freeConnection(dbConn);

        // 5. List allowed output formats:
        formats = new ArrayList<OutputFormat>(10);
        formats.add(new VOTableFormat(this));                            // VOTable (BINARY serialization) (default)
        formats.add(new VOTableFormat(this, DataFormat.FITS));           //    "    (FITS serialization)
        formats.add(new VOTableFormat(this, DataFormat.BINARY2));        //    "    (BINARY2 serialization)
        formats.add(new VOTableFormat(this, DataFormat.TABLEDATA));      //    "    (TABLEDATA serialization)
        formats.add(new SVFormat(this, SVFormat.COMMA_SEPARATOR, true)); // CSV
        formats.add(new SVFormat(this, SVFormat.TAB_SEPARATOR, true));   // TSV
        formats.add(new TextFormat(this));                               // text/plain (with nice array presentation)
        formats.add(new JSONFormat(this));                               // JSON
        formats.add(new FITSFormat(this));                               // FITS
        formats.add(new HTMLFormat(this));                               // HTML (just the "table" node)

        // 6a. Coordinate systems:
        coordinateSystems = new ArrayList<String>(1);
        coordinateSystems.add("ICRS * *");

        // 6b. UDFs (User Defined Functions):
        allowedUdfs = new ArrayList<FunctionDef>(4);
        try{
            allowedUdfs.add(FunctionDef.parse("random() -> double"));
            allowedUdfs.add(FunctionDef.parse("rtrim(txt String) -> String"));
            allowedUdfs.add(FunctionDef.parse("rpad(txt varchar, len int, fill varchar) -> VARCHAR"));
            allowedUdfs.add(FunctionDef.parse("initcap(txt varchar) -> VARCHAR"));
        }catch(ParseException pe){
            throw new TAPException("Can not initialize the TAP service! There is a wrong UDF definition: " + pe.getMessage(), pe);
        }
    }

    public final boolean isAvailable(){
        return available;
    }

    public final String getAvailability(){
        return availability;
    }

    public final void setAvailable(final boolean isAvailable, final String explanation){
        this.available = isAvailable;
        this.availability = explanation;
    }

    public UWSFileManager getFileManager(){
        return fileManager;
    }

    public final TAPFactory getFactory(){
        return factory;
    }

    public TAPLog getLogger(){
        return logger;
    }

    public final TAPMetadata getTAPMetadata(){
        return metadata;
    }

    public OutputFormat getOutputFormat(final String format){
        for(OutputFormat f : formats){
            if (f.getMimeType().equalsIgnoreCase(format) || f.getShortMimeType().equalsIgnoreCase(format))
                return f;
        }
        return null;
    }

    public Iterator<OutputFormat> getOutputFormats(){
        return formats.iterator();
    }

    public int[] getFetchSize()            { return null; }

    public int getNbMaxAsyncJobs()         { return maxAsync; }

    public int[] getExecutionDuration()    { return executionDuration; }

    public int[] getRetentionPeriod()      { return retentionPeriod; }

    public int[] getOutputLimit()          { return outputLimit; }

    public LimitUnit[] getOutputLimitType(){ return outputLimitType; }

    public boolean uploadEnabled()         { return uploadEnabled; }

    public long getMaxUploadSize()          { return maxUploadSize; }

    public long[] getUploadLimit()          { return uploadLimit;	}

    public LimitUnit[] getUploadLimitType(){ return uploadLimitType; }

    public boolean fixOnFailEnabled(){ return false; }

    public Collection<String> getCoordinateSystems(){
        return coordinateSystems;
    }

    public Collection<String> getGeometries(){
        return null; /* => ALL */
    }

    public Collection<FunctionDef> getUDFs(){
        return allowedUdfs;
    }

    public UserIdentifier getUserIdentifier(){
        return null;
    }

    public String getProviderName() {
        return PROVIDER;
    }

    public String getProviderDescription() {
        return PROVIDER_DESCRIPTION;
    }

    // Implement the missing getBaseUrl method
    @Override
    public URL getBaseUrl() {
        // Return the base URL for the service, adjust as needed
        try {
            URI uri = new URI("http://localhost:8080/tap");
            return uri.toURL();
        }  catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
