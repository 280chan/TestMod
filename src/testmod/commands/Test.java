package testmod.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.LandingSound;

import basemod.ReflectionHacks;
import testmod.cards.AbstractTestCard;
import testmod.cards.colorless.SunMoon;
import testmod.cards.colorless.WeaknessCounterattack;
import testmod.mymod.TestMod;

@SuppressWarnings("unused")
public class Test extends TestCommand {
	
	public void execute(String[] tokens, int depth) {
		if (tokens.length > 1) {
			cmdHelp();
			return;
		}
		TestMod.info("升级遗物数量: " + TestMod.UP_RELICS.size());
	}

	private static void cmdHelp() {
		tooManyTokensError();
	}
	
	private void example() {
		//*
		// \u000a cmdHelp();
		//*/
	}
	
	
	
}
