package main;
import com.beust.jcommander.Parameter;



public class rc5Obj {
    @Parameter(names = {"-l","--login"}, required = true, description = "Login")
    public String login;
    @Parameter(names = {"-p","--pass"}, required = true, description = "Password. Must be 10+ symbols of Cyrillic,Latin and arithmetic operands. Mustn't consists only from login's characters.")
    public String pass;
    @Parameter(names = {"-i","--input"}, required = true,description = "Input file location")
	public String input ;
	@Parameter(names = {"-o","--output"}, description = "Output file location")
	public String output = System.getProperty("user.dir");
	public String key;
	@Parameter(names = {"-r","--rounds"}, description = "Rounds number(1-255)")
	public String rounds = "2";
	@Parameter(names = {"-b","--bsize"}, description = "Block size(16,32)")
	public String bsize = "16";
	@Parameter(names = {"-m","--mode"}, required = true,description = "Mode(encrypt,decrypt)")
	public String mode;
	public String vector;
	public String hash;
	public String size;

	public rc5Obj(){
	}
	public void isCLICorrect(){
		try{
			if (Integer.parseInt(rounds) < 1 || Integer.parseInt(rounds) > 255){
				System.out.println("Incorrect rounds number: "+ rounds+". Should be 1-255.");
				System.exit(0);
			}
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect rounds number: "+ rounds+". Should be 1-255.");
			System.exit(0);
		}

		try{
			if (Integer.parseInt(bsize) != 16 && Integer.parseInt(bsize) != 32){
				System.out.println("Incorrect block size number: "+ bsize+". Should be 16 or 32.");
				System.exit(0);
			}
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect block size number: "+ bsize+". Should be 16 or 32.");
			System.exit(0);
		}

		try{
			if (mode.compareToIgnoreCase("encrypt") !=0 && mode.compareToIgnoreCase("decrypt") !=0){
				System.out.println("Incorrect mode: "+ mode+". Should be encrypt or decrypt.");
				System.exit(0);
			}
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect mode: "+ mode+". Should be encrypt or decrypt.");
			System.exit(0);
		}

	}
}