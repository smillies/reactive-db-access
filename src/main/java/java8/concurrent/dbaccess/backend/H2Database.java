package java8.concurrent.dbaccess.backend;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;

import util.Files;

/** Starts an in-memory H2 database and creates tables in the PUBLIC schema. */
public class H2Database implements Database {

    // multiple connections to the same in-memory database are required. In this case, the database URL must include a name.
    // To keep the content of an in-memory database as long as the virtual machine is alive, use jdbc:h2:mem:test;DB_CLOSE_DELAY=-1.
    // the INIT script will create tables in the PUBLIC schema on first connection.
    private static final Path SCHEMA_DEF = Files.resourceToPath(H2Database.class, "/mentions.sql");
    private static String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM '" + SCHEMA_DEF.toString().replaceAll("\\\\","/") + "'";
    private static JdbcConnectionPool cp = JdbcConnectionPool.create(url, "sa", "");
    static {
        try {
            // trigger table creation
            Connection conn = cp.getConnection();
            conn.close();
       
            // To access an in-memory database from another process or from another computer, you need to start a TCP server in the same
            // process as the in-memory database was created. The other processes then need to access the database over TCP/IP or TLS.
            // Unfortunately, automatic mixed mode (AUTO_SERVER=TRUE) is not supported with an in-memory database.
            Server server = Server.createTcpServer().start();
            System.out.println("Server started and connection is open.");
            System.out.println("URL: jdbc:h2:" + server.getURL() + "/mem:test");
        }
        catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public H2Database() {
    }
    
    public static void main(String[] args) {
    }

    @Override
    public Connection getConnection() throws SQLException {
        return cp.getConnection(); // with auto commit
    }
}
