package relics;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import mymod.TestMod;

public class Alchemist extends AbstractTestRelic implements ClickableRelic {
	private boolean used = false;
	
	public Alchemist() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public void onEquip() {
		if (!TestMod.addPotionSlotMultiplayer()) {
			p().potionSlots++;
			p().potions.add(new PotionSlot(p().potionSlots - 1));
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
	
	private boolean canSwap() {
		return !this.inCombat();
	}
	
	public void atPreBattle() {
		this.toggleState(!(this.used = false));
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
	
	private Stream<AbstractPotion> potions() {
		return p().potions.stream().filter(po -> !(po instanceof PotionSlot));
	}
	
	@Override
	public void onRightClick() {
		if (this.canUse()) {
			AbstractPotion p = potions().filter(po -> po.canUse()).findFirst().orElse(null);
			if (p != null) {
				p.use(p.targetRequired ? AbstractDungeon.getRandomMonster() : null);
				TestMod.info("炼金术士: 使用了" + p.name);
				this.toggleState(false);
				this.used = true;
			} else {
				TestMod.info("炼金术士: 没有可使用药水");
			}
		} else if (this.canSwap() && p().potionSlots > 1 && potions().count() > 0) {
			ArrayList<AbstractPotion> l = potions().skip(1).collect(toArrayList());
			l.add(potions().findFirst().get());
			this.getNumberList(l.size(), p().potionSlots).forEach(i -> l.add(new PotionSlot(i)));
			p().potions = l;
			p().adjustPotionPositions();
			TestMod.info("炼金术士: 交换了药水排序");
		}
	}

	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.floorNum <= 48;
	}
	
}