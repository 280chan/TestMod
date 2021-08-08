package relics;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import mymod.TestMod;

public class Alchemist extends AbstractClickRelic {
	public static final String ID = "Alchemist";
	private boolean used = false;
	
	public Alchemist() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onEquip() {
		if (!TestMod.addPotionSlotMultiplayer()) {
			AbstractDungeon.player.potionSlots++;
			AbstractDungeon.player.potions.add(new PotionSlot(AbstractDungeon.player.potionSlots - 1));
		}
    }
	
	private void toggleState(boolean active) {
		if (used)
			return;
		if (active) {
			this.counter = -2;
			this.beginLongPulse();
		} else {
			this.counter = -1;
			this.stopPulse();
		}
	}
	
	private boolean canUse() {
		return this.counter == -2 && !used;
	}
	
	public void atPreBattle() {
		this.used = false;
		this.toggleState(true);
    }
	
	public void atTurnStart() {
		this.toggleState(true);
    }
	
	public void onPlayerEndTurn() {
		this.toggleState(false);
    }
	
	public void onEnterRoom(final AbstractRoom room) {
		this.toggleState(false);
	}
	
	public void onVictory() {
		this.toggleState(false);
    }
	
	@Override
	protected void onRightClick() {
		if (this.canUse()) {
			this.toggleState(false);
			this.used = true;
			AbstractPlayer p = AbstractDungeon.player;
			int index = 0;
			for (AbstractPotion po : p.potions) {
				if (!(po instanceof PotionSlot) && po.canUse()) {
					break;
				}
				index++;
			}
			if (index < p.potionSlots) {
				AbstractPotion po = p.potions.get(index);
				if (po.targetRequired) {
					po.use(AbstractDungeon.getCurrRoom().monsters.getRandomMonster(true));
				} else {
					po.use(null);
				}
				TestMod.info("炼金术士: 使用了" + po.name);
			} else {
				TestMod.info("炼金术士: 没有可使用药水");
				this.used = false;
				this.toggleState(true);
			}
		}
	}

	public boolean canSpawn() {
		return (Settings.isEndless) || (AbstractDungeon.floorNum <= 48);
	}
	
}