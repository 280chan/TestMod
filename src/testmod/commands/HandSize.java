package testmod.commands;

import basemod.DevConsole;

public class HandSize extends TestCommand {
	
	public HandSize() {
		this.followup.put("add", HandSizeAdd.class);
	    this.followup.put("lose", HandSizeLose.class);
	    this.followup.put("set", HandSizeSet.class);
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
	    DevConsole.log("* add [amount]");
	    DevConsole.log("* lose [amount]");
	    DevConsole.log("* set [amount]");
	}
}
