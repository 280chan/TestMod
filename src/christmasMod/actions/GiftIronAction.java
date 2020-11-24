package christmasMod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import christmasMod.mymod.ChristmasMod;

public class GiftIronAction extends AbstractGameAction {
	private DamageInfo info;
	private static final float DURATION = 0.1F;

	public GiftIronAction(AbstractCreature target, DamageInfo info, int amount) {
		this.info = info;
		setValues(target, info);
		this.actionType = ActionType.DAMAGE;
		this.duration = DURATION;
		this.amount = amount;
	}

	@Override
	public void update() {
		if ((this.duration == DURATION) && (this.target != null)) {
			AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY,
					AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
			if (!(this.target.isDead || this.target.isDying || this.target.isEscaping)) {
				this.target.damage(this.info);
				if (this.target.isDying || this.target.currentHealth <= 0) {
					for (int i = 0; i < this.amount; i++)
						AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(ChristmasMod.randomGift(true)));
				}
			}
		} else {
			System.out.println("目标为null???");
		}
		this.isDone = true;
	}

}
