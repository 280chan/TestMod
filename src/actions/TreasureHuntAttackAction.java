package actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class TreasureHuntAttackAction extends AbstractGameAction {
	private DamageInfo info;
	private static final float DURATION = 0.1F;
	private boolean upgraded;

	public TreasureHuntAttackAction(AbstractCreature target, DamageInfo info, boolean upgraded) {
		this.info = info;
		setValues(target, info);
		this.actionType = AbstractGameAction.ActionType.DAMAGE;
		this.duration = DURATION;
		this.upgraded = upgraded;
	}

	@Override
	public void update() {
		if ((this.duration == DURATION) && (this.target != null)) {
			AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY,
					AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

			this.target.damage(this.info);
			boolean toDeck = (this.target.isDying || this.target.currentHealth <= 0) && (!this.target.halfDead);
			boolean toHand = !AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead();
			
			AbstractDungeon.actionManager.addToTop(new TreasureHuntAction(toHand, toDeck && upgraded));
		}
		tickDuration();
	}

}
