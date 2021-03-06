package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
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

public class D_4 extends AbstractTestRelic implements MiscMethods {
	public static final String ID = "D_4";
	private static Situation nextSituation = null;
	private static final Situation[] SITUATIONS = Situation.values();
	private static final int LENGTH = SITUATIONS.length;
	private Random rng = null;
	
	public static enum Situation {
		EXHAUST, REGAIN, DUALPLAY, NIGHTMARE;
		private Situation() {
		}
		public String toString() {
			return D_4Power.getString(this);
		}
	}
	
	public D_4() {
		super(ID, RelicTier.SPECIAL, LandingSound.MAGICAL);
	}
	
	public String getUpdatedDescription() {
		if (!this.isObtained)
			return DESCRIPTIONS[0];
		if (AbstractDungeon.currMapNode == null || AbstractDungeon.getCurrRoom().phase != RoomPhase.COMBAT)
			return DESCRIPTIONS[0];
		return DESCRIPTIONS[1] + nextSituation;
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
		this.addToBot(new GainEnergyAction(cost));
	}
	
	private void exhaust(AbstractCard card, UseCardAction action) {
		card.exhaustOnUseOnce = true;
		action.exhaustCard = true;
	}
	
	private void nightmare(AbstractCard c) {
		AbstractPlayer p = AbstractDungeon.player;
		this.addToBot(new ApplyPowerAction(p, p, new NightmarePower(p, 3, c)));
	}
	
	public void onUseCard(final AbstractCard card, final UseCardAction action) {
		if (!card.isInAutoplay && isActive) {
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
	}
	
	private int getRoll(int n) {
		return this.rng.random(n - 1);
	}
	
	private void setNextSituation() {
		nextSituation = SITUATIONS[getRoll(LENGTH)];
		this.tryUpdatePower();
		this.updateDescription(AbstractDungeon.player.chosenClass);
	}
	
	public void atPreBattle() {
		if (!isActive)
			return;
		this.rng = this.copyRNG(AbstractDungeon.miscRng);
		this.init();
    }
	
	public void onVictory() {
		if (!isActive)
			return;
		nextSituation = null;
		this.updateDescription(AbstractDungeon.player.chosenClass);
	}
	
	private void init() {
		this.setNextSituation();
	}
	
	private void tryUpdatePower() {
		AbstractPlayer p = AbstractDungeon.player;
		if (!D_4Power.hasThis(p)) {
			TestMod.info(this.name + ": Init or got fucked");
			p.powers.add(new D_4Power(p, nextSituation));
		} else {
			boolean changed = false;
			for (int i = 0; i < p.powers.size(); i++) {
				AbstractPower power = p.powers.get(i);
				if (power instanceof D_4Power) {
					if (!changed) {
						changed = true;
						p.powers.set(i, new D_4Power(p, nextSituation));
					} else {
						TestMod.info(this.name + ": WTF Dupe Power");
						p.powers.remove(i--);
					}
				}
			}
		}
	}
	
}