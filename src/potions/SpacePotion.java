package potions;

import com.megacrit.cardcrawl.actions.common.ObtainPotionAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.shrines.WeMeetAgain;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import mymod.TestMod;

public class SpacePotion extends AbstractPotion {
	public static final String POTION_ID = TestMod.makeID("SpacePotion");
	public static final String NAME = "空间药水";
	public static final String[] DESCRIPTIONS = { "使用后永久增加 #b", " 个药水栏位。超过 #b10 个栏位的部分改为获得相同数量瓶随机药水。" };

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
				AbstractDungeon.actionManager.addToBottom(new ObtainPotionAction(AbstractDungeon.returnRandomPotion(true)));
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
