package potions;

import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.WeMeetAgain;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import mymod.TestMod;

public class SpacePotion extends AbstractTestPotion {
	public static final String POTION_ID = TestMod.makeID("SpacePotion");
	private static final PotionStrings PS = Strings(POTION_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;

	public SpacePotion() {
		super(NAME, POTION_ID, PotionRarity.UNCOMMON, PotionSize.T, PotionColor.NONE);
		this.isThrown = false;
	}

	public void initializeData() {
		this.potency = getPotency();
		this.description = this.getDesc();
		this.tips.clear();
		this.tips.add(new PowerTip(this.name, this.description));
	}

	public String getDesc() {
		return DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];
	}

	public void use(AbstractCreature target) {
		AbstractPlayer p = AbstractDungeon.player;
		int tmp = this.potency;
		while (tmp > 0) {
			tmp--;
			if (p.potionSlots < 10) {
				p.potionSlots++;
				p.potions.add(new PotionSlot(AbstractDungeon.player.potionSlots - 1));
			} else {
				this.addToBot(new ObtainPotionAction(AbstractDungeon.returnRandomPotion(true)));
			}
		}
	}

	public boolean canUse() {
		if ((AbstractDungeon.actionManager.turnHasEnded)
				&& (AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT)) {
			return false;
		}
		if ((AbstractDungeon.getCurrRoom().event != null)
				&& ((AbstractDungeon.getCurrRoom().event instanceof WeMeetAgain))) {
			return false;
		}
		return true;
	}
	
	public AbstractPotion makeCopy() {
		return new SpacePotion();
	}

	public int getPotency(int ascensionLevel) {
		return 1;
	}
}
