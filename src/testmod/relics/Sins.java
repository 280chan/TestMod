package testmod.relics;

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

import testmod.cards.AbstractTestCurseCard;
import testmod.cards.curse.Envy;
import testmod.cards.curse.Gluttony;
import testmod.cards.curse.Greed;
import testmod.cards.curse.Lust;
import testmod.cards.curse.Sloth;
import testmod.cards.curse.Wrath;
import testmod.mymod.TestMod;
import testmod.screens.BossRelicSelectScreen;
import testmod.utils.MiscMethods;

public class Sins extends AbstractTestRelic implements MiscMethods {
	private static final UIStrings UI = MISC.uiString();
	public static final String ID = "SevenDeadlySins";
	
	public static final AbstractCard[] SINS = { new Pride(), new Lust(), new Wrath(), new Sloth(), new Envy(),
			new Greed(), new Gluttony() };
	public static final AbstractRelic[] RELICS = {new DuVuDoll(), new DarkstonePeriapt(), new BlueCandle()};
	
	public static final String SAVE_NAME = "preMaxHP";
	
	public static int preMaxHP;
    public static int dontCopy = 0;
    public static boolean isFull = true;
	public static boolean isSelect;
	
	public static int screenQueue = 0;
	
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
		return Stream.of(SINS).allMatch(this::has);
	}
	
	public void updateFull() {		
		if (checkFull() != isFull) {
			isFull = !isFull;
		}
	}
	
	private boolean has(AbstractCard card) {
		return p().masterDeck.group.stream().map(c -> c.cardID).anyMatch(card.cardID::equals);
	}
	
	private boolean hasGluttony() {
		return this.has(SINS[6]);
	}
	
	private void replicate() {
    	if (hasDarkstonePeriapt()) {
    		TestMod.info("玩家有黑石护符,下一次不复制暴食");
    		dontCopy = (int) p().relics.stream().filter(r -> r instanceof DarkstonePeriapt).count();
    	}
    	TestMod.info("开始获得暴食");
    	obtainCard(new Gluttony());
    	TestMod.info("获得暴食完毕");
	}
	
    private boolean hasDarkstonePeriapt() {
    	return p().hasRelic("Darkstone Periapt");
    }
    
    public void checkChangeMaxHP() {
    	if (p().maxHealth == preMaxHP)
    		return;
    	boolean increase = p().maxHealth > preMaxHP;
    	String tmp = "检测到血量上限变化,旧上限:" + preMaxHP + ",新上限:";
		preMaxHP = p().maxHealth;
		TestMod.info(tmp + preMaxHP);
    	if (increase && hasGluttony()) {
    		TestMod.info("拥有暴食");
    		if (dontCopy > 0) {
    			TestMod.info("暴食引起,不复制暴食");
    			dontCopy--;
			} else if ((p().hasRelic("Omamori"))
					&& (p().getRelic("Omamori").counter != 0)) {
				TestMod.info("御守有效,不复制暴食");
				((Omamori) p().getRelic("Omamori")).use();
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
		MISC.p().relics.forEach(r -> r.onObtainCard(c));
		UnlockTracker.markCardAsSeen(c.cardID);
		MISC.p().masterDeck.addToTop(c);
		MISC.addHoarderCard(MISC.p().masterDeck, c);
		MISC.p().relics.forEach(r -> r.onMasterDeckChange());
	}
	
	public static void equipAction() {
		TestMod.info("获得七原罪诅咒与遗物");
		AbstractTestRelic.setTryEquip(Sins.class, false);
		Stream.of(RELICS).forEach(r -> TestMod.obtain(MISC.p(), r, false));
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
		if (isFull && (room instanceof MonsterRoomBoss || room instanceof TreasureRoomBoss)) {
			beginLongPulse();
			if (this.isActive)
				Sins.isSelect = false;
		} else {
			stopPulse();
		}
		if (!isActive)
			return;
		this.save();
		this.updateFull();
    }

	public void onChestOpen(final boolean bossChest) {
		if (this.isActive) {
			this.save();
			this.updateFull();
		}
		if (isFull && bossChest) {
			stopPulse();
			if (this.isActive && !Sins.isSelect) {
				screenQueue = (int) this.relicStream(Sins.class).count() - 1;
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