package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import mymod.TestMod;
import utils.MiscMethods;

public class DeterminationOfClimber extends MyRelic implements MiscMethods {
	public static final String ID = "DeterminationOfClimber";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "在每场战斗内，每当你打出的牌的耗能比前一张牌高时，每高出 #b1 点，获得 #b1 能量，回复 #b1 生命，抽 #b1 张牌，对所有敌人造成 #b1 伤害，获得 #b1 金币。";
	
	private static Color color = null;
	
	public DeterminationOfClimber() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		if (AbstractDungeon.player == null)
			return setDescription(null);
		return setDescription(AbstractDungeon.player.chosenClass);
	}
	
	private String setDescription(PlayerClass c) {
		return this.setDescription(c, this.DESCRIPTIONS[0], this.DESCRIPTIONS[1]);
	}

	public void updateDescription(PlayerClass c) {
		this.description = this.setDescription(c);
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.description));
	    initializeTips();
	}
	
	public void atPreBattle() {
		this.counter = -1;
	}
	
	public void act(int count) {
		AbstractCreature p = AbstractDungeon.player;
		AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(count));
		AbstractDungeon.actionManager.addToBottom(new HealAction(p, p, count));
		AbstractDungeon.actionManager.addToBottom(new DrawCardAction(p, count));
		AbstractDungeon.actionManager.addToBottom(new AbstractGameAction(){
			@Override
			public void update() {
				AbstractDungeon.player.gainGold(count);
				this.isDone = true;
			}
		});
		AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, DamageInfo.createDamageMatrix(count, true), DamageType.THORNS, AttackEffect.BLUNT_LIGHT));
		this.show();
	}
	
	private int getValue(AbstractCard c) {
		if (c.freeToPlayOnce || c.cost == -2)
			return 0;
		if (c.cost == -1)
			return EnergyPanel.totalCount;
		return c.costForTurn;
	}
	
	public void onUseCard(final AbstractCard c, final UseCardAction action) {
		if (!c.isInAutoplay) {
			if (this.counter > -1) {
				int amount = this.getValue(c);
				if (amount > this.counter)
					this.act(amount - this.counter);
			}
			this.counter = this.getValue(c);
		}
	}
	
	public void onRefreshHand() {
		if (color == null)
			color = this.initGlowColor();
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			if (this.getValue(c) > this.counter && this.counter != -1 && c.hasEnoughEnergy() && c.cardPlayable(AbstractDungeon.getRandomMonster())) {
				this.addToGlowChangerList(c, color);
				active = true;
			} else
				this.removeFromGlowList(c, color);
		}
		if (active)
			this.beginLongPulse();
		else
			this.stopPulse();
	}

	public void onVictory() {
		this.stopPulse();
	}
}