package commands;

import basemod.DevConsole;

public class Relic extends TestCommand {
	
	public Relic() {
		this.followup.put("add", RelicAdd.class);
	    this.followup.put("remove", RelicRemove.class);
	    this.followup.put("set", RelicSetCounter.class);
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
	    DevConsole.log("* add");
	    DevConsole.log("* remove");
	    DevConsole.log("* set [amount]");
	}
}
