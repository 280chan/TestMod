package testmod.commands;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;

import testmod.cards.AbstractTestCard;
import testmod.cards.colorless.SunMoon;
import testmod.cards.colorless.WeaknessCounterattack;
import testmod.mymod.TestMod;

@SuppressWarnings("unused")
public class Test extends TestCommand {
	
	private void example() {
		//*
		// \u000a cmdHelp();
		//*/
	}
	
	public void execute(String[] tokens, int depth) {
		if (tokens.length > 1) {
			cmdHelp();
			return;
		}
		TestMod.info("升级遗物数量: " + TestMod.UP_RELICS.size());
		/*p().masterDeck.group.clear();
		TestMod.CARDS.stream().map(c -> c.makeCopy()).forEach(p().masterDeck.group::add);*/
	}

	private static void cmdHelp() {
		tooManyTokensError();
	}
	
}
