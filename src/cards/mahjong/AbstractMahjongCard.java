
package cards.mahjong;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;

import cards.AbstractTestCard;
import mymod.TestMod;
import relics.Mahjong;
import utils.MahjongHelper;
import utils.MahjongHelper.MahjongComparable;
import utils.MiscMethods;

public abstract class AbstractMahjongCard extends AbstractTestCard implements MiscMethods, MahjongComparable {
	private static final String ID_PREFIX = "Mahjong";
	private static final String[] COLOR_ID = { "m", "p", "s", "z" };

	private static final CardStrings cardStrings = AbstractTestCard.Strings(ID_PREFIX);
	private static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final String[] COLOR_NAME = getNames(0);
	private static final String[] NUM_NAME = getNames(1);
	private static final String[] ZI_NAME = getNames(2);
	public static final int COLOR_W = 0, COLOR_P = 1, COLOR_S = 2, COLOR_Z = 3;
	private static Random rng;
	private int color;
	private int num;
	
	private int preMgc;
	
	private int doraApplied = 0;
	
	private static String[] getNames(int type) {
		if (type == 0)
			return getNames(0, 3);
		if (type == 1)
			return getNames(3, 13);
		if (type == 2)
			return getNames(13, 20);
		return null;
	}
	
	private static String[] getNames(int start, int end) {
		String[] tmp = new String[end - start];
		for (int i = 0; i < end - start; i++) {
			tmp[i] = EXTENDED_DESCRIPTION[start + i];
		}
		return tmp;
	}
	
	public boolean isW() {
		return this.color == COLOR_W;
	}
	
	public boolean isP() {
		return this.color == COLOR_P;
	}
	
	public boolean isS() {
		return this.color == COLOR_S;
	}
	
	public boolean isZ() {
		return this.color == COLOR_Z;
	}
	
	public int mathNum() {
		if (this.color == COLOR_Z)
			return -1;
		if (this.num == 0)
			return 5;
		return this.num;
	}
	
	public AbstractMahjongCard(int color, int num, String desc, CardType type, CardTarget target) {
		super(getID(color, num), getName(color, num), 0, desc, type, CardRarity.SPECIAL, target);
		this.color = color;
		this.num = num;
	}
	
	public static AbstractMahjongCard mahjong(int numberID) {
		if (numberID < 10)
			return new MahjongWs(numberID);
		if (numberID < 20)
			return new MahjongPs(numberID - 10);
		if (numberID < 30)
			return new MahjongSs(numberID - 20);
		return new MahjongZs(numberID - 29);
	}
	
	private static String getID(int color, int num) {
		return ID_PREFIX + num + COLOR_ID[color];
	}
	
	private static String getName(int color, int num) {
		if (color == 3)
			return ZI_NAME[num - 1];
		return NUM_NAME[num] + COLOR_NAME[color];
	}
	
	public static void setRng() {
		rng = INSTANCE.copyRNG(AbstractDungeon.cardRng);
	}
	
	public AbstractMahjongCard pureRandomMahjong() {
		return TestMod.randomItem(TestMod.MAHJONGS, rng);
	}
	
	public int color() {
		return this.color;
	}
	
	public int num() {
		return this.num;
	}
	
	private boolean isDora(AbstractMahjongCard hint) {
		if (this.color != hint.color)
			return false;
		if (this.color != 3) {
			if (hint.num == 0)
				return this.num == 6;
			if (hint.num == 9)
				return this.num == 1;
			if (hint.num == 4 && this.num == 0)
				return true;
			return this.num == hint.num + 1;
		}
		if ((this.num - 4.5) * (hint.num - 4.5) < 0)
			return false;
		if (hint.num == 4)
			return this.num == 1;
		if (hint.num < 4)
			return this.num == hint.num + 1;
		if (hint.num == 7)
			return this.num == 5;
		return this.num == hint.num + 1;
	}
	
	public int countDora(ArrayList<AbstractMahjongCard> doraHint) {
		int count = 0;
		for (AbstractMahjongCard hint : doraHint)
			if (this.isDora(hint))
				count++;
		return count;
	}
	
	public int numberID() {
		if (this.color < 3)
			return this.color * 10 + this.num;
		return 29 + this.num;
	}
	
	public void triggerOnGlowCheck() {
		this.glowColor = AbstractCard.BLUE_BORDER_GLOW_COLOR.cpy();
		if (!AbstractDungeon.player.hasRelic(Mahjong.ID))
			return;
		Mahjong r = (Mahjong)AbstractDungeon.player.getRelic(Mahjong.ID);
		if (this.countDora(r.doraHint()) > 0)
			this.glowColor = AbstractCard.GOLD_BORDER_GLOW_COLOR.cpy();
	}
	
	public void resetAttributes() {
		while (this.doraApplied > 0) {
			this.baseDamage /= 2;
			this.baseBlock /= 2;
			this.baseMagicNumber /= 2;
			this.doraApplied--;
		}
		super.resetAttributes();
	}
	
	public void calculateCardDamage(AbstractMonster m) {
		int c = 0;
		if (AbstractDungeon.player.hasRelic(Mahjong.ID)) {
			this.preMgc = this.baseMagicNumber;
			Mahjong r = (Mahjong)AbstractDungeon.player.getRelic(Mahjong.ID);
			int count = c = this.countDora(r.doraHint());
			if (this.doraApplied < count) {
				for (; this.doraApplied < count; this.doraApplied++) {
					this.baseDamage *= 2;
					this.baseBlock *= 2;
					this.baseMagicNumber *= 2;
				}
				this.magicNumber = this.baseMagicNumber;
			} else if (this.doraApplied > count) {
				for (; this.doraApplied > count; this.doraApplied--) {
					this.baseDamage /= 2;
					this.baseBlock /= 2;
					this.baseMagicNumber /= 2;
				}
				this.magicNumber = this.baseMagicNumber;
			}
		}
		super.calculateCardDamage(m);
		if (c > 0) {
			this.baseMagicNumber = this.preMgc;
			this.isMagicNumberModified = true;
			this.upgradedMagicNumber = true;
		}
	}
	
	public void applyPowers() {
		
		super.applyPowers();
	}

	@Override
	public int compareTo(MahjongComparable c) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}