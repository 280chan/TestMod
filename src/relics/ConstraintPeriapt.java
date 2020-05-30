package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import mymod.TestMod;
import utils.MiscMethods;

public class ConstraintPeriapt extends MyRelic implements MiscMethods {
	public static final String ID = "ConstraintPeriapt";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "战斗开始时，将 #b1 张 #y灼伤 洗入抽牌堆。回合结束时，不考虑能量因素，你手牌中每有一张不能被打出的牌，恢复 #b1 生命并对所有敌人造成 #b10 点伤害。";//遗物效果的文本描叙。
	
	public ConstraintPeriapt() {
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
	
	public void atPreBattle() {
		AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDrawPileAction(new Burn(), 1, true, true));
	}
	
	public void onRefreshHand() {
		this.updateHandGlow();
	}
	
	private void updateHandGlow() {
		boolean active = false;
		int preEnergy = EnergyPanel.totalCount;
		for (AbstractCard c : AbstractDungeon.player.hand.group) {
			EnergyPanel.totalCount = c.costForTurn;
			if (!this.checkPlayable(AbstractDungeon.player, c)) {
				active = true;
				break;
			}
		}
		EnergyPanel.totalCount = preEnergy;
		if (active)
			this.beginLongPulse();
		else
			this.stopPulse();
	}
	
	public void onPlayerEndTurn() {
		int amount = 0;
		int preEnergy = EnergyPanel.totalCount;
		AbstractPlayer p = AbstractDungeon.player;
		for (AbstractCard c : p.hand.group) {
			boolean canUse = false;
			EnergyPanel.totalCount = c.costForTurn;
			canUse = this.checkPlayable(p, c);
			EnergyPanel.totalCount = preEnergy;
			if (!canUse) {
				amount++;
			}
		}
		for (int i = 0; i < amount; i++) {
			p.heal(1);
			AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(p, DamageInfo.createDamageMatrix(10, true), DamageType.THORNS, AttackEffect.FIRE));
		}
		
    }
	
	private boolean checkPlayable(AbstractPlayer p, AbstractCard c) {
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters)
			if (c.canUse(p, m))
				return true;
		return false;
	}

	public void onVictory() {
		this.stopPulse();
	}
}