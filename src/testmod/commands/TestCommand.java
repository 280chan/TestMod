package testmod.commands;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import testmod.utils.MiscMethods;

public abstract class TestCommand extends ConsoleCommand implements MiscMethods {
	public static void add(String name, Class<? extends TestCommand> c) {
		ConsoleCommand.addCommand(name, c);
	}

	protected void errorMsg() {
		DevConsole.log("Err: TestCommand failed to execute.");
	}
}
