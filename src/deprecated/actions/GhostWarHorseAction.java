package deprecated.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.WraithFormPower;

/**
 * @deprecated
 */
public class GhostWarHorseAction extends AbstractGameAction {
	private static final float DURATION = Settings.ACTION_DUR_FAST;
	private CardType cardType;
	
	public GhostWarHorseAction(AbstractCard cardPlayed, int amount) {
		this.actionType = ActionType.SPECIAL;
		this.duration = DURATION;
		this.amount = amount;
		cardType = cardPlayed.type;
		if (cardType != CardType.ATTACK && cardType != CardType.POWER && cardType != CardType.SKILL)
			this.isDone = true;
	}

	@Override
	public void update() {
		if (this.duration == DURATION) {
			AbstractPlayer player = AbstractDungeon.player;
			switch (this.cardType) {
			case POWER:
				if (player.hasPower("Wraith Form v2")) {
					AbstractPower p = player.getPower("Wraith Form v2");
					p.amount--;
					p.updateDescription();
				} else {
					player.powers.add(new WraithFormPower(player, -1));
				}
				break;
			case ATTACK:
				int amt = this.amount;
				if (player.hasPower("Wraith Form v2")) {
					AbstractPower p = player.getPower("Wraith Form v2");
					amt -= p.amount;
				}
				if (amt > 0) {
					AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(player, player, new DexterityPower(player, amt), amt));
				}
				break;
			case SKILL:
				if (player.hasPower("Wraith Form v2")) {
					AbstractPower p = player.getPower("Wraith Form v2");
					p.amount++;
					if (p.amount == 0)
						AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(player, player, p));
					else
						p.updateDescription();
					AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(player, player, new DexterityPower(player, -1), -1));
				}
				break;
			default:
				break;
			}
		}
		this.isDone = true;
	}

}
