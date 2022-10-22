package testmod.relicsup;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;

public class FissionDeviceUp extends AbstractUpgradedRelic implements ClickableRelic {
	private boolean playerTurn = false;
	private static final ArrayList<AbstractCard> LIST = new ArrayList<AbstractCard>();
	
	public void onUseCard(AbstractCard c, UseCardAction a) {
		if (LIST.contains(c)) {
			this.playAgain(c, a.target instanceof AbstractMonster ? (AbstractMonster) a.target : null);
			this.show();
		}
	}
	
	private long amount() {
		return this.relicStream(FissionDeviceUp.class).count();
	}
	
	private boolean check(AbstractCard c) {
		return c.cost > -1 && c.costForTurn > 0 && !c.freeToPlay();
	}
	
	private AbstractCard devide(AbstractCard c) {
		AbstractCard r = c.makeStatEquivalentCopy();
		long a = amount();
		r.baseBlock = c.baseBlock == -1 ? -1 : (int) (a * c.baseBlock / c.costForTurn);
		r.baseDamage = c.baseDamage == -1 ? -1 : (int) (a * c.baseDamage / c.costForTurn);
		r.baseMagicNumber = c.baseMagicNumber == -1 ? -1 : (int) (a * c.baseMagicNumber / c.costForTurn);
		r.magicNumber = c.magicNumber == -1 ? -1 : (int) (a * c.magicNumber / c.costForTurn);
		if (r.cost != 1)
			r.isCostModified = true;
		r.setCostForTurn(r.cost = 1);
		return r;
	}
	
	private boolean not(int input) {
		return input == -1 || input == 0;
	}
	
	private AbstractCard free(AbstractCard c) {
		AbstractCard copy = c.makeStatEquivalentCopy();
		long a = amount();
		copy.baseBlock *= a;
		copy.baseDamage *= a;
		copy.baseMagicNumber *= a;
		copy.magicNumber *= a;
		if (copy.cost != 0)
			copy.isCostModified = true;
		copy.setCostForTurn(copy.cost = 0);
		return copy;
	}
	
	private AbstractCard remainder(AbstractCard c) {
		AbstractCard r = c.makeStatEquivalentCopy();
		long a = amount();
		r.baseBlock = c.baseBlock == -1 ? -1 : (int) (a * c.baseBlock % c.costForTurn);
		r.baseDamage = c.baseDamage == -1 ? -1 : (int) (a * c.baseDamage % c.costForTurn);
		r.baseMagicNumber = c.baseMagicNumber == -1 ? -1 : (int) (a * c.baseMagicNumber % c.costForTurn);
		r.magicNumber = c.magicNumber == -1 ? -1 : (int) (a * c.magicNumber % c.costForTurn);
		if (r.cost != 0)
			r.isCostModified = true;
		r.setCostForTurn(r.cost = 0);
		return Stream.of(r.baseBlock, r.baseDamage, r.baseMagicNumber, r.magicNumber).allMatch(this::not) ? free(c) : r;
	}
	
	private ArrayList<AbstractCard> split(AbstractCard c) {
		ArrayList<AbstractCard> tmp = new ArrayList<AbstractCard>();
		for (int i = 0; i < c.costForTurn; i++)
			tmp.add(this.devide(c));
		tmp.add(this.remainder(c));
		tmp.forEach(a -> a.freeToPlayOnce = a.isEthereal = true);
		LIST.addAll(tmp);
		return tmp;
	}
	
	private void addSplit(AbstractCard c) {
		this.split(c).forEach(a -> AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(a)));
		p().hand.group.remove(c);
	}
	
	public void onEquip() {
		this.playerTurn = this.inCombat();
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
		if (this.isActive)
			LIST.clear();
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