package commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;

public abstract class TestCommand extends ConsoleCommand {
	public static void add(String name, Class<? extends TestCommand> c) {
		ConsoleCommand.addCommand(name, c);
	}

	protected void errorMsg() {
		DevConsole.log("Err: TestCommand failed to execute.");
	}
}
