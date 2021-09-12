package relics;

import java.util.function.Predicate;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import utils.MiscMethods;

public class EqualTreatment extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "EqualTreatment";
	
	private static Color color = null;
	
	public EqualTreatment() {
		super(ID, RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	public void atTurnStart() {
		this.changeState(true);
	}
	
	private void changeState(boolean state) {
		if (state) {
			this.beginLongPulse();
			this.counter = -2;
		} else {
			this.stopPulse();
			this.counter = -1;
		}
	}
	
	private boolean alive(AbstractMonster m) {
		return !(m.isDead || m.isDying || m.halfDead || m.isEscaping);
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (c.target == CardTarget.ENEMY && this.counter == -2) {
			AbstractDungeon.getCurrRoom().monsters.monsters.stream().filter(this::alive)
					.filter(((Predicate<AbstractMonster>) action.target::equals).negate()).forEach(m -> {
						c.calculateCardDamage(m);
						c.use(AbstractDungeon.player, m);
					});
			this.changeState(false);
			this.show();
		}
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		ColorRegister cr = new ColorRegister(color);
		this.streamIfElse(AbstractDungeon.player.hand.group.stream(),
				c -> c.target == CardTarget.ENEMY && this.counter == -2, cr::addToGlowChangerList,
				cr::removeFromGlowList);
	}
	
	public void onVictory() {
		this.changeState(false);
	}

}