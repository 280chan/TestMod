package actions;

import java.util.UUID;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import utils.MiscMethods;

public class HandmadeProductsAttackAction extends AbstractGameAction implements MiscMethods {
	private DamageInfo info;
	private static final float DURATION = 0.1F;
	private UUID uuid;

	public HandmadeProductsAttackAction(AbstractCreature target, DamageInfo info, int amount, UUID uuid) {
		this.info = info;
		this.setValues(target, info);
		this.amount = amount;
		this.actionType = ActionType.DAMAGE;
		this.duration = DURATION;
		this.uuid = uuid;
	}

	private void modify(AbstractCard c) {
		if (c.cost > -1)
			c.updateCost(this.amount);
		c.applyPowers();
		c.baseDamage = 0;
	}
	
	@Override
	public void update() {
		if ((this.duration == DURATION) && (this.target != null)) {
			AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY,
					AbstractGameAction.AttackEffect.SLASH_DIAGONAL));

			this.target.damage(this.info);
			if (((this.target.isDying) || (this.target.currentHealth <= 0)) && (!this.target.halfDead)) {
				for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
					if (c.uuid.equals(this.uuid)) {
						this.modify(c);
					}
				}
				for (AbstractCard c : GetAllInBattleInstances.get(this.uuid)) {
					this.modify(c);
				}
			}
			if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
				AbstractDungeon.actionManager.clearPostCombatActions();
			}
		}
		tickDuration();
	}

}
