package testmod.potions;

import java.util.function.Consumer;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.WeMeetAgain;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import testmod.mymod.TestMod;
import testmod.relics.Alchemist;
import testmod.relicsup.AlchemistUp;

public class SpacePotion extends AbstractTestPotion {
	public static final String POTION_ID = TestMod.makeID("SpacePotion");
	private static final PotionStrings PS = Strings(POTION_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;

	public SpacePotion() {
		super(NAME, POTION_ID, PotionRarity.UNCOMMON, PotionSize.T, PotionColor.NONE);
		this.isThrown = false;
	}
	
	public String getDesc() {
		return DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];
	}
	
	private static class RandomPotionAction extends AbstractGameAction {
		private Consumer<Integer> action;
		public RandomPotionAction(int amount, Consumer<Integer> action) {
			this.amount = amount;
			this.action = action;
		}

		@Override
		public void update() {
			this.isDone = true;
			this.action.accept(this.amount);
		}
	}

	public void use(AbstractCreature target) {
		int tmp = this.potency;
		while (tmp > 0) {
			tmp--;
			if (p().potionSlots < 10 || this.relicStream(Alchemist.class).count() > 0
					|| this.relicStream(AlchemistUp.class).count() > 0) {
				if (!TestMod.addPotionSlotMultiplayer()) {
					p().potionSlots++;
					p().potions.add(new PotionSlot(p().potionSlots - 1));
				}
			} else if (p().hasRelic("Sozu")) {
				p().getRelic("Sozu").flash();
				break;
			} else {
				this.atb(new RandomPotionAction(tmp + 1, t -> {
					while (t > 0 && p().potions.stream().anyMatch(s -> s instanceof PotionSlot)) {
						t--;
						p().obtainPotion(AbstractDungeon.returnRandomPotion(true));
					}
				}));
				break;
			}
		}
	}

	public boolean canUse() {
		if (AbstractDungeon.currMapNode == null)
			return true;
		if (AbstractDungeon.actionManager.turnHasEnded && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT)
			return false;
		if (AbstractDungeon.getCurrRoom().event != null && AbstractDungeon.getCurrRoom().event instanceof WeMeetAgain)
			return false;
		return true;
	}

	public int getPotency(int ascensionLevel) {
		return 1;
	}
	
}
