package testmod.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.stream.Stream;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.NoSkillsPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.LandingSound;
import com.megacrit.cardcrawl.rewards.RewardItem;

import basemod.ReflectionHacks;
import halloweenMod.mymod.HalloweenMod;
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
		
		print("chronoMods.bingo.SendBingoPatches.bingoExhaust");
		print("chronoMods.bingo.SendBingoPatches$bingoExhaust");
	}
	
	private static void print(String cls) {
		try {
			Class<?> c = Class.forName(cls);
			ReflectionHacks.privateStaticMethod(c, "Postfix", AbstractDungeon.class)
					.invoke(new Object[] { CardCrawlGame.dungeon });
		} catch (ClassNotFoundException e) {
			TestMod.info("找不到类" + cls);
		}
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
