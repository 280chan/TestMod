package christmasMod.utils;

import java.util.ArrayList;

import com.badlogic.gdx.math.RandomXS128;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.random.Random;

public interface ChristmasMiscMethods {
	
	public static class RNGTools {
		private static Random rng;
		public static Random cardRNG;
		
		static void setRNG(Random source) {
			rng = new Random();
			rng.random = new RandomXS128(source.random.getState(0), source.random.getState(1));
			rng.counter = 0;
		}
		
		static void setCardRNG(Random source) {
			cardRNG = new Random();
			cardRNG.random = new RandomXS128(source.random.getState(0), source.random.getState(1));
			cardRNG.counter = 0;
		}
	}
	
	public default void setCardRNG(Random source) {
		RNGTools.setCardRNG(source);
	}
	
	public default void setRNG(Random source) {
		RNGTools.setRNG(source);
	}
	
	public default AbstractCard randomCard(ArrayList<AbstractCard> list, boolean upgraded) {
		AbstractCard c = list.get((int) (RNGTools.rng.random() * list.size())).makeCopy();
		if (upgraded && c.canUpgrade())
			c.upgrade();
		return c;
	}
	
	public default float cardRng() {
		return RNGTools.cardRNG.random();
	}
	
}
