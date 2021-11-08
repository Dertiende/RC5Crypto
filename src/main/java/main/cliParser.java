package main;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

public class cliParser {
	JCommander jCommander;
	String[] argv;
    @Parameter(names = {"-l","--login"}, description = "Login")
    public String login;
    @Parameter(names = {"-p","--pass"}, description = "Password")
    public String pass;
    @Parameter(names = {"-i","--input"}, description = "Input file location")
	public String input ;
	@Parameter(names = {"-o","--output"}, description = "Output file location")
	public String output = System.getProperty("user.dir");
	@Parameter(names = {"-k","--key"}, description = "Key")
	public String key;
	@Parameter(names = {"-r","--rounds"}, description = "Rounds number(1-255)")
	public String rounds = "2";
	@Parameter(names = {"-b","--bsize"}, description = "Block size(16,32,64)")
	public String bsize="16";
	@Parameter(names = {"-m","--mode"}, description = "Mode(encrypt,decrypt)")
	public String mode;

	public cliParser(){
	}

}
