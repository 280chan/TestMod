package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import mymod.TestMod;
import utils.MiscMethods;

public class EqualTreatment extends MyRelic implements MiscMethods {
	public static final String ID = "EqualTreatment";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "你在每回合打出的第一张指向单个敌人的牌，也会对其余所有敌人各生效一次。";//遗物效果的文本描叙。
	
	private static Color color = null;
	
	public EqualTreatment() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.MAGICAL);
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
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (c.target == CardTarget.ENEMY && this.counter == -2) {
			for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
				if (m.isDead || m.isDying || m.halfDead || m.isEscaping)
					continue;
				if (m.equals(action.target))
					continue;
				c.calculateCardDamage(m);
				c.use(AbstractDungeon.player, m);
			}
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
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (c.target == CardTarget.ENEMY && this.counter == -2) {
				this.addToGlowChangerList(c, color);
			} else
				this.removeFromGlowList(c, color);
		}
	}
	
	public void onVictory() {
		this.changeState(false);
	}

}