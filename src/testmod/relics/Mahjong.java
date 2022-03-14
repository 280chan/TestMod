package testmod.relics;

import java.util.ArrayList;
import java.util.Random;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;

import testmod.cards.mahjong.AbstractMahjongCard;
import testmod.mymod.TestMod;

public class Mahjong extends AbstractTestRelic {
	public static final String ID = "Mahjong";
	
	public static final String[] YAMA_NAME = { "w0", "w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "p0", "p1",
			"p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9",
			"z1", "z2", "z3", "z4", "z5", "z6", "z7" };
	
	public static final int[] YAMA_DEFAULT = { 1, 4, 4, 4, 4, 3, 4, 4, 4, 4, 1, 4, 4, 4, 4, 3, 4, 4, 4, 4, 1, 4, 4, 4,
			4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 };

	public static final String[] KANG_NAME = {"KANG0", "KANG1", "KANG2", "KANG3"};
	public static final String[] DORA_NAME = {"DORA0", "DORA1", "DORA2", "DORA3", "DORA4"};
	public static final String HAND_NAME = "Mahjong_HAND";
	public static final String SAVE_KANG = "MahjongKang";
	public static final String SAVE_TURN = "MahjongTurns";
	public static final String SAVE_REACH = "MahjongReach";
	
	private ArrayList<Integer> yama = new ArrayList<Integer>();
	private ArrayList<AbstractMahjongCard> hand = new ArrayList<AbstractMahjongCard>();
	private ArrayList<AbstractMahjongCard> kang = new ArrayList<AbstractMahjongCard>();
	
	private ArrayList<AbstractMahjongCard> doraHint = new ArrayList<AbstractMahjongCard>();
	private ArrayList<AbstractMahjongCard> uraHint = new ArrayList<AbstractMahjongCard>();
	
	private ArrayList<Integer> history = new ArrayList<Integer>();
	private ArrayList<Integer> kangID = new ArrayList<Integer>();
	
	private static Random rng;
	private int numKang = 0;
	private boolean reach = false;
	
	private int turns = 0;
	
	private static boolean saveLater = false;
	
	public Mahjong() {
		super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		if (!isObtained)
			return DESCRIPTIONS[0];
		if (this.counter == 0)
			return DESCRIPTIONS[0];
		String desc = DESCRIPTIONS[0] + " NL 现在是第 #b" + this.counter + " 巡。";
		if (this.doraHint.size() > 0) {
			desc += "宝牌指示牌为: ";
			for (AbstractMahjongCard hint : doraHint) {
				desc += " #y" + hint.name + " 、";
			}
			desc = desc.substring(0, desc.length() - 1) + "。";
		}
		return desc;
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	private void saveTurns() {
		TestMod.save(SAVE_TURN, this.turns);
	}
	
	public static void saveDefaultYama() {
		for (int i = 0; i < 37; i++)
			TestMod.save(YAMA_NAME[i], YAMA_DEFAULT[i]);
	}
	
	private void save() {
		TestMod.info("Mahjong开始save");
		if (this.doraHint.isEmpty()) {
			this.reset();
		}
		TestMod.save(SAVE_KANG, this.numKang);
		TestMod.save(SAVE_TURN, this.turns);
		TestMod.save(SAVE_REACH, this.reach);
		for (int i = 0; i < 37; i++)
			TestMod.save(YAMA_NAME[i], this.yama.get(i));
		for (int i = 0; i < this.numKang; i++)
			TestMod.save(KANG_NAME[i], this.kangID.get(i));
		for (int i = 0; i < this.numKang + 1; i++)
			TestMod.save(DORA_NAME[i], this.doraHint.get(i).numberID());
		for (int i = 0; i < this.hand.size(); i++)
			TestMod.save(HAND_NAME + i, this.hand.get(i).numberID());
		TestMod.info("Mahjong save完毕");
	}
	
	public static void load(int turns, boolean reach, int[] yama, int[] kangID, int[] hintID, int[] handID) {
		if (AbstractDungeon.player.hasRelic(TestMod.makeID(ID))) {
			Mahjong current = (Mahjong)AbstractDungeon.player.getRelic(TestMod.makeID(ID));
			TestMod.info("Mahjong开始load");
			if (current.total(yama) == 0 || current.total(yama) == current.total(YAMA_DEFAULT)) {
				saveLater = true;
				TestMod.info("saveLater变为true");
				return;
			}
			current.reach = reach;
			current.turns = turns;
			current.numKang = kangID.length;
			current.yama.clear();
			for (int i = 0; i < 37; i++)
				current.yama.add(yama[i]);
			
			current.kangID.clear();
			for (int i = 0; i < current.numKang; i++)
				current.kangID.add(kangID[i]);	// TODO
			
			current.doraHint.clear();
			for (int i = 0; i < current.numKang + 1; i++)
				current.doraHint.add(AbstractMahjongCard.mahjong(hintID[i]));

			current.hand.clear();
			for (int i = 0; i < 13 - 3 * current.numKang; i++)
				current.hand.add(AbstractMahjongCard.mahjong(handID[i]));
			
			TestMod.info("Mahjong load完毕");
		}
	}
	
	public void onEquip() {
		this.reset();
		saveLater = true;
	}
	
	public static void setRng() {
		rng = new Random(Settings.seed.longValue() + ((Integer)AbstractDungeon.floorNum).longValue());
		TestMod.info("rng 已设置");
	}
	
	private int ran(int range, long seedFix) {
		for (long i = 0; i < seedFix; i++)
			rng.nextBoolean();
		return rng.nextInt(range);
	}
	
	private void reset() {
		TestMod.info("Mahjong开始reset");
		this.yama.clear();
		for (int i : YAMA_DEFAULT)
			this.yama.add(i);
		this.kang.clear();
		this.kangID.clear();
		this.doraHint.clear();
		this.uraHint.clear();
		this.history.clear();
		this.numKang = 0;
		this.counter = 0;
		this.turns = 0;
		if (AbstractDungeon.floorNum > 0) {
			this.clearAndSetNewHand();
			this.doraHint();
		}
		TestMod.info("Mahjong reset完毕");
	}
	
	private void clearAndSetNewHand() {
		this.hand.clear();
		while (this.hand.size() < 13)
			this.hand.add(this.randomFromYama());
		for (int i = 0; i < 39; i++)
			this.randomFromYama();
		TestMod.info("Mahjong Hand分配完毕");
	}
	
	private int total(ArrayList<Integer> yama) {
		int sum = 0;
		for (int i : yama)
			sum += i;
		return sum;
	}
	
	private int total(int[] yama) {
		int sum = 0;
		for (int i : yama)
			sum += i;
		return sum;
	}
	
	private int countLeft() {
		return this.total(this.yama) - 14 + 1 + this.numKang;
	}
	
	private AbstractMahjongCard randomFromYama() {
		int index = this.ran(this.total(this.yama), this.historySeed());
		for (int i = 0; i < yama.size(); i++) {
			index -= yama.get(i);
			if (index < 0) {
				yama.set(i, yama.get(i) - 1);
				index = i;
				break;
			}
		}
		return AbstractMahjongCard.mahjong(index);
	}
	
	public ArrayList<AbstractMahjongCard> doraHint() {
		while (this.doraHint.size() < 1 + this.numKang)
			this.doraHint.add(this.randomFromYama());
		return this.doraHint;
	}
	
	private ArrayList<AbstractMahjongCard> uraHint() {
		while (this.uraHint.size() < 1 + this.numKang && this.reach)
			this.uraHint.add(this.randomFromYama());
		return this.uraHint;
	}
	
	private void updateDora() {
		this.doraHint();
		// TODO
	}
	
	private void kang(int color, int num) {

		// TODO
		this.numKang++;
		this.updateDora();
	}
	
	private boolean checkMultiple() {
		// TODO
		return false;
	}
	
	public void atBattleStart() {
		this.history.clear();
		// TODO
		TestMod.info("开始战斗");
		if (saveLater) {
			saveLater = false;
			this.save();
		}
    }
	
	
	
	private long historySeed() {
		Long seed = 0L;
		for (Integer i : this.history) {
			seed += i.longValue() + 1L;
			seed *= i.longValue() + 1L;
		}
		return seed;
	}
	
	public void atTurnStart() {
		this.counter++;
		if (this.counter == 0)
			this.counter++;
		this.updateDescription(AbstractDungeon.player.chosenClass);
		if (this.turns < this.counter) {
			this.turns = this.counter;
			this.saveTurns();
		} else if (this.turns == this.counter) {
			// rng.nextBoolean();
		}
		
		// TODO
		this.addToBot(new MakeTempCardInHandAction(this.randomFromYama()));
		if (this.countLeft() < 4)
			this.reset();
		else {
			this.randomFromYama();
			this.randomFromYama();
			this.randomFromYama();
		}
    }
	
	public void onVictory() {
		this.save();
    }

}