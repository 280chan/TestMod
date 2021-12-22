package relics;

import java.util.stream.Stream;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.curses.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.CardCrawlGame.GameMode;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BlueCandle;
import com.megacrit.cardcrawl.relics.DarkstonePeriapt;
import com.megacrit.cardcrawl.relics.DuVuDoll;
import com.megacrit.cardcrawl.relics.Omamori;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import cards.AbstractTestCurseCard;
import cards.curse.Envy;
import cards.curse.Gluttony;
import cards.curse.Greed;
import cards.curse.Lust;
import cards.curse.Sloth;
import cards.curse.Wrath;
import mymod.TestMod;
import screens.BossRelicSelectScreen;
import utils.MiscMethods;

public class Sins extends AbstractTestRelic implements MiscMethods {
	private static final UIStrings UI = INSTANCE.uiString();
	public static final String ID = "SevenDeadlySins";
	
	public static final AbstractCard[] SINS = { new Pride(), new Lust(), new Wrath(), new Sloth(), new Envy(),
			new Greed(), new Gluttony() };
	public static final AbstractRelic[] RELICS = {new DuVuDoll(), new DarkstonePeriapt(), new BlueCandle()};
	
	public static final String SAVE_NAME = "preMaxHP";
	
	public static int preMaxHP;
    public static boolean dontCopy = false;
    public static boolean isFull = true;
	public static boolean isSelect;
	
	public static void load(int loadValue) {
		preMaxHP = loadValue;
	}
	
	public static boolean checkModified() {
		return preMaxHP != AbstractDungeon.player.maxHealth;
	}
	
    private void save() {
    	TestMod.save(SAVE_NAME, preMaxHP);
    }
    
	public Sins() {
		super(RelicTier.SPECIAL, LandingSound.HEAVY, BAD);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	private boolean checkFull() {
		boolean full = true;
		for (AbstractCard c : SINS)
			full &= this.has(c);
		return full;
	}
	
	public void updateFull() {		
		if (checkFull() != isFull) {
			isFull = !isFull;
		}
	}
	
	private boolean has(AbstractCard card) {
		for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
			if (c.cardID.equals(card.cardID))
				return true;
		return false;
	}
	
	private boolean hasGluttony() {
		return this.has(SINS[6]);
	}
	
	private void replicate() {
    	if (hasDarkstonePeriapt()) {
    		TestMod.info("玩家有黑石护符,下一次不复制暴食");
    		dontCopy = true;
    	}
    	TestMod.info("开始获得暴食");
    	obtainCard(new Gluttony());
    	TestMod.info("获得暴食完毕");
	}
	
    private boolean hasDarkstonePeriapt() {
    	return AbstractDungeon.player.hasRelic("Darkstone Periapt");
    }
    
    public void checkChangeMaxHP() {
    	if (AbstractDungeon.player.maxHealth == preMaxHP)
    		return;
    	boolean increase = AbstractDungeon.player.maxHealth > preMaxHP;
    	String tmp = "检测到血量上限变化,旧上限:" + preMaxHP + ",新上限:";
		preMaxHP = AbstractDungeon.player.maxHealth;
		TestMod.info(tmp + preMaxHP);
    	if (increase && hasGluttony()) {
    		TestMod.info("拥有暴食");
    		if (dontCopy) {
    			TestMod.info("暴食引起,不复制暴食");
    			dontCopy = false;
			} else if ((AbstractDungeon.player.hasRelic("Omamori"))
					&& (AbstractDungeon.player.getRelic("Omamori").counter != 0)) {
				TestMod.info("御守有效,不复制暴食");
				((Omamori) AbstractDungeon.player.getRelic("Omamori")).use();
			} else {
				TestMod.info("准备复制暴食");
				replicate();
			}
    	}
    }
    
    public void update() {
    	super.update();
    	if (!isActive)
    		return;
		this.checkChangeMaxHP();
    	
    }
    
	public void onMasterDeckChange() {
		if (!isActive)
			return;
		this.updateFull();
	}
	
	private static void obtainCard(AbstractCard c) {
		INSTANCE.p().relics.forEach(r -> r.onObtainCard(c));
		UnlockTracker.markCardAsSeen(c.cardID);
		INSTANCE.p().masterDeck.addToTop(c);
		INSTANCE.addHoarderCard(INSTANCE.p().masterDeck, c);
		INSTANCE.p().relics.forEach(r -> r.onMasterDeckChange());
	}
	
	public static void equipAction() {
		TestMod.info("获得七原罪诅咒与遗物");
		AbstractTestRelic.setTryEquip(Sins.class, false);
		Stream.of(RELICS).forEach(r -> TestMod.obtain(INSTANCE.p(), r, false));
		Stream.of(SINS).forEach(c -> obtainCard(c.makeCopy()));
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!isActive)
			return;
		this.setTryEquip(true);
		this.checkChangeMaxHP();
    }
	
	public static boolean isSin(AbstractCard c) {
		return c instanceof Pride || c instanceof AbstractTestCurseCard;
	}
	
	public void onUnequip() {
		if (!isActive)
			return;
		this.setTryUnequip(true);
	}
	
	public static void unequipAction() {
		AbstractTestRelic.setTryUnequip(Sins.class, false);
		for (int i = 0; i < AbstractDungeon.player.masterDeck.group.size(); i++) {
			AbstractCard c = AbstractDungeon.player.masterDeck.group.get(i);
			if (isSin(c)) {
				AbstractDungeon.player.masterDeck.group.remove(c);
				i--;
				TestMod.info("七原罪: 移除" + c.name);
			}
		}
		for (AbstractRelic r : RELICS) {
			AbstractDungeon.player.loseRelic(r.relicId);
			TestMod.info("七原罪: 移除" + r.name);
		}
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		Sloth.startBattle();
    }
	
	public void onPlayerEndTurn() {
		if (!isActive)
			return;
		Sloth.endTurn();
    }
	
	public void onEnterRoom(final AbstractRoom room) {
		if (!isActive)
			return;
		this.save();
		this.updateFull();
		if (isFull && (room instanceof MonsterRoomBoss || room instanceof TreasureRoomBoss)) {
			beginLongPulse();
			Sins.isSelect = false;
		} else {
			stopPulse();
		}
    }

	public void onChestOpen(final boolean bossChest) {
		if (!isActive)
			return;
		this.save();
		this.updateFull();
		if (isFull && bossChest) {
			stopPulse();
			if (!Sins.isSelect) {
				new BossRelicSelectScreen(true, UI.TEXT[0], UI.TEXT[1], UI.TEXT[2]).open();;
			}
		}
	}
	
	public static boolean isObtained() {
		if (CardCrawlGame.mode != GameMode.GAMEPLAY && CardCrawlGame.mode != GameMode.DUNGEON_TRANSITION)
			return true;
		return AbstractDungeon.player.hasRelic(TestMod.makeID(ID));
	}
	
	public static AbstractCard copyCurse() {
		AbstractCard c = AbstractDungeon.getCard(CardRarity.CURSE);
		if (isSin(c)) {
			return copyCurse();
		}
		return c.makeCopy();
	}

}