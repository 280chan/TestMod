package testmod.relicsup;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.PotionPopUp;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import basemod.ReflectionHacks;
import testmod.mymod.TestMod;

public class AlchemistUp extends AbstractUpgradedRelic implements ClickableRelic {
	private boolean used = false;
	private static boolean target = false, regain = false;
	private static int index = -1;
	private static AlchemistUp current = null;
	
	public AlchemistUp() {
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
		this.toggleState(!(this.used = target = false));
    }
	
	public void atTurnStart() {
		this.toggleState(!(this.used = target = false));
    }
	
	public void onPlayerEndTurn() {
		this.toggleState(false);
    }
	
	public void onEnterRoom(final AbstractRoom room) {
		this.toggleState(false);
	}
	
	public void onVictory() {
		this.toggleState(false);
		target = false;
    }
	
	private Stream<AbstractPotion> potions() {
		return p().potions.stream().filter(po -> !(po instanceof PotionSlot));
	}
	
	@Override
	public void onRightClick() {
		if (this.canUse()) {
			AbstractPotion p = potions().filter(po -> po.canUse()).findFirst().orElse(null);
			if (p != null) {
				if (p.targetRequired) {
					if (!this.hasEnemies()) {
						TestMod.info("炼金术士+: 没有可使用目标");
						return;
					}
					openPopUp(AbstractDungeon.topPanel.potionUi, p);
				} else {
					p.use(null);
					p().relics.forEach(r -> r.onUsePotion());
					this.pretendUse(p);
				}
			} else {
				TestMod.info("炼金术士+: 没有可使用药水");
			}
		} else if (this.canSwap() && p().potionSlots > 1 && potions().count() > 0) {
			ArrayList<AbstractPotion> l = potions().skip(1).collect(toArrayList());
			l.add(potions().findFirst().get());
			p().potions.stream().filter(po -> po instanceof PotionSlot).forEach(l::add);
			p().potions.clear();
			p().potions = l;
			for (int i = 0; i < p().potions.size(); i++)
				p().potions.get(i).setAsObtained(i);
			TestMod.info("炼金术士+: 交换了药水排序");
		}
	}
	
	private void pretendUse(AbstractPotion p) {
		TestMod.info("炼金术士+: 使用了" + p.name);
		this.toggleState(false);
		this.used = true;
	}

	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.floorNum <= 48;
	}
	
	private void openPopUp(PotionPopUp ui, AbstractPotion p) {
		ui.open(index = p.slot, p);
		ui.isHidden = ui.targetMode = target = true;
		current = this;
	}
	
	private void regainUse() {
		this.used = target = false;
		this.toggleState(regain = true);
	}
	
	@SpirePatch(clz = TopPanel.class, method = "destroyPotion")
	public static class TopPanelPatch {
		@SpirePrefixPatch()
		public static SpireReturn<Void> Prefix(TopPanel p, int slot) {
			return target && slot == index ? SpireReturn.Return() : SpireReturn.Continue();
		}
	}
	
	@SpirePatch(clz = PotionPopUp.class, method = "updateTargetMode")
	public static class PotionPopUpPatch {
		@SpireInsertPatch(rloc = 4)
		public static void Insert(PotionPopUp ui) {
			if (target) {
				current.regainUse();
			}
		}
		@SpirePostfixPatch()
		public static void Postfix(PotionPopUp ui) {
			if (regain) {
				index = -1;
				regain = false;
				return;
			}
			if (target && !ui.targetMode) {
				current.pretendUse(ReflectionHacks.getPrivate(ui, PotionPopUp.class, "potion"));
			}
			index = (target &= ui.targetMode) ? index : -1;
		}
	}
	
}