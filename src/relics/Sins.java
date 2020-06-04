package relics;

import java.util.Iterator;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.CardCrawlGame.GameMode;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.BlueCandle;
import com.megacrit.cardcrawl.relics.DarkstonePeriapt;
import com.megacrit.cardcrawl.relics.DuVuDoll;
import com.megacrit.cardcrawl.relics.Omamori;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import cards.curse.Envy;
import cards.curse.Gluttony;
import cards.curse.Greed;
import cards.curse.Lust;
import cards.curse.Sloth;
import cards.curse.Wrath;
import mymod.TestMod;
import utils.BossRelicSelectScreen;
import utils.MiscMethods;

public class Sins extends MyRelic implements MiscMethods {
	public static final String ID = "SevenDeadlySins";
	
	public static final AbstractCard[] SINS = {new Pride(), new Lust(), new Wrath(), new Sloth(), new Envy(), new Greed(), new Gluttony()};
	public static final AbstractRelic[] RELICS = {new DuVuDoll(), new DarkstonePeriapt(), new BlueCandle()};
	
	public static int preMaxHP;
    public static boolean dontCopy = false;
    public static boolean isFull = true;
	public static boolean isSelect;
    
	private static final String RELIC_SELECT_BDESC = "请选择达成七原罪任务的奖励。";
	private static final String RELIC_SELECT_TITLE = "七原罪:";
	private static final String RELIC_SELECT_INFO = "请勇士选择通过试炼的奖励。";
	
    private void save() {
    	TestMod.saveVariable("preMaxHP", preMaxHP);
    }
    
	public Sins() {
		super(ID, RelicTier.SPECIAL, LandingSound.HEAVY);
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
    		System.out.println("玩家有黑石护符,下一次不复制暴食");
    		dontCopy = true;
    	}
    	System.out.println("开始获得暴食");
    	obtainCard(new Gluttony());
    	System.out.println("获得暴食完毕");
	}
	
    private boolean hasDarkstonePeriapt() {
    	return AbstractDungeon.player.hasRelic("Darkstone Periapt");
    }
    
    public void checkChangeMaxHP() {
    	if (AbstractDungeon.player.maxHealth == preMaxHP)
    		return;
    	boolean increase = AbstractDungeon.player.maxHealth > preMaxHP;
		System.out.print("检测到血量上限变化,旧上限:" + preMaxHP + ",新上限:");
		preMaxHP = AbstractDungeon.player.maxHealth;
		System.out.println(preMaxHP);
    	if (increase && hasGluttony()) {
			System.out.println("拥有暴食");
    		if (dontCopy) {
    			System.out.println("暴食引起,不复制暴食");
    			dontCopy = false;
			} else if ((AbstractDungeon.player.hasRelic("Omamori")) && (AbstractDungeon.player.getRelic("Omamori").counter != 0)) {
    			System.out.println("御守有效,不复制暴食");
				((Omamori) AbstractDungeon.player.getRelic("Omamori")).use();
			} else {
    			System.out.println("准备复制暴食");
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
	
	private void obtainCard(AbstractCard c) {
		Iterator<AbstractRelic> var1 = AbstractDungeon.player.relics.iterator();
		AbstractRelic r;
		while (var1.hasNext()) {
			r = (AbstractRelic) var1.next();
			r.onObtainCard(c);
		}
		
		UnlockTracker.markCardAsSeen(c.cardID);
		CardGroup group = AbstractDungeon.player.masterDeck;
		group.addToTop(c);
		this.addHoarderCard(group, c);
		
		var1 = AbstractDungeon.player.relics.iterator();
		while (var1.hasNext()) {
			r = (AbstractRelic) var1.next();
			r.onMasterDeckChange();
		}
	}
	
	public static void equipAction() {
		System.out.println("获得七原罪诅咒与遗物");
		MyRelic.setTryEquip(Sins.class, false);
		for (AbstractRelic r : RELICS) {
			TestMod.obtain(AbstractDungeon.player, r, false);
		}
		for (AbstractCard c : SINS) {
			new Sins().obtainCard(c.makeCopy());
		}
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!isActive)
			return;
		this.setTryEquip(true);
		this.checkChangeMaxHP();
    }
	
	public static boolean isSin(AbstractCard c) {
		switch (c.cardID) {
		case Pride.ID:
		case Lust.ID:
		case Wrath.ID:
		case Sloth.ID:
		case Envy.ID:
		case Greed.ID:
		case Gluttony.ID:
			return true;
		default:
			return false;
		}
	}
	
	public void onUnequip() {
		if (!isActive)
			return;
		this.setTryUnequip(true);
	}
	
	public static void unequipAction() {
		MyRelic.setTryUnequip(Sins.class, false);
		for (int i = 0; i < AbstractDungeon.player.masterDeck.group.size(); i++) {
			AbstractCard c = AbstractDungeon.player.masterDeck.group.get(i);
			if (isSin(c)) {
				AbstractDungeon.player.masterDeck.group.remove(c);
				i--;
				System.out.println("七原罪: 移除" + c.name);
			}
		}
		for (AbstractRelic r : RELICS) {
			AbstractDungeon.player.loseRelic(r.relicId);
			System.out.println("七原罪: 移除" + r.name);
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
				new BossRelicSelectScreen(true, RELIC_SELECT_BDESC, RELIC_SELECT_TITLE, RELIC_SELECT_INFO).open();;
			}
		}
	}
	
	public static boolean isObtained() {
		if (CardCrawlGame.mode != GameMode.GAMEPLAY && CardCrawlGame.mode != GameMode.DUNGEON_TRANSITION)
			return true;
		return AbstractDungeon.player.hasRelic(ID);
	}
	
	public static AbstractCard copyCurse() {
		AbstractCard c = AbstractDungeon.getCard(CardRarity.CURSE);
		if (isSin(c)) {
			return copyCurse();
		}
		return c;
	}

}