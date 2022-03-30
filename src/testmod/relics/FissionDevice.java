package testmod.relics;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

public class FissionDevice extends AbstractTestRelic implements ClickableRelic {
	private boolean playerTurn = false;
	
	public FissionDevice() {
		super(RelicTier.COMMON, LandingSound.CLINK);
	}
	
	private boolean check(AbstractCard c) {
		return c.cost > -1 && c.costForTurn > 1 && !c.freeToPlayOnce;
	}
	
	private AbstractCard devide(AbstractCard c) {
		AbstractCard r = c.makeStatEquivalentCopy();
		r.baseBlock = c.baseBlock == -1 ? -1 : c.baseBlock / c.costForTurn;
		r.baseDamage = c.baseDamage == -1 ? -1 : c.baseDamage / c.costForTurn;
		r.baseMagicNumber = c.baseMagicNumber == -1 ? -1 : c.baseMagicNumber / c.costForTurn;
		r.magicNumber = c.magicNumber == -1 ? -1 : c.magicNumber / c.costForTurn;
		if (r.cost != 1)
			r.isCostModified = true;
		r.setCostForTurn(r.cost = 1);
		r.exhaust = r.exhaustOnUseOnce = true;
		return r;
	}
	
	private boolean not(int input) {
		return input == -1 || input == 0;
	}
	
	private AbstractCard remainder(AbstractCard c) {
		AbstractCard r = c.makeStatEquivalentCopy();
		r.baseBlock = c.baseBlock == -1 ? -1 : c.baseBlock % c.costForTurn;
		r.baseDamage = c.baseDamage == -1 ? -1 : c.baseDamage % c.costForTurn;
		r.baseMagicNumber = c.baseMagicNumber == -1 ? -1 : c.baseMagicNumber % c.costForTurn;
		r.magicNumber = c.magicNumber == -1 ? -1 : c.magicNumber % c.costForTurn;
		if (r.cost != 0)
			r.isCostModified = true;
		r.setCostForTurn(r.cost = 0);
		r.exhaust = r.exhaustOnUseOnce = true;
		return Stream.of(r.baseBlock, r.baseDamage, r.baseMagicNumber, r.magicNumber).allMatch(this::not) ? null : r;
	}
	
	private ArrayList<AbstractCard> split(AbstractCard c) {
		ArrayList<AbstractCard> tmp = new ArrayList<AbstractCard>();
		for (int i = 0; i < c.costForTurn; i++)
			tmp.add(this.devide(c));
		AbstractCard r = this.remainder(c);
		if (r != null) {
			tmp.add(r);
		}
		return tmp;
	}
	
	private void addSplit(AbstractCard c) {
		this.split(c).forEach(a -> AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(a)));
		p().hand.group.remove(c);
	}
	
	public void atTurnStart() {
		this.playerTurn = true;
    }
	
	public void onPlayerEndTurn() {
		this.playerTurn = false;
    }
	
	public void onVictory() {
		this.playerTurn = false;
		this.stopPulse();
    }
	
	public void onRefreshHand() {
		if (this.inCombat() && this.playerTurn && p().hand.group.stream().anyMatch(this::check)) {
			this.beginLongPulse();
		} else {
			this.stopPulse();
		}
	}

	@Override
	public void onRightClick() {
		if (this.inCombat() && this.playerTurn && p().hand.group.stream().anyMatch(this::check)) {
			this.playerTurn = false;
			this.show();
			this.stopPulse();
			ArrayList<AbstractCard> list = p().hand.group.stream().filter(this::check).collect(toArrayList());
			if (list.size() == 1) {
				this.addSplit(list.get(0));
				list.clear();
				return;
			}
			CardGroup tmp = new CardGroup(CardGroupType.UNSPECIFIED);
			tmp.group = list;
			AbstractDungeon.gridSelectScreen.open(tmp, 1, this.name, false);
			this.addTmpActionToTop(() -> {
				this.addSplit(AbstractDungeon.gridSelectScreen.selectedCards.get(0));
				AbstractDungeon.gridSelectScreen.selectedCards.clear();
				list.clear();
			});
		}
	}

}