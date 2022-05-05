package testmod.commands;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard.CardType;

import testmod.mymod.TestMod;

@SuppressWarnings("unused")
public class Test extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		if (tokens.length > 1) {
			cmdHelp();
			return;
		}
		
		/*p().masterDeck.group.clear();
		TestMod.CARDS.stream().map(c -> c.makeCopy()).forEach(p().masterDeck.group::add);*/
		
	}

	private static void cmdHelp() {
		tooManyTokensError();
	}
}
