package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.Omamori;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

public class Laevatain extends AbstractTestRelic {
	public static final String ID = "Laevatain";
	
	public Laevatain() {
		super(ID, RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onEquip() {
		for (AbstractCard c : Sins.SINS) {
			AbstractDungeon.curseCardPool.removeCard(c.cardID);
		}
		CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
		for (int i = 0; i < 3; i++) {
			if ((AbstractDungeon.player.hasRelic("Omamori"))
					&& (AbstractDungeon.player.getRelic("Omamori").counter != 0)) {
				((Omamori) AbstractDungeon.player.getRelic("Omamori")).use();
			} else {
				AbstractCard curse = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
				UnlockTracker.markCardAsSeen(curse.cardID);
				group.addToBottom(curse.makeCopy());
			}
		}
	    AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, "魔剑侵袭...");
    }
	
	public void atPreBattle() {
		this.counter = 0;
		this.show();
		applyStrength(3);
	}
	
	private int countCurse() {
		int count = 0;
		for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
			if (c.type == CardType.CURSE)
				count++;
		return count;
	}
	
	private void applyStrength(int amount) {
		this.addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, amount), amount));
	}
	
	public void atTurnStart() {
		this.counter++;
		if (this.counter == 3) {
			this.counter = 0;
			if (countCurse() != 0) {
				applyStrength(countCurse());
				this.show();
			}
		}
    }
	
	public void onVictory() {
		this.counter = -1;
    }
	
}