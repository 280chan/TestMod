package testmod.relicsup;

import java.util.ArrayList;
import java.util.Random;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.BaseMod;
import testmod.mymod.TestMod;
import testmod.utils.HandSizeCounterUpdater;

public class HyperplasticTissueUp extends AbstractUpgradedRelic implements HandSizeCounterUpdater {
	private int delta = 0;
	
	public HyperplasticTissueUp() {
		super(RelicTier.COMMON, LandingSound.SOLID);
	}
	
	public void onCardDraw(AbstractCard c) {
		if (c.type == CardType.STATUS || c.type == CardType.CURSE) {
			this.updateHandSize(2);
			this.delta += 2;
			this.dealDmg();
		}
    }
	
	public int[] dmg(int a) {
		if (a == 0)
			return new int[] {};
		int total = BaseMod.MAX_HAND_SIZE;
		if (a == 1)
			return new int[] { total };
		Random rng = new Random(AbstractDungeon.cardRandomRng.randomLong());
		if (a == 2) {
			int tmp = rng.nextInt(total);
			return new int[] { tmp, total - tmp };
		}
		int[] dmg = new int[a];
		for (int i = 0; i < a; i++)
			dmg[i] = 0;
		rng.ints(total, 0, a).forEach(i -> dmg[i]++);
		return dmg;
	}
	
	public void dealDmg() {
		ArrayList<AbstractMonster> l = AbstractDungeon.getMonsters().monsters.stream().filter(m -> !m.isDeadOrEscaped())
				.collect(toArrayList());
		if (l.isEmpty() || BaseMod.MAX_HAND_SIZE < 1)
			return;
		int[] dmg = dmg(l.size());
		for (int i = 0; i < l.size(); i++)
			this.atb(new DamageAction(l.get(i), new DamageInfo(null, dmg[i], DamageType.THORNS)));
		this.show();
	}
	
	public void onPlayerEndTurn() {
		this.dealDmg();
	}
	
	public void onEquip() {
		this.updateHandSize(2);
		this.delta = 0;
		TestMod.setActivity(this);
		if (this.inCombat()) {
			if (this.isActive)
				this.atPreBattle();
			this.dealDmg();
		}
    }
	
	public void onUnequip() {
		this.updateHandSize(-(this.delta + 2));
    }
	
	public void atPreBattle() {
		this.delta = 0;
		this.updateHandSize(0);
    }
	
	public void onVictory() {
		this.updateHandSize(-this.delta);
		this.delta = 0;
    }

}