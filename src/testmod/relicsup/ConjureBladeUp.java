package testmod.relicsup;

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

public class ConjureBladeUp extends AbstractUpgradedRelic {
	private static final HashMap<AbstractCard, Integer> BONUS = new HashMap<AbstractCard, Integer>();
	
	public void atPreBattle() {
		BONUS.clear();
		if (this.isActive && p().powers.stream().noneMatch(p -> p instanceof ConjureBladePowerUp))
			p().powers.add(new ConjureBladePowerUp());
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
	
	private class ConjureBladePowerUp extends AbstractTestPower implements InvisiblePower {
		public ConjureBladePowerUp() {
			this.owner = p();
			this.type = PowerType.BUFF;
			this.updateDescription();
			this.addMap(p -> new ConjureBladePowerUp(), true, true);
		}
		
		public void updateDescription() {
			this.description = "";
		}
		
		private int cost(AbstractCard c) {
			return c.freeToPlay() || c.cost == -2 ? 0 : (c.cost == -1 ? EnergyPanel.totalCount : c.costForTurn);
		}
		
		private int handSum() {
			return p().hand.group.stream().mapToInt(this::cost).sum();
		}
		
		private int bonus(AbstractCard c) {
			int tmp = handSum() * (int) relicStream(ConjureBladeUp.class).count();
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