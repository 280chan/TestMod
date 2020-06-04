package relics;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;

import mymod.TestMod;

public class Faith extends MyRelic {
	public static final String ID = "Faith";
	
	private boolean gained = false;
	private static final int TMP_GOLD = 1000000;
	private int preGold;
	
	public static void reset() {
		TestMod.saveVariable("FaithGained", false);
		TestMod.saveVariable("FaithPreGold", 0);
	}
	
	private void save() {
		TestMod.saveVariable("FaithGained", this.gained);
		TestMod.saveVariable("FaithPreGold", this.preGold);
	}
	
	public static void load(boolean gained, int preGold) {
		if (AbstractDungeon.player.hasRelic(TestMod.makeID(ID))) {
			((Faith) (AbstractDungeon.player.getRelic(TestMod.makeID(ID)))).gained = gained;
			((Faith) (AbstractDungeon.player.getRelic(TestMod.makeID(ID)))).preGold = preGold;
		}
	}
	
	public Faith() {
		super(ID, RelicTier.SHOP, LandingSound.CLINK);
		this.counter = 0;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	private void damage(AbstractPlayer p) {
		p.damage(new DamageInfo(p, Math.max(Math.max(p.currentHealth / 4, this.counter / 10), 1), DamageType.HP_LOSS));
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!this.isActive)
			return;
		if (AbstractDungeon.floorNum > 0 && AbstractDungeon.getCurrRoom() instanceof ShopRoom) {
			this.gainGold(AbstractDungeon.player);
		}
	}
	
	public void onUnequip() {
		if (!this.isActive)
			return;
		AbstractPlayer p = AbstractDungeon.player;
		if (this.gained) {
			this.gained = false;
			p.gold = this.preGold;
			p.displayGold = this.preGold;
		}
	}
	
	public void onLoseGold() {
		if (this.gained && this.isActive) {
			AbstractPlayer p = AbstractDungeon.player;
			this.counter += this.preGold + TMP_GOLD - p.gold;
			this.gained = false;
			p.gold = this.preGold;
			p.displayGold = this.preGold;
			
			this.stopPulse();
			this.flash();
		}
    }
	
	private void gainGold(AbstractPlayer p) {
		this.preGold = p.gold;
		p.gold += TMP_GOLD;
		p.displayGold += TMP_GOLD;
		this.gained = true;
		
		this.beginLongPulse();
	}
	
	private void gainGoldCheck(AbstractPlayer p, AbstractRoom room) {
		if (this.counter > 0) {
			if (this.counter > p.gold) {
				this.counter -= p.gold;
				p.gold = 0;
				this.damage(p);
				this.flash();
			} else {
				p.gold -= this.counter;
				this.counter = 0;
			}
		}

		this.gainGold(p);
	}
	
	public void justEnteredRoom(final AbstractRoom room) {
		if (!this.isActive)
			return;
		this.save();
		AbstractPlayer p = AbstractDungeon.player;
		if (this.gained) {
			this.gained = false;
			p.gold = this.preGold;
			p.displayGold = this.preGold;
		}
		if (room instanceof ShopRoom) {
			this.gainGoldCheck(p, room);
		} else {
			this.stopPulse();
		}
	}
	
	public boolean canSpawn() {
		if (!Settings.isEndless && AbstractDungeon.actNum > 1) {
			return false;
		}
		return true;
	}
}