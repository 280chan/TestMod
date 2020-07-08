package utils;

import cards.mahjong.AbstractMahjongCard;

public interface MahjongHelper {
	
	public static interface MahjongComparable {
		public abstract int compareTo(MahjongComparable c);
	}
	
	static class Helper {
		public static void sort(MahjongComparable[] cards) {
			for (int i = 0; i < cards.length - 1; i++)
				for (int j = 0; j < cards.length - 1 - i; j++)
					if (cards[j].compareTo(cards[j + 1]) > 0) {
						MahjongComparable temp = cards[j];
						cards[j] = cards[j + 1];
						cards[j + 1] = temp;
					}
		}
	}
	
	static class ShunZi {
		public AbstractMahjongCard[] cards = new AbstractMahjongCard[3];
		
		public ShunZi(AbstractMahjongCard... cards) {
			this.cards = cards;
		}
		
		public static boolean check(AbstractMahjongCard... cards) {
			if (cards.length != 3)
				return false;
			for (AbstractMahjongCard c : cards)
				if (c.isZ())
					return false;
			int color = cards[0].color();
			if (cards[1].color() != color || cards[2].color() != color)
				return false;
			if (cards[0].mathNum() == cards[1].mathNum() || cards[0].mathNum() == cards[2].mathNum() || cards[1].mathNum() == cards[2].mathNum())
				return false;
			sort(cards);
			return cards[0].mathNum() + 1 == cards[1].mathNum() && cards[1].mathNum() + 1 == cards[2].mathNum();
		}
		
		private static void sort(AbstractMahjongCard[] cards) {
			Helper.sort(cards);
		}
		
	}
	
	
}
