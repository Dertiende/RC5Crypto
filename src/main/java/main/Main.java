package main;

import com.beust.jcommander.JCommander;
import sqlite.sqliteDB;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException, NoSuchAlgorithmException{

        cliParser cli = utils.getCLI(args);
        long crc = utils.getCRC32(cli.input);
        sqliteDB db = new sqliteDB();
        if (db.isUserExist(cli.login)){
            if (db.isPassCorrect(cli.login,cli.pass)){
                System.out.println("correct pass");
            }
        }
        else{
            db.createUser(cli.login,cli.pass);
        }
        System.out.println(Long.toHexString(crc));
        rc5 rc5 = new rc5(Integer.parseInt(cli.bsize),Integer.parseInt(cli.rounds), cli.key.getBytes(StandardCharsets.UTF_8));

        if (cli.mode.compareToIgnoreCase("encrypt") == 0){
            rc5.encryptFile(cli.input,cli.output);
            }
        else {
            rc5.temporal();
            rc5.decryptFile(cli.input,cli.output);
            }

    }
}
