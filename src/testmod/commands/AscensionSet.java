package testmod.commands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.AsyncSaver;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.saveAndContinue.SaveFileObfuscator;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import testmod.relics.AscensionHeart;

public class AscensionSet extends TestCommand {
	public void execute(String[] tokens, int depth) {
		try {
			int i = Integer.parseInt(tokens[2]);
			AbstractDungeon.ascensionLevel = i;
			AbstractDungeon.isAscensionMode = i != 0;
			AbstractDungeon.topPanel.setupAscensionMode();
			relicStream(AscensionHeart.class).peek(r -> r.initCounter()).forEach(r -> r.updateDescription(null));
			save(SaveAndContinue.loadSaveFile(p().chosenClass));
		} catch (Exception e) {
			Ascension.cmdHelp();
		}
	}

	public ArrayList<String> extraOptions(String[] tokens, int depth) {
		ArrayList<String> result = smallNumbers();
	    if (tokens.length == depth + 1)
	        return result; 
		if (tokens[depth + 1].matches("\\d+"))
			complete = true;
		return result;
	}
	
	@SpirePatch(clz = TopPanel.class, method = "setupAscensionMode")
	public static class TopPanelPatch {
		private static int tmp = 0;
		@SpirePrefixPatch
		public static void Prefix(TopPanel p) {
			if ((tmp = AbstractDungeon.ascensionLevel) > 20)
				AbstractDungeon.ascensionLevel = 20;
			else if (tmp < 0)
				AbstractDungeon.ascensionLevel = 0;
		}
		@SpirePostfixPatch
		public static void Postfix(TopPanel p) {
			AbstractDungeon.ascensionLevel = tmp;
		}
	}
	
	private static Object get(Field f, SaveFile sf) {
		try {
			return f.get(sf);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static void save(SaveFile sf) {
		HashMap<Object, Object> params = new HashMap<Object, Object>();
		Stream.of(sf.getClass().getDeclaredFields()).filter(f -> !f.getName().equals("logger"))
				.forEach(f -> params.put(f.getName(), get(f, sf)));
		params.compute("is_ascension_mode", (a, b) -> AbstractDungeon.isAscensionMode);
		params.compute("ascension_level", (a, b) -> AbstractDungeon.ascensionLevel);
		
		Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
	    String data = gson.toJson(params);
	    String filepath = SaveAndContinue.getPlayerSavePath(AbstractDungeon.player.chosenClass);

		if (Settings.isBeta) {
			AsyncSaver.save(filepath + "BETA", data);
		}

		AsyncSaver.save(filepath, SaveFileObfuscator.encode(data, "key"));
	}
}
