package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.NightmarePower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import mymod.TestMod;
import powers.D_4Power;
import utils.MiscMethods;

public class D_4 extends MyRelic implements MiscMethods {
	
	public static final String ID = "D_4";
	public static final String IMG = TestMod.relicIMGPath(ID);
	
	public static final String DESCRIPTION = "每当你打出一张牌时，将随机触发以下 #b4 种效果之一。将这张牌 #y消耗 ，回复其消费的能量，额外打出一次， #y夜魇 这张牌。你可以提前知道接下来一张牌将会触发的效果。";//遗物效果的文本描叙。
	
	public static Situation nextSituation = null;
	private static final Situation[] SITUATIONS = {Situation.EXHAUST, Situation.REGAIN, Situation.DUALPLAY, Situation.NIGHTMARE};
	private static final int LENGTH = SITUATIONS.length;
	private Random rng = null;
	
	public static enum Situation {
		EXHAUST, REGAIN, DUALPLAY, NIGHTMARE;
		private Situation() {
		}
		public String toString() {
			switch (this) {
			case NIGHTMARE:
				return " #y夜魇 这张牌";
			case DUALPLAY:
				return "额外打出一次";
			case EXHAUST:
				return "将这张牌 #y消耗 ";
			case REGAIN:
				return "回复消费的 #y能量 ";
			default:
				return "为什么会出错？？？";
			}
		}
	}
	
	public D_4() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.BOSS, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		if (!this.isObtained)
			return DESCRIPTIONS[0];
		if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT)
			return DESCRIPTIONS[0];
		return DESCRIPTIONS[1] + " NL 下一张牌: NL " + nextSituation;
	}
	
	public void updateDescription(AbstractPlayer.PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, getUpdatedDescription()));
	    if (nextSituation == Situation.EXHAUST)
	    	this.initializeTips();
	}
	
	private void dualPlay(AbstractCard card, AbstractMonster m) {
		this.playAgain(card, m);
	}
	
	private void regain(AbstractCard card) {
		int cost = card.costForTurn;
		if (cost == -1)
			cost = card.energyOnUse;
		else if (cost == -2)
			cost = 0;
		AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(cost));
	}
	
	private void exhaust(AbstractCard card, UseCardAction action) {
		card.exhaustOnUseOnce = true;
		action.exhaustCard = true;
	}
	
	private void nightmare(AbstractCard c) {
		AbstractPlayer p = AbstractDungeon.player;
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new NightmarePower(p, 3, c)));
	}
	
	public void onUseCard(final AbstractCard card, final UseCardAction action) {
		if (!card.purgeOnUse && isActive) {
			AbstractMonster m = null;
			if (action.target != null) {
				m = (AbstractMonster) action.target;
			}
			this.show();
			switch (nextSituation) {
			case DUALPLAY:
				dualPlay(card, m);
				break;
			case EXHAUST:
				exhaust(card, action);
				break;
			case REGAIN:
				regain(card);
				break;
			case NIGHTMARE:
				nightmare(card);
				break;
			default:
				break;
			}
			this.setNextSituation();
		}
	}//触发时机：当一张卡被打出且卡牌效果生效后。(参考死灵之书)
	//targetCard.可调用卡牌信息,比如稀有度、费用。
	
	private int getRoll(int n) {
		return this.rng.random(n - 1);
	}
	
	private void setNextSituation() {
		nextSituation = SITUATIONS[getRoll(LENGTH)];
		this.tryRemove();
		this.updateDescription(AbstractDungeon.player.chosenClass);
	}
	
	public void atPreBattle() {
		if (!isActive) {
			return;
		}
		this.rng = this.copyRNG(AbstractDungeon.miscRng);
		this.init();
    }//触发时机：每一场战斗（具体作用时机未知）
	
	private void init() {
		AbstractPlayer p = AbstractDungeon.player;
		this.setNextSituation();
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new D_4Power(p, nextSituation)));
	}
	
	private void tryRemove() {
		AbstractPlayer p = AbstractDungeon.player;
		for (AbstractPower power : p.powers) {
			if (power instanceof D_4Power) {
				AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(p, p, power.ID));
			}
		}
	}
	
}