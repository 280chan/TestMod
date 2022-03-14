package testmod.commands;

import basemod.DevConsole;

public class Ascension extends TestCommand {
	public Ascension() {
	    this.followup.put("set", AscensionSet.class);
	    this.requiresPlayer = true;
	    this.simpleCheck = true;
	}
	
	public void execute(String[] tokens, int depth) {
		cmdHelp();
	}
	
	public void errorMsg() {
		cmdHelp();
	}
	
	public static void cmdHelp() {
		DevConsole.couldNotParse();
	    DevConsole.log("options are:");
	    DevConsole.log("* set [amount]");
	}
}
