package testmod.relics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.InvisiblePower;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

import testmod.powers.AbstractTestPower;

public class ConjureBlade extends AbstractTestRelic {
	private static final HashMap<AbstractCard, Integer> BONUS = new HashMap<AbstractCard, Integer>();
	
	public void atPreBattle() {
		BONUS.clear();
		if (this.isActive && p().powers.stream().noneMatch(p -> p instanceof ConjureBladePower))
			p().powers.add(new ConjureBladePower());
    }
	
	public void onDrawOrDiscard() {
		if (!this.isActive)
			return;
		Consumer<AbstractCard> c = tryCalDmg(chooseMonster());
		p().hand.group.forEach(c);
	}
	
	private Consumer<AbstractCard> tryCalDmg(AbstractMonster m) {
		return c -> {
			if (m == null) {
				c.applyPowers();
			} else {
				c.calculateCardDamage(m);
			}
		};
	}
	
	private AbstractMonster chooseMonster() {
		if (!hasEnemies())
			return null;
		if (AbstractDungeon.getMonsters().hoveredMonster != null)
			return AbstractDungeon.getMonsters().hoveredMonster;
		ArrayList<AbstractMonster> tmp = AbstractDungeon.getMonsters().monsters.stream()
				.filter(m -> !m.isDeadOrEscaped()).collect(toArrayList());
		return tmp.size() == 1 ? tmp.get(0) : null;
	}
	
	private class ConjureBladePower extends AbstractTestPower implements InvisiblePower {
		public ConjureBladePower() {
			this.owner = p();
			this.type = PowerType.BUFF;
			this.updateDescription();
			this.addMap(p -> new ConjureBladePower(), true, true);
		}
		
		public void updateDescription() {
			this.description = "";
		}
		
		private int index(AbstractCard c) {
			return p().hand.group.indexOf(c);
		}
		
		private ArrayList<AbstractCard> getNeighbor(AbstractCard c) {
			return !p().hand.contains(c) ? new ArrayList<AbstractCard>()
					: p().hand.group.stream().filter(a -> Math.abs(index(a) - index(c)) == 1).collect(toArrayList());
		}
		
		private int cost(AbstractCard c) {
			return c.freeToPlay() || c.cost == -2 ? 0 : (c.cost == -1 ? EnergyPanel.totalCount : c.costForTurn);
		}
		
		private int bonus(AbstractCard c) {
			int tmp = getNeighbor(c).stream().mapToInt(this::cost).sum() * (int) relicStream(ConjureBlade.class).count();
			BONUS.computeIfPresent(c, (a, b) -> tmp > 0 ? tmp : Math.max(tmp, b));
			BONUS.putIfAbsent(c, tmp);
			return BONUS.get(c);
		}
		
		public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard c) {
			return super.atDamageGive(damage, type, c) + ((type == DamageInfo.DamageType.NORMAL) ? bonus(c) : 0);
		}
		
		public float modifyBlock(float block, AbstractCard c) {
			return super.modifyBlock(block, c) + bonus(c);
		}
		
		public void onAfterUseCard(AbstractCard c, UseCardAction a) {
			this.addTmpActionToBot(() -> BONUS.remove(c));
		}
	}

}