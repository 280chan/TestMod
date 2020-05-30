package relics;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import actions.DéjàvuAction;
import mymod.TestMod;

public class Déjàvu extends MyRelic {
	public static final String ID = "Déjàvu";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "如果你在回合内只出过一种类型的牌，且回合结束时没有能量剩余，下回合开始时将这些牌的复制品放入手牌，其耗能将在回合内变为 #b0 且打出时 #y消耗 。";
	
	private ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
	private boolean active = false;
	private boolean endTurn = false;
	
	public Déjàvu() {
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
	
	public AbstractRelic makeCopy() {
		return new Déjàvu();
	}//复制该遗物信息的方法。
	
	public void setState(boolean active) {
		this.active = active;
		if (active) {
			this.beginLongPulse();
		} else {
			this.stopPulse();
		}
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {//参数：c-使用的卡牌，m-目标敌人。
		if (this.endTurn) {
			return;
		}
		if (this.list.isEmpty()) {
			this.list.add(c);
			this.setState(true);
		} else if (c.type == this.list.get(0).type) {
			this.list.add(c);
		} else if (this.active) {
			this.setState(false);
		}
	}
	
	public void atTurnStart() {
		this.endTurn = false;
		if (this.active && !this.list.isEmpty()) {
			AbstractDungeon.actionManager.addToBottom(new DéjàvuAction(this, this.list));
		} else {
			this.list.clear();
			this.setState(false);
		}
    }
	
	public void onPlayerEndTurn() {
		this.endTurn = true;
		if (this.active && EnergyPanel.totalCount > 0)
			this.setState(false);
    }
	
	public void onVictory() {
		this.setState(false);
    }
	
}