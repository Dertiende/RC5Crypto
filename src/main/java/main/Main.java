package main;

import sqlite.sqliteDB;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, SQLException, NoSuchAlgorithmException{
        keyGenerator keyGen = new keyGenerator();
        rc5Obj cli = utils.getCLI(args);
        cli.isCLICorrect();
        if (!keyGen.isPassRelevant(cli.login,cli.pass)){
            cli.pass = keyGen.getLab3Key();
            System.out.println("Password too weak.\nPlease, write down new  generated strong password and use it next time: \n"+cli.pass);
            Scanner in = new Scanner(System.in);
            System.out.println("Print 'ok': ");
            String inp = in.nextLine();
            while (inp.compareToIgnoreCase("ok") !=0 &&
                   inp.compareToIgnoreCase("ок") !=0){
                System.out.println("Wrong input. Print 'ok': ");
                inp = in.nextLine();
            }
        }
        sqliteDB db = new sqliteDB(cli);
        if (db.isUserExist(cli.login)){
            if (db.isPassCorrect(cli.login,cli.pass)){
                System.out.println("Successful authorisation.");
            }
            else{
                System.out.println("User exists.Wrong password.");
                System.exit(0);
            }
        }
        else{
            db.createUser(cli.login,cli.pass);
        }
        if (cli.mode.compareToIgnoreCase("encrypt") == 0){
            rc5 rc5 = new rc5(cli);
            rc5.encryptFile(cli.input,cli.output);
            }
        else {
            utils.getDecodeInfo(cli,cli.input);
            sqliteDB.getRC5Data(cli.hash);
            rc5 rc5 = new rc5(cli);
            rc5.decryptFile(cli.input,cli.output);
            }

    }
}
