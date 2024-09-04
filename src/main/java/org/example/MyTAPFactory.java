package org.example;

import tap.AbstractTAPFactory;
import tap.ServiceConnection;
import tap.TAPException;
import tap.db.DBConnection;
import tap.db.JDBCConnection;

import adql.translator.MySQLTranslator;

import uws.service.log.UWSLog.LogLevel;

import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class MyTAPFactory extends AbstractTAPFactory {
    private final DataSource dataSource;

    public MyTAPFactory(final ServiceConnection serviceConnection) throws TAPException {
        super(serviceConnection);

        try {
            InitialContext initialContext = new InitialContext();
            dataSource = (DataSource) initialContext.lookup("java:comp/env/jdbc/mysql");
            if (dataSource == null) {
                throw new TAPException("Could not find DataSource");
            }
        } catch (NamingException namingException) {
            throw new TAPException("Can not load the JNDI context", namingException);
        }


    }

    @Override
    public DBConnection getConnection(final String connectionID) throws TAPException {
        try {
            return new JDBCConnection(dataSource.getConnection(), new MySQLTranslator(), connectionID, service.getLogger());
        } catch (SQLException e) {
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
