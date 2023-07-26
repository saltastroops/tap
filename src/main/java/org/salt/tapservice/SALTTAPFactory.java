package org.salt.tapservice;

import tap.AbstractTAPFactory;
import tap.ServiceConnection;
import tap.TAPException;
import tap.db.DBConnection;
import tap.db.JDBCConnection;

import adql.translator.PostgreSQLTranslator;

import uws.service.log.UWSLog.LogLevel;

import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class SALTTAPFactory extends AbstractTAPFactory {
    private final DataSource ds;

    public SALTTAPFactory(final ServiceConnection serviceConn) throws TAPException{
        super(serviceConn);

        // Fetch the datasource from JNDI:
        try{
            InitialContext cxt = new InitialContext();
            ds = (DataSource)cxt.lookup("java:/comp/env/jdbc/postgres");
            if (ds == null)
                throw new TAPException("Data source not found!");
        }catch(NamingException ne){
            throw new TAPException("Can not load the JNDI context!", ne);
        }
    }

    public DBConnection getConnection(final String connID) throws TAPException{
        try{
            return new JDBCConnection(ds.getConnection(), new PostgreSQLTranslator(), connID, service.getLogger());
        }catch(SQLException e){
            throw new TAPException("Can not create a database connection!", e);
        }
    }

    public void freeConnection(final DBConnection dbConn){
        try{
            ((JDBCConnection)dbConn).getInnerConnection().close();
        }catch(SQLException e){
            service.getLogger().logTAP(LogLevel.WARNING, dbConn, "CLOSE_CONNECTION", "Can not close the connection!", e);
        }
    }

    public void destroy(){ /* The DataSource will be destroyed by Tomcat. */}

}